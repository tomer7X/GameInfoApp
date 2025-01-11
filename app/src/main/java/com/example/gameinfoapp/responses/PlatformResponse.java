package com.example.gameinfoapp.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlatformResponse {
    @SerializedName("results")
    private List<Platform> platforms;

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public static class Platform {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

