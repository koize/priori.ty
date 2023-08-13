package com.koize.priority.ui.home;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.koize.priority.NotiReceiver;
import com.koize.priority.ui.focusmode.FocusModeActivity;
import com.koize.priority.ui.monthlyplanner.EventData;
import com.koize.priority.ui.monthlyplanner.EventShowAndEditPopUp;
import com.koize.priority.ui.monthlyplanner.MonthlyPlannerPage;
import com.koize.priority.R;
import com.koize.priority.settings.SettingsActivity;
import com.koize.priority.databinding.FragmentHomeBinding;
import com.koize.priority.ui.reminders.RemindersData;
import com.koize.priority.ui.reminders.RemindersEditPopUp;
import com.koize.priority.ui.reminders.RemindersNewPopUp;
import com.koize.priority.ui.routineplanner.RoutinePlannerPage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MaterialCardView monthlyPlannerButton;
    private Chip settingsChip;
    private Chip aboutChip;
    private Chip focusModeChip;
    private Chip routineChip;
    private TextView greetingText;
    FirebaseUser user;
    String name;
    String partOfDay;
    TextView versionNo;

    Chip newReminderChip;
    ProgressBar remindersTodayProgressBar;
    TextView remindersTodayEmpty;

    Chip newEventChip;
    ProgressBar eventsTodayProgressBar;
    TextView eventsTodayEmpty;

    ProgressBar eventsProgressBar;
    TextView eventsEmpty;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reminderDatabaseReference;
    DatabaseReference eventDatabaseReference;
    DatabaseReference eventHolsDatabaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;

    long date;
    //Reminders today recyclerview stuff

    RemindersData remindersData;
    RecyclerView remindersTodayRV;
    HomeRemindersTodayAdapter remindersAdapter;
    ArrayList<RemindersData> remindersDataArrayList;

    //Events today recyclerview stuff
    EventData eventData;
    RecyclerView eventsTodayRV;
    HomeEventsTodayAdapter eventsTodayAdapter;
    ArrayList<EventData> eventsTodayDataArrayList;

    //Events recyclerview stuff
    RecyclerView eventsRV;
    HomeEventsAdapter eventsAdapter;
    ArrayList<EventData> eventsDataArrayList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            name = user.getDisplayName();
        }

        versionNo = root.findViewById(R.id.home_version_no);
        greetingText = root.findViewById(R.id.title_home_greeting);
        monthlyPlannerButton = root.findViewById(R.id.button_home_open_montly_planner);
        monthlyPlannerButton.setOnClickListener(monthlyPlannerButtonListener);
        settingsChip = root.findViewById(R.id.button_home_settings);
        settingsChip.setOnClickListener(settingsChipListener);
        aboutChip = root.findViewById(R.id.button_home_about);
        aboutChip.setOnClickListener(aboutChipListener);
        focusModeChip = root.findViewById(R.id.button_home_focus);
        focusModeChip.setOnClickListener(focusModeChipListener);
        routineChip = root.findViewById(R.id.button_home_routine);
        routineChip.setOnClickListener(routineChipListener);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY) ;
        if(timeOfDay >= 0 && timeOfDay < 12){
            partOfDay = "morning";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            partOfDay = "afternoon";
        }else if(timeOfDay >= 16 && timeOfDay < 24){
            partOfDay = "evening";
        }else{
            partOfDay = "day";
        }

        if ((name != null) && (name!="")) {
            greetingText.setText("Good " + partOfDay + ", "+name+"!");
        }
        else {
            greetingText.setText("Good " + partOfDay + ", " + "Peasant" + "!");
        }
        PackageManager pm = getActivity().getPackageManager();
        String pkgName = getActivity().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String ver = pkgInfo.versionName;
        versionNo.setText("Version v" + ver);

        firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name!="") {
                reminderDatabaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/reminders");
                eventDatabaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1, 5) + "/events");
                eventHolsDatabaseReference = firebaseDatabase.getReference("hols");
                storage = FirebaseStorage.getInstance("gs://priority-135fc.appspot.com");
                storageRef = storage.getReference("users/" + name + "_" + user.getUid().substring(1, 5) + "/events" + "/images");
            }
            else if (name == "") {
                reminderDatabaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/reminders");
                eventDatabaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/events");
                eventHolsDatabaseReference = firebaseDatabase.getReference("hols");
                storage = FirebaseStorage.getInstance("gs://priority-135fc.appspot.com");
                storageRef = storage.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/events" + "/images");
            }
            else {
                throw new IllegalStateException("Unexpected value: " + name);
            }

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }

        //REMINDERS TODAY RECYCLERVIEW
        createReminderNotificationChannel();
        newReminderChip = root.findViewById(R.id.button_home_add_reminders);
        newReminderChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemindersData remindersData = new RemindersData();
                RemindersNewPopUp remindersNewPopUp = new RemindersNewPopUp(remindersData, getParentFragmentManager(), user, reminderDatabaseReference, v);
                remindersNewPopUp.showPopupWindow(v);            }
        });
        remindersTodayEmpty = root.findViewById(R.id.home_reminders_today_empty);
        remindersTodayProgressBar = root.findViewById(R.id.home_reminders_today_loading);
        remindersTodayRV = root.findViewById(R.id.home_reminders_today_recycler);
        remindersTodayRV.setMinimumHeight(200);
        remindersDataArrayList = new ArrayList<>();
        remindersAdapter = new HomeRemindersTodayAdapter(remindersDataArrayList, getContext(), this::onRemindersClick);
        remindersTodayRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        remindersTodayRV.setAdapter(remindersAdapter);



        //EVENTS TODAY RECYCLERVIEW
        createEventNotificationChannel();
        newEventChip = root.findViewById(R.id.button_home_add_events);
        newEventChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MonthlyPlannerPage.class);
                startActivity(intent);
            }
        });
        eventsTodayEmpty = root.findViewById(R.id.home_events_today_empty);
        eventsTodayProgressBar = root.findViewById(R.id.home_events_today_loading);
        eventsTodayRV = root.findViewById(R.id.home_events_today_recycler);
        eventsTodayRV.setMinimumHeight(200);
        eventsTodayDataArrayList = new ArrayList<>();
        eventsTodayAdapter = new HomeEventsTodayAdapter(eventsTodayDataArrayList, getContext(), this::onEventsTodayClick);
        eventsTodayRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        eventsTodayRV.setAdapter(eventsTodayAdapter);


        //EVENTS RECYCLERVIEW

        eventsEmpty = root.findViewById(R.id.home_events_empty);
        eventsProgressBar = root.findViewById(R.id.home_events_loading);
        eventsRV = root.findViewById(R.id.home_events_recycler);
        eventsRV.setMinimumHeight(200);
        eventsDataArrayList = new ArrayList<>();
        eventsAdapter = new HomeEventsAdapter(eventsDataArrayList, getContext(), this::onEventsClick);
        eventsRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        eventsRV.setAdapter(eventsAdapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalDate today = LocalDate.now();

        date = getEpochMilliseconds(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth());


        //REMINDERS
        if (user != null) {
            getRemindersSortByDate(getEpochMilliseconds(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()));

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
            remindersTodayEmpty.setVisibility(View.VISIBLE);
            remindersTodayEmpty.setText("Sign in to create reminders!");
            remindersTodayProgressBar.setVisibility(View.GONE);
        }

        //EVENTS TODAY
        if (user != null) {
            getEventsToday(getEpochMilliseconds(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()));
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
            eventsTodayEmpty.setVisibility(View.VISIBLE);
            eventsTodayEmpty.setText("Sign in to create events!");
            eventsTodayProgressBar.setVisibility(View.GONE);
        }

        //EVENTS
        if (user != null) {
            getEventsSortByDate(getEpochMilliseconds(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()));
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
            eventsEmpty.setVisibility(View.VISIBLE);
            eventsEmpty.setText("Sign in to create events!");
            eventsProgressBar.setVisibility(View.GONE);
        }
    }
    public static long getEpochMilliseconds(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        return calendar.getTimeInMillis();
    }
    public void getRemindersSortByDate(long date) {

        remindersDataArrayList.clear();
        Query query = reminderDatabaseReference.orderByChild("firstReminderDateTime");
        query.addValueEventListener(reminderTodayListener);
        remindersAdapter.notifyDataSetChanged();
    }

    ValueEventListener reminderTodayListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            remindersDataArrayList.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                RemindersData remindersData = dataSnapshot.getValue(RemindersData.class);
                if (remindersData.getFirstReminderDateTime() != 0 && remindersData.getReminderPendingIntent() == null && remindersData.getFirstReminderDateTime() - 28800000 > System.currentTimeMillis()) {
                    scheduleReminderNoti(remindersData);
                }
                if (remindersData.getFirstReminderDateEpoch() == date + 28800000 ) {
                    remindersDataArrayList.add(remindersData);
                }
            }
            remindersAdapter.notifyDataSetChanged();
            if (remindersDataArrayList.isEmpty()) {
                remindersTodayProgressBar.setVisibility(View.GONE);
                remindersTodayEmpty.setVisibility(View.VISIBLE);
            } else {
                remindersTodayProgressBar.setVisibility(View.GONE);
                remindersTodayEmpty.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT)
                    .show();
        }
    };



    public void scheduleReminderNoti(RemindersData remindersData) {
        long reminderDateTime = remindersData.getFirstReminderDateTime() - 28800000;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderDateTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm:a");
        String formattedTime = formatter.format(dateTime);
        Notification.Builder builder = new Notification.Builder(requireContext(), "reminders");
        builder.setContentTitle(remindersData.getReminderTitle());
        builder.setContentText(remindersData.getReminderTitle() + " at " + formattedTime);
        builder.setSmallIcon(R.drawable.baseline_access_time_24);
        Notification notification = builder.build();
        Intent intent = new Intent(getContext(), NotiReceiver.class);
        intent.putExtra(NotiReceiver.NOTIFICATION, notification);
        intent.putExtra("id", remindersData.getReminderId());
        intent.putExtra("menuFragment", "reminderFragment");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), remindersData.getReminderId(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderDateTime, pendingIntent);
        remindersData.setReminderPendingIntent(pendingIntent);
    }

    public void onRemindersClick(int position) {
        RemindersData remindersData = remindersDataArrayList.get(position);
        RemindersEditPopUp remindersEditPopUp = new RemindersEditPopUp(remindersData, getParentFragmentManager(), user, reminderDatabaseReference, remindersTodayRV);
        remindersEditPopUp.showPopupWindowEdit(remindersTodayRV, remindersData);
    }

    private void getEventsToday(long dateSelected) {
        Query query = eventDatabaseReference.orderByChild("eventStartDateTime");
        query.addValueEventListener(eventTodayListener);
        /*query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsTodayDataArrayList.clear();
                getHolsCalendar(dateSelected);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventData eventData = dataSnapshot.getValue(EventData.class);
                    if (eventData.getEventPendingIntent() == null && eventData.getEventStartDateTime()  > System.currentTimeMillis()) {
                        scheduleEventNoti(eventData);
                        if (eventData.getEventReminderDateTime() != 0) {
                            scheduleEventReminderNoti(eventData);
                        }
                    }
                    if ((eventData.getEventStartDateEpoch()) <= dateSelected && (eventData.getEventEndDateEpoch()) + 28800000 >= dateSelected) {
                        eventsTodayDataArrayList.add(eventData);
                    }
                }
                eventsTodayAdapter.notifyDataSetChanged();
                if (eventsTodayDataArrayList.isEmpty()) {
                    eventsTodayProgressBar.setVisibility(View.GONE);
                    eventsTodayEmpty.setVisibility(View.VISIBLE);
                } else {
                    eventsTodayProgressBar.setVisibility(View.GONE);
                    eventsTodayEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    ValueEventListener eventTodayListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            eventsTodayDataArrayList.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                EventData eventData = dataSnapshot.getValue(EventData.class);
                getHolsCalendar(date);
                if (eventData.getEventPendingIntent() == null && eventData.getEventStartDateTime()  > System.currentTimeMillis()) {
                    scheduleEventNoti(eventData);
                    if (eventData.getEventReminderDateTime() != 0) {
                        scheduleEventReminderNoti(eventData);
                    }
                }
                if ((eventData.getEventStartDateEpoch()) <= date && (eventData.getEventEndDateEpoch()) + 28800000 >= date) {
                    eventsTodayDataArrayList.add(eventData);
                }
            }
            eventsTodayAdapter.notifyDataSetChanged();
            if (eventsTodayDataArrayList.isEmpty()) {
                eventsTodayProgressBar.setVisibility(View.GONE);
                eventsTodayEmpty.setVisibility(View.VISIBLE);
            } else {
                eventsTodayProgressBar.setVisibility(View.GONE);
                eventsTodayEmpty.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT)
                    .show();
        }
    };
    public void onEventsTodayClick(int position) {
        EventData eventData = eventsTodayDataArrayList.get(position);
        EventShowAndEditPopUp eventShowAndEditPopUp = new EventShowAndEditPopUp(eventData, getActivity(), eventDatabaseReference, user, storageRef, getParentFragmentManager());
        eventShowAndEditPopUp.showSavedEventPopupWindow(eventsTodayRV, eventData);
    }
    public void getHolsCalendar(long dateSelected){
        long sixMonths = 15778800000L;
        Query queryHoliday = eventHolsDatabaseReference.orderByChild("eventStartDateTime");
        queryHoliday.addValueEventListener(eventsTodayHolsListener);
        /*queryHoliday.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    EventData eventData = dataSnapshot.getValue(EventData.class);
                    if (eventData.getEventPendingIntent() == null && eventData.getEventStartDateTime()  > System.currentTimeMillis()) {
                        scheduleEventNoti(eventData);
                        if (eventData.getEventReminderDateTime() != 0) {
                            scheduleEventReminderNoti(eventData);
                        }
                    }
                    if ((eventData.getEventStartDateEpoch()) <= dateSelected && (eventData.getEventEndDateEpoch()) + 28800000 >= dateSelected) {
                        eventsTodayDataArrayList.add(eventData);
                    }
                }
                eventsTodayAdapter.notifyDataSetChanged();
                if (eventsTodayDataArrayList.isEmpty()) {
                    eventsTodayProgressBar.setVisibility(View.GONE);
                    eventsTodayEmpty.setVisibility(View.VISIBLE);
                } else {
                    eventsTodayProgressBar.setVisibility(View.GONE);
                    eventsTodayEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    ValueEventListener eventsTodayHolsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                EventData eventData = dataSnapshot.getValue(EventData.class);
                if (eventData.getEventPendingIntent() == null && eventData.getEventStartDateTime()  > System.currentTimeMillis()) {
                    scheduleEventNoti(eventData);
                    if (eventData.getEventReminderDateTime() != 0) {
                        scheduleEventReminderNoti(eventData);
                    }
                }
                if ((eventData.getEventStartDateEpoch()) <= date && (eventData.getEventEndDateEpoch()) + 28800000 >= date) {
                    eventsTodayDataArrayList.add(eventData);
                }
            }
            eventsTodayAdapter.notifyDataSetChanged();
            if (eventsTodayDataArrayList.isEmpty()) {
                eventsTodayProgressBar.setVisibility(View.GONE);
                eventsTodayEmpty.setVisibility(View.VISIBLE);
            } else {
                eventsTodayProgressBar.setVisibility(View.GONE);
                eventsTodayEmpty.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT)
                    .show();
        }
    };
    public void scheduleEventNoti(EventData eventData) {
        long eventStartDateTime = eventData.getEventStartDateTime();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventStartDateTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm:a");
        String formattedTime = formatter.format(dateTime);
        Notification.Builder builder = new Notification.Builder(requireContext(), "events");
        builder.setContentTitle("Event: "+eventData.getEventTitle());
        builder.setContentText(eventData.getEventTitle() + " at " + formattedTime);
        builder.setSmallIcon(R.drawable.baseline_access_time_24);
        Notification notification = builder.build();
        Intent intent = new Intent(requireContext(), NotiReceiver.class);
        intent.putExtra(NotiReceiver.NOTIFICATION, notification);
        intent.putExtra("id", eventData.getEventId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), eventData.getEventId(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, eventStartDateTime - 28800000, pendingIntent);
        eventData.setEventPendingIntent(pendingIntent);
    }
    public void scheduleEventReminderNoti(EventData eventData) {
        long eventReminderDateTime = eventData.getEventReminderDateTime() ;
        long eventStartDateTime = eventData.getEventStartDateTime();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(eventStartDateTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm:a");
        String formattedTime = formatter.format(dateTime);
        Notification.Builder builder = new Notification.Builder(requireContext(), "events");
        builder.setContentTitle("Reminder for Event: " +  eventData.getEventTitle());
        builder.setContentText(eventData.getEventTitle() + " at " + formattedTime);
        builder.setSmallIcon(R.drawable.baseline_access_time_24);
        Notification notification = builder.build();
        Intent intent = new Intent(requireContext(), NotiReceiver.class);
        intent.putExtra(NotiReceiver.NOTIFICATION, notification);
        intent.putExtra("id", eventData.getEventId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), eventData.getEventId(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, eventReminderDateTime - 28800000, pendingIntent);
        eventData.setEventPendingIntent(pendingIntent);
    }

    private void getEventsSortByDate(long dateSelected) {
        Query query = eventDatabaseReference.orderByChild("eventStartDateTime");
        query.addValueEventListener(eventListener);
        /*query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsTodayDataArrayList.clear();
                getHolsCalendar(dateSelected);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventData eventData = dataSnapshot.getValue(EventData.class);
                    if (eventData.getEventPendingIntent() == null && eventData.getEventStartDateTime()  > System.currentTimeMillis()) {
                        scheduleEventNoti(eventData);
                        if (eventData.getEventReminderDateTime() != 0) {
                            scheduleEventReminderNoti(eventData);
                        }
                    }
                    if ((eventData.getEventStartDateEpoch()) <= dateSelected && (eventData.getEventEndDateEpoch()) + 28800000 >= dateSelected) {
                        eventsTodayDataArrayList.add(eventData);
                    }
                }
                eventsTodayAdapter.notifyDataSetChanged();
                if (eventsTodayDataArrayList.isEmpty()) {
                    eventsTodayProgressBar.setVisibility(View.GONE);
                    eventsTodayEmpty.setVisibility(View.VISIBLE);
                } else {
                    eventsTodayProgressBar.setVisibility(View.GONE);
                    eventsTodayEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            eventsDataArrayList.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                EventData eventData = dataSnapshot.getValue(EventData.class);
                if (date - eventData.getEventEndDateTime() <= 432000000) {
                    eventsDataArrayList.add(eventData);
                }
            }
            eventsAdapter.notifyDataSetChanged();
            if (eventsDataArrayList.isEmpty()) {
                eventsProgressBar.setVisibility(View.GONE);
                eventsEmpty.setVisibility(View.VISIBLE);
            } else {
                eventsProgressBar.setVisibility(View.GONE);
                eventsEmpty.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT)
                    .show();
        }
    };
    public void onEventsClick(int position) {
        EventData eventData = eventsDataArrayList.get(position);
        EventShowAndEditPopUp eventShowAndEditPopUp = new EventShowAndEditPopUp(eventData, getActivity(), eventDatabaseReference, user, storageRef, getParentFragmentManager());
        eventShowAndEditPopUp.showSavedEventPopupWindow(eventsRV, eventData);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Query query = reminderDatabaseReference.orderByChild("firstReminderDateTime");
        query.removeEventListener(reminderTodayListener);
        Query query1 = eventDatabaseReference.orderByChild("eventStartDateTime");
        query1.removeEventListener(eventTodayListener);
        Query query2 = eventHolsDatabaseReference.orderByChild("eventStartDateTime");
        query2.removeEventListener(eventsTodayHolsListener);
        binding = null;

    }

    View.OnClickListener monthlyPlannerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), MonthlyPlannerPage.class);
            startActivity(intent);
        }
    };
    View.OnClickListener settingsChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener aboutChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), MonthlyPlannerPage.class); //TODO change to ABOUT page
            startActivity(intent);
        }
    };

    View.OnClickListener focusModeChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), FocusModeActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener routineChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), RoutinePlannerPage.class);
            startActivity(intent);
        }
    };

    private void createReminderNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "reminders";
            String description = "reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("reminders", name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void createEventNotificationChannel() {
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
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}