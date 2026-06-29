package com.autonomous.supplychain.intelligence.api;

import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import com.autonomous.supplychain.intelligence.domain.BottleneckObservation;

import java.time.Instant;
import java.util.UUID;

public record BottleneckResponse(
        UUID id,
        String locationName,
        ExceptionCode exceptionCode,
        Severity severity,
        Long shipmentsImpacted,
        Double averageDwellMinutes,
        Instant updatedAt
) {
    public static BottleneckResponse from(BottleneckObservation observation) {
        return new BottleneckResponse(
                observation.getId(),
                observation.getLocationName(),
                observation.getExceptionCode(),
                observation.getSeverity(),
                observation.getShipmentsImpacted(),
                observation.getAverageDwellMinutes(),
                observation.getUpdatedAt()
        );
    }
}
