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

public class GameDetailFragment extends Fragment {

    private TextView textViewGameTitle, textViewReleaseDate, textViewRating;
    private ImageView imageViewBackground;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_detail, container, false);

        // Initialize views
        textViewGameTitle = view.findViewById(R.id.text_view_game_title);
        textViewReleaseDate = view.findViewById(R.id.text_view_release_date);
        textViewRating = view.findViewById(R.id.text_view_rating);
        imageViewBackground = view.findViewById(R.id.image_view_background);

        // Retrieve the arguments passed from MainFragment
        if (getArguments() != null) {
            String gameTitle = getArguments().getString("gameTitle", "N/A");
            String releaseDate = getArguments().getString("releaseDate", "N/A");
            double rating = getArguments().getDouble("rating", 0.0);
            String backgroundImage = getArguments().getString("backgroundImage", "");

            // Set data to views
            textViewGameTitle.setText(gameTitle);
            textViewReleaseDate.setText("Release Date: " + releaseDate);
            textViewRating.setText("Rating: " + rating);

            // Load the background image using Glide
            Glide.with(this)
                    .load(backgroundImage)
                    .into(imageViewBackground);
        }

        return view;
    }
}
