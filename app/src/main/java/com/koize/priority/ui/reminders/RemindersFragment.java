package com.koize.priority.ui.reminders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.CategoryPopUp;
import com.koize.priority.R;
import com.koize.priority.databinding.FragmentRemindersBinding;

public class RemindersFragment extends Fragment {

    private FragmentRemindersBinding binding;
    private FloatingActionButton addReminderButton;
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
    public String firstReminderDate;
    public String secondReminderDate;
    public long firstReminderDateTime;
    public long secondReminderDateTime;


    public static final int INPUT_METHOD_NEEDED = 1;
    EditText reminderTitle;
    Chip firstReminderChip;
    Chip secondReminderChip;
    EditText reminderLocationText;
    Chip reminderLocationChip;
    Chip reminderCategoryChip;
    Chip reminderSaveChip;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RemindersData remindersData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RemindersViewModel remindersViewModel =
                new ViewModelProvider(this).get(RemindersViewModel.class);

        binding = FragmentRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addReminderButton = root.findViewById(R.id.button_reminder_add);
        addReminderButton.setOnClickListener(addReminderListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("users/" + name + "/reminders");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener addReminderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //ReminderPopUp reminderPopUp = new ReminderPopUp();
            //reminderPopUp.showPopupWindow(v);
            showPopupWindow(v);}
    };

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
        reminderTitle = popupView.findViewById(R.id.new_reminder_title);
        firstReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_1);
        secondReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_2);
        reminderLocationText = popupView.findViewById(R.id.new_reminder_location_text);
        reminderLocationChip = popupView.findViewById(R.id.button_new_reminder_getLocation);
        reminderCategoryChip = popupView.findViewById(R.id.button_new_reminder_choose_category);
        reminderSaveChip = popupView.findViewById(R.id.button_new_reminder_save);

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

                // now create the instance of the material date
                // picker
                MaterialDatePicker materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                firstReminderDate = materialDatePicker.getHeaderText();
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

                // now create the instance of the material date
                // picker
                MaterialDatePicker materialDatePicker = materialDateBuilder.build();

                materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");


                materialDatePicker.addOnPositiveButtonClickListener(
                        new MaterialPickerOnPositiveButtonClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                secondReminderDate = materialDatePicker.getHeaderText();
                                mTimePicker2();

                            }
                        });
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
                remindersData = new RemindersData();
                remindersData.setReminderTitle(reminderTitle.getText().toString());
                remindersData.setFirstReminderTimeHr(firstReminderTimeHr);
                remindersData.setFirstReminderTimeMin(firstReminderTimeMin);
                remindersData.setSecondReminderTimeHr(secondReminderTimeHr);
                remindersData.setSecondReminderTimeMin(secondReminderTimeMin);
                remindersData.setFirstReminderDateTime(firstReminderDateTime);
                remindersData.setSecondReminderDateTime(secondReminderDateTime);
                remindersData.setReminderLocationName(reminderLocationText.getText().toString());
                remindersData.setReminderCategory(reminderCategoryChip.getText().toString());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child(remindersData.getReminderTitle()).setValue(remindersData);
                        Snackbar.make(view, "Reminder Saved", Snackbar.LENGTH_SHORT)
                                .show();
                        popupWindow.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });





    }
    public void mTimePicker1() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("1st Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        firstReminderTimeHr = materialTimePicker.getHour();
                        firstReminderTimeMin = materialTimePicker.getMinute();
                        firstReminderChip.setText(firstReminderDate + ", " + String.format("%02d:%02d", firstReminderTimeHr, firstReminderTimeMin));
                    }
                }
        );
        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }
    public void mTimePicker2() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("2nd Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();
        materialTimePicker.addOnPositiveButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        secondReminderTimeHr = materialTimePicker.getHour();
                        secondReminderTimeMin = materialTimePicker.getMinute();
                        secondReminderChip.setText(secondReminderDate + ", " + String.format("%02d:%02d", secondReminderTimeHr, secondReminderTimeMin));
                    }
                }
        );
        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    }




