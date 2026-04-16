package com.walmart.moviesapi.store;

import com.walmart.moviesapi.events.MovieEvent;
import com.walmart.moviesapi.events.MovieEventProducer;
import com.walmart.moviesapi.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryMovieStore implements MovieStore {

    private final ConcurrentMap<String, Movie> movies = new ConcurrentHashMap<>();
    private final MovieEventProducer producer;

    public InMemoryMovieStore(MovieEventProducer producer) {
        this.producer = producer;
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movies.values());
    }

    @Override
    public Optional<Movie> findById(String id) {
        return Optional.ofNullable(movies.get(id));
    }

    @Override
    public Movie create(Movie movie) {
        movie.setId(UUID.randomUUID().toString());
        movies.put(movie.getId(), movie);
        producer.publish(new MovieEvent("created", movie));
        return movie;
    }

    @Override
    public Optional<Movie> update(String id, Movie movie) {
        if (!movies.containsKey(id)) {
            return Optional.empty();
        }
        Movie existing = movies.get(id);
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
        movies.put(id, movie);
        producer.publish(new MovieEvent("updated", movie));
        return Optional.of(movie);
    }

    @Override
    public boolean delete(String id) {
        Movie removed = movies.remove(id);
        if (removed != null) {
            producer.publish(new MovieEvent("deleted", removed));
            return true;
        }
        return false;
    }
}
