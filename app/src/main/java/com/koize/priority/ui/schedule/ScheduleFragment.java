package com.koize.priority.ui.schedule;

import static com.koize.priority.ui.schedule.CalendarAdapter.daysInWeekArray;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.R;
import com.koize.priority.databinding.FragmentScheduleBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koize.priority.ui.journal.JournalData;
import com.koize.priority.ui.reminders.RemindersData;
import com.koize.priority.ui.reminders.RemindersEditPopUp;


public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    private FragmentScheduleBinding binding;
    private Button buttonnext;
    private Button buttonprev;

    private FloatingActionButton addScheduleButton;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText editNotes;
    ArrayList<ScheduleData> scheduleDataArrayList;
    ScheduleAdapter scheduleAdapter;
    RecyclerView scheduleRV;
    LocalDate PressOnDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScheduleViewModel scheduleViewModel =
                new ViewModelProvider(this).get(ScheduleViewModel.class);

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarRecyclerView = root.findViewById(R.id.calendarRecyclerView);
        monthYearText = root.findViewById(R.id.monthYearTV);

        buttonnext = root.findViewById(R.id.buttonnext);
        buttonprev = root.findViewById(R.id.buttonprev);

        editNotes = root.findViewById(R.id.schedule_Notes);

        buttonprev.setOnClickListener(buttonprevListener);
        buttonnext.setOnClickListener(buttonnextListener);

        addScheduleButton = root.findViewById(R.id.button_schedule_add);
        addScheduleButton.setOnClickListener(addScheduleListener);

        scheduleRV = root.findViewById(R.id.scheduleRV);

        CalendarAdapter.selectedDate = LocalDate.now();
        setWeekView();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name!="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/schedule");

            }
            else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/schedule");
            }
            else {
                throw new IllegalStateException("Unexpected value: " + name);
            }

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }
        scheduleDataArrayList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleDataArrayList,getContext(),this:: onScheduleClick);
        scheduleRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        scheduleRV.setAdapter(scheduleAdapter);
        getSchedule();

        return root;
    }

    private void getSchedule() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ScheduleData scheduleData = dataSnapshot.getValue(ScheduleData.class);
                    scheduleDataArrayList.add(scheduleData);
                }
                //ScheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    View.OnClickListener addScheduleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String notes = editNotes.getText().toString();
            SchedulePopUp schedulePopUp = new SchedulePopUp();
            schedulePopUp.showPopupWindow(v, getParentFragmentManager(),PressOnDate,notes,user,databaseReference);
        }
    };



    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarAdapter.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarAdapter.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days,  this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(),7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    public void previousWeekAction() {
        CalendarAdapter.selectedDate = CalendarAdapter.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction() {
        CalendarAdapter.selectedDate = CalendarAdapter.selectedDate.plusWeeks(1);
        setWeekView();
    }

    View.OnClickListener buttonprevListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            previousWeekAction();
              }
    };

    View.OnClickListener buttonnextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nextWeekAction();
        }
    };

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarAdapter.selectedDate = date;
        PressOnDate = date;
        setWeekView();
    }

    public static String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onScheduleClick(int position) {
        //RemindersData remindersData = remindersDataArrayList.get(position);
        //RemindersEditPopUp remindersEditPopUp = new RemindersEditPopUp(remindersData, getParentFragmentManager(), user, databaseReference, reminderRV);
        //remindersEditPopUp.showPopupWindowEdit(reminderRV, remindersData);
    }
}