package com.example.gameinfoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;

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
    private String currentQuery = null; // Current search query
    private String currentOrdering = null; // Current ordering
    private GameAdapter adapter;

    private List<Game> gameList = new ArrayList<>();
    private final String API_KEY = "80d338883fdf4b43a0ae4829f21e0863";
    private final int PAGE_SIZE = 40;
    private SwitchCompat switchMode;

    private int currentPage = 1;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        searchView = view.findViewById(R.id.search_view);
        switchMode = view.findViewById(R.id.switchMode);
        ImageView filterButton = view.findViewById(R.id.btn_filter);

        Spinner sortSpinner = view.findViewById(R.id.spinner_sort);
        setupSortSpinner(sortSpinner);

        // Setup RecyclerView
        setupRecyclerView();

        // Fetch initial games
        fetchGames(currentQuery, currentOrdering);

        // Setup SearchView
        setupSearchView();

        // Setup Dark Mode Toggle
        setupDarkModeToggle(filterButton);

        // Navigate to Filter Screen
        filterButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_main_to_filter);
        });

        return view;
    }

    private void setupDarkModeToggle(ImageView filterButton) {
        SharedPreferences preferences = requireContext().getSharedPreferences("settings", 0);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);

        // Set initial states
        switchMode.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        filterButton.setImageResource(isDarkMode ? R.drawable.dark_ic_filter : R.drawable.ic_filter);

        // Handle Dark Mode Toggle
        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // Update the filter button image
            filterButton.setImageResource(isChecked ? R.drawable.dark_ic_filter : R.drawable.ic_filter);

            // Optional: Add thumb animation
            animateThumb(buttonView, isChecked);
        });
    }

    private void setupRecyclerView() {
        adapter = new GameAdapter(gameList, this::navigateToDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreGames();
                    }
                }
            }
        });
    }


    private void applyLocalSorting(List<Game> games, String ordering) {
        if (ordering == null) return;

        games.sort((game1, game2) -> {
            try {
                switch (ordering) {
                    case "-rating":
                        return Double.compare(game2.getRating(), game1.getRating());
                    case "rating":
                        return Double.compare(game1.getRating(), game2.getRating());
                    case "-released":
                        return game2.getReleased().compareTo(game1.getReleased());
                    case "released":
                        return game1.getReleased().compareTo(game2.getReleased());
                    default:
                        return 0;
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error during sorting: " + e.getMessage());
                return 0;
            }
        });
    }



    private void setupSortSpinner(Spinner spinner) {
        String[] sortOptions = {
                "Default",
                "Rating - High to Low",
                "Rating - Low to High",
                "Released - High to Low",
                "Released - Low to High"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Default
                        currentOrdering = null;
                        break;
                    case 1: // Rating - High to Low
                        currentOrdering = "-rating";
                        break;
                    case 2: // Rating - Low to High
                        currentOrdering = "rating";
                        break;
                    case 3: // Released - High to Low
                        currentOrdering = "-released";
                        break;
                    case 4: // Released - Low to High
                        currentOrdering = "released";
                        break;
                }

                // Log the selected sorting option
                logDebug("Selected sorting: " + sortOptions[position] + " (ordering: " + currentOrdering + ")");

                // Fetch games with updated ordering
                fetchGames(currentQuery, currentOrdering);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }





    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                fetchGames(currentQuery, currentOrdering);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    currentQuery = null;
                    fetchGames(currentQuery, currentOrdering);
                }
                return false;
            }
        });
    }


    private void logDebug(String message) {
        System.out.println("DEBUG: " + message);
    }

    private void logError(String message) {
        System.err.println("ERROR: " + message);
    }



    private List<Game> filterGames(List<Game> games) {
        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            boolean hasValidReleaseDate = game.getReleased() != null && !game.getReleased().isEmpty();
            boolean hasValidRating = game.getRating() > 0.0;

            if (hasValidReleaseDate && hasValidRating) {
                filteredGames.add(game);
            } else {
                System.out.println("DEBUG: Filtered out game: " + game.getName() + " | Released: " + game.getReleased() + " | Rating: " + game.getRating());
            }
        }

        System.out.println("DEBUG: Total games after filtering: " + filteredGames.size());
        return filteredGames;
    }


    private void fetchGames(String query, String ordering) {
        currentPage = 1;
        isLoading = true;

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        System.out.println("DEBUG: Fetching games with query: " + query + ", ordering: " + ordering);

        Call<GameResponse> call = gameApi.getGames(API_KEY, query, currentPage, PAGE_SIZE, ordering);
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameList.clear();

                    List<Game> apiResults = response.body().getResults();
                    System.out.println("DEBUG: API returned " + apiResults.size() + " games.");

                    // Filter and sort the games
                    List<Game> filteredResults = filterGames(apiResults);
                    applyLocalSorting(filteredResults, ordering);

                    if (filteredResults.isEmpty()) {
                        System.out.println("DEBUG: No games available after filtering.");
                    } else {
                        gameList.addAll(filteredResults);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    System.out.println("DEBUG: API response unsuccessful or empty.");
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                System.out.println("ERROR: Failed to fetch games: " + t.getMessage());
                isLoading = false;
            }
        });
    }





    private void loadMoreGames() {
        isLoading = true;
        currentPage++;

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        Call<GameResponse> call = gameApi.getGames(API_KEY, currentQuery, currentPage, PAGE_SIZE, currentOrdering);
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Game> filteredResults = filterGames(response.body().getResults());
                    gameList.addAll(filteredResults);

                    adapter.notifyDataSetChanged();
                } else {
                    logError("API response unsuccessful or empty.");
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                logError("Failed to fetch games: " + t.getMessage());
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
        animator.setDuration(400);
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