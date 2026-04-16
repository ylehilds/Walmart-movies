package com.walmart.moviesapi.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MovieEventProducer {

    private static final Logger log = LoggerFactory.getLogger(MovieEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public MovieEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${movie.events.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(MovieEvent event) {
        try {
            kafkaTemplate.send(topic, event);
        } catch (Exception ex) {
            // If Kafka is unavailable, don't fail the API call; just log it.
            log.warn("Failed to publish event to Kafka: {}", ex.getMessage());
        }
    }
}
