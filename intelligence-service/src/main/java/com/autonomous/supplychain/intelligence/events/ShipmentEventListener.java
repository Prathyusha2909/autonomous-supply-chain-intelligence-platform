package com.autonomous.supplychain.intelligence.events;

import com.autonomous.supplychain.common.events.LogisticsTopics;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import com.autonomous.supplychain.intelligence.service.IntelligenceEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventListener {
    private static final Logger log = LoggerFactory.getLogger(ShipmentEventListener.class);

    private final IntelligenceEventProcessor eventProcessor;

    public ShipmentEventListener(IntelligenceEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @KafkaListener(topics = LogisticsTopics.SHIPMENT_EVENTS, groupId = "${app.kafka.group-id:intelligence-service}")
    public void onShipmentEvent(ShipmentEvent event) {
        log.info("Processing shipment event {} for {}", event.eventType(), event.shipmentId());
        eventProcessor.process(event);
    }
}
