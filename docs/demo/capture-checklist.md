# Capture Checklist

Use this checklist to replace the SVG previews in `docs/screenshots` with real screenshots before applying.

- `docs/screenshots/dashboard.png`: React dashboard at `http://localhost:5173`.
- `docs/screenshots/kafka-stream.png`: terminal running `kafka-console-consumer` for `shipment.events`.
- `docs/screenshots/swagger-api.png`: `shipment-service` or `intelligence-service` at `/swagger-ui.html`.
- `docs/screenshots/grafana-dashboard.png`: Grafana dashboard at `http://localhost:3000`.
- Demo video link: upload a 2-3 minute screen recording and add the URL to `README.md`.

Recommended recording flow:

1. Show `docs/architecture.svg`.
2. Start Docker Compose.
3. Create shipment with `curl`.
4. Raise exception event.
5. Show Kafka event.
6. Show intelligence risk API response.
7. Ask the copilot.
8. Show Swagger and Grafana.
