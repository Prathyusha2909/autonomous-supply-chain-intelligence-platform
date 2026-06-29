CREATE TABLE shipments (
    id VARCHAR(40) PRIMARY KEY,
    order_number VARCHAR(80) NOT NULL UNIQUE,
    carrier VARCHAR(120) NOT NULL,
    origin VARCHAR(160) NOT NULL,
    destination VARCHAR(160) NOT NULL,
    status VARCHAR(40) NOT NULL,
    current_location_name VARCHAR(160),
    current_latitude DOUBLE PRECISION,
    current_longitude DOUBLE PRECISION,
    planned_eta TIMESTAMPTZ NOT NULL,
    predicted_eta TIMESTAMPTZ,
    risk_score DOUBLE PRECISION,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE shipment_events (
    event_id UUID PRIMARY KEY,
    shipment_id VARCHAR(40) NOT NULL REFERENCES shipments(id),
    event_type VARCHAR(60) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    location_name VARCHAR(160),
    severity VARCHAR(40),
    exception_code VARCHAR(80),
    notes VARCHAR(2000)
);

CREATE INDEX idx_shipments_status_updated ON shipments(status, updated_at DESC);
CREATE INDEX idx_shipment_events_shipment_time ON shipment_events(shipment_id, occurred_at DESC);
