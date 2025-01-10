package com.example.gameinfoapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GameApi {
    @GET("games")
    Call<GameResponse> getGames(
            @Query("key") String apiKey,
            @Query("search") String query,
            @Query("page") int page,
            @Query("page_size") int pageSize,
            @Query("ordering") String ordering // Add ordering query parameter
    );

    @GET("games/{id}")
    Call<GameDetail> getGameDetails(
            @Path("id") String gameId,
            @Query("key") String apiKey
    );

}
