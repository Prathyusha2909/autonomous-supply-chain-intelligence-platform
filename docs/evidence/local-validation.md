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

- Verified repository history has 7 commits on `origin/main` before this validation update.

## Blocked On Local Machine

- `docker compose -f infra/docker-compose.yml up --build` cannot run yet because Docker is not installed.
- Current C drive free space is about 0.28 GB, which is not enough for a safe Docker Desktop installation plus image pulls for Kafka, PostgreSQL, Grafana, and the Spring services.
- Live Kafka, Swagger, and Grafana browser screenshots require Docker runtime support on the local machine.

The repository includes Docker Compose, Kubernetes manifests, Swagger/OpenAPI configuration, Prometheus/Grafana provisioning, AI provider adapters, and a demo capture checklist so the remaining live proof artifacts can be recorded once Docker has enough local disk space.
