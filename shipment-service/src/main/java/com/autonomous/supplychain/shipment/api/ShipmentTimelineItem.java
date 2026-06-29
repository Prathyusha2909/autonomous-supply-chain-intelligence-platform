package com.autonomous.supplychain.shipment.api;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import com.autonomous.supplychain.shipment.domain.ShipmentEventEntity;

import java.time.Instant;
import java.util.UUID;

public record ShipmentTimelineItem(
        UUID eventId,
        EventType eventType,
        Instant occurredAt,
        String locationName,
        Severity severity,
        ExceptionCode exceptionCode,
        String notes
) {
    public static ShipmentTimelineItem from(ShipmentEventEntity entity) {
        return new ShipmentTimelineItem(
                entity.getEventId(),
                entity.getEventType(),
                entity.getOccurredAt(),
                entity.getLocationName(),
                entity.getSeverity(),
                entity.getExceptionCode(),
                entity.getNotes()
        );
    }
}
