CREATE TABLE shipment_risk_profiles (
    shipment_id VARCHAR(40) PRIMARY KEY,
    order_number VARCHAR(80),
    carrier VARCHAR(120),
    origin VARCHAR(160),
    destination VARCHAR(160),
    last_location_name VARCHAR(160),
    status VARCHAR(40),
    last_event_type VARCHAR(60),
    severity VARCHAR(40),
    exception_code VARCHAR(80),
    planned_eta TIMESTAMPTZ,
    predicted_eta TIMESTAMPTZ,
    risk_score DOUBLE PRECISION,
    delay_minutes BIGINT,
    root_cause VARCHAR(2000),
    recommendation VARCHAR(2000),
    updated_at TIMESTAMPTZ
);

CREATE TABLE bottleneck_observations (
    id UUID PRIMARY KEY,
    location_name VARCHAR(160),
    exception_code VARCHAR(80),
    severity VARCHAR(40),
    shipments_impacted BIGINT,
    average_dwell_minutes DOUBLE PRECISION,
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_risk_profiles_score ON shipment_risk_profiles(risk_score DESC);
CREATE INDEX idx_bottlenecks_impact ON bottleneck_observations(shipments_impacted DESC, updated_at DESC);
CREATE UNIQUE INDEX uq_bottleneck_location_exception ON bottleneck_observations(location_name, exception_code);
