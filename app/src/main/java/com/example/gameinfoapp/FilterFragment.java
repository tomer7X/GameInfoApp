package com.example.gameinfoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterFragment extends Fragment {

    private RangeSlider sliderRating, sliderReleasedYear;
    private RecyclerView recyclerPlatforms, recyclerGenres, recyclerCompanies;
    private Button btnApply, btnReset;

    private List<String> selectedPlatforms = new ArrayList<>();
    private List<String> selectedGenres = new ArrayList<>();
    private List<String> selectedCompanies = new ArrayList<>();

    private final String API_KEY = "80d338883fdf4b43a0ae4829f21e0863";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        // Initialize views
        sliderRating = view.findViewById(R.id.slider_rating);
        sliderReleasedYear = view.findViewById(R.id.slider_released_year);
        recyclerPlatforms = view.findViewById(R.id.recycler_platforms);
        recyclerGenres = view.findViewById(R.id.recycler_genres);
        recyclerCompanies = view.findViewById(R.id.recycler_companies);
        btnApply = view.findViewById(R.id.btn_apply);
        btnReset = view.findViewById(R.id.btn_reset);

        sliderRating.setValues(0f, 5f);
        sliderReleasedYear.setValues(1970f, 2025f);

        // Load filter data
        loadPlatforms();
        loadGenres();
        loadCompanies();

        // Reset button functionality
        btnReset.setOnClickListener(v -> resetFilters());

        // Apply button functionality
        btnApply.setOnClickListener(v -> applyFilters());

        Bundle arguments = getArguments();
        if (arguments != null) {
            float min = arguments.getFloat("yearMin", 1970f);
            float max = arguments.getFloat("yearMax", 2025f);
            sliderReleasedYear.setValues(min, max);
        }

        return view;
    }

    private void loadPlatforms() {
        GameApi gameApi = RetrofitClient.getRetrofitInstance().create(GameApi.class);
        gameApi.getPlatforms(API_KEY).enqueue(new Callback<PlatformResponse>() {
            @Override
            public void onResponse(Call<PlatformResponse> call, Response<PlatformResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlatformResponse.Platform> platforms = response.body().getPlatforms();
                    List<String> platformNames = new ArrayList<>();
                    for (PlatformResponse.Platform platform : platforms) {
                        platformNames.add(platform.getName());
                    }
                    setupRecyclerView(recyclerPlatforms, platformNames, selectedPlatforms);
                }
            }

            @Override
            public void onFailure(Call<PlatformResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void loadGenres() {
        GameApi gameApi = RetrofitClient.getRetrofitInstance().create(GameApi.class);
        gameApi.getGenres(API_KEY).enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GenreResponse.Genre> genres = response.body().getGenres();
                    List<String> genreNames = new ArrayList<>();
                    for (GenreResponse.Genre genre : genres) {
                        genreNames.add(genre.getName());
                    }
                    setupRecyclerView(recyclerGenres, genreNames, selectedGenres);
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void loadCompanies() {
        GameApi gameApi = RetrofitClient.getRetrofitInstance().create(GameApi.class);
        gameApi.getCompanies(API_KEY).enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CompanyResponse.Company> companies = response.body().getCompanies();
                    List<String> companyNames = new ArrayList<>();
                    for (CompanyResponse.Company company : companies) {
                        companyNames.add(company.getName());
                    }
                    setupRecyclerView(recyclerCompanies, companyNames, selectedCompanies);
                }
            }

            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<String> items, List<String> selectedItems) {
        MultipleChoiceAdapter adapter = new MultipleChoiceAdapter(items, selectedItems);
        recyclerView.setAdapter(adapter);
    }

    private void resetFilters() {
        sliderRating.setValues(0f, 5f);
        sliderReleasedYear.setValues(1970f, 2025f);
        selectedPlatforms.clear();
        selectedGenres.clear();
        selectedCompanies.clear();
        recyclerPlatforms.getAdapter().notifyDataSetChanged();
        recyclerGenres.getAdapter().notifyDataSetChanged();
        recyclerCompanies.getAdapter().notifyDataSetChanged();
    }

    private void applyFilters() {
        // Pass filters back to MainFragment
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedPlatforms", new ArrayList<>(selectedPlatforms));
        bundle.putStringArrayList("selectedGenres", new ArrayList<>(selectedGenres));
        bundle.putStringArrayList("selectedCompanies", new ArrayList<>(selectedCompanies));
        bundle.putFloat("ratingMin", sliderRating.getValues().get(0));
        bundle.putFloat("ratingMax", sliderRating.getValues().get(1));
        String yearMin = Math.round(sliderReleasedYear.getValues().get(0)) + "-01-01";
        String yearMax = Math.round(sliderReleasedYear.getValues().get(1)) + "-12-31";
        bundle.putFloat("yearMin", Math.round(sliderReleasedYear.getValues().get(0)));
        bundle.putFloat("yearMax", Math.round(sliderReleasedYear.getValues().get(1)));
        bundle.putString("yearRange", yearMin + "," + yearMax);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_filterFragment_to_mainFragment, bundle);
    }
}
