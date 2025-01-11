package com.example.gameinfoapp.api;

import com.example.gameinfoapp.responses.CompanyResponse;
import com.example.gameinfoapp.classes.GameDetail;
import com.example.gameinfoapp.responses.GameMovieResponse;
import com.example.gameinfoapp.responses.GameResponse;
import com.example.gameinfoapp.responses.GenreResponse;
import com.example.gameinfoapp.responses.PlatformResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GameApi {

    @GET("games/{id}")
    Call<GameDetail> getGameDetails(
            @Path("id") String gameId,
            @Query("key") String apiKey
    );

    @GET("games/{id}/movies")
    Call<GameMovieResponse> getGameMovies(
            @Path("id") String gameId,
            @Query("key") String apiKey
    );

    @GET("games")
    Call<GameResponse> getGames(
            @Query("key") String apiKey,
            @Query("search") String query,
            @Query("page") int page,
            @Query("page_size") int pageSize,
            @Query("ordering") String ordering,
            @Query("dates") String dates,
            @Query("genres") String genres,
            @Query("platforms") String platforms,
            @Query("publishers") String companies
    );

    @GET("genres")
    Call<GenreResponse> getGenres(@Query("key") String apiKey);

    @GET("platforms")
    Call<PlatformResponse> getPlatforms(@Query("key") String apiKey);

    @GET("publishers")
    Call<CompanyResponse> getCompanies(
            @Query("key") String apiKey,
            @Query("page") int page,
            @Query("page_size") int pageSize
    );

}
