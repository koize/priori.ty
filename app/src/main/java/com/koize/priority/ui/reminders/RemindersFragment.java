package com.koize.priority.ui.reminders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.category.CategoryPopUp;
import com.koize.priority.R;
import com.koize.priority.databinding.FragmentRemindersBinding;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RemindersFragment extends Fragment implements CategoryPopUp.CategoryCallBack {

    private FragmentRemindersBinding binding;
    private FloatingActionButton addReminderButton;
    private MaterialTextView reminderEmpty;
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
    public RemindersData remindersData;
    private RecyclerView reminderRV;
    private RemindersAdapter remindersAdapter;

    private CategoryData categoryData;
    private ArrayList<RemindersData> remindersDataArrayList;



    public static final int INPUT_METHOD_NEEDED = 1;
    TextView reminderTitle;
    Chip firstReminderChip;
    Chip secondReminderChip;
    EditText reminderLocationText;
    Chip reminderLocationChip;
    Chip reminderCategoryChip;
    Chip reminderSaveChip;
    Chip categoryCard;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RemindersViewModel remindersViewModel =
                new ViewModelProvider(this).get(RemindersViewModel.class);

        binding = FragmentRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addReminderButton = root.findViewById(R.id.button_reminder_add);
        addReminderButton.setOnClickListener(addReminderListener);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name!="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + name + "/reminders");
            }
            else if (name=="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/reminders");
            }
            else {
                throw new IllegalStateException("Unexpected value: " + name);
            }

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }

        reminderEmpty = root.findViewById(R.id.reminders_morning_empty);
        reminderRV = root.findViewById(R.id.recycler_reminder_morning);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        remindersDataArrayList = new ArrayList<>();
        remindersAdapter = new RemindersAdapter(remindersDataArrayList, getContext(), this::onRemindersClick, this::onRemindersCheckBoxDelete);
        reminderRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        reminderRV.setAdapter(remindersAdapter);
        getReminders();
        RecyclerViewRefresher recyclerViewRefresher = new RecyclerViewRefresher(reminderRV);
        recyclerViewRefresher.startRefreshing();

    }

    public class RecyclerViewRefresher {

        private Handler handler;
        private Runnable refreshRunnable;

        public RecyclerViewRefresher(RecyclerView recyclerView) {
            handler = new Handler(Looper.getMainLooper());
            refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    // Refresh the recyclerview's viewholder
                    recyclerView.getAdapter().notifyDataSetChanged();

                    // Schedule the refresh to run again in 2 seconds
                    handler.postDelayed(refreshRunnable, 2000);
                }
            };
        }

        public void startRefreshing() {
            handler.post(refreshRunnable);
        }

        public void stopRefreshing() {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    View.OnClickListener addReminderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupWindow(v);
        }
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
        categoryCard = popupView.findViewById(R.id.new_reminder_category_card);
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
                                firstReminderDateTime = (long) selection;
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
                                secondReminderDateTime = (long) selection;
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
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData -> RemindersFragment.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);
            }
        });
        reminderSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    remindersData = new RemindersData();
                    remindersData.setReminderTitle(reminderTitle.getText().toString());
                    remindersData.setFirstReminderTimeHr(firstReminderTimeHr);
                    remindersData.setFirstReminderTimeMin(firstReminderTimeMin);
                    remindersData.setSecondReminderTimeHr(secondReminderTimeHr);
                    remindersData.setSecondReminderTimeMin(secondReminderTimeMin);
                    remindersData.setFirstReminderDateTime(firstReminderDateTime);
                    remindersData.setSecondReminderDateTime(secondReminderDateTime);

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
                    remindersData.setReminderCategory(categoryData);
                    databaseReference.child(remindersData.getReminderTitle()).setValue(remindersData);
                    Snackbar.make(view, "Reminder Saved", Snackbar.LENGTH_SHORT)
                            .show();
                    popupWindow.dismiss();


                } else {
                    Snackbar.make(view, "Please sign in to save reminders", Snackbar.LENGTH_SHORT)
                            .show();
                }

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
                        firstReminderDateTime = firstReminderDateTime + (firstReminderTimeHr * 3600000) + (firstReminderTimeMin * 60000);
                        firstReminderChip.setText(firstReminderDate + ", " + String.format("%02d:%02d", firstReminderTimeHr, firstReminderTimeMin));
                    }
                }
        );
        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }
    public void mTimePicker1Edit() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("1st Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
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
                        secondReminderDateTime = secondReminderDateTime + (secondReminderTimeHr * 3600000) + (secondReminderTimeMin * 60000);
                        secondReminderChip.setText(secondReminderDate + ", " + String.format("%02d:%02d", secondReminderTimeHr, secondReminderTimeMin));
                    }
                }
        );
        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    public void mTimePicker2Edit() {
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("2nd Reminder Time").setTimeFormat(TimeFormat.CLOCK_24H);
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
        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    @Override
    public void sendCategory(CategoryData categoryData) {
        categoryCard.setText(categoryData.getCategoryTitle());
        categoryCard.setChipBackgroundColor(ColorStateList.valueOf(categoryData.getCategoryColor()));
        categoryCard.setVisibility(View.VISIBLE);
        this.categoryData = categoryData;

    }

    private void getReminders() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                remindersDataArrayList.clear();

                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    RemindersData remindersData = reminderSnapshot.getValue(RemindersData.class);
                    Collections.sort(remindersDataArrayList, new Comparator<RemindersData>() {
                        @Override
                        public int compare(RemindersData o1, RemindersData o2) {
                            return Long.valueOf(o1.getFirstReminderDateTime()).compareTo(o2.getFirstReminderDateTime()); // To compare integer values                        }
                    }});
                    remindersDataArrayList.add(remindersData);
                }
                remindersAdapter.notifyDataSetChanged();
                if (remindersDataArrayList.isEmpty()) {
                    reminderEmpty.setVisibility(View.VISIBLE);
                } else {
                    reminderEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onRemindersClick(int position) {
        RemindersData remindersData = remindersDataArrayList.get(position);
        showPopupWindowEdit(reminderRV, remindersData);
    }

    public void onRemindersCheckBoxDelete(int position) {
        Snackbar.make(reminderRV, "Reminder: " + remindersDataArrayList.get(position).getReminderTitle() + "completed!", Snackbar.LENGTH_SHORT)
                .show();
        databaseReference.child(remindersDataArrayList.get(position).getReminderTitle()).removeValue();
        remindersDataArrayList.clear();
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
        reminderTitle = popupView.findViewById(R.id.new_reminder_title);
        reminderTitle.setText(remindersData.getReminderTitle());

        firstReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_1);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");
        long epochTime1 = remindersData.getFirstReminderDateTime() - 28800000;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime1), ZoneId.of("Asia/Singapore"));
        firstReminderChip.setText(dateFormat.format(dateTime));

        secondReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_2);
        long epochTime2 = remindersData.getSecondReminderDateTime() - 28800000;
        LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime2), ZoneId.of("Asia/Singapore"));
        secondReminderChip.setText(dateFormat.format(dateTime2));

        reminderLocationText = popupView.findViewById(R.id.new_reminder_location_text);
        reminderLocationText.setText(remindersData.getReminderLocationName());
        reminderLocationChip = popupView.findViewById(R.id.button_new_reminder_getLocation);

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
                materialDateBuilder.setSelection(remindersData.getFirstReminderDateTime());

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
                                firstReminderDateTime = (long) selection;
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
                materialDateBuilder.setSelection(remindersData.getSecondReminderDateTime());

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
                                secondReminderDateTime = (long) selection;
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
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData -> RemindersFragment.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);
            }
        });
        reminderSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    remindersData.setReminderTitle(reminderTitle.getText().toString());
                    remindersData.setFirstReminderTimeHr(firstReminderTimeHr);
                    remindersData.setFirstReminderTimeMin(firstReminderTimeMin);
                    remindersData.setSecondReminderTimeHr(secondReminderTimeHr);
                    remindersData.setSecondReminderTimeMin(secondReminderTimeMin);
                    remindersData.setFirstReminderDateTime(firstReminderDateTime);
                    remindersData.setSecondReminderDateTime(secondReminderDateTime);

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
                    remindersData.setReminderCategory(categoryData);
                    databaseReference.child(remindersData.getReminderTitle()).setValue(remindersData);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(reminderRV.getContext());

                // Set the message show for the Alert time
                builder.setMessage("Delete reminder: " + remindersData.getReminderTitle() + "? ");

                // Set Alert Title
                builder.setTitle("Delete?");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(true);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    databaseReference.child(remindersData.getReminderTitle()).removeValue();
                    Snackbar.make(reminderRV, "Reminder deleted!", Snackbar.LENGTH_SHORT)
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
}




