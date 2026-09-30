/**
 * SSAMS / ARTMS – 50,000 Concurrent Virtual Users (50k VUs) Peak Benchmark
 * =========================================================================
 * Simulates high-concurrency peak load events in Nepalese schools:
 *   - School Board / Terminal Examination Result Publication Day
 *   - Monthly / Terminal Fee Collection & eSewa Payment Rush
 *   - Morning 09:00 AM Attendance & Class Routine Inspection Rush
 *
 * Execution:
 *   # Install k6 (https://k6.io/docs/get-started/installation/)
 *   k6 run --vus 50000 --duration 10m tests/load/k6_50k_concurrent_benchmark.js
 *
 * Distributed Execution (Multiple Load Generators):
 *   k6 run --execution-segment '0:1/4' tests/load/k6_50k_concurrent_benchmark.js
 */

import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Custom Metrics
const resultLookupDuration = new Trend('result_lookup_duration_ms');
const feeOverviewDuration  = new Trend('fee_overview_duration_ms');
const loginDuration        = new Trend('login_duration_ms');
const successfulQueries    = new Counter('successful_50k_queries');
const errorRate            = new Rate('error_rate_50k');

export const options = {
  scenarios: {
    peak_50k_surge: {
      executor: 'ramping-vus',
      startVUs: 1000,
      stages: [
        { duration: '1m',  target: 10000 }, // Warmup to 10k VUs
        { duration: '2m',  target: 25000 }, // Step up to 25k VUs
        { duration: '3m',  target: 50000 }, // Peak 50,000 concurrent active users
        { duration: '4m',  target: 50000 }, // Sustained 50k load for 4 minutes
        { duration: '2m',  target: 10000 }, // Ramp down
        { duration: '30s', target: 0 },     // Final teardown
      ],
      gracefulRampDown: '30s',
    },
  },
  thresholds: {
    // 95% of all HTTP requests must complete under 350ms
    http_req_duration: ['p(90)<250', 'p(95)<350', 'p(99)<800'],
    // Maximum allowable error rate under 50k concurrent users is 0.5%
    error_rate_50k: ['rate<0.005'],
    // Result lookup (heavily cached) must respond under 150ms
    result_lookup_duration_ms: ['p(95)<150'],
    // Fee overview must respond under 200ms
    fee_overview_duration_ms: ['p(95)<200'],
  },
  discardResponseBodies: false,
};

const BASE_URL    = __ENV.API_BASE_URL || 'http://localhost:8080/api/v1';
const TENANT_CODE = __ENV.TENANT_CODE || 'SHREE_SUSANSKRIT';

// Synthetic pool of student IDs for randomized queries
const DEMO_STUDENT_IDS = [
  'e9000000-0000-0000-0000-000000000001',
  'e9000000-0000-0000-0000-000000000002',
  'e9000000-0000-0000-0000-000000000003',
  'e9000000-0000-0000-0000-000000000004',
  'e9000000-0000-0000-0000-000000000005',
];

export default function () {
  let authToken = '';
  const studentId = DEMO_STUDENT_IDS[Math.floor(Math.random() * DEMO_STUDENT_IDS.length)];

  // 1. User Authentication (Login)
  group('1. Student Authentication', () => {
    const startLogin = Date.now();
    const loginPayload = JSON.stringify({
      tenantCode: TENANT_CODE,
      username: 'student.aarav',
      password: 'Susanskrit@2083',
    });

    const params = {
      headers: { 'Content-Type': 'application/json' },
      timeout: '5s',
    };

    const res = http.post(`${BASE_URL}/auth/login`, loginPayload, params);
    loginDuration.add(Date.now() - startLogin);

    const isOk = check(res, {
      'login status 200': (r) => r.status === 200,
    });

    if (isOk) {
      successfulQueries.add(1);
      errorRate.add(0);
      try {
        const body = JSON.parse(res.body);
        if (body.data && body.data.accessToken) {
          authToken = body.data.accessToken;
        }
      } catch (e) {
        // fallback
      }
    } else {
      errorRate.add(1);
    }
  });

  // Short realistic pause between user taps (200ms - 800ms)
  sleep(0.3 + Math.random() * 0.5);

  const authHeaders = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': authToken ? `Bearer ${authToken}` : '',
      'X-Tenant-Code': TENANT_CODE,
    },
    timeout: '5s',
  };

  // 2. High-Surge Read: Examination Result & Marksheet Lookup (Cached in Redis)
  group('2. Examination Result Query', () => {
    const startResult = Date.now();
    const res = http.get(`${BASE_URL}/results/students/${studentId}`, authHeaders);
    resultLookupDuration.add(Date.now() - startResult);

    const isOk = check(res, {
      'result lookup status 200 or 404': (r) => r.status === 200 || r.status === 404,
    });

    if (isOk) {
      successfulQueries.add(1);
      errorRate.add(0);
    } else {
      errorRate.add(1);
    }
  });

  sleep(0.2 + Math.random() * 0.4);

  // 3. High-Surge Read: Fee Overview & Ledger Balance
  group('3. Fee Invoices & Payment Overview', () => {
    const startFee = Date.now();
    const res = http.get(`${BASE_URL}/payments/student/${studentId}/fee-overview`, authHeaders);
    feeOverviewDuration.add(Date.now() - startFee);

    const isOk = check(res, {
      'fee overview status 200': (r) => r.status === 200,
    });

    if (isOk) {
      successfulQueries.add(1);
      errorRate.add(0);
    } else {
      errorRate.add(1);
    }
  });

  sleep(0.2 + Math.random() * 0.3);

  // 4. Daily Attendance & Timetable View
  group('4. Attendance & Class Routine Query', () => {
    const res = http.get(`${BASE_URL}/attendance/students/${studentId}/summary`, authHeaders);
    const isOk = check(res, {
      'attendance summary status 200 or 404': (r) => r.status === 200 || r.status === 404,
    });

    if (isOk) {
      successfulQueries.add(1);
      errorRate.add(0);
    } else {
      errorRate.add(1);
    }
  });
}
