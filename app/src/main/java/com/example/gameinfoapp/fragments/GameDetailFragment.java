package com.example.gameinfoapp.fragments;

import android.net.Uri;
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
import com.example.gameinfoapp.api.GameApi;
import com.example.gameinfoapp.classes.GameDetail;
import com.example.gameinfoapp.classes.GameMovie;
import com.example.gameinfoapp.responses.GameMovieResponse;
import com.example.gameinfoapp.R;
import com.example.gameinfoapp.api.RetrofitClient;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GameDetailFragment extends Fragment {

    private TextView textViewGameTitle, textViewReleaseDate, textViewRating, textViewDescription, textViewGenres, textViewPlatforms, textViewPublishers, textViewDeveloeprs;
    private PlayerView playerView;
    private ExoPlayer player;
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
        textViewPublishers = view.findViewById(R.id.text_view_publishers);
        textViewDeveloeprs = view.findViewById(R.id.text_view_developers);

        playerView = view.findViewById(R.id.player_view);

        // Retrieve the game ID from arguments
        if (getArguments() != null) {
            String gameId = getArguments().getString("gameId");
            fetchGameDetails(gameId);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false); // Pause the player
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.release(); // Release resources
            player = null;
        }
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
                    Call<GameMovieResponse> callGetMovie = gameApi.getGameMovies(gameId, API_KEY);
                    callGetMovie.enqueue(new Callback<GameMovieResponse>() {
                        @Override
                        public void onResponse(Call<GameMovieResponse> call, Response<GameMovieResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                GameMovieResponse gameMovieResponse = response.body();
                                List<GameMovie> gameMovieList = gameMovieResponse.getResults();
                                if (gameMovieList != null && !gameMovieList.isEmpty()) {
                                    GameMovie gameMovie = gameMovieList.get(0);
                                    if (gameMovie != null) {
                                        gameDetail.setTrailer(gameMovie.getData().getMax());
                                    }
                                }

                                updateUI(gameDetail);
                            }
                        }

                        @Override
                        public void onFailure(Call<GameMovieResponse> call, Throwable t) {
                            updateUI(gameDetail);
                        }
                    });

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


        textViewDeveloeprs.setText("Developers: " + gameDetail.getDevelopers());
        textViewPublishers.setText("Publishers: " + gameDetail.getPublishers());

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

        // Extract Developers names
        if (gameDetail.getDevelopers() != null && !gameDetail.getDevelopers().isEmpty()) {
            StringBuilder developers = new StringBuilder();
            for (GameDetail.Developer developer : gameDetail.getDevelopers()) {
                developers.append(developer.getName()).append(", ");
            }
            textViewDeveloeprs.setText("Developers: " + developers.toString().replaceAll(", $", ""));
        } else {
            textViewDeveloeprs.setText("Developers: Not Available");
        }

        // Extract Publishers names
        if (gameDetail.getPublishers() != null && !gameDetail.getPublishers().isEmpty()) {
            StringBuilder publishers = new StringBuilder();
            for (GameDetail.Publisher publisher : gameDetail.getPublishers()) {
                publishers.append(publisher.getName()).append(", ");
            }
            textViewPublishers.setText("Publishers: " + publishers.toString().replaceAll(", $", ""));
        } else {
            textViewPublishers.setText("Publishers: Not Available");
        }



        // Load the background image using Glide
        Glide.with(this)
                .load(gameDetail.getBackgroundImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageViewBackground);

        if (gameDetail.getTrailer() != null) {
            player = new ExoPlayer.Builder(getContext()).build();
            playerView.setPlayer(player);

            // Set the video URL
            String videoUrl = gameDetail.getTrailer();
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(mediaItem);

            // Prepare and play
            player.prepare();
        } else {
            textViewDescription.setText(gameDetail.getDescriptionRaw() + "\n\n\nNO TRAILER FOUND");
            playerView.setVisibility(View.GONE);
        }

    }
}
