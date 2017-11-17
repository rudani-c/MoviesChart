package com.rudanic.movieschart.event;


public class FavoriteChangeEvent {

    public final boolean isFavorite;

    public FavoriteChangeEvent(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isFavoriteChanged() {
        return isFavorite;
    }

}
