package com.example.gameinfoapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GameApi {
    @GET("games")
    Call<GameResponse> getGames(
            @Query("key") String apiKey,
            @Query("search") String query,
            @Query("page") int page, // Add page parameter
            @Query("page_size") int pageSize // Add page size parameter
    );

}
