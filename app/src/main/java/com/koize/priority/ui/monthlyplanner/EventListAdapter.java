package com.koize.priority.ui.monthlyplanner;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.koize.priority.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>{
    private ArrayList<EventData> eventDataListArrayList;
    private Context context;
    private EventListClickInterface eventListClickInterface;
    private EventListExtraInterface eventListExtraInterface;
    long daysSinceNow;
    long hoursSinceNow;
    long minutesSinceNow;
    long secondsSinceNow;
    private int[] colors = {Color.RED, Color.parseColor("#FFB4AB")};
    private MaterialCardView rowDateTimeCard;

    public EventListAdapter(ArrayList<EventData> eventDataListArrayList, Context context, EventListClickInterface eventListClickInterface, EventListExtraInterface eventListExtraInterface) {
        this.eventDataListArrayList = eventDataListArrayList;
        this.context = context;
        this.eventListClickInterface = eventListClickInterface;
        this.eventListExtraInterface = eventListExtraInterface;
    }

    @NonNull
    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_events_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListAdapter.ViewHolder holder, int position) {
        EventData eventData = eventDataListArrayList.get(position);
        holder.eventTitle.setText(eventData.getEventTitle());
        holder.eventCategory.setText(eventData.getEventCategory().getCategoryTitle());
        holder.eventCategory.setChipBackgroundColor(ColorStateList.valueOf(eventData.getEventCategory().getCategoryColor()));
        holder.eventLocation.setText(eventData.getEventLocationName());
        if (holder.eventLocation.getText().toString().equals("")) {
            holder.eventLocation.setVisibility(View.GONE);
        } else {
            holder.eventLocation.setVisibility(View.VISIBLE);
        }
        if (eventData.getEventAllDay()) {
            holder.eventTime.setText("All Day");
        } else {
            holder.eventTime.setText(convertTimestampToTimeRange(eventData.getEventStartDateTime(), eventData.getEventEndDateTime(), eventData.getEventAllDay()));
        }
        holder.eventDaysLeft.setText(convertTimestampToDaysLeft(eventData.getEventStartDateTime()));
        holder.eventDateRange.setText(convertTimestampToDateRange(eventData.getEventStartDateTime(), eventData.getEventEndDateTime()));
    }

    public String convertTimestampToDaysLeft(long timestamp) {
        // Calculate the number of days since the current date
        daysSinceNow = (timestamp - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);

        // If the timestamp is today, return "Today, 10.30am"
        if (daysSinceNow <= 0) {
            return "Today";
        } else if (daysSinceNow == 1){
            return "Tomorrow";
        } else {
            return daysSinceNow + " Days";
        }
    }

    public String convertTimestampToDateRange(long timestamp1, long timestamp2) {

        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp1), ZoneId.systemDefault());
        LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        String formattedStartDate = formatter.format(dateTime1);
        String formattedEndDate = formatter.format(dateTime2);

        // Calculate the number of hours since the current time
        /* hoursSinceNow = (timestamp1 - System.currentTimeMillis()) / (3600 * 1000);
        minutesSinceNow = (timestamp1 - System.currentTimeMillis()) / (60 * 1000);
        secondsSinceNow = (timestamp1 - System.currentTimeMillis()) / (1000);

        // Get the hour of the timestamp
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
        return formattedStartDate + " - " + formattedEndDate;
    }

    public String convertTimestampToTimeRange(long timestamp1, long timestamp2, boolean isAllDay) {

        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp1), ZoneId.systemDefault());
        LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedTime = formatter.format(dateTime1);
        String formattedTime2 = formatter.format(dateTime2);

        // Calculate the number of hours since the current time
        hoursSinceNow = (timestamp1 - System.currentTimeMillis()) / (3600 * 1000);
        minutesSinceNow = (timestamp1 - System.currentTimeMillis()) / (60 * 1000);
        secondsSinceNow = (timestamp1 - System.currentTimeMillis()) / (1000);

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

    public int getItemCount() {
        return eventDataListArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView eventTitle;
        private Chip eventCategory;
        private Chip eventLocation;
        private Chip eventTime;
        private MaterialCardView eventDateTimeCard;
        private TextView eventDaysLeft;
        private TextView eventDateRange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            eventTitle = view.findViewById(R.id.row_events_list_title);
            eventCategory = view.findViewById(R.id.row_events_list_category);
            eventLocation = view.findViewById(R.id.row_events_list_location);
            eventTime = view.findViewById(R.id.row_events_list_time);
            eventDateTimeCard = view.findViewById(R.id.row_category_list_card);
            eventDaysLeft = view.findViewById(R.id.row_events_list_daysleft);
            eventDateRange = view.findViewById(R.id.row_events_list_date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventListClickInterface.onEventListClick(getAdapterPosition());
                }
            });
        }
    }

    public interface EventListClickInterface {
        void onEventListClick(int position);
    }

    public interface EventListExtraInterface {
        void onEventListExtraClick(int position);
    }

}
