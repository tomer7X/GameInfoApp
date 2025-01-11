package com.example.gameinfoapp;

import android.app.AlertDialog;
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

    private RangeSlider sliderReleasedYear;
    private Button btnPlatforms, btnGenres, btnCompanies, btnApply, btnReset;

    private List<String> selectedPlatforms = new ArrayList<>();
    private List<String> platforms = new ArrayList<>();
    private List<String> selectedGenres = new ArrayList<>();
    private List<String> genres = new ArrayList<>();
    private List<String> selectedCompanies = new ArrayList<>();
    private List<String> companies = new ArrayList<>();

    private final String API_KEY = "80d338883fdf4b43a0ae4829f21e0863";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        sliderReleasedYear = view.findViewById(R.id.slider_released_year);
        btnPlatforms = view.findViewById(R.id.btn_platforms);
        btnGenres = view.findViewById(R.id.btn_genres);
        btnCompanies = view.findViewById(R.id.btn_companies);
        btnApply = view.findViewById(R.id.btn_apply);
        btnReset = view.findViewById(R.id.btn_reset);

        sliderReleasedYear.setValues(1970f, 2025f);

        // Load filter data
        loadPlatforms();
        loadGenres();
        loadCompanies();


        btnPlatforms.setOnClickListener(v -> showMultiChoiceDialog("Select Platforms", platforms, selectedPlatforms));
        btnGenres.setOnClickListener(v -> showMultiChoiceDialog("Select Genres", genres, selectedGenres));
        btnCompanies.setOnClickListener(v -> showMultiChoiceDialog("Select Companies", companies, selectedCompanies));

        // Reset button functionality
        btnReset.setOnClickListener(v -> resetFilters());

        // Apply button functionality
        btnApply.setOnClickListener(v -> applyFilters());

        Bundle arguments = getArguments();
        if (arguments != null) {
            selectedPlatforms = arguments.getStringArrayList("selectedPlatforms");
            selectedGenres = arguments.getStringArrayList("selectedGenres");
            selectedCompanies = arguments.getStringArrayList("selectedCompanies");
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
                    List<PlatformResponse.Platform> platformList = response.body().getPlatforms();
                    platforms.clear();
                    for (PlatformResponse.Platform platform : platformList) {
                        platforms.add(platform.getName());
                    }
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
                    List<GenreResponse.Genre> genreList = response.body().getGenres();
                    genres.clear();
                    for (GenreResponse.Genre genre : genreList) {
                        genres.add(genre.getName());
                    }
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
                    List<CompanyResponse.Company> companyList = response.body().getCompanies();
                    companies.clear();
                    for (CompanyResponse.Company company : companyList) {
                        companies.add(company.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<CompanyResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void showMultiChoiceDialog(String title, List<String> items, List<String> selectedItems) {
        boolean[] checkedItems = new boolean[items.size()];
        for (int i = 0; i < items.size(); i++) {
            checkedItems[i] = selectedItems.contains(items.get(i));
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMultiChoiceItems(items.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedItems.add(items.get(which));
                    } else {
                        selectedItems.remove(items.get(which));
                    }
                })
                .setPositiveButton("OK", null)
                .show();
    }


    private void resetFilters() {
        sliderReleasedYear.setValues(1970f, 2025f);
        selectedPlatforms.clear();
        selectedGenres.clear();
        selectedCompanies.clear();
    }

    private void applyFilters() {
        // Pass filters back to MainFragment
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedPlatforms", new ArrayList<>(selectedPlatforms));
        bundle.putStringArrayList("selectedGenres", new ArrayList<>(selectedGenres));
        bundle.putStringArrayList("selectedCompanies", new ArrayList<>(selectedCompanies));
        String yearMin = Math.round(sliderReleasedYear.getValues().get(0)) + "-01-01";
        String yearMax = Math.round(sliderReleasedYear.getValues().get(1)) + "-12-31";
        bundle.putFloat("yearMin", Math.round(sliderReleasedYear.getValues().get(0)));
        bundle.putFloat("yearMax", Math.round(sliderReleasedYear.getValues().get(1)));
        bundle.putString("yearRange", yearMin + "," + yearMax);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_filterFragment_to_mainFragment, bundle);
    }
}
