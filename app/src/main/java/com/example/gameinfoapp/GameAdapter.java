package com.example.gameinfoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> gameList = new ArrayList<>();


    public GameAdapter(List<Game> gameList) {
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position); // Adjusted to hold `Game` objects instead of strings
        holder.gameNameTextView.setText(game.getName());

        // Add click listener to navigate to GameDetailFragment
        holder.itemView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);

            // Create a Bundle to pass game details
            Bundle bundle = new Bundle();
            bundle.putString("gameTitle", game.getName());
            bundle.putString("releaseDate", game.getReleased());
            bundle.putDouble("rating", game.getRating());
            bundle.putString("backgroundImage", game.getBackgroundImage());

            // Navigate to GameDetailFragment
            navController.navigate(R.id.action_main_to_detail, bundle);
        });
    }


    @Override
    public int getItemCount() {
        return gameList.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameNameTextView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameNameTextView = itemView.findViewById(R.id.text_view_game_name);
        }
    }
}
