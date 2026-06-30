# Demo Video Script

Target length: 2 to 3 minutes.

Generated local walkthrough: [supply-chain-demo-walkthrough.mp4](supply-chain-demo-walkthrough.mp4)

## 0:00-0:20 Architecture

Show `docs/architecture.svg`.

Narration:

> This is an event-driven supply chain intelligence platform. A React control tower calls a Spring Cloud Gateway. Shipment events flow through Kafka into an intelligence service that scores delay risk, detects bottlenecks, and powers an OpenAI or Claude-backed operations copilot.

## 0:20-0:50 Start Stack

Command:

```bash
docker compose -f infra/docker-compose.yml up --build
```

Show:

- Kafka container healthy
- PostgreSQL containers healthy
- `shipment-service`, `intelligence-service`, `gateway-service`, and `event-simulator` started

## 0:50-1:25 Create Shipment And Event

Run:

```bash
curl -X POST http://localhost:8080/api/shipments \
  -H "Content-Type: application/json" \
  -d '{"orderNumber":"PO-44519","carrier":"Maersk","origin":"Shanghai, CN","destination":"Los Angeles, US","plannedEta":"2026-07-05T16:00:00Z"}'
```

Then:

```bash
curl -X POST http://localhost:8080/api/shipments/SHP-8F2A91CD/events \
  -H "Content-Type: application/json" \
  -d '{"eventType":"EXCEPTION_RAISED","locationName":"Port of Singapore","latitude":1.2644,"longitude":103.82,"predictedEta":"2026-07-06T03:30:00Z","dwellMinutes":420,"severity":"HIGH","exceptionCode":"PORT_CONGESTION","notes":"Vessel waiting for berth assignment."}'
```

Show Kafka consumer output for `shipment.events`.

## 1:25-1:55 Delay Risk And Dashboard

Open:

- `http://localhost:5173`
- `http://localhost:8082/api/intelligence/risks?threshold=0.45`

Show:

- high-risk shipment
- bottleneck card
- risk recommendation

## 1:55-2:25 AI Copilot

Run:

```bash
curl -X POST http://localhost:8080/api/copilot/query \
  -H "Content-Type: application/json" \
  -d '{"question":"Which shipments are likely delayed and what should ops do next?"}'
```

Show:

- `query-plan` evidence
- OpenAI or Claude provider evidence when `AI_PROVIDER` and API key are configured
- offline fallback answer if no API key is configured

## 2:25-2:50 Observability

Open:

- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8082/swagger-ui.html`
- `http://localhost:3000`
- `http://localhost:9090`

Show:

- OpenAPI endpoints
- Grafana dashboard
- Prometheus targets
