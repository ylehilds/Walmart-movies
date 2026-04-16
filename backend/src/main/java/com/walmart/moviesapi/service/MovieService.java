package com.walmart.moviesapi.service;

import com.azure.cosmos.CosmosException;
import com.walmart.moviesapi.model.Movie;
import com.walmart.moviesapi.store.MovieStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieStore store;

    public MovieService(MovieStore store) {
        this.store = store;
    }

    public List<Movie> findAll() {
        return store.findAll();
    }

    public Optional<Movie> findById(String id) {
        return store.findById(id);
    }

    public Movie create(Movie movie) {
        return store.create(movie);
    }

    public Optional<Movie> update(String id, Movie movie) {
        return store.update(id, movie);
    }

    public boolean delete(String id) {
        return store.delete(id);
    }
}
