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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.koize.priority.FocusModeActivity;
import com.koize.priority.ui.monthlyplanner.MonthlyPlannerPage;
import com.koize.priority.R;
import com.koize.priority.settings.SettingsActivity;
import com.koize.priority.databinding.FragmentHomeBinding;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MaterialCardView monthlyPlannerButton;
    private Chip settingsChip;
    private Chip aboutChip;
    private Chip focusModeChip;
    private TextView greetingText;
    FirebaseUser user;
    String name;
    String partOfDay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            name = user.getDisplayName();
        }

        greetingText = root.findViewById(R.id.title_home_greeting);
        monthlyPlannerButton = root.findViewById(R.id.button_home_open_montly_planner);
        monthlyPlannerButton.setOnClickListener(monthlyPlannerButtonListener);
        settingsChip = root.findViewById(R.id.button_home_settings);
        settingsChip.setOnClickListener(settingsChipListener);
        aboutChip = root.findViewById(R.id.button_home_about);
        aboutChip.setOnClickListener(aboutChipListener);
        focusModeChip = root.findViewById(R.id.button_home_focus);
        focusModeChip.setOnClickListener(focusModeChipListener);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY) + 8;
        if(timeOfDay >= 0 && timeOfDay < 12){
            partOfDay = "morning";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            partOfDay = "afternoon";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            partOfDay = "evening";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            partOfDay = "night";
        }else{
            partOfDay = "day";
        }

        if ((name != null) && (name!="")) {
            greetingText.setText("Good " + partOfDay + ", "+name+"!");
        }
        else {
            greetingText.setText("Good " + partOfDay + ", " + "Peasant" + "!");
        }

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