package com.koize.priority.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.koize.priority.FocusModeActivity;
import com.koize.priority.MonthlyPlannerPage;
import com.koize.priority.R;
import com.koize.priority.SettingsActivity;
import com.koize.priority.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MaterialCardView monthlyPlannerButton;
    private Chip settingsChip;
    private Chip aboutChip;
    private Chip focusModeChip;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        monthlyPlannerButton = root.findViewById(R.id.button_home_open_montly_planner);
        monthlyPlannerButton.setOnClickListener(monthlyPlannerButtonListener);
        settingsChip = root.findViewById(R.id.button_home_settings);
        settingsChip.setOnClickListener(settingsChipListener);
        aboutChip = root.findViewById(R.id.button_home_about);
        aboutChip.setOnClickListener(aboutChipListener);
        focusModeChip = root.findViewById(R.id.button_home_focus);
        focusModeChip.setOnClickListener(focusModeChipListener);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener monthlyPlannerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), MonthlyPlannerPage.class);
            startActivity(intent);
        }
    };
    View.OnClickListener settingsChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener aboutChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), MonthlyPlannerPage.class); //TODO change to ABOUT page
            startActivity(intent);
        }
    };

    View.OnClickListener focusModeChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), FocusModeActivity.class);
            startActivity(intent);
        }
    };
}