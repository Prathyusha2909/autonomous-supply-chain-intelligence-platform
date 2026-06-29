package com.autonomous.supplychain.shipment.domain;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shipment_events")
public class ShipmentEventEntity {
    @Id
    private UUID eventId;

    @Column(nullable = false)
    private String shipmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private Instant occurredAt;

    private String locationName;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    private ExceptionCode exceptionCode;

    @Column(length = 2000)
    private String notes;

    protected ShipmentEventEntity() {
    }

    public ShipmentEventEntity(
            UUID eventId,
            String shipmentId,
            EventType eventType,
            Instant occurredAt,
            String locationName,
            Severity severity,
            ExceptionCode exceptionCode,
            String notes
    ) {
        this.eventId = eventId;
        this.shipmentId = shipmentId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.locationName = locationName;
        this.severity = severity;
        this.exceptionCode = exceptionCode;
        this.notes = notes;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getLocationName() {
        return locationName;
    }

    public Severity getSeverity() {
        return severity;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public String getNotes() {
        return notes;
    }
}
