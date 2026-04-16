package com.walmart.moviesapi.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MovieEventListener {

    private static final Logger log = LoggerFactory.getLogger(MovieEventListener.class);

    @KafkaListener(topics = "${movie.events.topic}", groupId = "movies-api-listener")
    public void listen(MovieEvent event) {
        log.info("Received movie event: {} -> {}", event.getAction(), event.getPayload());
    }
}
