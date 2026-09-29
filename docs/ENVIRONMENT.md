# Environment Configuration

Never commit real secrets.

## Backend

```env
APP_ENV=local
SERVER_PORT=8080
DATABASE_URL=jdbc:postgresql://localhost:5432/artms
DATABASE_USERNAME=artms
DATABASE_PASSWORD=change-me
REDIS_URL=redis://localhost:6379
JWT_ISSUER=artms
JWT_ACCESS_TTL_MINUTES=15
JWT_REFRESH_TTL_DAYS=30
STORAGE_ENDPOINT=http://localhost:9000
STORAGE_BUCKET=artms-private
STORAGE_ACCESS_KEY=change-me
STORAGE_SECRET_KEY=change-me
STORAGE_REGION=us-east-1
WEBSOCKET_ENABLED=true
```

## Flutter
Use `--dart-define` or a secure build configuration for:
- API base URL;
- WebSocket URL;
- environment name;
- public analytics configuration.

Do not embed private secrets in the mobile app.

## Environments
Local:
- Docker Compose.
Development:
- shared non-production services.
Staging:
- production-like.
Production:
- managed/private infrastructure.

## Profiles
Spring profiles:
- `local`
- `dev`
- `staging`
- `prod`

## Secret Management
Production secrets should be stored in a secret manager rather than plaintext environment files.

## Configuration Rule
Business configuration such as grading is stored in PostgreSQL and managed through the admin UI; environment variables are for infrastructure/runtime configuration only.
