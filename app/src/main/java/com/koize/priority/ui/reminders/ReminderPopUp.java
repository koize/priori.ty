package com.koize.priority.ui.reminders;

import static androidx.core.content.ContextCompat.getSystemService;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ReportFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.koize.priority.CategoryPopUp;
import com.koize.priority.R;

public class ReminderPopUp extends AppCompatActivity {
    public int firstReminderTimeHr;
    public int firstReminderTimeMin;
    public int secondReminderTimeHr;
    public int secondReminderTimeMin;
    public int firstReminderDateDay;
    public int firstReminderDateMonth;
    public int firstReminderDateYear;
    public int secondReminderDateDay;
    public int secondReminderDateMonth;
    public int secondReminderDateYear;


    public static final int INPUT_METHOD_NEEDED = 1;

    public void showPopupWindow(final View view) {

        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_reminders_add, null);
        reminderView = popupView.findViewById(R.id.reminderPopUpLayout);

        //Specify the length and width through constants

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(reminderView, width, height, focusable);
        // Closes the popup window when touch outside
        //Handler for clicking on the inactive zone of the window
        EditText reminderTitle = popupView.findViewById(R.id.new_reminder_title);
        Chip firstReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_1);
        Chip secondReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_2);
        EditText reminderLocationText = popupView.findViewById(R.id.new_reminder_location_text);
        Chip reminderLocationChip = popupView.findViewById(R.id.button_new_reminder_getLocation);
        Chip reminderCategoryChip = popupView.findViewById(R.id.button_new_reminder_choose_category);
        Chip reminderSaveChip = popupView.findViewById(R.id.button_new_reminder_save);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
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


        firstReminderChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker1();
            }
        });


        secondReminderChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c = java.util.Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);


                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                secondReminderDateDay = dayOfMonth;
                                secondReminderDateMonth = monthOfYear + 1;
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),

                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                secondReminderTimeHr = hourOfDay;
                                secondReminderTimeMin = minute;
                            }
                        }, hour, minute, false);
                // at last we are calling show to
                // display our time picker dialog.
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();

                timePickerDialog.show();
                secondReminderChip.setText(secondReminderDateDay + "/" + (secondReminderDateMonth) + ", " + secondReminderTimeHr + ":" + secondReminderTimeMin);
            }
        });
        reminderLocationChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        reminderCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp categoryPopUp = new CategoryPopUp();
                categoryPopUp.showPopupWindow(v);
            }
        });
        reminderSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





    }
    public void materialDatePicker1() {
        // now register the text view and the button with
        // their appropriate IDs

        // now create instance of the material date picker
        // builder make sure to add the "datePicker" which
        // is normal material date picker which is the first
        // type of the date picker in material design date
        // picker
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText("SELECT A DATE");

        // now create the instance of the material date
        // picker
        MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        ((Chip)findViewById(R.id.button_new_reminder_choose_date_1)).setText("Date is : " + materialDatePicker.getHeaderText());
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });
    }

    protected void datePicker1 () {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //String date = getResources().getString(R.string.date_format);
                //String formatDate = String.format(date, year, month + 1, dayOfMonth);
                firstReminderDateDay = dayOfMonth;
                firstReminderDateMonth = month + 1;
                firstReminderDateYear = year;
                timePicker1();
            }
        }, year, month, dayOfMonth);
        calendar.add(Calendar.MONTH, 1);
        long now = System.currentTimeMillis() - 1000;
        long maxDate = calendar.getTimeInMillis();
        datePickerDialog.getDatePicker().setMinDate(now);
        datePickerDialog.getDatePicker().setMaxDate(maxDate); //After one month from now
        datePickerDialog.show();
    }

    protected void timePicker1 ( /*final String date*/){
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //String time = getResources().getString(R.string.time_format);
                //String formatTime = String.format(time, hourOfDay, minute);
                //String dateTime = date + "   " + formatTime;
                firstReminderTimeHr = hourOfDay;
                firstReminderTimeMin = minute;
                //firstReminderChip.setText(firstReminderDateDay + "/" + (firstReminderDateMonth) + "/" + firstReminderDateYear + ", " + firstReminderTimeHr + ":" + firstReminderTimeMin);
            }
        }, hour + 1, minute, false);
        timePickerDialog.show();
    }
}