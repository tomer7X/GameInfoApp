package com.example.gameinfoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private Button filterButton;
    private GameAdapter adapter;
    private List<Game> gameList = new ArrayList<>();
    private final String API_KEY = "80d338883fdf4b43a0ae4829f21e0863"; // Replace with your RAWG API key
    private SwitchCompat switchMode;

    private int currentPage = 1; // Start from the first page
    private boolean isLoading = false; // Prevent multiple simultaneous API calls

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        searchView = view.findViewById(R.id.search_view);
        filterButton = view.findViewById(R.id.btn_filter);
        switchMode = view.findViewById(R.id.switchMode);

        // Setup RecyclerView
        setupRecyclerView();

        // Fetch games from API
        fetchGames(null);

        // Setup SearchView
        setupSearchView();

        // Setup Dark Mode Toggle
        setupDarkModeToggle();

        return view;
    }

    private void setupDarkModeToggle() {
        SharedPreferences preferences = requireContext().getSharedPreferences("settings", 0);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);

        // Set initial state
        switchMode.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Handle toggle click
        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the new mode in preferences
            preferences.edit().putBoolean("dark_mode", isChecked).apply();

            // Set the theme
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // Add thumb animation
            animateThumb(buttonView, isChecked);
        });
    }



    private void setupRecyclerView() {
        adapter = new GameAdapter(gameList, this::navigateToDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Add the endless scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Check if we've reached the end of the list
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        // Load more games
                        loadMoreGames();
                    }
                }
            }
        });
    }

    private void loadMoreGames() {
        isLoading = true; // Prevent multiple simultaneous API calls
        currentPage++; // Increment the page number

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        Call<GameResponse> call = gameApi.getGames(API_KEY, null, currentPage, 40); // Next page with 40 results
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameList.addAll(response.body().getResults()); // Append new games
                    adapter.notifyDataSetChanged();
                }
                isLoading = false; // Allow further API calls
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                // Handle errors
                isLoading = false; // Allow further API calls
            }
        });
    }
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchGames(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void fetchGames(String query) {
        currentPage = 1; // Reset to the first page when performing a new search
        isLoading = true;

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        Call<GameResponse> call = gameApi.getGames(API_KEY, query, currentPage, 40); // Fetch first page with 40 results
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameList.clear(); // Clear the existing list for new data
                    gameList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                }
                isLoading = false; // Allow further API calls
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                // Handle errors
                isLoading = false;
            }
        });
    }
    private void animateThumb(View switchWidget, boolean isChecked) {
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(
                switchWidget,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f, 1f),
                PropertyValuesHolder.ofFloat("rotation", 0f, isChecked ? 180f : -180f, 0f)
        );
        animator.setDuration(400); // Duration in milliseconds
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void navigateToDetail(String gameId) {
        NavController navController = Navigation.findNavController(requireView());
        Bundle bundle = new Bundle();
        bundle.putString("gameId", gameId);
        navController.navigate(R.id.action_main_to_detail, bundle);
    }
}
