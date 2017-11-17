package com.rudanic.movieschart.dto;

import com.rudanic.movieschart.model.MovieVideos;

import java.util.List;


public class MovieVideoResponse {

    private Integer id;
    private List<MovieVideos> results;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieVideos> getResults() {
        return results;
    }

    public void setResults(List<MovieVideos> results) {
        this.results = results;
    }
}
