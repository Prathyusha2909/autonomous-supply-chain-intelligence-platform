package com.autonomous.supplychain.shipment.events;

import com.autonomous.supplychain.common.events.LogisticsTopics;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ShipmentEventPublisher.class);

    private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;

    public ShipmentEventPublisher(KafkaTemplate<String, ShipmentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ShipmentEvent event) {
        try {
            kafkaTemplate.send(LogisticsTopics.SHIPMENT_EVENTS, event.shipmentId(), event);
        } catch (KafkaException ex) {
            log.warn("Shipment event {} could not be published immediately", event.eventId(), ex);
        }
    }
}
