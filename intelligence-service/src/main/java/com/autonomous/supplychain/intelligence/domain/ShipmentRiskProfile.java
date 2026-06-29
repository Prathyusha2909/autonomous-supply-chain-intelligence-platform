package com.autonomous.supplychain.intelligence.domain;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import com.autonomous.supplychain.common.model.ShipmentStatus;
import com.autonomous.supplychain.intelligence.service.RiskAssessment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "shipment_risk_profiles")
public class ShipmentRiskProfile {
    @Id
    private String shipmentId;

    private String orderNumber;
    private String carrier;
    private String origin;
    private String destination;
    private String lastLocationName;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @Enumerated(EnumType.STRING)
    private EventType lastEventType;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    private ExceptionCode exceptionCode;

    private Instant plannedEta;
    private Instant predictedEta;
    private Double riskScore;
    private Long delayMinutes;

    @Column(length = 2000)
    private String rootCause;

    @Column(length = 2000)
    private String recommendation;

    private Instant updatedAt;

    protected ShipmentRiskProfile() {
    }

    public ShipmentRiskProfile(String shipmentId) {
        this.shipmentId = shipmentId;
        this.riskScore = 0.0;
        this.delayMinutes = 0L;
        this.updatedAt = Instant.now();
    }

    public void apply(ShipmentEvent event, RiskAssessment assessment) {
        this.orderNumber = firstNonNull(event.orderNumber(), this.orderNumber);
        this.carrier = firstNonNull(event.carrier(), this.carrier);
        this.origin = firstNonNull(event.origin(), this.origin);
        this.destination = firstNonNull(event.destination(), this.destination);
        this.lastLocationName = event.currentLocation() == null ? this.lastLocationName : event.currentLocation().name();
        this.status = firstNonNull(event.status(), this.status);
        this.lastEventType = event.eventType();
        this.severity = firstNonNull(event.severity(), this.severity);
        this.exceptionCode = firstNonNull(event.exceptionCode(), this.exceptionCode);
        this.plannedEta = firstNonNull(event.plannedEta(), this.plannedEta);
        this.predictedEta = firstNonNull(event.predictedEta(), this.predictedEta);
        this.riskScore = assessment.riskScore();
        this.delayMinutes = assessment.delayMinutes();
        this.rootCause = assessment.rootCause();
        this.recommendation = assessment.recommendation();
        this.updatedAt = Instant.now();
    }

    private static <T> T firstNonNull(T value, T fallback) {
        return value == null ? fallback : value;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getLastLocationName() {
        return lastLocationName;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public EventType getLastEventType() {
        return lastEventType;
    }

    public Severity getSeverity() {
        return severity;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public Instant getPlannedEta() {
        return plannedEta;
    }

    public Instant getPredictedEta() {
        return predictedEta;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public Long getDelayMinutes() {
        return delayMinutes;
    }

    public String getRootCause() {
        return rootCause;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
