package com.koize.priority.ui.routineplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.R;

import java.util.ArrayList;
import java.util.Random;

public class RoutineEditorPage extends AppCompatActivity {
    EditText routineTitleET;
    String habitDescription;
    String habitDescriptionEdit;
    PopupWindow popupWindowDescription;
    PopupWindow popupWindow;
    Chip habitDescriptionSaveChip;
    EditText habitDescriptionTyper;
    PopupWindow popupWindowDuration;
    NumberPicker durationPickerHr;
    NumberPicker durationPickerMin;
    public static final int INPUT_METHOD_NEEDED = 1;
    EditText habitTitle;
    Chip habitSaveChip;
    Chip durationSaveChip;
    Chip habitSetDurationChip;
    Chip habitAddDescriptionChip;
    Chip habitPickerNewHabitChip;
    Chip habitSelectorEditChip;
    Chip habitSelectorDeleteChip;
    PopupWindow popupWindowSelector;
    PopupWindow popupWindowEditHabit;
    private FloatingActionButton habitPickerButton;
    Chip habitEditor_saveRoutine;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference habitsDatabaseReference;
    DatabaseReference routineDatabaseReference;
    HabitsData habitsData;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    public RecyclerView habitsRV;
    private HabitsAdapter HabitsAdapter;
    public ArrayList<HabitsData> habitsDataArrayList;
    public int habitDurationHr;
    public int habitDurationMin;
    TextView habitEditHeaderTV;
    private ArrayList<HabitsData> routineEditorDataArrayList;
    RecyclerView routineEditorRV;
    private RoutineEditorAdapter RoutineEditorAdapter;
    RoutineData routineData;
    Chip DeleteBtn;
    Chip BackBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_editor_page);

        habitPickerButton = findViewById(R.id.button_habitPicker_add);
        habitPickerButton.setOnClickListener(addHabitListener);

        habitEditor_saveRoutine = findViewById(R.id.button_routineEditor_save);
        habitEditor_saveRoutine.setOnClickListener(saveRoutineListener);

        DeleteBtn = findViewById(R.id.button_routineEditor_delete);
        DeleteBtn.setOnClickListener(DeleteRoutineListener);

        BackBtn = findViewById(R.id.button_routineEditor_back);
        BackBtn.setOnClickListener(BackRoutineListener);

        routineTitleET = findViewById(R.id.routineEditor_title);

        routineTitleET.setText(RoutinePlannerPage.routineDataMain.getRoutineTitle());

        user = FirebaseAuth.getInstance().getCurrentUser();

        routineEditorRV = findViewById(R.id.recycler_routineEditor);
        routineEditorDataArrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();

        RoutineEditorAdapter = new RoutineEditorAdapter(routineEditorDataArrayList, getApplicationContext(),this::onRoutineHabitClick,this::onRoutineHabitLongClick);
        routineEditorRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getApplicationContext()));
        routineEditorRV.setAdapter(RoutineEditorAdapter);
        getRoutineEditor();
    }

    private void getRoutineEditor() {
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name != "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                routineDatabaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/routine");
            } else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                routineDatabaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/routine");
            } else {
                throw new IllegalStateException("Unexpected value: " + name);
            }


        }
        routineDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //routineEditorDataArrayList.clear();
                //for(DataSnapshot dataSnapShot : snapshot.getChildren()){
                //    RoutineData routineData = dataSnapShot.getValue(RoutineData.class);
                //    routineEditorDataArrayList.add(routineData);
                //}
                //RoutineData routineData = RoutinePlannerPage.routineDataMain;
                //routineEditorDataArrayList.add(RoutinePlannerPage.routineDataMain);
                routineEditorDataArrayList.clear();
                if(RoutinePlannerPage.routineHabits != null){
                    for(HabitsData habits : RoutinePlannerPage.routineHabits){
                        routineEditorDataArrayList.add(habits);
                    }
                }
                RoutineEditorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void onRoutineHabitLongClick(int i) {
    }

    public void onRoutineHabitClick(int i) {

    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getHabits() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                habitsDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HabitsData habitsData = dataSnapshot.getValue(HabitsData.class);
                    habitsDataArrayList.add(habitsData);
                }
                HabitsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    View.OnClickListener saveRoutineListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int totalDuration = 0;
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
                Snackbar.make(v.findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                        .show();
            }
            // database reference

            if(routineTitleET.getText().toString().isEmpty()){
                Snackbar.make(v, "Please enter a title!", Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }else{
                RoutinePlannerPage.routineDataMain.setRoutineTitle(routineTitleET.getText().toString());
            }
            if(routineEditorDataArrayList.isEmpty()){
                Snackbar.make(v, "Please add a habit!", Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }
            RoutinePlannerPage.routineDataMain.setRoutineIcon("");
            for(HabitsData habits : RoutinePlannerPage.routineHabits){
                totalDuration += habits.getHabitsDuration();
            }
            RoutinePlannerPage.routineDataMain.setRoutineTotalDuration(totalDuration);
            try{
                databaseReference.child(RoutinePlannerPage.routineDataMain.getRoutineTextId()).setValue(RoutinePlannerPage.routineDataMain);
            }
            catch(Exception e){
                e.getCause().getCause();
            }
            Snackbar.make(v, "Journal Saved", Snackbar.LENGTH_SHORT)
                    .show();
            RoutinePlannerPage.routineDataMain = new RoutineData();
            RoutinePlannerPage.routineHabits = new ArrayList<>();
            Intent intent = new Intent(getApplicationContext(), RoutinePlannerPage.class);
            startActivity(intent);
            //finish();
        }
    };
    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
        String name = user.getDisplayName();
        routineDatabaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/routine");
        routineDatabaseReference.child(RoutinePlannerPage.routineDataMain.getRoutineTextId()).removeValue();

    }
    //////////////////////////////////////////////////////////////////////////////////////////
    View.OnClickListener addHabitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupWindow(v);}
        /////////////////////////////////////////////////////////////////////////////////////
        public void showPopupWindow(final View view) {
            //Create a View object yourself through inflater
            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_habit_picker, null);


            habitPickerNewHabitChip = popupView.findViewById(R.id.habit_add_new_habit);

            //Specify the length and width through constants

            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

            //Make Inactive Items Outside Of PopupWindow
            boolean focusable = true;

            //Create a window with our parameters
            popupWindow = new PopupWindow(popupView, width, height, focusable);
            // Closes the popup window when touch outside
            //Handler for clicking on the inactive zone of the window

            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String name = user.getDisplayName();
                if ((name != null) && name != "") {
                    firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    databaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/habits");
                } else if (name == "") {
                    firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    databaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/habits");
                } else {
                    throw new IllegalStateException("Unexpected value: " + name);
                }


            }
            else{
                Snackbar.make(view.findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                        .show();
            }



            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            habitsDataArrayList = new ArrayList<>();
            habitsRV = popupView.findViewById(R.id.recycler_habit_list);
            firebaseAuth = FirebaseAuth.getInstance();
            HabitsAdapter = new HabitsAdapter(habitsDataArrayList, getApplicationContext(), this::onHabitClick, this::onHabitLongClick);
            habitsRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(popupView.getContext()));
            habitsRV.setAdapter(HabitsAdapter);
            getHabits();

            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            //Set the location of the window on the screen
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            View container = popupWindow.getContentView().getRootView();
            if (container != null) {
                WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                if (wm != null) {
                    wm.updateViewLayout(container, p);
                }
            }
            //Initialize the elements of our window, install the handler
            habitPickerNewHabitChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewHabitPopUp newHabitPopUp = new NewHabitPopUp();
                    newHabitPopUp.showPopupWindow(v);
                }
            });



        }
        /////////////////////////////////////////////////////////////////////////////////////
        private void onHabitLongClick(int position, View view) {
            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_habit_selection, null);


            habitSelectorEditChip = popupView.findViewById(R.id.popup_habit_selection_edit);
            habitSelectorDeleteChip = popupView.findViewById(R.id.popup_habit_selection_delete);
            habitEditHeaderTV = popupView.findViewById(R.id.popup_habitSelection_header);

            HabitsData habitsData4 = habitsDataArrayList.get(position);
            habitEditHeaderTV.setText("Are you sure you want \n to delete '" + habitsData4.getHabitsTitle() + "' ?");



            //Specify the length and width through constants

            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

            //Make Inactive Items Outside Of PopupWindow
            boolean focusable = true;

            //Create a window with our parameters
            popupWindowSelector = new PopupWindow(popupView, width, height, focusable);
            // Closes the popup window when touch outside
            //Handler for clicking on the inactive zone of the window

            popupWindowSelector.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popupWindowSelector.dismiss();
                        return true;
                    }
                    return false;
                }
            });

            popupWindowSelector.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindowSelector.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
            popupWindowSelector.setOutsideTouchable(true);
            popupWindowSelector.setInputMethodMode(INPUT_METHOD_NEEDED);
            popupWindowSelector.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            //Set the location of the window on the screen
            popupWindowSelector.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            View container = popupWindowSelector.getContentView().getRootView();
            if (container != null) {
                WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                if (wm != null) {
                    wm.updateViewLayout(container, p);
                }
            }
            //Initialize the elements of our window, install the handler

            /*
            habitSelectorEditChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowSelector.dismiss();

                    LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.popup_habit_add, null);


                    habitSaveChip = popupView.findViewById(R.id.button_new_habit_save);
                    habitSetDurationChip = popupView.findViewById(R.id.button_new_habit_setDuration);
                    habitAddDescriptionChip = popupView.findViewById(R.id.button_new_habit_addDescription);
                    habitTitle = popupView.findViewById(R.id.new_habit_title);
                    HabitsData habitsDataEdit = habitsDataArrayList.get(position);
                    //Specify the length and width through constants

                    int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    //Make Inactive Items Outside Of PopupWindow
                    boolean focusable = true;

                    //Create a window with our parameters
                    popupWindowEditHabit = new PopupWindow(popupView, width, height, focusable);
                    // Closes the popup window when touch outside
                    //Handler for clicking on the inactive zone of the window
                    TextView EditHabitHeader = popupView.findViewById(R.id.new_habit_add_header);
                    EditHabitHeader.setText("Edit Habit");


                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String name = user.getDisplayName();
                        if ((name != null) && name != "") {
                            firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                            databaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/habits");
                        } else if (name == "") {
                            firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                            databaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/habits");
                        } else {
                            throw new IllegalStateException("Unexpected value: " + name);
                        }
                    }else{
                        RoutineEditorPage routineEditorPage = new RoutineEditorPage();
                        Snackbar.make(findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    popupWindowEditHabit.setTouchInterceptor(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popupWindowEditHabit.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });

                    popupWindowEditHabit.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupWindowEditHabit.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
                    popupWindowEditHabit.setOutsideTouchable(true);
                    popupWindowEditHabit.setInputMethodMode(INPUT_METHOD_NEEDED);
                    popupWindowEditHabit.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                    //Set the location of the window on the screen
                    popupWindowEditHabit.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                    View container = popupWindowEditHabit.getContentView().getRootView();
                    if (container != null) {
                        WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
                        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        p.dimAmount = 0.3f;
                        if (wm != null) {
                            wm.updateViewLayout(container, p);
                        }
                    }


                    //Initialize the elements of our window, install the handler

                    habitSaveChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (user != null) {
                                if(habitTitle.getText().toString().isEmpty()){
                                    Snackbar.make(view, "Please enter a title!", Snackbar.LENGTH_SHORT)
                                            .show();
                                }else{
                                    habitsDataEdit.setHabitsTitle(habitTitle.getText().toString());
                                }

                                int totalHabitDuration;
                                totalHabitDuration = (habitDurationHr * 60) + habitDurationMin;
                                habitsDataEdit.setHabitsDuration(totalHabitDuration);

                                habitsDataEdit.setHabitsDescription(habitDescription);

                                try{
                                    databaseReference.child(habitsDataEdit.getHabitsTextId()).setValue(habitsDataEdit);
                                }
                                catch(Exception e){
                                    e.getCause().getCause();
                                }
                                popupWindowEditHabit.dismiss();
                                Snackbar.make(view, "Habit Saved", Snackbar.LENGTH_SHORT)
                                        .show();
                            } else {
                                Snackbar.make(view, "Please sign in to save journal", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });

                    habitSetDurationChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
                            View popupView = inflater.inflate(R.layout.popup_duration_setter, null);

                            durationSaveChip = popupView.findViewById(R.id.duration_setter_save);

                            durationPickerHr = popupView.findViewById(R.id.durationPickerHR);
                            durationPickerMin = popupView.findViewById(R.id.durationPickerMIN);

                            HabitsData habitsData3 = habitsDataArrayList.get(position);
                            int habitDurationFill = habitsData3.getHabitsDuration();
                            int habitDurationFillMin = habitDurationFill % 60;
                            int habitDurationFillHr = (habitDurationFill-habitDurationFillMin)/60;

                            durationPickerHr.setMinValue(0);
                            durationPickerHr.setMaxValue(24);
                            durationPickerMin.setMinValue(0);
                            durationPickerMin.setMaxValue(60);

                            durationPickerHr.setValue(habitDurationFillHr);
                            durationPickerMin.setValue(habitDurationFillMin);

                            //Specify the length and width through constants

                            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                            //Make Inactive Items Outside Of PopupWindow
                            boolean focusable = true;

                            //Create a window with our parameters
                            popupWindowDuration = new PopupWindow(popupView, width, height, focusable);
                            // Closes the popup window when touch outside
                            //Handler for clicking on the inactive zone of the window


                            user = FirebaseAuth.getInstance().getCurrentUser();

                            popupWindowDuration.setTouchInterceptor(new View.OnTouchListener() {
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                        popupWindowDuration.dismiss();
                                        return true;
                                    }
                                    return false;
                                }
                            });

                            popupWindowDuration.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popupWindowDuration.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
                            popupWindowDuration.setOutsideTouchable(true);
                            popupWindowDuration.setInputMethodMode(INPUT_METHOD_NEEDED);
                            popupWindowDuration.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                            //Set the location of the window on the screen
                            popupWindowDuration.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                            View container = popupWindowDuration.getContentView().getRootView();
                            if (container != null) {
                                WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
                                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                p.dimAmount = 0.3f;
                                if (wm != null) {
                                    wm.updateViewLayout(container, p);
                                }
                            }

                            durationSaveChip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    habitDurationHr = durationPickerHr.getValue();
                                    habitDurationMin = durationPickerMin.getValue();
                                    habitSetDurationChip.setText(habitDurationHr + " Hr " + habitDurationMin + " Min");
                                    if(habitDurationHr == 0){
                                        habitSetDurationChip.setText(habitDurationMin);
                                    }else if(habitDurationMin > 0) {
                                        habitSetDurationChip.setText(habitDurationHr + " Hr " + habitDurationMin + " Min ");
                                    }else{
                                        habitSetDurationChip.setText("Set Duration");
                                    }
                                    popupWindowDuration.dismiss();
                                }
                            });

                        }
                    });

                    habitAddDescriptionChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
                            View popupView = inflater.inflate(R.layout.popup_habit_add_description, null);

                            habitDescriptionTyper = popupView.findViewById(R.id.addDescriptionET);
                            habitDescriptionSaveChip = popupView.findViewById(R.id.button_add_description_chipsave);

                            HabitsData habitsData2 = habitsDataArrayList.get(position);
                            String habitDescriptionFill = habitsData2.getHabitsDescription();
                            habitDescriptionTyper.setText(habitDescriptionFill);

                            //Specify the length and width through constants

                            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                            //Make Inactive Items Outside Of PopupWindow
                            boolean focusable = true;

                            //Create a window with our parameters
                            popupWindowDescription = new PopupWindow(popupView, width, height, focusable);
                            // Closes the popup window when touch outside
                            //Handler for clicking on the inactive zone of the window

                            popupWindowDescription.setTouchInterceptor(new View.OnTouchListener() {
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                        popupWindowDescription.dismiss();
                                        return true;
                                    }
                                    return false;
                                }
                            });

                            popupWindowDescription.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popupWindowDescription.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
                            popupWindowDescription.setOutsideTouchable(true);
                            popupWindowDescription.setInputMethodMode(INPUT_METHOD_NEEDED);
                            popupWindowDescription.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                            //Set the location of the window on the screen
                            popupWindowDescription.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                            View container = popupWindowDescription.getContentView().getRootView();
                            if (container != null) {
                                WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
                                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                p.dimAmount = 0.3f;
                                if (wm != null) {
                                    wm.updateViewLayout(container, p);
                                }
                            }

                            habitDescriptionSaveChip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    habitDescription = habitDescriptionTyper.getText().toString();
                                    if (habitDescription.length() > 12) {
                                        habitAddDescriptionChip.setText("Add Description");
                                    } else if(habitDescription.length() > 12) {
                                        habitAddDescriptionChip.setText(habitDescription);
                                    }else{
                                        habitAddDescriptionChip.setText(habitDescription.substring(0, 12) + "...");
                                    }
                                    popupWindowDescription.dismiss();
                                }
                            });


                        }
                    });
                    HabitsData habitsData1 = habitsDataArrayList.get(position);
                    habitTitle.setText(habitsData1.getHabitsTitle());

                    if(habitsData1.getHabitsDescription() == null){
                        habitAddDescriptionChip.setText("Add Description");
                    }else if(habitsData1.getHabitsDescription().length() > 12) {
                        habitAddDescriptionChip.setText(habitsData1.getHabitsDescription() + "...");
                    }else{
                        habitAddDescriptionChip.setText(habitsData1.getHabitsDescription());
                    }

                    int habitDurationFill = habitsData1.getHabitsDuration();
                    int habitDurationFillMin = habitDurationFill % 60;
                    int habitDurationFillHr = (habitDurationFill-habitDurationFillMin)/60;
                    if(habitDurationFill < 60){
                        habitSetDurationChip.setText(habitDurationFillMin+" Min ");
                    }else if(habitDurationFill > 0) {
                        habitSetDurationChip.setText(habitDurationFillHr + " Hr " + habitDurationFillMin + " Min ");
                    }else{
                        habitSetDurationChip.setText("Set Duration");
                    }
                }
            });
            */

            habitSelectorDeleteChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(habitsRV.getContext());

                    // Set the message show for the Alert time
                    builder.setMessage("Delete the following habit: " + habitsDataArrayList.get(position).getHabitsTitle() + "? ");

                    // Set Alert Title
                    builder.setTitle("Warning!");

                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(true);

                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // When the user click yes button then app will close
                        databaseReference.child(habitsDataArrayList.get(position).getHabitsTextId()).removeValue();
                        Snackbar.make(habitsRV, "Habit Deleted", Snackbar.LENGTH_SHORT)
                                .show();
                        dialog.dismiss();
                    });

                    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // If user click no then dialog box is canceled.
                        dialog.cancel();
                    });

                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();
                    popupWindowSelector.dismiss();
                }
            });
        }
        /////////////////////////////////////////////////////////////////////////////////////
        private void onHabitClick(int position) {
            if (user != null) {
                String name = user.getDisplayName();
                if ((name != null) && name != "") {
                    firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    routineDatabaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/routine");
                } else if (name == "") {
                    firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    routineDatabaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/routine");
                } else {
                    throw new IllegalStateException("Unexpected value: " + name);
                }
            }

            HabitsData habitsData5 = habitsDataArrayList.get(position);
            if(RoutinePlannerPage.routineHabits != null) {
                RoutinePlannerPage.routineHabits.add(habitsData5);
            }else{
                RoutinePlannerPage.routineHabits = new ArrayList<>();
                RoutinePlannerPage.routineHabits.add(habitsData5);
            }
            RoutinePlannerPage.routineDataMain.setRoutineHabitsList(RoutinePlannerPage.routineHabits);
            routineDatabaseReference.child(RoutinePlannerPage.routineDataMain.getRoutineTextId()).setValue(RoutinePlannerPage.routineDataMain);
            popupWindow.dismiss();
        }
    };

    View.OnClickListener BackRoutineListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RoutineEditorPage.this, RoutinePlannerPage.class);
            startActivity(intent);
        }

    };
    View.OnClickListener DeleteRoutineListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoutineEditorPage.this);

            // Set the message show for the Alert time
            builder.setMessage("Delete Routine?");

            // Set Alert Title
            builder.setTitle("Warning!");

            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
            builder.setCancelable(true);

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                // When the user click yes button then app will close
                onBackPressed();
            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        }

    };


}