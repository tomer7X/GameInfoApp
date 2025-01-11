package com.example.gameinfoapp.fragments;

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

import com.example.gameinfoapp.responses.CompanyResponse;
import com.example.gameinfoapp.api.GameApi;
import com.example.gameinfoapp.responses.GenreResponse;
import com.example.gameinfoapp.responses.PlatformResponse;
import com.example.gameinfoapp.R;
import com.example.gameinfoapp.api.RetrofitClient;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterFragment extends Fragment {

    private RangeSlider sliderReleasedYear;
    private Button btnPlatforms, btnGenres, btnCompanies, btnApply, btnReset;



    private List<String> selectedPlatforms = new ArrayList<>();
    private List<String> platforms = new ArrayList<>();
    private List<Integer> platformIds = new ArrayList<>();
    private Map<String, Integer> platformMap = new HashMap<>();


    private List<String> selectedGenres = new ArrayList<>();
    private List<String> genres = new ArrayList<>();
    private List<Integer> genreIds = new ArrayList<>();
    private Map<String, Integer> genreMap = new HashMap<>();




    private List<String> selectedCompanies = new ArrayList<>();
    private List<String> companies = new ArrayList<>();

    private List<Integer> companyIds = new ArrayList<>();
    private Map<String, Integer> companyMap = new HashMap<>();


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
                    platformMap.clear();
                    for (PlatformResponse.Platform platform : platformList) {
                        platformMap.put(platform.getName(), platform.getId());
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
                    genreMap.clear(); // Clear the map before adding new genres
                    for (GenreResponse.Genre genre : genreList) {
                        genreMap.put(genre.getName(), genre.getId()); // Add name and ID to the map
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
        gameApi.getCompanies(API_KEY,1,40).enqueue(new Callback<CompanyResponse>() {
            @Override
            public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CompanyResponse.Company> companyList = response.body().getCompanies();
                    companyMap.clear(); // Clear the map before adding new genres
                    for (CompanyResponse.Company company : companyList) {
                        companyMap.put(company.getName(), company.getId()); // Add name and ID to the map
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

    private String getPlatformIdsString() {
        // Use a Set to ensure no duplicates

        // Iterate through the list of genres and retrieve their IDs from the map
        if(selectedPlatforms.isEmpty()){
            //stay empty list.
        }
        else{
            for (String platform : platforms) {
                if (platformMap.containsKey(platform) && selectedPlatforms.contains(platform)) {
                    platformIds.add(platformMap.get(platform));
                }
            }
        }


        // Convert the Set of IDs to a comma-separated string
        return platformIds.stream()
                .map(String::valueOf) // Convert each ID to a String
                .collect(Collectors.joining(",")); // Join with commas

    }

    private String getGenreIdsString() {
        // Use a Set to ensure no duplicates

        // Iterate through the list of genres and retrieve their IDs from the map
        if(selectedGenres.isEmpty()){
            //stay empty list.
        }
        else{
            for (String genre : genres) {
                if (genreMap.containsKey(genre) && selectedGenres.contains(genre)) {
                    genreIds.add(genreMap.get(genre));
                }
            }
        }


        // Convert the Set of IDs to a comma-separated string
        return genreIds.stream()
                .map(String::valueOf) // Convert each ID to a String
                .collect(Collectors.joining(",")); // Join with commas

    }
    private String getCompanyIdsString() {
        // Use a Set to ensure no duplicates

        // Iterate through the list of genres and retrieve their IDs from the map
        if(selectedCompanies.isEmpty()){
            //stay empty list.
        }
        else{
            for (String company : companies) {
                if (companyMap.containsKey(company) && selectedCompanies.contains(company)) {
                    companyIds.add(companyMap.get(company));
                }
            }
        }


        // Convert the Set of IDs to a comma-separated string
        return companyIds.stream()
                .map(String::valueOf) // Convert each ID to a String
                .collect(Collectors.joining(",")); // Join with commas

    }



    private void applyFilters() {
        // Pass filters back to MainFragment
        Bundle bundle = new Bundle();
        //String genres = String.join(",", selectedPlatforms);
        //bundle.putString("selectedGenres", genres);


        bundle.putStringArrayList("selectedPlatforms", new ArrayList<>(selectedPlatforms));
        bundle.putStringArrayList("selectedGenres", new ArrayList<>(selectedGenres));
        bundle.putStringArrayList("selectedCompanies", new ArrayList<>(selectedCompanies));

        bundle.putString("selectedGenresID",getGenreIdsString());
        bundle.putString("selectedPlatformsID",getPlatformIdsString());
        bundle.putString("selectedCompaniesID",getCompanyIdsString());



        String yearMin = Math.round(sliderReleasedYear.getValues().get(0)) + "-01-01";
        String yearMax = Math.round(sliderReleasedYear.getValues().get(1)) + "-12-31";
        bundle.putFloat("yearMin", Math.round(sliderReleasedYear.getValues().get(0)));
        bundle.putFloat("yearMax", Math.round(sliderReleasedYear.getValues().get(1)));
        bundle.putString("yearRange", yearMin + "," + yearMax);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_filterFragment_to_mainFragment, bundle);
    }
}
