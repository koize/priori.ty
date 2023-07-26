package com.koize.priority.ui.routineplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koize.priority.R;

public class RoutinePlannerPage extends AppCompatActivity {
    private FloatingActionButton addRoutineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_planner_page);
        addRoutineButton = findViewById(R.id.button_routine_add);
        addRoutineButton.setOnClickListener(addRoutineListener);
    }


    View.OnClickListener addRoutineListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RoutinePlannerPage.this, RoutineEditorPage.class);
            startActivity(intent);
        }
    };
}

