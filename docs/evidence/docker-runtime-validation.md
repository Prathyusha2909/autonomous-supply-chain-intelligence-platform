# Docker Runtime Validation

Captured on 2026-06-30 after installing Docker Desktop to `D:\DockerDesktop` and running:

```bash
docker compose -f infra/docker-compose.yml up --build -d
```

## Containers

```text
autonomous-supply-chain-event-simulator-1        Up   0.0.0.0:8083->8083/tcp
autonomous-supply-chain-frontend-1               Up   0.0.0.0:5173->80/tcp
autonomous-supply-chain-gateway-service-1        Up   0.0.0.0:8080->8080/tcp
autonomous-supply-chain-grafana-1                Up   0.0.0.0:3000->3000/tcp
autonomous-supply-chain-intelligence-db-1        Up   0.0.0.0:5433->5432/tcp
autonomous-supply-chain-intelligence-service-1   Up   0.0.0.0:8082->8082/tcp
autonomous-supply-chain-kafka-1                  Up   0.0.0.0:9092->9092/tcp
autonomous-supply-chain-prometheus-1             Up   0.0.0.0:9090->9090/tcp
autonomous-supply-chain-shipment-db-1            Up   0.0.0.0:5432->5432/tcp
autonomous-supply-chain-shipment-service-1       Up   0.0.0.0:8081->8081/tcp
autonomous-supply-chain-zookeeper-1              Up   2181/tcp, 2888/tcp, 3888/tcp
```

## Health Checks

```text
GET http://localhost:8080/actuator/health -> {"status":"UP","groups":["liveness","readiness"]}
GET http://localhost:8081/actuator/health -> {"status":"UP","groups":["liveness","readiness"]}
GET http://localhost:8082/actuator/health -> {"status":"UP","groups":["liveness","readiness"]}
```

## Kafka Topics

```text
__consumer_offsets
shipment.events
shipment.exception-alerts
```

## Live Demo Flow

```text
Created shipment: SHP-27FCCA85 / PO-LIVE-145809
Raised event: EXCEPTION_RAISED at Port of Singapore
Shipment state: EXCEPTION, riskScore 0.9416666666666667
Risk read model: riskScore 0.99, delayMinutes 690
Copilot answer: 2 shipment(s) are above the delay risk threshold.
```
