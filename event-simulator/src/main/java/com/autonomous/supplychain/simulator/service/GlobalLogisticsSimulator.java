package com.autonomous.supplychain.simulator.service;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.events.LogisticsTopics;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.GeoPoint;
import com.autonomous.supplychain.common.model.Severity;
import com.autonomous.supplychain.common.model.ShipmentStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class GlobalLogisticsSimulator {
    private static final Logger log = LoggerFactory.getLogger(GlobalLogisticsSimulator.class);

    private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
    private final Random random = new Random();
    private final List<SimulatedShipment> shipments = List.of(
            new SimulatedShipment("SHP-SIM-001", "PO-90001", "Maersk", "Shanghai, CN", "Los Angeles, US",
                    List.of(
                            new GeoPoint("Port of Shanghai", 31.2304, 121.4737),
                            new GeoPoint("Port of Singapore", 1.2644, 103.8200),
                            new GeoPoint("Port of Los Angeles", 33.7405, -118.2775)
                    )),
            new SimulatedShipment("SHP-SIM-002", "PO-90002", "DHL", "Hamburg, DE", "Chicago, US",
                    List.of(
                            new GeoPoint("Hamburg Warehouse", 53.5511, 9.9937),
                            new GeoPoint("Rotterdam Port", 51.9244, 4.4777),
                            new GeoPoint("Chicago DC", 41.8781, -87.6298)
                    )),
            new SimulatedShipment("SHP-SIM-003", "PO-90003", "FedEx", "Mumbai, IN", "Dubai, AE",
                    List.of(
                            new GeoPoint("Nhava Sheva", 18.9490, 72.9512),
                            new GeoPoint("Jebel Ali Port", 25.0118, 55.0610),
                            new GeoPoint("Dubai South FC", 24.8969, 55.1614)
                    ))
    );

    public GlobalLogisticsSimulator(KafkaTemplate<String, ShipmentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void seedCreatedEvents() {
        for (SimulatedShipment shipment : shipments) {
            publish(ShipmentEvent.builder(shipment.shipmentId(), EventType.SHIPMENT_CREATED)
                    .orderNumber(shipment.orderNumber())
                    .carrier(shipment.carrier())
                    .origin(shipment.origin())
                    .destination(shipment.destination())
                    .currentLocation(shipment.route().get(0))
                    .status(ShipmentStatus.CREATED)
                    .plannedEta(Instant.now().plus(5, ChronoUnit.DAYS))
                    .predictedEta(Instant.now().plus(5, ChronoUnit.DAYS))
                    .severity(Severity.LOW)
                    .notes("Synthetic shipment seeded by event simulator.")
                    .build());
        }
    }

    @Scheduled(fixedDelayString = "${app.simulator.fixed-delay-ms:5000}")
    public void publishNextEvent() {
        SimulatedShipment shipment = shipments.get(random.nextInt(shipments.size()));
        GeoPoint location = shipment.route().get(random.nextInt(shipment.route().size()));
        boolean exception = random.nextDouble() < 0.35;
        EventType eventType = exception ? EventType.EXCEPTION_RAISED : EventType.LOCATION_UPDATED;
        Severity severity = exception ? randomSeverity() : Severity.LOW;
        ExceptionCode exceptionCode = exception ? randomException() : null;
        int dwellMinutes = exception ? 180 + random.nextInt(720) : random.nextInt(90);
        Instant plannedEta = Instant.now().plus(4, ChronoUnit.DAYS);
        Instant predictedEta = plannedEta.plus(dwellMinutes / 2L, ChronoUnit.MINUTES);

        ShipmentEvent event = ShipmentEvent.builder(shipment.shipmentId(), eventType)
                .orderNumber(shipment.orderNumber())
                .carrier(shipment.carrier())
                .origin(shipment.origin())
                .destination(shipment.destination())
                .currentLocation(location)
                .status(exception ? ShipmentStatus.EXCEPTION : ShipmentStatus.IN_TRANSIT)
                .plannedEta(plannedEta)
                .predictedEta(predictedEta)
                .dwellMinutes(dwellMinutes)
                .severity(severity)
                .exceptionCode(exceptionCode)
                .notes(exception ? "Synthetic disruption detected in logistics network." : "Synthetic location heartbeat.")
                .build();
        publish(event);
    }

    private void publish(ShipmentEvent event) {
        kafkaTemplate.send(LogisticsTopics.SHIPMENT_EVENTS, event.shipmentId(), event);
        log.info("Published {} for {}", event.eventType(), event.shipmentId());
    }

    private Severity randomSeverity() {
        Severity[] values = {Severity.MEDIUM, Severity.HIGH, Severity.CRITICAL};
        return values[random.nextInt(values.length)];
    }

    private ExceptionCode randomException() {
        ExceptionCode[] values = {
                ExceptionCode.PORT_CONGESTION,
                ExceptionCode.CUSTOMS_HOLD,
                ExceptionCode.WEATHER_RISK,
                ExceptionCode.MISSED_CONNECTION,
                ExceptionCode.CAPACITY_SHORTAGE
        };
        return values[random.nextInt(values.length)];
    }

    private record SimulatedShipment(
            String shipmentId,
            String orderNumber,
            String carrier,
            String origin,
            String destination,
            List<GeoPoint> route
    ) {
    }
}
