package com.autonomous.supplychain.shipment.api;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventIngestRequest(
        @NotNull EventType eventType,
        String locationName,
        Double latitude,
        Double longitude,
        Instant predictedEta,
        Integer dwellMinutes,
        Severity severity,
        ExceptionCode exceptionCode,
        String notes
) {
}
