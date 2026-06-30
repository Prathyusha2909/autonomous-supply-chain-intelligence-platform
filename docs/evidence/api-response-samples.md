# API Response Evidence Samples

These samples are the exact payload shape expected from the running stack.

## Create Shipment

Request:

```bash
curl -X POST http://localhost:8080/api/shipments \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "PO-44519",
    "carrier": "Maersk",
    "origin": "Shanghai, CN",
    "destination": "Los Angeles, US",
    "plannedEta": "2026-07-05T16:00:00Z"
  }'
```

Response:

```json
{
  "id": "SHP-8F2A91CD",
  "orderNumber": "PO-44519",
  "carrier": "Maersk",
  "origin": "Shanghai, CN",
  "destination": "Los Angeles, US",
  "status": "CREATED",
  "plannedEta": "2026-07-05T16:00:00Z",
  "predictedEta": "2026-07-05T16:00:00Z",
  "riskScore": 0.1
}
```

## Raise Exception

Request:

```bash
curl -X POST http://localhost:8080/api/shipments/SHP-8F2A91CD/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "EXCEPTION_RAISED",
    "locationName": "Port of Singapore",
    "latitude": 1.2644,
    "longitude": 103.82,
    "predictedEta": "2026-07-06T03:30:00Z",
    "dwellMinutes": 420,
    "severity": "HIGH",
    "exceptionCode": "PORT_CONGESTION",
    "notes": "Vessel waiting for berth assignment."
  }'
```

Response:

```json
{
  "id": "SHP-8F2A91CD",
  "status": "EXCEPTION",
  "currentLocationName": "Port of Singapore",
  "predictedEta": "2026-07-06T03:30:00Z",
  "riskScore": 0.65
}
```

## Ask Copilot

Request:

```bash
curl -X POST http://localhost:8080/api/copilot/query \
  -H "Content-Type: application/json" \
  -d '{"question":"Which shipments are likely delayed and what should ops do next?"}'
```

Response:

```json
{
  "answer": "The highest-risk shipment is SHP-8F2A91CD. Port congestion at Port of Singapore is driving dwell time and ETA variance.",
  "recommendations": [
    "Escalate to the control tower and request a carrier recovery plan.",
    "Notify customer success with the revised ETA.",
    "Evaluate alternate routing if the next scan does not improve."
  ],
  "evidence": [
    {
      "type": "query-plan",
      "title": "DELAY_RISK",
      "detail": "SELECT shipment_id, order_number, carrier, origin, destination, last_location_name, status, exception_code, risk_score, delay_minutes, root_cause, recommendation FROM shipment_risk_profiles WHERE risk_score >= 0.45 ORDER BY risk_score DESC LIMIT 10"
    }
  ]
}
```
