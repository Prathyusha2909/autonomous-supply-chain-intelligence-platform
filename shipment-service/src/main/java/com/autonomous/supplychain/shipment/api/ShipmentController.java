package com.autonomous.supplychain.shipment.api;

import com.autonomous.supplychain.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    List<ShipmentResponse> listShipments() {
        return shipmentService.listShipments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ShipmentResponse createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        return shipmentService.createShipment(request);
    }

    @GetMapping("/{shipmentId}")
    ShipmentResponse getShipment(@PathVariable("shipmentId") String shipmentId) {
        return shipmentService.getShipment(shipmentId);
    }

    @PostMapping("/{shipmentId}/events")
    ShipmentResponse ingestEvent(@PathVariable("shipmentId") String shipmentId, @Valid @RequestBody EventIngestRequest request) {
        return shipmentService.ingestEvent(shipmentId, request);
    }

    @GetMapping("/{shipmentId}/events")
    List<ShipmentTimelineItem> getTimeline(@PathVariable("shipmentId") String shipmentId) {
        return shipmentService.getTimeline(shipmentId);
    }
}
