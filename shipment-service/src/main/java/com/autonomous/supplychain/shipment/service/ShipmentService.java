package com.autonomous.supplychain.shipment.service;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import com.autonomous.supplychain.common.model.GeoPoint;
import com.autonomous.supplychain.common.model.Severity;
import com.autonomous.supplychain.common.model.ShipmentStatus;
import com.autonomous.supplychain.shipment.api.CreateShipmentRequest;
import com.autonomous.supplychain.shipment.api.EventIngestRequest;
import com.autonomous.supplychain.shipment.api.ShipmentResponse;
import com.autonomous.supplychain.shipment.api.ShipmentTimelineItem;
import com.autonomous.supplychain.shipment.domain.ShipmentEntity;
import com.autonomous.supplychain.shipment.domain.ShipmentEventEntity;
import com.autonomous.supplychain.shipment.events.ShipmentEventPublisher;
import com.autonomous.supplychain.shipment.repository.ShipmentEventRepository;
import com.autonomous.supplychain.shipment.repository.ShipmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository eventRepository;
    private final ShipmentEventPublisher eventPublisher;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            ShipmentEventRepository eventRepository,
            ShipmentEventPublisher eventPublisher
    ) {
        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        String shipmentId = "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ShipmentEntity shipment = new ShipmentEntity(
                shipmentId,
                request.orderNumber(),
                request.carrier(),
                request.origin(),
                request.destination(),
                request.plannedEta()
        );
        shipmentRepository.save(shipment);

        ShipmentEvent event = ShipmentEvent.builder(shipmentId, EventType.SHIPMENT_CREATED)
                .orderNumber(request.orderNumber())
                .carrier(request.carrier())
                .origin(request.origin())
                .destination(request.destination())
                .status(ShipmentStatus.CREATED)
                .plannedEta(request.plannedEta())
                .predictedEta(request.plannedEta())
                .severity(Severity.LOW)
                .notes("Shipment created and awaiting carrier pickup.")
                .build();

        saveEvent(event);
        eventPublisher.publish(event);
        return ShipmentResponse.from(shipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> listShipments() {
        return shipmentRepository.findTop50ByOrderByUpdatedAtDesc()
                .stream()
                .map(ShipmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(String shipmentId) {
        return ShipmentResponse.from(findShipment(shipmentId));
    }

    @Transactional(readOnly = true)
    public List<ShipmentTimelineItem> getTimeline(String shipmentId) {
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found");
        }
        return eventRepository.findByShipmentIdOrderByOccurredAtDesc(shipmentId)
                .stream()
                .map(ShipmentTimelineItem::from)
                .toList();
    }

    @Transactional
    public ShipmentResponse ingestEvent(String shipmentId, EventIngestRequest request) {
        ShipmentEntity shipment = findShipment(shipmentId);
        updateShipmentProjection(shipment, request);
        shipment.touch();
        shipmentRepository.save(shipment);

        ShipmentEvent event = ShipmentEvent.builder(shipmentId, request.eventType())
                .orderNumber(shipment.getOrderNumber())
                .carrier(shipment.getCarrier())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .currentLocation(toGeoPoint(request))
                .status(shipment.getStatus())
                .plannedEta(shipment.getPlannedEta())
                .predictedEta(shipment.getPredictedEta())
                .dwellMinutes(request.dwellMinutes())
                .severity(request.severity())
                .exceptionCode(request.exceptionCode())
                .notes(request.notes())
                .build();

        saveEvent(event);
        eventPublisher.publish(event);
        return ShipmentResponse.from(shipment);
    }

    private ShipmentEntity findShipment(String shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
    }

    private void updateShipmentProjection(ShipmentEntity shipment, EventIngestRequest request) {
        if (request.locationName() != null || request.latitude() != null || request.longitude() != null) {
            shipment.updateLocation(request.locationName(), request.latitude(), request.longitude());
        }
        if (request.predictedEta() != null) {
            shipment.setPredictedEta(request.predictedEta());
        }

        shipment.setStatus(nextStatus(request));
        shipment.setRiskScore(Math.max(shipment.getRiskScore(), riskFrom(request)));
    }

    private ShipmentStatus nextStatus(EventIngestRequest request) {
        return switch (request.eventType()) {
            case SHIPMENT_CREATED -> ShipmentStatus.CREATED;
            case LOCATION_UPDATED, ETA_REVISED, WAREHOUSE_DEPARTED -> ShipmentStatus.IN_TRANSIT;
            case WAREHOUSE_ARRIVED -> ShipmentStatus.AT_WAREHOUSE;
            case EXCEPTION_RAISED -> ShipmentStatus.EXCEPTION;
            case EXCEPTION_RESOLVED -> ShipmentStatus.IN_TRANSIT;
            case DELIVERED -> ShipmentStatus.DELIVERED;
        };
    }

    private double riskFrom(EventIngestRequest request) {
        double dwellRisk = request.dwellMinutes() == null ? 0.0 : Math.min(0.35, request.dwellMinutes() / 1440.0);
        double severityRisk = switch (request.severity() == null ? Severity.LOW : request.severity()) {
            case LOW -> 0.1;
            case MEDIUM -> 0.35;
            case HIGH -> 0.65;
            case CRITICAL -> 0.9;
        };
        return Math.min(0.99, severityRisk + dwellRisk);
    }

    private GeoPoint toGeoPoint(EventIngestRequest request) {
        if (request.locationName() == null && request.latitude() == null && request.longitude() == null) {
            return null;
        }
        return new GeoPoint(
                request.locationName(),
                request.latitude() == null ? 0.0 : request.latitude(),
                request.longitude() == null ? 0.0 : request.longitude()
        );
    }

    private void saveEvent(ShipmentEvent event) {
        eventRepository.save(new ShipmentEventEntity(
                event.eventId(),
                event.shipmentId(),
                event.eventType(),
                event.occurredAt() == null ? Instant.now() : event.occurredAt(),
                event.currentLocation() == null ? null : event.currentLocation().name(),
                event.severity(),
                event.exceptionCode(),
                event.notes()
        ));
    }
}
