package com.example.gameinfoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private boolean isLoading = false;
    private int currentPage = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        searchView = view.findViewById(R.id.search_view);
        filterButton = view.findViewById(R.id.btn_filter);

        // Setup RecyclerView
        setupRecyclerView();

        // Fetch games from the API
        fetchGames(null, currentPage); // Null query to fetch all games

        // Setup SearchView for searching games
        setupSearchView();

        // Navigate to FilterFragment when filter button is clicked
        filterButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_main_to_filter);
        });

        return view;
    }

    private void setupRecyclerView() {
        adapter = new GameAdapter(gameList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Add Pagination Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && layoutManager.findLastVisibleItemPosition() == gameList.size() - 1) {
                    // Load next page when reaching the end of the list
                    isLoading = true;
                    currentPage++;
                    fetchGames(null, currentPage);
                }
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPage = 1; // Reset to the first page for new search
                gameList.clear(); // Clear the existing list for new search results
                fetchGames(query, currentPage); // Fetch games based on the search query
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // Optional: You can update results in real-time
            }
        });
    }

    private void fetchGames(String query, int page) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GameApi gameApi = retrofit.create(GameApi.class);

        int pageSize = 40; // Number of games per page (maximum: 40)

        Call<GameResponse> call = gameApi.getGames(API_KEY, query, page, pageSize);
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Game> games = response.body().getResults();
                    updateRecyclerView(games); // Update RecyclerView with fetched games
                } else {
                    System.out.println("API Error: " + response.message());
                }
                isLoading = false; // Allow loading more pages
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                System.out.println("Network Error: " + t.getMessage());
                isLoading = false; // Allow retry
            }
        });
    }

    private void updateRecyclerView(List<Game> games) {
        gameList.addAll(games); // Append fetched games to the list
        adapter.notifyDataSetChanged(); // Notify the adapter that the data has change
    }
}
