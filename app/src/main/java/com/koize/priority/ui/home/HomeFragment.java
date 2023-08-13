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
import com.koize.priority.NotiReceiver;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.focusmode.FocusModeActivity;
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
import java.util.Objects;

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
    //Reminders today recyclerview stuff

    RemindersData remindersData;
    RecyclerView remindersTodayRV;
    HomeRemindersTodayAdapter remindersAdapter;
    ArrayList<RemindersData> remindersDataArrayList;


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

            }
            else if (name == "") {
                reminderDatabaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/reminders");
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


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalDate today = LocalDate.now();

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

        query.addValueEventListener(new ValueEventListener() {
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
        });
        remindersAdapter.notifyDataSetChanged();
    }



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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
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