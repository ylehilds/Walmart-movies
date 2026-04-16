package com.walmart.moviesapi.store;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.walmart.moviesapi.events.MovieEvent;
import com.walmart.moviesapi.events.MovieEventProducer;
import com.walmart.moviesapi.model.Movie;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CosmosMovieStore implements MovieStore {

    private final CosmosContainer container;
    private final MovieEventProducer producer;

    public CosmosMovieStore(CosmosContainer container, MovieEventProducer producer) {
        this.container = container;
        this.producer = producer;
    }

    @Override
    public List<Movie> findAll() {
        CosmosPagedIterable<Movie> items = container.queryItems(
                "SELECT * FROM c",
                new CosmosQueryRequestOptions(),
                Movie.class);
        return items.stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Movie> findById(String id) {
        try {
            return Optional.of(container.readItem(
                    id,
                    new PartitionKey(id),
                    new CosmosItemRequestOptions(),
                    Movie.class).getItem());
        } catch (CosmosException ex) {
            if (ex.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    @Override
    public Movie create(Movie movie) {
        movie.setId(UUID.randomUUID().toString());
        Movie saved = container.createItem(movie).getItem();
        producer.publish(new MovieEvent("created", saved));
        return saved;
    }

    @Override
    public Optional<Movie> update(String id, Movie movie) {
        try {
            // Ensure the item exists before updating and merge missing fields
            Movie existing = container.readItem(
                    id,
                    new PartitionKey(id),
                    new CosmosItemRequestOptions(),
                    Movie.class).getItem();

            if (movie.getTitle() == null) {
                movie.setTitle(existing.getTitle());
            }
            if (movie.getGenre() == null) {
                movie.setGenre(existing.getGenre());
            }
            if (movie.getYear() == null) {
                movie.setYear(existing.getYear());
            }
            if (movie.getRating() == null) {
                movie.setRating(existing.getRating());
            }

            movie.setId(id);
            Movie updated = container.replaceItem(
                    movie,
                    id,
                    new PartitionKey(id),
                    new CosmosItemRequestOptions()
            ).getItem();
            if (updated == null) {
                updated = movie;
            }
            producer.publish(new MovieEvent("updated", updated));
            return Optional.of(updated);
        } catch (CosmosException ex) {
            if (ex.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            Movie existing = container.readItem(
                    id,
                    new PartitionKey(id),
                    new CosmosItemRequestOptions(),
                    Movie.class).getItem();
            container.deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
            producer.publish(new MovieEvent("deleted", existing));
            return true;
        } catch (CosmosException ex) {
            if (ex.getStatusCode() == 404) {
                return false;
            }
            throw ex;
        }
    }
}
