package com.example.gameinfoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GameDetailFragment extends Fragment {

    private TextView textViewGameTitle, textViewReleaseDate, textViewRating, textViewDescription, textViewGenres, textViewPlatforms;
    private ImageView imageViewBackground;
    private final String API_KEY = "80d338883fdf4b43a0ae4829f21e0863"; // Replace with your RAWG API key

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_detail, container, false);

        // Initialize views
        textViewGameTitle = view.findViewById(R.id.text_view_game_title);
        textViewReleaseDate = view.findViewById(R.id.text_view_release_date);
        textViewRating = view.findViewById(R.id.text_view_rating);
        textViewDescription = view.findViewById(R.id.text_view_description);
        imageViewBackground = view.findViewById(R.id.image_view_background);
        textViewPlatforms = view.findViewById(R.id.text_view_platforms);
        textViewGenres = view.findViewById(R.id.text_view_genres);

        // Retrieve the game ID from arguments
        if (getArguments() != null) {
            String gameId = getArguments().getString("gameId");
            fetchGameDetails(gameId);
        }

        return view;
    }

    private void fetchGameDetails(String gameId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        Call<GameDetail> call = gameApi.getGameDetails(gameId, API_KEY);
        call.enqueue(new Callback<GameDetail>() {
            @Override
            public void onResponse(Call<GameDetail> call, Response<GameDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GameDetail gameDetail = response.body();
                    updateUI(gameDetail);
                }
            }

            @Override
            public void onFailure(Call<GameDetail> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void updateUI(GameDetail gameDetail) {
        textViewGameTitle.setText(gameDetail.getName());
        textViewReleaseDate.setText("Release Date: " + gameDetail.getReleased());
        textViewRating.setText("Rating: " + gameDetail.getRating() + "⭐");
        textViewDescription.setText(gameDetail.getDescriptionRaw());

        // Extract platform names
        if (gameDetail.getPlatforms() != null && !gameDetail.getPlatforms().isEmpty()) {
            StringBuilder platforms = new StringBuilder();
            for (GameDetail.Platform platform : gameDetail.getPlatforms()) {
                platforms.append(platform.getPlatform().getName()).append(", ");
            }
            textViewPlatforms.setText("Platforms: " + platforms.toString().replaceAll(", $", ""));
        } else {
            textViewPlatforms.setText("Platforms: Not Available");
        }

        // Extract genre names
        if (gameDetail.getGenres() != null && !gameDetail.getGenres().isEmpty()) {
            StringBuilder genres = new StringBuilder();
            for (GameDetail.Genre genre : gameDetail.getGenres()) {
                genres.append(genre.getName()).append(", ");
            }
            textViewGenres.setText("Genres: " + genres.toString().replaceAll(", $", ""));
        } else {
            textViewGenres.setText("Genres: Not Available");
        }

        // Load the background image using Glide
        Glide.with(this)
                .load(gameDetail.getBackgroundImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageViewBackground);
    }
}
