package com.koize.priority.ui.routineplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.MainActivity;
import com.koize.priority.R;
import com.koize.priority.ui.journal.JournalData;

import java.util.ArrayList;
import java.util.Random;

public class RoutinePlannerPage extends AppCompatActivity {
    private FloatingActionButton addRoutineButton;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public static RoutineData routineDataMain;
    HabitsData habitsData;

    public static ArrayList<HabitsData> routineHabits;
    RecyclerView routineRV;
    RecyclerView routineHabitsRV;
    ArrayList<HabitsData> routineDataHabitsList;
    ArrayList<RoutineData> routineDataArrayList;
    NestedAdapter NestedAdapter;

    RoutineAdapter RoutineAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_planner_page);
        addRoutineButton = findViewById(R.id.button_routine_add);
        addRoutineButton.setOnClickListener(addRoutineListener);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name != "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/routine");
            } else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/routine");
            } else {
                throw new IllegalStateException("Unexpected value: " + name);
            }


        }
        else{
            Snackbar.make(this.findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }
        routineRV = findViewById(R.id.recycler_routinePlanner);
        routineDataArrayList = new ArrayList<>();

        RoutineAdapter = new RoutineAdapter(routineDataArrayList,getApplicationContext(),this::onRoutineClick);
        routineRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getApplicationContext()));
        routineRV.setAdapter(RoutineAdapter);
        getRoutine();

        routineDataHabitsList = new ArrayList<>();
    }

    private void getRoutine() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routineDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RoutineData routineData = dataSnapshot.getValue(RoutineData.class);
                    routineDataArrayList.add(routineData);
                    routineDataHabitsList = routineData.getRoutineHabitsList();
                }
                RoutineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onRoutineClick(int position) {
        routineDataMain = routineDataArrayList.get(position);
        routineHabits = routineDataMain.getRoutineHabitsList();
        Intent intent = new Intent(RoutinePlannerPage.this, RoutineEditorPage.class);
        startActivity(intent);
    }


    View.OnClickListener addRoutineListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RoutinePlannerPage.this, RoutineEditorPage.class);
            startActivity(intent);
            /*
            HabitsData habitsData1 = new HabitsData();
            habitsData1.setHabitsId(11111);
            habitsData1.setHabitsTitle("Exercise");
            habitsData1.setHabitsTextId("Exercise");
            habitsData1.setHabitsDuration(10);
            habitsData1.setHabitsDescription("habitDescription");

            HabitsData habitsData2 = new HabitsData();
            habitsData2.setHabitsId(22222);
            habitsData2.setHabitsTitle("peepee");
            habitsData2.setHabitsTextId("Exercise");
            habitsData2.setHabitsDuration(10);
            habitsData2.setHabitsDescription("habitDescription");

            routineHabits = new ArrayList<>();
            routineHabits.add(habitsData1);
            routineHabits.add(habitsData2);;
            */
            routineHabits = new ArrayList<>();
            routineDataMain = new RoutineData();
            routineDataMain.setRoutineIcon("");
            routineDataMain.setRoutineTitle("");
            routineDataMain.setRoutineTotalDuration(0);
            routineDataMain.setRoutineHabitsList(routineHabits);
            routineDataMain.setRoutineId(new Random().nextInt(1000000));
            routineDataMain.setRoutineTextId("routine_"+ routineDataMain.getRoutineId());
            databaseReference.child(routineDataMain.getRoutineTextId()).setValue(routineDataMain);
            //databaseReference.child(routineDataMain.getRoutineTextId()).child("habitlist").setValue(routineDataMain.getRoutineHabits());

        }
    };
    @Override
    public void onBackPressed()
    {
        finish();
        Intent intent = new Intent(RoutinePlannerPage.this, MainActivity.class);
        startActivity(intent);
    }
}

