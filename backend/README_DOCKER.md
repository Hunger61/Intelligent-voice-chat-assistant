Docker usage for this project

Prerequisites
- Docker Engine (or Docker Desktop) with Docker Compose v2

Quick start (app + MySQL + Redis + MinIO)

1) Prepare env file:
- From repository root, copy `.env.example` to `.env` and adjust values if needed.

2) Build and start all services (run from repository root):
- `docker compose build`
- `docker compose up -d`

3) Verify service status:
- `docker compose ps`

4) Tail app logs:
- `docker compose logs -f app`

5) Access ports (default values from `.env`):
- App: `http://localhost:8080`
- MinIO API: `http://localhost:${VC_MINIO_API_PORT}`
- MinIO Console: `http://localhost:${VC_MINIO_CONSOLE_PORT}`

Useful commands
- Stop services: `docker compose down`
- Stop and remove volumes (reset data): `docker compose down -v`

Notes
- Compose sets `SPRING_PROFILES_ACTIVE=docker`, so Spring loads `application-docker.yml`.
- The `minio-init` container auto-creates bucket `${VC_MINIO_BUCKET}` on first start.
- `OLLAMA_BASE_URL` defaults to `http://host.docker.internal:11434`; change it if Ollama runs elsewhere.

