package com.example.gameinfoapp;

import java.util.List;

import okhttp3.internal.platform.Platform;

public class Game {
    private String name;
    private String released;
    private double rating;
    private String background_image;

    private String id;

    private List<GameDetail.Platform> platforms;



    public String getName() {
        return name;
    }

    public String getReleased() {
        return released;
    }

    public double getRating() {
        return rating;
    }

    public String getBackgroundImage() {
        return background_image;
    }

    public String getId() { return id;
    }
}


