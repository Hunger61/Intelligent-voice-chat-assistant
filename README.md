# VocalChat Monorepo

This repository contains backend and frontend projects side by side.

## Directory layout

- `backend/`: Java Spring Boot backend
- `frontend/`: Vue 3 + Vite frontend
- `docker-compose.yml`: local stack for backend dependencies and app container
- `.env.example`: compose environment template

## Quick start

### Backend (local)

```powershell
Set-Location backend
.\mvnw.cmd spring-boot:run
```

### Frontend (local)

```powershell
Set-Location frontend
npm install
npm run dev
```

### Docker compose (from repo root)

```powershell
Copy-Item .env.example .env -Force
docker compose build
docker compose up -d
```

