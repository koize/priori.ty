package com.koize.priority.ui.reminders;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.NotiReceiver;
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
import java.util.Random;

public class RemindersFragment extends Fragment {

    private FragmentRemindersBinding binding;
    private FloatingActionButton addReminderButton;
    private MaterialTextView reminderEmpty;
    public int firstReminderTimeHr;
    public int firstReminderTimeMin;
    public int secondReminderTimeHr;
    public int secondReminderTimeMin;

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
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;
    Switch reminderAutoSortCheckBox;
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RemindersViewModel remindersViewModel =
                new ViewModelProvider(this).get(RemindersViewModel.class);

        binding = FragmentRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addReminderButton = root.findViewById(R.id.button_reminder_add);
        addReminderButton.setOnClickListener(addReminderListener);
        reminderAutoSortCheckBox = root.findViewById(R.id.reminder_autosort_checkbox);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name!="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/reminders");

            }
            else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/reminders");
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
        progressBar = root.findViewById(R.id.reminder_loading);
        reminderEmpty.setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createNotificationChannel();
        remindersDataArrayList = new ArrayList<>();
        remindersAdapter = new RemindersAdapter(remindersDataArrayList, getContext(), this::onRemindersClick, this::onRemindersCheckBoxDelete);
        reminderRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        reminderRV.setAdapter(remindersAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {

                    @Override
                    public int getMovementFlags(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder) {

                        return makeMovementFlags(
                                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                ItemTouchHelper.END | ItemTouchHelper.START
                        );
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        int draggedItemIndex = viewHolder.getAdapterPosition();
                        int targetIndex = target.getAdapterPosition();
                        Collections.swap(remindersDataArrayList, draggedItemIndex, targetIndex);
                        remindersAdapter.notifyItemMoved(draggedItemIndex, targetIndex);
                        return false;
                    }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }
);
        touchHelper.attachToRecyclerView(reminderRV);
        /*reminderAutoSortCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (reminderAutoSortCheckBox.isChecked()) {
                    getRemindersSortByDate();
                    SharedPreferences.Editor editor= getContext().getSharedPreferences("reminder_auto_sort", MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                } else if (!reminderAutoSortCheckBox.isChecked())
                    getRemindersSortByName();
                    SharedPreferences.Editor editor= getContext().getSharedPreferences("reminder_auto_sort", MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                }
        });
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("reminder_auto_sort", MODE_PRIVATE);
        reminderAutoSortCheckBox.setChecked(true);
        getRemindersSortByDate();
        reminderAutoSortCheckBox.setChecked(sharedPreferences.getBoolean("value",true));*/
        if (user != null) {
            getRemindersSortByDate();
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
            reminderEmpty.setVisibility(View.VISIBLE);
            reminderEmpty.setText("Sign in to create reminders!");
            progressBar.setVisibility(View.GONE);
        }
        reminderAutoSortCheckBox.setVisibility(View.GONE); //DONT MAKE VISIBLE IT WILL BREAK THE APP
        //RecyclerViewRefresher recyclerViewRefresher = new RecyclerViewRefresher(reminderRV);
        //recyclerViewRefresher.startRefreshing();

    }
    /*private void getRemindersSortByName() {
        remindersDataArrayList.clear();
        Query query = databaseReference.orderByChild("reminderTitle");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                remindersDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RemindersData remindersData = dataSnapshot.getValue(RemindersData.class);
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
        remindersAdapter.notifyDataSetChanged();
    }*/

    public void getRemindersSortByDate() {

        remindersDataArrayList.clear();
        Query query = databaseReference.orderByChild("firstReminderDateTime");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                remindersDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RemindersData remindersData = dataSnapshot.getValue(RemindersData.class);
                    remindersDataArrayList.add(remindersData);

                    if (remindersData.getFirstReminderDateTime() != 0 && remindersData.getReminderPendingIntent() == null && remindersData.getFirstReminderDateTime() - 28800000 > System.currentTimeMillis()) {
                        scheduleNoti(remindersData);
                    }
                }
                remindersAdapter.notifyDataSetChanged();
                if (remindersDataArrayList.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    reminderEmpty.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    reminderEmpty.setVisibility(View.GONE);
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

    public void scheduleNoti(RemindersData remindersData) {
        long reminderDateTime = remindersData.getFirstReminderDateTime() - 28800000;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderDateTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm:a");
        String formattedTime = formatter.format(dateTime);
        Notification.Builder builder = new Notification.Builder(getActivity().getApplicationContext(), "reminders");
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
            RemindersData remindersData = new RemindersData();
            RemindersNewPopUp remindersNewPopUp = new RemindersNewPopUp(remindersData, getParentFragmentManager(), user, databaseReference, v);
            remindersNewPopUp.showPopupWindow(v);
        }
    };

    public void onRemindersClick(int position) {
        RemindersData remindersData = remindersDataArrayList.get(position);
        RemindersEditPopUp remindersEditPopUp = new RemindersEditPopUp(remindersData, getParentFragmentManager(), user, databaseReference, reminderRV);
        remindersEditPopUp.showPopupWindowEdit(reminderRV, remindersData);
    }

    public void onRemindersCheckBoxDelete(int position) {
        Snackbar.make(reminderRV, "Reminder: " + remindersDataArrayList.get(position).getReminderTitle() + " completed!", Snackbar.LENGTH_SHORT)
                .show();
        if (remindersDataArrayList.get(position).getSecondReminderDateTime() != 0) {
            remindersDataArrayList.get(position).setFirstReminderDateTime(remindersDataArrayList.get(position).getSecondReminderDateTime());
            remindersDataArrayList.get(position).setSecondReminderDateTime(0);
            try {
                databaseReference.child(remindersDataArrayList.get(position).getReminderTextId()).setValue(remindersDataArrayList.get(position));
            }
            catch (Exception e) {
                Log.e(TAG, "onRemindersCheckBoxDelete: ",e.getCause().getCause() );
                Snackbar.make(reminderRV, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }

            remindersDataArrayList.clear();
        } else {
            Intent intent = new Intent(getContext(), NotiReceiver.class);
            intent.putExtra(NotiReceiver.NOTIFICATION, remindersDataArrayList.get(position).getReminderTitle());
            intent.putExtra("id", remindersDataArrayList.get(position).getReminderId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), remindersDataArrayList.get(position).getReminderId(), intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            databaseReference.child(remindersDataArrayList.get(position).getReminderTextId()).removeValue();
            remindersDataArrayList.clear();
        }

    }


    private void createNotificationChannel() {
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
}




