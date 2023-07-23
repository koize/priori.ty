package com.koize.priority.ui.monthlyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koize.priority.R;

public class MonthlyPlannerPage extends AppCompatActivity {
    private FloatingActionButton addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_planner_page);
        addEventButton = findViewById(R.id.button_monthly_planner_add);
        addEventButton.setOnClickListener(addEventListener);
    }

    View.OnClickListener addEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MonthlyPlannerPopUp eventPopUp = new MonthlyPlannerPopUp();
            eventPopUp.showPopupWindow(v);
        }
    };
}