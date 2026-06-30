# Local Validation

Date: 2026-06-30

## Completed

- Freed C drive space by clearing generated crash logs, Windows temp files, and npm cache.
- Reinstalled frontend dependencies with `npm.cmd install`.
- Built the React dashboard with:

```bash
npm.cmd run build
```

Result:

```text
vite build completed successfully
dist/index.html
dist/assets/index-CfLJUcDQ.css
dist/assets/index-8S83vpjX.js
```

- Captured the built dashboard screenshot at `docs/screenshots/dashboard.png`.
- Installed FFmpeg Essentials 8.1.1 with winget for local demo asset generation.
- Generated a 120-second demo walkthrough video at `docs/demo/supply-chain-demo-walkthrough.mp4`.
- Installed Apache Maven 3.9.16 locally under `.tools/` and kept that tool cache out of git.
- Ran the full Java test suite with:

```bash
.\.tools\apache-maven-3.9.16\bin\mvn.cmd -q test
```

Result:

```text
Maven test lifecycle completed successfully.
Surefire: 5 tests run, 0 failures, 0 errors, 0 skipped.
```

- Installed Docker Desktop 4.78.0 to `D:\DockerDesktop` and kept Docker WSL data on `D:\DockerData\wsl`.
- Ran the full local stack with:

```bash
docker compose -f infra/docker-compose.yml up --build -d
```

Result:

```text
11 containers up: frontend, gateway, shipment-service, intelligence-service,
event-simulator, Kafka, Zookeeper, PostgreSQL x2, Prometheus, and Grafana.
```

- Verified health checks for gateway, shipment service, and intelligence service returned `{"status":"UP"}`.
- Verified Kafka topics: `shipment.events` and `shipment.exception-alerts`.
- Captured live browser screenshots for the React dashboard, Swagger UI, and Grafana dashboard.
- Ran the shipment creation -> Kafka event -> delay risk -> copilot answer demo flow successfully.
- Verified repository history has 8 commits on `origin/main` before this runtime proof update.

## Runtime Notes

Useful local URLs after startup:

- Frontend: `http://localhost:5173`
- Gateway: `http://localhost:8080`
- Shipment Swagger UI: `http://localhost:8081/swagger-ui.html`
- Intelligence Swagger UI: `http://localhost:8082/swagger-ui.html`
- Grafana: `http://localhost:3000` (`admin` / `admin`)
