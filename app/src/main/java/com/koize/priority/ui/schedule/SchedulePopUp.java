package com.koize.priority.ui.schedule;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.koize.priority.R;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.category.CategoryPopUp;
import com.koize.priority.ui.reminders.RemindersData;
import com.koize.priority.ui.reminders.RemindersNewPopUp;

import java.time.LocalDate;
import java.util.Random;

public class SchedulePopUp implements CategoryPopUp.CategoryCallBack {
    public static final int INPUT_METHOD_NEEDED = 1;
    public int startTimeHr;
    public int startTimeMin;
    public int endTimeHr;
    public int endTimeMin;
    String habitDescription;
    EditText habitDescriptionTyper;
    PopupWindow popupWindowDescription;
    Chip habitDescriptionSaveChip;


    TextView scheduleTitleChip;
    Chip scheduleStartTimeChip;
    Chip scheduleEndTimeChip;
    Chip scheduleCategoryChip;
    Chip scheduleDescriptionChip;
    Chip scheduleSaveChip;
    Chip categoryCard;
    FragmentManager fragmentManager;
    CategoryData categoryData;
    LocalDate SelectedDate;
    String Notes;
    FirebaseUser user;
    DatabaseReference databaseReference;
    ScheduleData scheduleData;
    EditText scheduleTitleET;
    public void showPopupWindow(final View view, FragmentManager fragmentManager,LocalDate SelectedDate,String Notes,FirebaseUser user, DatabaseReference databaseReference) {

        this.fragmentManager = fragmentManager;
        this.SelectedDate = SelectedDate;
        this.Notes = Notes;
        this.user = user;
        this.databaseReference = databaseReference;
        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_schedule_add, null);
        reminderView = popupView.findViewById(R.id.schedulePopUpLayout);

        //Specify the length and width through constants

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(reminderView, width, height, focusable);
        // Closes the popup window when touch outside
        //Handler for clicking on the inactive zone of the window

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);



        /* final View root = popupView.getRootView();
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                root.getWindowVisibleDisplayFrame(r);

                // Calculate the difference between the original height and the new height
                int heightDiff = r.height() - root.getHeight();

                // Now update the Popup's position
                // The first value is the x-axis, which stays the same.
                // Second value is the y-axis. We still want it centered, so move it up by 50% of the height
                // change
                // The third and the fourth values are default values to keep the width/height
                popupWindow.update(0, heightDiff / 2, -1, -1);
            }
        });*/


        //Set the location of the window on the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        View container = popupWindow.getContentView().getRootView();
        if(container != null) {
            WindowManager wm = (WindowManager)container.getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams)container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.3f;
            if(wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
        //Initialize the elements of our window, install the handler
        scheduleTitleChip = popupView.findViewById(R.id.title_new_schedule);
        scheduleStartTimeChip = popupView.findViewById(R.id.button_new_schedule_chooseStartTime);
        scheduleEndTimeChip = popupView.findViewById(R.id.button_new_schedule_chooseEndTime);
        scheduleCategoryChip = popupView.findViewById(R.id.button_new_schedule_chooseCategory);
        scheduleDescriptionChip = popupView.findViewById(R.id.button_new_schedule_addDescription);
        scheduleSaveChip = popupView.findViewById(R.id.button_new_schedule_save);
        categoryCard = popupView.findViewById(R.id.new_schedule_category_card);
        scheduleTitleET = popupView.findViewById(R.id.scheduleTitle);

        scheduleStartTimeChip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mTimePicker1();
            }
        });
        scheduleEndTimeChip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mTimePicker2();
            }
        });
        scheduleCategoryChip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData -> SchedulePopUp.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);

            }
        });
        scheduleDescriptionChip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                            scheduleDescriptionChip.setText(habitDescription.substring(0, 12) + "...");
                        } else {
                            scheduleDescriptionChip.setText(habitDescription);
                        }
                        popupWindowDescription.dismiss();
                    }
                });


            }
        });
        scheduleSaveChip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (user != null) {
                    scheduleData = new ScheduleData();
                    scheduleData.setScheduleId(new Random().nextInt(1000000));
                    if (scheduleTitleET.getText().toString().isEmpty()) {
                        Snackbar.make(view, "Please enter a title!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    else {
                        scheduleData.setScheduleTitle(scheduleTitleET.getText().toString());
                    }
                    scheduleData.setScheduleTextId(scheduleTitleET.getText().toString().toLowerCase().replaceAll("\\s", "") + "_" + scheduleData.getScheduleId());
                    scheduleData.setStartTimeHr(startTimeHr);
                    scheduleData.setStartTimeMin(startTimeMin);
                    scheduleData.setEndTimeHr(endTimeHr);
                    scheduleData.setEndTimeMin(endTimeMin);
                    if (categoryData == null) {
                        categoryData = new CategoryData();
                        categoryData.setCategoryTitle("Others");
                    }
                    scheduleData.setScheduleCategory(categoryData);
                    try {
                        databaseReference.child(SelectedDate.toString()).child(scheduleData.getScheduleTextId()).setValue(scheduleData);

                    }
                    catch (Exception e) {

                    }
                    Snackbar.make(view, "Activity Saved", Snackbar.LENGTH_SHORT)
                            .show();
                    popupWindow.dismiss();


                } else {
                    Snackbar.make(view, "Please sign in to save reminders", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    //functions?
    public void mTimePicker1() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("1st Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startTimeHr = materialTimePicker.getHour();
                        startTimeMin = materialTimePicker.getMinute();
                        scheduleStartTimeChip.setText(String.format("%02d:%02d", startTimeHr, startTimeMin));
                    }
                }
        );
        materialTimePicker.show(fragmentManager, "MATERIAL_TIME_PICKER");
    }

    public void mTimePicker2() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("1st Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        endTimeHr = materialTimePicker.getHour();
                        endTimeMin = materialTimePicker.getMinute();
                        scheduleEndTimeChip.setText(String.format("%02d:%02d", endTimeHr, endTimeMin));
                    }
                }
        );
        materialTimePicker.show(fragmentManager, "MATERIAL_TIME_PICKER");
    }

    @Override
    public void sendCategory(CategoryData categoryData) {
        categoryCard.setText(categoryData.getCategoryTitle());
        categoryCard.setChipBackgroundColor(ColorStateList.valueOf(categoryData.getCategoryColor()));
        categoryCard.setVisibility(View.VISIBLE);
        this.categoryData = categoryData;
    }
}