package com.rudanic.movieschart.event;

import com.rudanic.movieschart.model.Movies;


public class MovieSelectedEvent {

    public final Movies movies;

    public MovieSelectedEvent(Movies movies) {
        this.movies = movies;
    }

    public Movies getSelectedMovie() {
        return movies;
    }

}
