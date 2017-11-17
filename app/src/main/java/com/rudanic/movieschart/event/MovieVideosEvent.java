package com.rudanic.movieschart.event;

import com.rudanic.movieschart.model.MovieVideos;


public class MovieVideosEvent {

    public final MovieVideos movieVideos;

    public MovieVideosEvent(MovieVideos movieVideos) {
        this.movieVideos = movieVideos;
    }

    public MovieVideos getMovieVideos() {
        return movieVideos;
    }

}
