package com.example.gameinfoapp;

import java.util.List;

public class GameMovie {
    private Data data;

    public Data getData() {
        return data;
    }
    
    public static class Data {
        private String max;

        public String getMax() {
            return max;
        }
    }
}
