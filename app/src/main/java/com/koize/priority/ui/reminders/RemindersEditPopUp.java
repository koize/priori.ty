package com.koize.priority.ui.reminders;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.koize.priority.NotiReceiver;
import com.koize.priority.R;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.category.CategoryPopUp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RemindersEditPopUp extends Fragment implements CategoryPopUp.CategoryCallBack {
    RemindersData remindersData;
    EditText reminderTitle;
    Chip firstReminderChip;
    Chip secondReminderChip;
    EditText reminderLocationText;
    Chip reminderCategoryChip;
    Chip categoryCard;
    Chip reminderSaveChip;
    private MaterialTextView reminderEmpty;
    public int firstReminderTimeHr;
    public int firstReminderTimeMin;
    public int secondReminderTimeHr;
    public int secondReminderTimeMin;

    public String firstReminderDate;
    public String secondReminderDate;
    public long firstReminderDateTime;
    public long secondReminderDateTime;
    public long firstReminderDateEpoch;
    public long secondReminderDateEpoch;

    CategoryData categoryData;
    FragmentManager fragmentManager;
    FirebaseUser user;
    DatabaseReference databaseReference;

    public static final int INPUT_METHOD_NEEDED = 1;

    public RemindersEditPopUp() {
        remindersData = new RemindersData();
    }

    public RemindersEditPopUp(RemindersData remindersData, FragmentManager fragmentManager, FirebaseUser user, DatabaseReference databaseReference, View view) {
        this.remindersData = remindersData;
        this.fragmentManager = fragmentManager;
        this.user = user;
        this.databaseReference = databaseReference;
    }



    public void showPopupWindowEdit(final View view, RemindersData remindersData) {

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
        TextView popUpTitle = popupView.findViewById(R.id.title_new_reminders);
        popUpTitle.setText("Edit Reminder");
        reminderTitle = popupView.findViewById(R.id.new_reminder_title);
        reminderTitle.setText(remindersData.getReminderTitle());

        firstReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");
        long epochTime1 = remindersData.getFirstReminderDateTime() - 28800000;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime1), ZoneId.of("Asia/Singapore"));
        firstReminderChip.setText(dateFormat.format(dateTime));

        secondReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_2);
        if (remindersData.getSecondReminderDateTime() == 0) {
            secondReminderChip.setText("Not set");
        } else {
            long epochTime2 = remindersData.getSecondReminderDateTime() - 28800000;
            LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime2), ZoneId.of("Asia/Singapore"));
            secondReminderChip.setText(dateFormat.format(dateTime2));
        }

        reminderLocationText = popupView.findViewById(R.id.new_reminder_location_text);
        reminderLocationText.setText(remindersData.getReminderLocationName());

        reminderCategoryChip = popupView.findViewById(R.id.button_new_reminder_choose_category);

        categoryCard = popupView.findViewById(R.id.new_reminder_category_card);
        categoryCard.setChipBackgroundColor(ColorStateList.valueOf(remindersData.getReminderCategory().getCategoryColor()));
        categoryCard.setText(remindersData.getReminderCategory().getCategoryTitle());

        reminderSaveChip = popupView.findViewById(R.id.button_new_reminder_save);
        Chip reminderDeleteChip = popupView.findViewById(R.id.button_new_reminder_delete);
        reminderDeleteChip.setVisibility(View.VISIBLE);

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
                materialDateBuilder.setTitleText("1st Reminder Date");
                if(remindersData.getFirstReminderDateTime() != 0) {
                    materialDateBuilder.setSelection(remindersData.getFirstReminderDateTime());
                }

                // now create the instance of the material date
                // picker
                MaterialDatePicker materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(fragmentManager, "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                firstReminderDate = materialDatePicker.getHeaderText();
                                firstReminderDateTime = (long) selection;
                                firstReminderDateEpoch = (long) selection;
                                mTimePicker1();

                            }
                        });


            }
        });


        secondReminderChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                materialDateBuilder.setTitleText("2nd Reminder Date");
                if (remindersData.getSecondReminderDateTime() != 0){
                    materialDateBuilder.setSelection(remindersData.getSecondReminderDateTime());
                }

                // now create the instance of the material date
                // picker
                MaterialDatePicker materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(fragmentManager, "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                secondReminderDate = materialDatePicker.getHeaderText();
                                secondReminderDateTime = (long) selection;
                                secondReminderDateEpoch = (long) selection;
                                mTimePicker2();

                            }
                        });
            }
        });

        reminderCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData -> RemindersEditPopUp.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);
            }
        });
        reminderSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    if (reminderTitle.getText().toString().isEmpty()) {
                        Snackbar.make(view, "Please enter a title", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    else {
                        remindersData.setReminderTitle(reminderTitle.getText().toString());
                    }
                    remindersData.setReminderPendingIntent(null);
                    remindersData.setFirstReminderTimeHr(firstReminderTimeHr);
                    remindersData.setFirstReminderTimeMin(firstReminderTimeMin);
                    remindersData.setSecondReminderTimeHr(secondReminderTimeHr);
                    remindersData.setSecondReminderTimeMin(secondReminderTimeMin);
                    remindersData.setFirstReminderDateTime(firstReminderDateTime);
                    remindersData.setSecondReminderDateTime(secondReminderDateTime);
                    remindersData.setFirstReminderDateEpoch(firstReminderDateEpoch);
                    remindersData.setSecondReminderDateEpoch(secondReminderDateEpoch);

                    if (firstReminderTimeHr >= 0 && firstReminderTimeHr < 12) {
                        remindersData.setFirstReminderPartOfDay("morning");
                    } else if (firstReminderTimeHr >= 12 && firstReminderTimeHr < 16) {
                        remindersData.setFirstReminderPartOfDay("afternoon");
                    } else if (firstReminderTimeHr >= 16 && firstReminderTimeHr < 20) {
                        remindersData.setFirstReminderPartOfDay("evening");
                    } else if (firstReminderTimeHr >= 20 && firstReminderTimeHr < 24) {
                        remindersData.setFirstReminderPartOfDay("night");
                    }
                    if (secondReminderTimeHr >= 0 && secondReminderTimeHr < 12) {
                        remindersData.setSecondReminderPartOfDay("morning");
                    } else if (secondReminderTimeHr >= 12 && secondReminderTimeHr < 16) {
                        remindersData.setSecondReminderPartOfDay("afternoon");
                    } else if (secondReminderTimeHr >= 16 && secondReminderTimeHr < 20) {
                        remindersData.setSecondReminderPartOfDay("evening");
                    } else if (secondReminderTimeHr >= 20 && secondReminderTimeHr < 24) {
                        remindersData.setSecondReminderPartOfDay("night");
                    }
                    remindersData.setReminderLocationName(reminderLocationText.getText().toString());
                    if (categoryData == null) {
                        categoryData = remindersData.getReminderCategory();
                    }
                    remindersData.setReminderCategory(categoryData);
                    try {
                        databaseReference.child(remindersData.getReminderTextId()).setValue(remindersData);

                    }
                    catch (Exception e) {
                        Log.e("reminderedit", e.getCause().getCause().toString());
                    }
                    Snackbar.make(view, "Reminder Saved", Snackbar.LENGTH_SHORT)
                            .show();
                    popupWindow.dismiss();


                } else {
                    Snackbar.make(view, "Please sign in to save reminders", Snackbar.LENGTH_SHORT)
                            .show();
                }

            }
        });

        reminderDeleteChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersEditPopUp.this.getContext());

                // Set the message show for the Alert time
                builder.setMessage("Delete reminder: " + remindersData.getReminderTitle() + "? ");

                // Set Alert Title
                builder.setTitle("Delete?");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(true);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    Intent intent = new Intent(getContext(), NotiReceiver.class);
                    intent.putExtra("title", remindersData.getReminderTitle());
                    intent.putExtra("id", remindersData.getReminderId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), remindersData.getReminderId(), intent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);                    databaseReference.child(remindersData.getReminderTextId()).removeValue();
                    Snackbar.make(view, "Reminder deleted!", Snackbar.LENGTH_SHORT)
                            .show();
                    dialog.dismiss();
                    popupWindow.dismiss();
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
        });


    }

    public void mTimePicker1() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("1st Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
        materialTimeBuilder.setHour(remindersData.getFirstReminderTimeHr());
        materialTimeBuilder.setMinute(remindersData.getFirstReminderTimeMin());
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        firstReminderTimeHr = materialTimePicker.getHour();
                        firstReminderTimeMin = materialTimePicker.getMinute();
                        firstReminderDateTime = firstReminderDateTime + (firstReminderTimeHr * 3600000) + (firstReminderTimeMin * 60000);
                        firstReminderChip.setText(firstReminderDate + ", " + String.format("%02d:%02d", firstReminderTimeHr, firstReminderTimeMin));
                    }
                }
        );
        materialTimePicker.show(fragmentManager, "MATERIAL_TIME_PICKER");
    }

    public void mTimePicker2() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("2nd Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
        materialTimeBuilder.setHour(remindersData.getSecondReminderTimeHr());
        materialTimeBuilder.setMinute(remindersData.getSecondReminderTimeMin());
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        secondReminderTimeHr = materialTimePicker.getHour();
                        secondReminderTimeMin = materialTimePicker.getMinute();
                        secondReminderDateTime = secondReminderDateTime + (secondReminderTimeHr * 3600000) + (secondReminderTimeMin * 60000);
                        secondReminderChip.setText(secondReminderDate + ", " + String.format("%02d:%02d", secondReminderTimeHr, secondReminderTimeMin));
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
