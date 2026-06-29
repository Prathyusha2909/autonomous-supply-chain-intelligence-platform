package com.autonomous.supplychain.shipment.config;

import com.autonomous.supplychain.common.events.LogisticsTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    NewTopic shipmentEventsTopic() {
        return TopicBuilder.name(LogisticsTopics.SHIPMENT_EVENTS)
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic exceptionAlertsTopic() {
        return TopicBuilder.name(LogisticsTopics.EXCEPTION_ALERTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
