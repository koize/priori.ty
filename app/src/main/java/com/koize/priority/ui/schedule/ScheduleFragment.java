package com.koize.priority.ui.schedule;

import static com.koize.priority.ui.schedule.CalendarAdapter.daysInWeekArray;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koize.priority.R;
import com.koize.priority.ReminderPopUp;
import com.koize.priority.SchedulePopUp;
import com.koize.priority.databinding.FragmentScheduleBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    private FragmentScheduleBinding binding;
    private Button buttonnext;
    private Button buttonprev;

    private FloatingActionButton addScheduleButton;

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

        buttonprev.setOnClickListener(buttonprevListener);
        buttonnext.setOnClickListener(buttonnextListener);

        addScheduleButton = root.findViewById(R.id.button_schedule_add);
        addScheduleButton.setOnClickListener(addScheduleListener);

        CalendarAdapter.selectedDate = LocalDate.now();
        setWeekView();

        return root;
    }

    View.OnClickListener addScheduleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SchedulePopUp schedulePopUp = new SchedulePopUp();
            schedulePopUp.showPopupWindow(v);        }
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

    View.OnClickListener addReminderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReminderPopUp reminderPopUp = new ReminderPopUp();
            reminderPopUp.showPopupWindow(v);        }
    };


    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarAdapter.selectedDate = date;
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
}