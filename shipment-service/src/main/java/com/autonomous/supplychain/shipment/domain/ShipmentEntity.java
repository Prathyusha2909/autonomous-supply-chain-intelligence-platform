package com.autonomous.supplychain.shipment.domain;

import com.autonomous.supplychain.common.model.ShipmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "shipments")
public class ShipmentEntity {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String carrier;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private String currentLocationName;
    private Double currentLatitude;
    private Double currentLongitude;

    @Column(nullable = false)
    private Instant plannedEta;

    private Instant predictedEta;
    private Double riskScore;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected ShipmentEntity() {
    }

    public ShipmentEntity(String id, String orderNumber, String carrier, String origin, String destination, Instant plannedEta) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.carrier = carrier;
        this.origin = origin;
        this.destination = destination;
        this.plannedEta = plannedEta;
        this.predictedEta = plannedEta;
        this.status = ShipmentStatus.CREATED;
        this.riskScore = 0.1;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
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

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getCurrentLocationName() {
        return currentLocationName;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void updateLocation(String name, Double latitude, Double longitude) {
        this.currentLocationName = name;
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
    }

    public Instant getPlannedEta() {
        return plannedEta;
    }

    public Instant getPredictedEta() {
        return predictedEta;
    }

    public void setPredictedEta(Instant predictedEta) {
        this.predictedEta = predictedEta;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
