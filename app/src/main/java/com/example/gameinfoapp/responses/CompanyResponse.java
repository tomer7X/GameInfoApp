package com.example.gameinfoapp.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompanyResponse {
    @SerializedName("results")
    private List<Company> companies;

    public List<Company> getCompanies() {
        return companies;
    }

    public static class Company {
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
