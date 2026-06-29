package com.autonomous.supplychain.intelligence.api;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import com.autonomous.supplychain.common.model.ShipmentStatus;
import com.autonomous.supplychain.intelligence.domain.ShipmentRiskProfile;

import java.time.Instant;

public record RiskProfileResponse(
        String shipmentId,
        String orderNumber,
        String carrier,
        String origin,
        String destination,
        String lastLocationName,
        ShipmentStatus status,
        EventType lastEventType,
        Severity severity,
        ExceptionCode exceptionCode,
        Instant plannedEta,
        Instant predictedEta,
        Double riskScore,
        Long delayMinutes,
        String rootCause,
        String recommendation,
        Instant updatedAt
) {
    public static RiskProfileResponse from(ShipmentRiskProfile profile) {
        return new RiskProfileResponse(
                profile.getShipmentId(),
                profile.getOrderNumber(),
                profile.getCarrier(),
                profile.getOrigin(),
                profile.getDestination(),
                profile.getLastLocationName(),
                profile.getStatus(),
                profile.getLastEventType(),
                profile.getSeverity(),
                profile.getExceptionCode(),
                profile.getPlannedEta(),
                profile.getPredictedEta(),
                profile.getRiskScore(),
                profile.getDelayMinutes(),
                profile.getRootCause(),
                profile.getRecommendation(),
                profile.getUpdatedAt()
        );
    }
}
