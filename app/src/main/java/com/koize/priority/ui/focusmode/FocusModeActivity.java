package com.koize.priority.ui.focusmode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.R;
import com.koize.priority.ui.routineplanner.HabitsData;
import com.koize.priority.ui.routineplanner.RoutineData;

import java.util.ArrayList;

public class FocusModeActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView focusRV;
    ArrayList<RoutineData> focusDataArrayList;
    ArrayList<HabitsData> focusHabitsArrayList;
    FocusAdapter FocusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

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
        }else{
            Snackbar.make(this.findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }

        focusRV = findViewById(R.id.recycler_focusMode);
        focusDataArrayList = new ArrayList<>();

        FocusAdapter = new FocusAdapter(focusDataArrayList,getApplicationContext(),this::onFocusClick);
        focusRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getApplicationContext()));
        focusRV.setAdapter(FocusAdapter);
        getFocus();
        focusHabitsArrayList = new ArrayList<>();
    }

    private void getFocus() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                focusDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RoutineData routineData = dataSnapshot.getValue(RoutineData.class);
                    focusDataArrayList.add(routineData);
                    focusHabitsArrayList = routineData.getRoutineHabitsList();
                }
                FocusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onFocusClick(int i) {

    }
}