# API Response Evidence Samples

These samples were captured from the running Docker Compose stack on 2026-06-30.
Use a unique `orderNumber` when replaying the create request because shipment order numbers are unique.

## Create Shipment

Request:

```bash
curl -X POST http://localhost:8080/api/shipments \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "PO-LIVE-145809",
    "carrier": "Maersk",
    "origin": "Shanghai, CN",
    "destination": "Los Angeles, US",
    "plannedEta": "2026-07-05T16:00:00Z"
  }'
```

Response:

```json
{
  "id": "SHP-27FCCA85",
  "orderNumber": "PO-LIVE-145809",
  "carrier": "Maersk",
  "origin": "Shanghai, CN",
  "destination": "Los Angeles, US",
  "status": "CREATED",
  "currentLocationName": null,
  "plannedEta": "2026-07-05T16:00:00Z",
  "predictedEta": "2026-07-05T16:00:00Z",
  "riskScore": 0.1
}
```

## Raise Exception

Request:

```bash
curl -X POST http://localhost:8080/api/shipments/SHP-27FCCA85/events \
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
  "id": "SHP-27FCCA85",
  "orderNumber": "PO-LIVE-145809",
  "carrier": "Maersk",
  "status": "EXCEPTION",
  "currentLocationName": "Port of Singapore",
  "predictedEta": "2026-07-06T03:30:00Z",
  "riskScore": 0.9416666666666667
}
```

## Delay Risk

Request:

```bash
curl "http://localhost:8080/api/intelligence/risks?threshold=0.45"
```

Response excerpt:

```json
{
  "shipmentId": "SHP-27FCCA85",
  "orderNumber": "PO-LIVE-145809",
  "carrier": "Maersk",
  "lastLocationName": "Port of Singapore",
  "status": "EXCEPTION",
  "lastEventType": "EXCEPTION_RAISED",
  "severity": "HIGH",
  "exceptionCode": "PORT_CONGESTION",
  "riskScore": 0.99,
  "delayMinutes": 690,
  "rootCause": "Port congestion is increasing dwell time and berth uncertainty.",
  "recommendation": "Escalate to control tower, notify customer success, and book an alternate recovery option."
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
  "answer": "2 shipment(s) are above the delay risk threshold. The highest-risk lanes need carrier recovery confirmation and proactive customer ETA updates.",
  "recommendations": [
    "Escalate to control tower, notify customer success, and book an alternate recovery option."
  ],
  "evidence": [
    {
      "type": "query-plan",
      "title": "DELAY_RISK",
      "detail": "SELECT shipment_id, order_number, carrier, origin, destination, last_location_name, status, exception_code, risk_score, delay_minutes, root_cause, recommendation FROM shipment_risk_profiles WHERE risk_score >= 0.45 ORDER BY risk_score DESC LIMIT 10"
    },
    {
      "type": "shipment",
      "title": "SHP-27FCCA85 risk 99%",
      "detail": "Port congestion is increasing dwell time and berth uncertainty."
    }
  ]
}
```
