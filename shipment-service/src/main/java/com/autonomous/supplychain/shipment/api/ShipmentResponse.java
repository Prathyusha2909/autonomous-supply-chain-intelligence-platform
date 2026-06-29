package com.autonomous.supplychain.shipment.api;

import com.autonomous.supplychain.common.model.ShipmentStatus;
import com.autonomous.supplychain.shipment.domain.ShipmentEntity;

import java.time.Instant;

public record ShipmentResponse(
        String id,
        String orderNumber,
        String carrier,
        String origin,
        String destination,
        ShipmentStatus status,
        String currentLocationName,
        Double currentLatitude,
        Double currentLongitude,
        Instant plannedEta,
        Instant predictedEta,
        Double riskScore,
        Instant updatedAt
) {
    public static ShipmentResponse from(ShipmentEntity entity) {
        return new ShipmentResponse(
                entity.getId(),
                entity.getOrderNumber(),
                entity.getCarrier(),
                entity.getOrigin(),
                entity.getDestination(),
                entity.getStatus(),
                entity.getCurrentLocationName(),
                entity.getCurrentLatitude(),
                entity.getCurrentLongitude(),
                entity.getPlannedEta(),
                entity.getPredictedEta(),
                entity.getRiskScore(),
                entity.getUpdatedAt()
        );
    }
}
