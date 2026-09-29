import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 },  // Ramp-up to 20 virtual users
    { duration: '1m', target: 50 },   // Sustained peak load
    { duration: '20s', target: 0 },   // Graceful cooldown
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'], // 95% of requests must complete within 500ms
    http_req_failed: ['rate<0.01'],                 // Error rate below 1%
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'http://localhost:8080/api/v1';
const TENANT_CODE = __ENV.TENANT_CODE || 'DEMO';

export default function () {
  let authToken = '';

  group('Authentication Flow', () => {
    const loginPayload = JSON.stringify({
      tenantCode: TENANT_CODE,
      username: 'admin',
      password: 'ChangeMe123!',
    });

    const params = {
      headers: {
        'Content-Type': 'application/json',
      },
    };

    const res = http.post(`${BASE_URL}/auth/login`, loginPayload, params);
    check(res, {
      'login status 200': (r) => r.status === 200,
      'token received': (r) => {
        try {
          const body = JSON.parse(r.body);
          if (body.data && body.data.accessToken) {
            authToken = body.data.accessToken;
            return true;
          }
          return false;
        } catch (e) {
          return false;
        }
      },
    });
  });

  sleep(1);

  if (authToken) {
    const authHeaders = {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
    };

    group('Student & Academic Query Flow', () => {
      const studentRes = http.get(`${BASE_URL}/students?page=0&size=20`, authHeaders);
      check(studentRes, {
        'students list status 200': (r) => r.status === 200,
      });

      const yearRes = http.get(`${BASE_URL}/academic-years`, authHeaders);
      check(yearRes, {
        'academic years status 200': (r) => r.status === 200,
      });
    });

    sleep(1);

    group('Health & Verification Flow', () => {
      const healthRes = http.get('http://localhost:8080/actuator/health');
      check(healthRes, {
        'health endpoint status 200': (r) => r.status === 200,
      });
    });
  }

  sleep(1);
}
