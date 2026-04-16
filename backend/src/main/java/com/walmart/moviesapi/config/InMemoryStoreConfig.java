package com.walmart.moviesapi.config;

import com.walmart.moviesapi.events.MovieEventProducer;
import com.walmart.moviesapi.store.InMemoryMovieStore;
import com.walmart.moviesapi.store.MovieStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "movies.persistence", havingValue = "memory", matchIfMissing = true)
public class InMemoryStoreConfig {

    @Bean
    public MovieStore inMemoryMovieStore(MovieEventProducer producer) {
        return new InMemoryMovieStore(producer);
    }
}
