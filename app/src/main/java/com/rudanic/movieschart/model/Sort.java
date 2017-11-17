package com.rudanic.movieschart.model;


public enum Sort {

    POPULAR("popular"),
    TOP_RATED("top_rated"),
    FAVORITE("favorite");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return value;
    }
}
