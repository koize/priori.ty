package com.koize.priority.ui.monthlyplanner;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.koize.priority.GetMap;
import com.koize.priority.NotiReceiver;
import com.koize.priority.R;
import com.koize.priority.ShowMap;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.category.CategoryPopUp;
import com.koize.priority.ui.reminders.RemindersData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class MonthlyPlannerPage extends AppCompatActivity implements CategoryPopUp.CategoryCallBack {
    public static final int INPUT_METHOD_NEEDED = 1;
    private static final int image_chooser_request_code = 999;
    private static final int get_map_request_code= 998;


    private FloatingActionButton addEventButton;
    EditText eventTitle;
    EditText eventType;
    TableRow dateChip;
    TextView dateText;
    TableRow timeChip;
    TextView timeText;
    Switch allDay;
    TableRow reminderDateChip;
    TextView reminderText;
    EditText eventLocationText;
    Chip eventLocationChip;
    double eventLatitude;
    double eventLongitude;
    Chip eventCategoryChip;
    Chip eventDescImageChip;
    EditText eventDescText;
    Chip eventSaveChip;
    private String eventStartDate;
    private String eventEndDate;
    private long eventStartDateEpoch;
    private long eventEndDateEpoch;
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

    private CategoryData categoryData;
    Chip eventCategoryCard;
    private ImageView eventImageView;
    private Uri eventImageUri;

    EventData eventData;

    private TextView eventCalenderEmpty;
    private ProgressBar eventCalenderLoading;
    private TextView eventListEmpty;
    private ProgressBar eventListLoading;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseEventListReference;
    DatabaseReference databaseEventCalenderReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseUser user;

    private CalendarView eventCalenderView;
    private RecyclerView eventCalenderRecyclerView;
    private RecyclerView eventListRecyclerView;
    private EventListAdapter eventListAdapter;
    private EventCalenderAdapter eventCalenderAdapter;
    private ArrayList<EventData> eventListDataArrayList;
    private ArrayList<EventData> eventCalenderDataArrayList;


    private MaterialTextView eventShowTitle;
    private MaterialTextView eventShowType;
    private MaterialTextView eventShowDate;
    private MaterialTextView eventShowTime;
    private TableRow eventShowReminderRow;
    private MaterialTextView eventShowReminder;
    private TableRow eventShowLocationRow;
    private MaterialTextView eventShowLocation;
    private Chip eventShowLocationMap;
    private Chip eventShowCategory;
    private TableRow eventShowDescRow;
    private MaterialTextView eventShowDesc;
    private ImageView eventShowImage;
    private Chip eventShowEdit;
    private Chip eventShowDelete;
    boolean checkExists = false;

    DatabaseReference databaseHolReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_planner_page);
        createNotificationChannel();
        addEventButton = findViewById(R.id.button_monthly_planner_add);
        addEventButton.setOnClickListener(addEventListener);
        eventCalenderEmpty = findViewById(R.id.events_calendar_empty);
        eventCalenderLoading = findViewById(R.id.events_calendar_loading);
        eventListEmpty = findViewById(R.id.events_list_empty);
        eventListLoading = findViewById(R.id.events_list_loading);
        eventCalenderView = findViewById(R.id.calendarView);
        eventCalenderRecyclerView = findViewById(R.id.events_calender_recycler);
        eventListRecyclerView = findViewById(R.id.events_list_recycler);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name != "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseEventListReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1, 5) + "/events");
                databaseHolReference = firebaseDatabase.getReference("hols");
                storage = FirebaseStorage.getInstance("gs://priority-135fc.appspot.com");
                storageRef = storage.getReference("users/" + name + "_" + user.getUid().substring(1, 5) + "/events" + "/images");
            } else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseEventListReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/events");
                databaseHolReference = firebaseDatabase.getReference("hols");
                storage = FirebaseStorage.getInstance("gs://priority-135fc.appspot.com");
                storageRef = storage.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/events" + "/images");
            } else {
                throw new IllegalStateException("Unexpected value: " + name);
            }

        } else {
            Snackbar.make(findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }

        eventListDataArrayList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(eventListDataArrayList, this, new EventListAdapter.EventListClickInterface() {
            @Override
            public void onEventListClick(int position) {
                EventData eventData = eventListDataArrayList.get(position);
                showSavedEventPopupWindow(eventListRecyclerView, eventData);
            }
        }, new EventListAdapter.EventListExtraInterface() {
            @Override
            public void onEventListExtraClick(int position) {

            }
        });
        eventListRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        eventListRecyclerView.setAdapter(eventListAdapter);
        if (user != null) {
            getEventsList();
        }

        eventCalenderDataArrayList = new ArrayList<>();
        eventCalenderAdapter = new EventCalenderAdapter(eventCalenderDataArrayList, this, new EventCalenderAdapter.EventCalenderClickInterface() {
            @Override
            public void onEventCalenderClick(int position) {
                EventData eventData = eventCalenderDataArrayList.get(position);
                showSavedEventPopupWindow(eventCalenderRecyclerView, eventData);
            }
        }, new EventCalenderAdapter.EventCalenderExtraInterface() {
            @Override
            public void onEventCalenderExtraClick(int position) {

            }
        });

        eventCalenderRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        eventCalenderRecyclerView.setAdapter(eventCalenderAdapter);
        eventCalenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                eventCalenderLoading.setVisibility(View.VISIBLE);
                long dateSelected = getEpochMilliseconds(year, month, dayOfMonth);
                getEventsCalender(dateSelected);
            }

        });
        LocalDate today = LocalDate.now();
        if (user != null) {
            getEventsCalender(getEpochMilliseconds(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()));
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
            eventCalenderEmpty.setVisibility(View.VISIBLE);
            eventCalenderLoading.setVisibility(View.GONE);
            eventListEmpty.setVisibility(View.VISIBLE);
            eventListLoading.setVisibility(View.GONE);
            eventCalenderEmpty.setText("Sign in to create events!");
            eventListEmpty.setText("Sign in to create events!");
        }
    }

    public static long getEpochMilliseconds(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        return calendar.getTimeInMillis();
    }


    private void getEventsCalender(long dateSelected) {
        Query query = databaseEventListReference.orderByChild("eventStartDateTime");
        Query queryHoliday = databaseHolReference.orderByChild("eventStartDateTime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventCalenderDataArrayList.clear();
                getHolsCalendar(dateSelected);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventData eventData = dataSnapshot.getValue(EventData.class);
                    if ((eventData.getEventStartDateEpoch() - 28800000) <= dateSelected && (eventData.getEventEndDateEpoch())  >= dateSelected) {
                        eventCalenderDataArrayList.add(eventData);
                    }
                }
                eventCalenderAdapter.notifyDataSetChanged();
                if (eventCalenderDataArrayList.isEmpty()) {
                    eventCalenderLoading.setVisibility(View.GONE);
                    eventCalenderEmpty.setVisibility(View.VISIBLE);
                } else {
                    eventCalenderLoading.setVisibility(View.GONE);
                    eventCalenderEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getEventsList() {
        Query query = databaseEventListReference.orderByChild("eventStartDateTime");
        long sixMonths = 15778800000L;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventListDataArrayList.clear();
                getHols();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventData eventData = dataSnapshot.getValue(EventData.class);
                    if (eventData.getEventEndDateTime() + 86400000   < System.currentTimeMillis()) {

                    } else if (eventData.getEventEndDateTime()   < System.currentTimeMillis() - sixMonths) {
                        databaseEventListReference.child(eventData.getEventTextId()).removeValue();
                    } else {
                        eventListDataArrayList.add(eventData);
                    }

                    if (eventData.getEventPendingIntent() == null && eventData.getEventStartDateTime()  > System.currentTimeMillis() + 28800000 ) {
                        scheduleNoti(eventData);
                        if (eventData.getEventReminderDateTime() != 0 && eventData.getEventReminderDateTime() > System.currentTimeMillis() + 28800000 ) {
                            scheduleReminderNoti(eventData);
                        }
                    }
                }
                eventListAdapter.notifyDataSetChanged();
                if (eventListDataArrayList.isEmpty()) {
                    eventListLoading.setVisibility(View.GONE);
                    eventListEmpty.setVisibility(View.VISIBLE);
                } else {
                    eventListLoading.setVisibility(View.GONE);
                    eventListEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void getHols(){
        long sixMonths = 15778800000L;
        Query queryHoliday = databaseHolReference.orderByChild("eventStartDateTime");
        queryHoliday.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    EventData eventData = dataSnapshot.getValue(EventData.class);
                    if (eventData.getEventPendingIntent() == null && eventData.getEventStartDateTime()  > System.currentTimeMillis()) {
                        scheduleNoti(eventData);
                        if (eventData.getEventReminderDateTime() != 0) {
                            scheduleReminderNoti(eventData);
                        }
                    }
                    if (eventData.getEventEndDateTime()  < System.currentTimeMillis()) {

                    } else {
                        eventListDataArrayList.add(eventData);
                    }
                    eventListAdapter.notifyDataSetChanged();
                    if (eventListDataArrayList.isEmpty()) {
                        eventListLoading.setVisibility(View.GONE);
                        eventListEmpty.setVisibility(View.VISIBLE);
                    } else {
                        eventListLoading.setVisibility(View.GONE);
                        eventListEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getHolsCalendar(long dateSelected){
        long sixMonths = 15778800000L;
        Query queryHoliday = databaseHolReference.orderByChild("eventStartDateTime");
        queryHoliday.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    EventData eventData = dataSnapshot.getValue(EventData.class);
                    if ((eventData.getEventStartDateEpoch()) <= dateSelected && (eventData.getEventEndDateEpoch()) + 28800000 >= dateSelected) {
                        eventCalenderDataArrayList.add(eventData);
                    }
                }
                eventCalenderAdapter.notifyDataSetChanged();
                if (eventCalenderDataArrayList.isEmpty()) {
                    eventCalenderLoading.setVisibility(View.GONE);
                    eventCalenderEmpty.setVisibility(View.VISIBLE);
                } else {
                    eventCalenderLoading.setVisibility(View.GONE);
                    eventCalenderEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void scheduleNoti(EventData eventData) {
        long eventStartDateTime = eventData.getEventStartDateTime();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventStartDateTime - 28800000 ), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a");
        String formattedTime = formatter.format(dateTime);
        Notification.Builder builder = new Notification.Builder(MonthlyPlannerPage.this, "events");
        builder.setContentTitle("Event: " + eventData.getEventTitle());
        builder.setContentText(eventData.getEventTitle() + " at " + formattedTime);
        builder.setSmallIcon(R.drawable.baseline_access_time_24);
        Notification notification = builder.build();
        Intent intent = new Intent(MonthlyPlannerPage.this, NotiReceiver.class);
        intent.putExtra(NotiReceiver.NOTIFICATION, notification);
        intent.putExtra("id", eventData.getEventId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MonthlyPlannerPage.this, eventData.getEventId(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) MonthlyPlannerPage.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, eventStartDateTime - 28800000, pendingIntent);
        eventData.setEventPendingIntent(pendingIntent);
    }

    public void scheduleReminderNoti(EventData eventData) {
        long eventReminderDateTime = eventData.getEventReminderDateTime() ;
        long eventStartDateTime = eventData.getEventStartDateTime();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventStartDateTime - 28800000), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a");
        String formattedTime = formatter.format(dateTime);
        Notification.Builder builder = new Notification.Builder(MonthlyPlannerPage.this, "events");
        builder.setContentTitle("Reminder for Event: " +  eventData.getEventTitle());
        builder.setContentText(eventData.getEventTitle() + " at " + formattedTime);
        builder.setSmallIcon(R.drawable.baseline_access_time_24);
        Notification notification = builder.build();
        Intent intent = new Intent(MonthlyPlannerPage.this, NotiReceiver.class);
        intent.putExtra(NotiReceiver.NOTIFICATION, notification);
        intent.putExtra("id", eventData.getEventId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MonthlyPlannerPage.this, eventData.getEventId() + 1, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) MonthlyPlannerPage.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, eventReminderDateTime - 28800000, pendingIntent);
        eventData.setEventPendingIntent(pendingIntent);
    }


    View.OnClickListener addEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showNewEventPopupWindow(v);
        }
    };

    private void uploadImage(File filePath, Uri uri, String eventTextId)
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            storageRef
                    .child(
                            eventTextId
                                    ).putFile(uri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Snackbar.make(findViewById(android.R.id.content), "Image Uploaded!", Snackbar.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
    public void showNewEventPopupWindow(final View view) {

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

        eventTitle = popupView.findViewById(R.id.new_event_title);
        eventType = popupView.findViewById(R.id.new_event_type);
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
        eventImageView = popupView.findViewById(R.id.new_event_image);
        eventDescText = popupView.findViewById(R.id.new_event_desc);
        eventSaveChip = popupView.findViewById(R.id.button_new_event_save);
        dateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

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

                                eventStartDateStartTime = (long) Pair.class.cast(selection).first;
                                eventEndDateTime = (long) Pair.class.cast(selection).second;
                                eventStartDateEpoch = eventStartDateStartTime;
                                eventEndDateEpoch = eventEndDateTime;
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
                    isAllDay = true;
                } else {
                    isAllDay = false;
                    timeText.setText("Time:");
                }
            }
        });
        reminderDateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

                // now define the properties of the
                // materialDateBuilder that is title text as SELECT A DATE
                materialDateBuilder.setTitleText("Reminder Date");

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
                Intent intent = new Intent(MonthlyPlannerPage.this, GetMap.class);
                startActivityForResult(intent, get_map_request_code);
            }
        });
        eventCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData -> MonthlyPlannerPage.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);
            }
        });
        eventDescImageChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthlyPlannerPage.this, ImageChooser.class);
                startActivityForResult(intent, image_chooser_request_code);            }
        });
        eventSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                /*databaseEventListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            EventData eventData = dataSnapshot.getValue(EventData.class);
                            if (eventData.getEventTitle().equals(eventTitle.getText().toString()))
                            {
                                checkExists = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
                if (user == null) {
                    Snackbar.make(findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    eventData = new EventData();
                    eventData.setEventId(new Random().nextInt(1000000));
                    if (eventTitle.getText().toString().isEmpty()) {
                        Snackbar.make(findViewById(android.R.id.content), "Please enter a title!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    } /*else if (checkExists == true)
                    {
                        Snackbar.make(findViewById(android.R.id.content), "Event already exists, please use another name", Snackbar.LENGTH_SHORT)
                                .show();
                    }*/else {
                        eventData.setEventTitle(eventTitle.getText().toString());
                        eventData.setEventTextId(eventTitle.getText().toString().toLowerCase().replaceAll("\\s", "") + "_" + eventData.getEventId());
                    }
                    if (eventType.getText().toString().isEmpty()) {
                        eventData.setEventType("Event");
                    } else {
                        eventData.setEventType(eventType.getText().toString());
                    }
                    if (eventStartDate == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Please select a date!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    } else {
                        eventData.setEventStartDate(eventStartDate);
                    }
                    eventData.setEventStartDateEpoch(eventStartDateEpoch);
                    eventData.setEventEndDateEpoch(eventEndDateEpoch);
                    eventData.setEventEndDate(eventEndDate);
                    eventData.setEventStartDateTime(eventStartDateStartTime);
                    eventData.setEventEndDateTime(eventEndDateTime);
                    if (isAllDay == false && eventStartDateStartTime == eventStartDateEpoch && eventEndDateTime == eventEndDateEpoch) {
                        isAllDay = true;
                    }
                    eventData.setEventAllDay(isAllDay);
                    eventData.setEventReminderDate(reminderDate);
                    eventData.setEventReminderDateTime(reminderDateTime);
                    eventData.setEventLocationName(eventLocationText.getText().toString());
                    eventData.setEventLatitude(eventLatitude);
                    eventData.setEventLongitude(eventLongitude);
                    if (eventData.getEventLatitude() != 0 && eventData.getEventLocationName() == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Please enter a location name!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    if (categoryData == null) {
                        categoryData = new CategoryData();
                        categoryData.setCategoryTitle("Others");
                        //categoryData.setCategoryColor(Color.parseColor("#FFB4AB"));
                    }
                    eventData.setEventCategory(categoryData);

                    if (eventImageUri != null) {
                        File file = new File(eventImageUri.getPath());
                        uploadImage(file, eventImageUri, eventData.getEventTextId());
                    }


                    eventData.setEventDesc(eventDescText.getText().toString());
                    if (eventData.getEventTitle() == "") {
                        Snackbar.make(findViewById(android.R.id.content), "Please enter a title!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else if (eventData.getEventStartDateTime() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Please select a date!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else if (eventData.getEventEndDateTime() + 86400000 < System.currentTimeMillis()) {
                        Snackbar.make(findViewById(android.R.id.content), "Event end time cannot be before current time!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else if (eventData.getEventReminderDateTime() != 0 && eventData.getEventReminderDateTime() + 28800000< System.currentTimeMillis()) {
                        Snackbar.make(findViewById(android.R.id.content), "Reminder time cannot be before current time!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else if (eventData.getEventReminderDateTime() != 0 && eventData.getEventReminderDateTime()  > eventData.getEventEndDateTime()) {
                        Snackbar.make(findViewById(android.R.id.content), "Reminder time cannot be after event end time!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        databaseEventListReference.child(eventData.getEventTextId()).setValue(eventData);
                        //databaseHolReference.child(eventData.getEventTextId()).setValue(eventData); //FOR ADDING HOLS ONLY
                        Snackbar.make(findViewById(android.R.id.content), "Event saved", Snackbar.LENGTH_SHORT)
                                .show();
                        checkExists = false; //reset checkExists
                        popupWindow.dismiss();
                    }

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
                        timeText.setText(String.format("%02d:%02d", eventStartHr, eventStartMin) + " - " + String.format("%02d:%02d", eventEndHr, eventEndMin));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == image_chooser_request_code && resultCode == RESULT_OK && data != null) {
            eventImageUri = (Uri) data.getExtras().get("image");
            eventImageView.setImageURI(eventImageUri);
        }

        if (requestCode == get_map_request_code && resultCode == RESULT_OK && data != null) {
            eventLatitude = (double) data.getExtras().get("lat");
            eventLongitude = (double) data.getExtras().get("lon");
            eventLocationChip.setText("Location set");
        }


    }

    @Override
    public void sendCategory(CategoryData categoryData) {
        eventCategoryCard.setText(categoryData.getCategoryTitle());
        eventCategoryCard.setChipBackgroundColor(ColorStateList.valueOf(categoryData.getCategoryColor()));
        eventCategoryCard.setVisibility(View.VISIBLE);
        this.categoryData = categoryData;

    }

    public void showSavedEventPopupWindow(final View view, EventData eventData) {

        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_events_show, null);

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

        eventShowTitle = popupView.findViewById(R.id.title_show_event);
        eventShowType = popupView.findViewById(R.id.show_event_type);
        eventShowDate = popupView.findViewById(R.id.show_event_date);
        eventShowTime = popupView.findViewById(R.id.show_event_time);
        eventShowReminder = popupView.findViewById(R.id.show_event_reminder);
        eventShowReminderRow = popupView.findViewById(R.id.button_show_event_reminder);
        eventShowLocationRow = popupView.findViewById(R.id.show_event_location_table_row);
        eventShowLocation = popupView.findViewById(R.id.show_event_location_name);
        eventShowLocationMap = popupView.findViewById(R.id.button_show_event_get_location);
        eventShowCategory = popupView.findViewById(R.id.show_event_category_card);
        eventShowDescRow = popupView.findViewById(R.id.show_event_desc_table_row);
        eventShowDesc = popupView.findViewById(R.id.show_event_desc);
        eventShowImage = popupView.findViewById(R.id.show_event_desc_image);
        eventShowEdit = popupView.findViewById(R.id.button_show_event_edit);
        eventShowDelete = popupView.findViewById(R.id.button_show_event_delete);

        eventShowTitle.setText(eventData.getEventTitle());
        eventShowType.setText(eventData.getEventType());
        eventShowDate.setText(eventData.getEventStartDate());
        if (eventData.getEventAllDay()) {
            eventShowTime.setText("All day");
        } else {
            eventShowTime.setText(convertTimestampToTimeRange(eventData.getEventStartDateTime(), eventData.getEventEndDateTime(), eventData.getEventAllDay()));
        }
        if (eventData.getEventReminderDateTime() == 0) {
            eventShowReminderRow.setVisibility(View.GONE);
        } else {
            eventShowReminderRow.setVisibility(View.VISIBLE);
        }
        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventData.getEventReminderDateTime() - 28800000 ), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedTime = formatter.format(dateTime1);
        eventShowReminder.setText(eventData.getEventReminderDate() + ", " + formattedTime);
        if (eventData.getEventLocationName().isEmpty() && eventData.getEventLatitude() == 0 && eventData.getEventLongitude() == 0) {
            eventShowLocationRow.setVisibility(View.GONE);
        } else {
            eventShowLocationRow.setVisibility(View.VISIBLE);
        }
        eventShowLocation.setText(eventData.getEventLocationName());
        eventShowCategory.setText(eventData.getEventCategory().getCategoryTitle());
        eventShowCategory.setChipBackgroundColor(ColorStateList.valueOf(eventData.getEventCategory().getCategoryColor()));
        if (eventData.getEventDesc().isEmpty()) {
            eventShowDesc.setText("No description");
        } else {
            eventShowDesc.setText(eventData.getEventDesc());
        }


        Glide.with(this)
                .load(storageRef.child(eventData.getEventTextId()))
                .into(eventShowImage);

        eventShowLocationMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventData.getEventLatitude() == 0 && eventData.getEventLongitude() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "No location set!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    Intent intent = new Intent(MonthlyPlannerPage.this, ShowMap.class);
                    intent.putExtra("locationName", eventData.getEventLocationName());
                    intent.putExtra("lat", eventData.getEventLatitude());
                    intent.putExtra("lon", eventData.getEventLongitude());
                    startActivity(intent);
                }
            }
        });
        eventShowEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventData.getEventType() != "Holiday") {
                    showEditEventPopupWindow(v, eventData);
                    popupWindow.dismiss();
                } else {
                   Snackbar.make(findViewById(android.R.id.content), "Cannot edit holiday!", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        eventShowDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MonthlyPlannerPage.this);

                // Set the message show for the Alert time
                builder.setMessage("Delete event: " + eventData.getEventTitle() + "? ");

                // Set Alert Title
                builder.setTitle("Delete?");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(true);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click yes button then app will close
                    Intent intent = new Intent(MonthlyPlannerPage.this, NotiReceiver.class);
                    intent.putExtra(NotiReceiver.NOTIFICATION, eventData.getEventTitle());
                    intent.putExtra("id", eventData.getEventId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MonthlyPlannerPage.this, eventData.getEventId(), intent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) MonthlyPlannerPage.this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    databaseEventListReference.child(eventData.getEventTextId()).removeValue();
                    storageRef.child(eventData.getEventTextId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });
                    Snackbar.make(eventListRecyclerView, "Event deleted!", Snackbar.LENGTH_SHORT)
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

    public void showEditEventPopupWindow(final View view, EventData eventData) {

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
        TextView eventTopTile = popupView.findViewById(R.id.title_new_event);
        eventTitle = popupView.findViewById(R.id.new_event_title);
        eventType = popupView.findViewById(R.id.new_event_type);
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
        eventImageView = popupView.findViewById(R.id.new_event_image);
        eventDescText = popupView.findViewById(R.id.new_event_desc);

        eventSaveChip = popupView.findViewById(R.id.button_new_event_save);

        eventTopTile.setText("Edit event");
        eventTitle.setText(eventData.getEventTitle());
        eventType.setText(eventData.getEventType());
        dateText.setText(eventData.getEventStartDate());
        if (eventData.getEventAllDay()) {
            timeText.setText("All day");
            eventStartHr = 0;
            eventStartMin = 0;
            eventEndHr = 23;
            eventEndMin = 59;
            isAllDay = true;
            allDay.setChecked(true);

        } else {
            isAllDay = false;
            allDay.setChecked(false);
            timeText.setText(convertTimestampToTimeRange(eventData.getEventStartDateTime(), eventData.getEventEndDateTime(), eventData.getEventAllDay()));
        }
        if (eventData.getEventReminderDateTime() == 0) {
            reminderText.setText("Reminder not set");
        } else {
            reminderHr = (int) ((eventData.getEventReminderDateTime() % 86400000) / 3600000);
            reminderMin = (int) (((eventData.getEventReminderDateTime() % 86400000) % 3600000) / 60000);
            reminderDate = eventData.getEventReminderDate();
            reminderDateTime = eventData.getEventReminderDateTime();

            LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventData.getEventReminderDateTime() - 28800000), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedTime = formatter.format(dateTime1);
            reminderText.setText(eventData.getEventReminderDate() + ", " + formattedTime);
        }
        eventLocationText.setText(eventData.getEventLocationName());
        eventCategoryCard.setText(eventData.getEventCategory().getCategoryTitle());
        eventCategoryCard.setChipBackgroundColor(ColorStateList.valueOf(eventData.getEventCategory().getCategoryColor()));
        Glide.with(this)
                .load(storageRef.child(eventData.getEventTextId()))
                .into(eventImageView);
        eventDescText.setText(eventData.getEventDesc());


        dateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

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

                                eventStartDateStartTime = (long) Pair.class.cast(selection).first;
                                eventEndDateTime = (long) Pair.class.cast(selection).second;
                                eventStartDateEpoch = (long) Pair.class.cast(selection).first;
                                eventEndDateEpoch = (long) Pair.class.cast(selection).second;
                                eventStartDate = materialDatePicker.getHeaderText();
                                dateText.setText(eventStartDate);
                                mTimePicker1();

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
                    isAllDay = true;
                    eventData.setEventAllDay(isAllDay);
                } else {
                    isAllDay = false;
                    eventData.setEventAllDay(isAllDay);
                    timeText.setText(convertTimestampToTimeRange(eventStartDateStartTime, eventEndDateTime, isAllDay));
                }
            }
        });
        reminderDateChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

                // now define the properties of the
                // materialDateBuilder that is title text as SELECT A DATE
                materialDateBuilder.setTitleText("Reminder Date");

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
                Intent intent = new Intent(MonthlyPlannerPage.this, GetMap.class);
                if (eventData.getEventLatitude() != 0 && eventData.getEventLongitude() != 0) {
                    intent.putExtra("lat", eventData.getEventLatitude());
                    intent.putExtra("lon", eventData.getEventLongitude());
                }
                startActivityForResult(intent, get_map_request_code);
            }
        });
        eventCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp categoryPopUp = new CategoryPopUp(categoryData -> MonthlyPlannerPage.this.sendCategory(categoryData));
                categoryPopUp.showPopupWindow(v);
            }
        });
        eventDescImageChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthlyPlannerPage.this, ImageChooser.class);
                startActivityForResult(intent, image_chooser_request_code);
            }
        });
        eventSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                if (user == null) {
                    Snackbar.make(findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    if (eventTitle.getText().toString().isEmpty()) {
                        Snackbar.make(findViewById(android.R.id.content), "Please enter a title!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    } else {
                        eventData.setEventTitle(eventTitle.getText().toString());
                    }
                    if (eventType.getText().toString().isEmpty()) {
                        eventData.setEventType("Event");
                    } else {
                        eventData.setEventType(eventType.getText().toString());
                    }
                    if (eventStartDateStartTime != 0) {
                        eventData.setEventStartDateEpoch(eventStartDateEpoch);
                        eventData.setEventEndDateEpoch(eventEndDateEpoch);
                        eventData.setEventStartDateTime(eventStartDateStartTime);
                        eventData.setEventEndDateTime(eventEndDateTime);
                    }
                    if (isAllDay == false && eventData.getEventStartDateTime() == eventData.getEventStartDateEpoch() && eventData.getEventEndDateTime() == eventData.getEventEndDateEpoch()) {
                        isAllDay = true;
                    }
                    eventData.setEventAllDay(isAllDay);
                    if (reminderDateTime != 0) {
                        eventData.setEventReminderDate(reminderDate);
                        eventData.setEventReminderDateTime(reminderDateTime);
                    }
                    if (eventData.getEventLatitude() != 0 && eventData.getEventLocationName() == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Please enter a location name!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    eventData.setEventLocationName(eventLocationText.getText().toString());
                    if (eventLatitude != 0){
                        eventData.setEventLatitude(eventLatitude);
                        eventData.setEventLongitude(eventLongitude);
                    }

                    if (categoryData == null) {
                        categoryData = new CategoryData();
                        categoryData.setCategoryTitle("Others");
                    }

                    if (eventData.getImageUri() == null && eventImageUri != null) {
                        File file = new File(eventImageUri.getPath());
                        uploadImage(file, eventImageUri, eventData.getEventTextId());
                    }
                    else if (eventData.getImageUri() == null && eventImageUri == null) {

                    }
                    else if (eventData.getImageUri() != null && eventImageUri != null) {
                        eventData.setImageUri(eventImageUri);
                    }
                    else if (eventData.getImageUri() != null && eventImageUri == null) {
                        eventData.setImageUri(eventData.getImageUri());
                    }
                    else {
                    }
                    eventData.setEventCategory(categoryData);
                    eventData.setEventDesc(eventDescText.getText().toString());
                    try {
                        databaseEventListReference.child(eventData.getEventTextId()).setValue(eventData);
                    }
                    catch (Exception e) {
                        Log.e("Error", e.toString(), e.getCause().getCause());
                    }
                    Snackbar.make(findViewById(android.R.id.content), "Event saved", Snackbar.LENGTH_SHORT)
                            .show();
                    popupWindow.dismiss();
                }
            }
        });


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "events";
            String description = "events";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("events", name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = MonthlyPlannerPage.this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
    public String convertTimestampToTimeRange(long timestamp1, long timestamp2, boolean isAllDay) {
        timestamp1 = timestamp1 - 28800000;
        timestamp2 = timestamp2 - 28800000;
        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp1), ZoneId.systemDefault());
        LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedTime = formatter.format(dateTime1);
        String formattedTime2 = formatter.format(dateTime2);

        // Calculate the number of hours since the current time


        // Get the hour of the timestamp
        /*
        int timestampHour = dateTime.getHour();

        // Determine the time of day
        String timeOfDay = "Morning";
        if (timestampHour >= 12) {
            timeOfDay = "Afternoon";
        } else if (timestampHour >= 18) {
            timeOfDay = "Evening";
        } else if (timestampHour >= 21) {
            timeOfDay = "Night";
        }

        if (hoursSinceNow == 0) {
            return minutesSinceNow + "min, " + timeOfDay;
        } else {
            return hoursSinceNow + "hr, " + timeOfDay;
        }
        */
        if (isAllDay) {
            return "All Day";
        } else {
            return formattedTime + " - " + formattedTime2;
        }
    }
}