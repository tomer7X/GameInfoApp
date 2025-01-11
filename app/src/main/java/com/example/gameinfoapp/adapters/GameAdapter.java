package com.example.gameinfoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gameinfoapp.classes.Game;
import com.example.gameinfoapp.R;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> gameList;
    private final OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(String gameId);
    }

    public GameAdapter(List<Game> gameList, OnGameClickListener listener) {
        this.gameList = gameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        if (gameList.isEmpty()) {
            holder.gameTitleTextView.setText("No games available");
            holder.gameRatingTextView.setText("");
            holder.gameReleasedTextView.setText("");
            holder.gameIconImageView.setImageResource(R.drawable.ic_launcher_background); // Placeholder image
            return;
        }

        Game game = gameList.get(position);
        holder.gameTitleTextView.setText(game.getName());
        holder.gameRatingTextView.setText("Rating: " + game.getRating() + "⭐");
        holder.gameReleasedTextView.setText("Release Date: " + game.getReleased());
        Glide.with(holder.itemView.getContext())
                .load(game.getBackgroundImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.gameIconImageView);

        holder.itemView.setOnClickListener(v -> listener.onGameClick(game.getId()));
    }


    @Override
    public int getItemCount() {
        return gameList.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameTitleTextView, gameRatingTextView, gameReleasedTextView;
        ImageView gameIconImageView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameReleasedTextView = itemView.findViewById(R.id.text_view_release_date);
            gameTitleTextView = itemView.findViewById(R.id.text_view_game_title);
            gameRatingTextView = itemView.findViewById(R.id.text_view_game_rating);
            gameIconImageView = itemView.findViewById(R.id.image_view_game_icon);
        }
    }
}