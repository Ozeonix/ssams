# Deployment

## Environments
- local
- development
- staging
- production

Never use production data in development without approved anonymization.

## Production Topology

```text
Internet
  |
DNS
  |
WAF / Load Balancer
  |
Spring Boot API instances
  |---- PostgreSQL
  |---- Redis
  |---- Object Storage
  |---- Observability
```

## Docker
Build immutable application images.
Run as non-root.
Use health checks.

## Database
- Managed PostgreSQL preferred.
- Automated backups.
- PITR where available.
- Encryption at rest.
- Private network.
- Restricted security group.

## Redis
Private network only.
No public exposure.

## Object Storage
Private bucket.
Versioning enabled.
Lifecycle rules documented.

## TLS
Use managed certificate or automated certificate renewal.

## CI/CD

Pipeline:
1. checkout;
2. dependency cache;
3. lint;
4. unit tests;
5. integration tests;
6. build;
7. security scans;
8. container build;
9. staging deployment;
10. smoke tests;
11. manual production approval;
12. production deployment;
13. post-deploy health check.

## Database Migration
- Flyway runs during controlled deployment.
- Backward-compatible migrations for rolling upgrades.
- Backup before risky migrations.

## Rollback
Application rollback must not assume database rollback.
Prefer expand/contract migrations.

## Monitoring
Track:
- API latency;
- error rate;
- DB connections;
- slow queries;
- queue depth;
- WebSocket connections;
- notification failures;
- storage errors;
- CPU/memory;
- backup status.

## Scaling
Horizontal scale API instances.
Use managed PostgreSQL scaling/read replicas only when measured need exists.

## Disaster Recovery
Document:
- RPO;
- RTO;
- restore commands;
- DNS failover;
- credential recovery;
- object storage recovery.

Run restore drills at least periodically.

## Production Checklist
- secrets configured;
- TLS;
- backups;
- monitoring;
- alerting;
- rate limits;
- admin MFA;
- CORS;
- DB private;
- storage private;
- migration successful;
- smoke tests passed.
