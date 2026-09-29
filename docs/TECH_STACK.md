# Technology Stack

## Backend
- Java 21 LTS.
- Spring Boot 3.x, pinned to a tested patch release.
- Spring Web.
- Spring Security.
- Spring Validation.
- Spring Data JPA.
- Hibernate.
- Flyway.
- Spring WebSocket/STOMP.
- Spring Actuator.
- Bean Validation.
- Jackson.
- Testcontainers.

## Database
- PostgreSQL 16+ recommended, pinned in deployment.
- Redis 7+ for cache, rate limiting, temporary state and job coordination.
- S3-compatible object storage such as AWS S3 or MinIO.

## Mobile
- Flutter stable channel.
- Dart.
- Android/iOS native integrations only where required.
- Java/Kotlin platform integration can be used for Android-specific functionality.
- State management: choose one standard and enforce it project-wide (Riverpod recommended).
- HTTP: Dio or equivalent.
- Secure storage for tokens.
- WebSocket client.
- Push notifications via FCM/APNs adapters.

## College Desktop
Browser-based responsive web admin.
Recommended implementation: Flutter Web if the team wants one Flutter UI codebase, or a dedicated web frontend if desktop data-grid requirements become dominant. The backend contract remains UI-independent.

## DevOps
- Docker.
- Docker Compose for local development.
- CI/CD with GitHub Actions/GitLab CI.
- Linux production runtime.
- Nginx/Caddy/load balancer.
- Kubernetes optional after scale.
- Prometheus-compatible metrics.
- Grafana.
- OpenTelemetry-compatible tracing.

## Testing
- JUnit 5.
- Mockito.
- Spring Boot Test.
- Testcontainers.
- REST Assured or MockMvc.
- Flutter unit/widget/integration tests.
- Static analysis.
- Dependency vulnerability scanning.

## PDF/Document
Use a server-side document service/library capable of deterministic PDF generation.
Never trust client-generated official results.

## Coding Rule
Pin versions in build files and lockfiles. Do not use dynamic version ranges in production.
