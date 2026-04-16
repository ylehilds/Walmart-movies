package com.walmart.moviesapi.config;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.walmart.moviesapi.events.MovieEventProducer;
import com.walmart.moviesapi.store.CosmosMovieStore;
import com.walmart.moviesapi.store.MovieStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "movies.persistence", havingValue = "cosmos")
public class CosmosStoreConfig {

    @Bean
    public CosmosClient cosmosClient(
            @Value("${azure.cosmos.endpoint}") String endpoint,
            @Value("${azure.cosmos.key}") String key) {
        return new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .gatewayMode()
                .buildClient();
    }

    @Bean
    public CosmosContainer moviesContainer(
            CosmosClient client,
            @Value("${azure.cosmos.database}") String databaseName,
            @Value("${movies.cosmos.autocreate:true}") boolean autoCreate) {
        if (autoCreate) {
            client.createDatabaseIfNotExists(databaseName);
            CosmosContainerProperties properties = new CosmosContainerProperties("movies", "/id");
            // Use database shared throughput to avoid exceeding account RU limits.
            client.getDatabase(databaseName).createContainerIfNotExists(properties);
        }
        return client.getDatabase(databaseName).getContainer("movies");
    }

    @Bean
    public MovieStore cosmosMovieStore(CosmosContainer container, MovieEventProducer producer) {
        return new CosmosMovieStore(container, producer);
    }
}
