package com.example.gameinfoapp;

import java.util.List;

public class GameDetail {
    private String name;
    private String released;
    private double rating;
    private String background_image;
    private String description_raw;
    private List<Genre> genres;
    private List<Platform> platforms;

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

    public String getDescriptionRaw() {
        return description_raw;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    // Nested Genre class
    public static class Genre {
        private String name;

        public String getName() {
            return name;
        }
    }

    // Nested Platform class
    public static class Platform {
        private PlatformDetail platform;

        public PlatformDetail getPlatform() {
            return platform;
        }

        public static class PlatformDetail {
            private String name;

            public String getName() {
                return name;
            }
        }
    }
}
