package com.example.gameinfoapp.fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.example.gameinfoapp.classes.Game;
import com.example.gameinfoapp.adapters.GameAdapter;
import com.example.gameinfoapp.api.GameApi;
import com.example.gameinfoapp.responses.GameResponse;
import com.example.gameinfoapp.R;
import com.example.gameinfoapp.api.RetrofitClient;
import com.example.gameinfoapp.utils.GlideCacheUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainFragment extends Fragment {

    private Bundle bundle;
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

    private String selectedGenresID = null;
    private String selectedPlatformsID = null;
    private String selectedCompaniesID = null;

    private String yearRange = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            bundle = arguments;
            yearRange = bundle.getString("yearRange", "");
            selectedGenresID = bundle.getString("selectedGenresID","");
            selectedPlatformsID = bundle.getString("selectedPlatformsID","");
            selectedCompaniesID = bundle.getString("selectedCompaniesID","");

            selectedCompaniesID = selectedCompaniesID.isEmpty() ? null : selectedCompaniesID;
            selectedPlatformsID = selectedPlatformsID.isEmpty() ? null : selectedPlatformsID;
            selectedGenresID = selectedGenresID.isEmpty() ? null : selectedGenresID;
        }


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
            GlideCacheUtils.clearDiskCache(requireContext());
            GlideCacheUtils.clearMemoryCache(requireContext());
            //Toast.makeText(requireContext(), "Glide cache cleared", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.action_main_to_filter, bundle);
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

    private void loadMoreGames() {
        isLoading = true;
        currentPage++;

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        Call<GameResponse> call = gameApi.getGames(API_KEY, currentQuery, currentPage, PAGE_SIZE, currentOrdering, yearRange,selectedGenresID,selectedPlatformsID,selectedCompaniesID);
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                isLoading = false;
            }
        });
    }

    private void setupSortSpinner(Spinner spinner) {
        // Add a default option for no sorting
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
                        currentOrdering = null; // Clear ordering
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
                fetchGames(currentQuery, currentOrdering); // Fetch games with updated ordering
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
                currentQuery = newText.isEmpty() ? null : newText;
                fetchGames(currentQuery, currentOrdering);
                return false;
            }
        });
    }

    private void fetchGames(String query, String ordering) {
        currentPage = 1;
        isLoading = true;

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        Call<GameResponse> call = gameApi.getGames(API_KEY, query, currentPage, PAGE_SIZE, ordering, yearRange,selectedGenresID,selectedPlatformsID,selectedCompaniesID);
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameList.clear();
                    gameList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
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