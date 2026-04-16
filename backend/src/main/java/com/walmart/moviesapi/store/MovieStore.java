package com.walmart.moviesapi.store;

import com.walmart.moviesapi.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieStore {
    List<Movie> findAll();
    Optional<Movie> findById(String id);
    Movie create(Movie movie);
    Optional<Movie> update(String id, Movie movie);
    boolean delete(String id);
}
