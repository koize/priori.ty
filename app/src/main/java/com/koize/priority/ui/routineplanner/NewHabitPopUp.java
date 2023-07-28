package com.koize.priority.ui.routineplanner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.koize.priority.R;

import java.util.ArrayList;


public class NewHabitPopUp extends RoutineEditorPage {
    EditText habitTitle;
    EditText habitDescriptionTyper;
    String habitDescription;
    Chip habitSaveChip;
    Chip durationSaveChip;
    Chip habitDescriptionSaveChip;
    Chip habitSetDurationChip;
    Chip habitAddDescriptionChip;
    NumberPicker durationPickerHr;
    NumberPicker durationPickerMin;
    PopupWindow popupWindow;
    PopupWindow popupWindowDescription;
    PopupWindow popupWindowDuration;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;

    HabitsData habitsData;

    public int habitDurationHr;
    public int habitDurationMin;
    HabitsAdapter HabitsAdapter;

    public static final int INPUT_METHOD_NEEDED = 1;
    private FirebaseAuth firebaseAuth;

    public void showPopupWindow(final View view) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_habit_add, null);


        habitSaveChip = popupView.findViewById(R.id.button_new_habit_save);
        habitSetDurationChip = popupView.findViewById(R.id.button_new_habit_setDuration);
        habitAddDescriptionChip = popupView.findViewById(R.id.button_new_habit_addDescription);
        habitTitle = popupView.findViewById(R.id.new_habit_title);

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
                databaseReference = firebaseDatabase.getReference("users/" + name + "/habits");
            } else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/habits");
            } else {
                throw new IllegalStateException("Unexpected value: " + name);
            }
        }else{
            RoutineEditorPage routineEditorPage = new RoutineEditorPage();
            Snackbar.make(this.findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
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

            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

            habitSaveChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null) {
                        habitsData = new HabitsData();
                        habitsData.setHabitsTitle(habitTitle.getText().toString());

                        int totalHabitDuration;
                        totalHabitDuration = (habitDurationHr * 60) + habitDurationMin;
                        habitsData.setHabitsDuration(totalHabitDuration);

                        //habitsData.setHabitsDescription(habitDescription);

                        databaseReference.child(habitsData.getHabitsTitle()).setValue(habitsData);
                        popupWindow.dismiss();
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

                    durationPickerHr.setMinValue(0);
                    durationPickerHr.setMaxValue(24);
                    durationPickerMin.setMinValue(0);
                    durationPickerMin.setMaxValue(60);

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
                                popupWindow.dismiss();
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
                                habitAddDescriptionChip.setText(habitDescription.substring(0, 12) + "...");
                            } else {
                                habitAddDescriptionChip.setText(habitDescription);
                            }
                            popupWindowDescription.dismiss();
                        }
                    });


                }
            });

        }

    public void showPopupEditWindow(final View view,int position) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_habit_add, null);


        habitSaveChip = popupView.findViewById(R.id.button_new_habit_save);
        habitSetDurationChip = popupView.findViewById(R.id.button_new_habit_setDuration);
        habitAddDescriptionChip = popupView.findViewById(R.id.button_new_habit_addDescription);
        habitTitle = popupView.findViewById(R.id.new_habit_title);

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
                databaseReference = firebaseDatabase.getReference("users/" + name + "/habits");
            } else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/habits");
            } else {
                throw new IllegalStateException("Unexpected value: " + name);
            }
        }else{
            RoutineEditorPage routineEditorPage = new RoutineEditorPage();
            Snackbar.make(this.findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
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

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        habitSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    habitsData = new HabitsData();
                    habitsData.setHabitsTitle(habitTitle.getText().toString());

                    int totalHabitDuration;
                    totalHabitDuration = (habitDurationHr * 60) + habitDurationMin;
                    habitsData.setHabitsDuration(totalHabitDuration);

                    //habitsData.setHabitsDescription(habitDescription);

                    databaseReference.child(habitsData.getHabitsTitle()).setValue(habitsData);
                    popupWindow.dismiss();
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

                durationPickerHr.setMinValue(0);
                durationPickerHr.setMaxValue(24);
                durationPickerMin.setMinValue(0);
                durationPickerMin.setMaxValue(60);

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
                            popupWindow.dismiss();
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
                            habitAddDescriptionChip.setText(habitDescription.substring(0, 12) + "...");
                        } else {
                            habitAddDescriptionChip.setText(habitDescription);
                        }
                        popupWindowDescription.dismiss();
                    }
                });


            }
        });

        HabitsData habitsData1 = habitsDataArrayList.get(position);
        habitTitle.setText(habitsData1.getHabitsTitle());
        habitDescriptionTyper.setText(habitsData1.getHabitsDescription());


    }

}
