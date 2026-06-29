package com.autonomous.supplychain.common.events;

import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.GeoPoint;
import com.autonomous.supplychain.common.model.Severity;
import com.autonomous.supplychain.common.model.ShipmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShipmentEvent(
        UUID eventId,
        String shipmentId,
        EventType eventType,
        Instant occurredAt,
        String orderNumber,
        String carrier,
        String origin,
        String destination,
        GeoPoint currentLocation,
        ShipmentStatus status,
        Instant plannedEta,
        Instant predictedEta,
        Integer dwellMinutes,
        Severity severity,
        ExceptionCode exceptionCode,
        String notes
) {
    public ShipmentEvent {
        eventId = eventId == null ? UUID.randomUUID() : eventId;
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        Objects.requireNonNull(shipmentId, "shipmentId is required");
        Objects.requireNonNull(eventType, "eventType is required");
    }

    public static Builder builder(String shipmentId, EventType eventType) {
        return new Builder(shipmentId, eventType);
    }

    public static final class Builder {
        private UUID eventId;
        private final String shipmentId;
        private final EventType eventType;
        private Instant occurredAt;
        private String orderNumber;
        private String carrier;
        private String origin;
        private String destination;
        private GeoPoint currentLocation;
        private ShipmentStatus status;
        private Instant plannedEta;
        private Instant predictedEta;
        private Integer dwellMinutes;
        private Severity severity;
        private ExceptionCode exceptionCode;
        private String notes;

        private Builder(String shipmentId, EventType eventType) {
            this.shipmentId = shipmentId;
            this.eventType = eventType;
        }

        public Builder eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder occurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Builder carrier(String carrier) {
            this.carrier = carrier;
            return this;
        }

        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder currentLocation(GeoPoint currentLocation) {
            this.currentLocation = currentLocation;
            return this;
        }

        public Builder status(ShipmentStatus status) {
            this.status = status;
            return this;
        }

        public Builder plannedEta(Instant plannedEta) {
            this.plannedEta = plannedEta;
            return this;
        }

        public Builder predictedEta(Instant predictedEta) {
            this.predictedEta = predictedEta;
            return this;
        }

        public Builder dwellMinutes(Integer dwellMinutes) {
            this.dwellMinutes = dwellMinutes;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder exceptionCode(ExceptionCode exceptionCode) {
            this.exceptionCode = exceptionCode;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ShipmentEvent build() {
            return new ShipmentEvent(
                    eventId,
                    shipmentId,
                    eventType,
                    occurredAt,
                    orderNumber,
                    carrier,
                    origin,
                    destination,
                    currentLocation,
                    status,
                    plannedEta,
                    predictedEta,
                    dwellMinutes,
                    severity,
                    exceptionCode,
                    notes
            );
        }
    }
}
