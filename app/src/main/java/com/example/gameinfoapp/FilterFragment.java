package com.example.gameinfoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class FilterFragment extends Fragment {

    private Spinner spinnerGenre, spinnerPlatform;
    private EditText editTextYear;
    private Button btnApply;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        // Initialize views
        spinnerGenre = view.findViewById(R.id.spinner_genre);
        spinnerPlatform = view.findViewById(R.id.spinner_platform);
        editTextYear = view.findViewById(R.id.edit_text_year);
        btnApply = view.findViewById(R.id.btn_apply);

        // Set up button click to navigate back to MainFragment
        btnApply.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);

            // Pass filter data back (e.g., via a ViewModel or directly)
            String selectedGenre = spinnerGenre.getSelectedItem().toString();
            String selectedPlatform = spinnerPlatform.getSelectedItem().toString();
            String selectedYear = editTextYear.getText().toString();

            // Navigate back to MainFragment
            navController.navigateUp();
        });

        return view;
    }
}
