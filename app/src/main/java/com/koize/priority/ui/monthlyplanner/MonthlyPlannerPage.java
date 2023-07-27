package com.koize.priority.ui.monthlyplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.koize.priority.R;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.category.CategoryPopUp;

public class MonthlyPlannerPage extends AppCompatActivity implements CategoryPopUp.CategoryCallBack {
    public static final int INPUT_METHOD_NEEDED = 1;
    private FloatingActionButton addEventButton;
    EditText eventTitle;
    TableRow dateChip;
    TextView dateText;
    TableRow timeChip;
    TextView timeText;
    Switch allDay;
    TableRow reminderDateChip;
    TextView reminderText;
    EditText eventLocationText;
    Chip eventLocationChip;
    Chip eventCategoryChip;
    Chip eventDescImageChip;
    EditText eventDescText;
    Chip eventSaveChip;
    private String eventStartDate;
    private String eventEndDate;
    private long eventStartDateStartTime;
    private long eventEndDateTime;

    private int eventStartHr;
    private int eventStartMin;
    private int eventEndHr;
    private int eventEndMin;

    private String reminderDate;
    private long reminderDateTime;
    private int reminderHr;
    private int reminderMin;
    private boolean isAllDay;
    private String eventLocation;
    private String eventDesc;

    private CategoryData categoryData;
    Chip eventCategoryCard;

    EventData eventData;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseEventReference;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_planner_page);
        addEventButton = findViewById(R.id.button_monthly_planner_add);
        addEventButton.setOnClickListener(addEventListener);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name!="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseEventReference = firebaseDatabase.getReference("users/" + name + "/events");
            }
            else if (name=="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseEventReference = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/events");
            }
            else {
                throw new IllegalStateException("Unexpected value: " + name);
            }

        } else {
            Snackbar.make(findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    View.OnClickListener addEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupWindow(v);
        }
    };

    public void showPopupWindow(final View view) {

        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_events_add, null);

        //Specify the length and width through constants

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
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

        eventTitle = popupView.findViewById(R.id.new_event_title);
        dateChip = popupView.findViewById(R.id.button_new_event_datepicker);
        dateText = popupView.findViewById(R.id.new_event_date_start_title);
        timeChip = popupView.findViewById(R.id.button_new_event_timepicker);
        timeText = popupView.findViewById(R.id.new_event_time_title);
        allDay = popupView.findViewById(R.id.switch_new_event_time_allday);
        reminderDateChip = popupView.findViewById(R.id.button_new_event_reminder);
        reminderText = popupView.findViewById(R.id.new_event_reminder_header);
        eventLocationText = popupView.findViewById(R.id.new_event_location_header);
        eventLocationChip = popupView.findViewById(R.id.button_new_event_get_location);
        eventCategoryChip = popupView.findViewById(R.id.button_new_event_get_category);
        eventCategoryCard = popupView.findViewById(R.id.new_event_category_card);
        eventDescImageChip = popupView.findViewById(R.id.button_new_event_get_image);
        eventDescText = popupView.findViewById(R.id.new_event_desc);
        eventSaveChip = popupView.findViewById(R.id.button_new_event_save);
        dateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

                // now define the properties of the
                // materialDateBuilder that is title text as SELECT A DATE
                materialDateBuilder.setTitleText("Choose event dates");

                // now create the instance of the material date
                // picker
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                eventStartDateStartTime = (long) selection;
                                eventEndDateTime = (long) selection;
                                eventStartDate = materialDatePicker.getHeaderText();
                                dateText.setText(eventStartDate);
                            }
                        });
            }
        });
        timeChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker1();
            }
        });
        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (allDay.isChecked()) {
                    timeText.setText("All day");
                    eventStartHr = 0;
                    eventStartMin = 0;
                    eventEndHr = 23;
                    eventEndMin = 59;
                } else {
                    mTimePicker1();
                }
            }
        });
        reminderDateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

                // now define the properties of the
                // materialDateBuilder that is title text as SELECT A DATE
                materialDateBuilder.setTitleText("1st Reminder Date");

                // now create the instance of the material date
                // picker
                MaterialDatePicker materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                reminderDate = materialDatePicker.getHeaderText();
                                reminderDateTime = (long) selection;
                                mTimePicker3();

                            }
                        });
            }
        });
        eventLocationChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        eventCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData1 -> MonthlyPlannerPage.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);
            }
        });
        eventDescImageChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        eventSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventData = new EventData();
                if (eventTitle.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Event title cannot be empty!", Snackbar.LENGTH_SHORT)
                            .show();
                } else if (eventStartDateStartTime == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Event start date cannot be empty!", Snackbar.LENGTH_SHORT)
                            .show();
                } else if (eventEndDateTime == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Event end date cannot be empty!", Snackbar.LENGTH_SHORT)
                            .show();
                } else if (reminderDateTime == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Event reminder date cannot be empty!", Snackbar.LENGTH_SHORT)
                            .show();
                } else if (eventLocationText.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Event location cannot be empty!", Snackbar.LENGTH_SHORT)
                            .show();
                } else if (eventDescText.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Event description cannot be empty!", Snackbar.LENGTH_SHORT)
                            .show();
                } else if (categoryData == null) {
                    Snackbar.make(findViewById(android.R.id.content), "Event category cannot be empty!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {

                    popupWindow.dismiss();
                }
            }
        });


    }
    public void mTimePicker1() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("Event start time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        eventStartHr = materialTimePicker.getHour();
                        eventStartMin = materialTimePicker.getMinute();
                        eventStartDateStartTime = eventStartDateStartTime + (eventStartHr * 3600000) + (eventStartMin * 60000);
                        mTimePicker2();
                    }
                }
        );
        materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    public void mTimePicker2() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("Event end time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        eventEndHr = materialTimePicker.getHour();
                        eventEndMin = materialTimePicker.getMinute();
                        eventEndDateTime = eventEndDateTime + (eventEndHr * 3600000) + (eventEndMin * 60000);
                        timeText.setText(String.format("%02d:%02d", eventStartHr, eventStartMin) + String.format("%02d:%02d", eventEndHr, eventEndMin));
                    }
                }
        );
        materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    public void mTimePicker3() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("Reminder time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reminderHr = materialTimePicker.getHour();
                        reminderMin = materialTimePicker.getMinute();
                        reminderDateTime = reminderDateTime + (reminderHr * 3600000) + (reminderMin * 60000);
                        reminderText.setText(reminderDate + ", " + String.format("%02d:%02d", reminderHr, reminderMin));
                    }
                }
        );
        materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    @Override
    public void sendCategory(CategoryData categoryData) {
        eventCategoryCard.setText(categoryData.getCategoryTitle());
        eventCategoryCard.setChipBackgroundColor(ColorStateList.valueOf(categoryData.getCategoryColor()));
        eventCategoryCard.setVisibility(View.VISIBLE);
        this.categoryData = categoryData;

    }
}