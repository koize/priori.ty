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

public class EventCalenderAdapter extends RecyclerView.Adapter<EventCalenderAdapter.ViewHolder> {
private ArrayList<EventData> eventDataCalenderArrayList;
    private Context context;
    private EventCalenderClickInterface eventCalenderClickInterface;
    private EventCalenderExtraInterface eventCalenderExtraInterface;
    long daysSinceNow;
    long hoursSinceNow;
    long minutesSinceNow;
    long secondsSinceNow;
    private int[] colors = {Color.RED, Color.parseColor("#FFB4AB")};
    private MaterialCardView rowDateTimeCard;

    public EventCalenderAdapter(ArrayList<EventData> eventDataCalenderArrayList, Context context, EventCalenderClickInterface eventCalenderClickInterface, EventCalenderExtraInterface eventCalenderExtraInterface) {
        this.eventDataCalenderArrayList = eventDataCalenderArrayList;
        this.context = context;
        this.eventCalenderClickInterface = eventCalenderClickInterface;
        this.eventCalenderExtraInterface = eventCalenderExtraInterface;
    }

    @NonNull
    @Override
    public EventCalenderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_events_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventCalenderAdapter.ViewHolder holder, int position) {
        EventData eventData = eventDataCalenderArrayList.get(position);
        holder.eventTitle.setText(eventData.getEventTitle());
        holder.eventCategory.setText(eventData.getEventCategory().getCategoryTitle());
        holder.eventCategory.setChipBackgroundColor(ColorStateList.valueOf(eventData.getEventCategory().getCategoryColor()));
        holder.eventLocation.setText(eventData.getEventLocationName());
        if (holder.eventLocation.getText().toString().equals("")) {
            holder.eventLocation.setVisibility(View.GONE);
        } else {
            holder.eventLocation.setVisibility(View.VISIBLE);
        }
        holder.eventTime.setText(convertTimestampToTimeRange(eventData.getEventStartDateTime(), eventData.getEventEndDateTime()));
        holder.eventType.setText(eventData.getEventType());

    }

    public String convertTimestampToTimeRange(long timestamp1, long timestamp2) {

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
        return formattedTime + " - " + formattedTime2;
    }

    @Override
    public int getItemCount() {
        return eventDataCalenderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eventTitle;
        private Chip eventCategory;
        private TextView eventLocation;
        private TextView eventTime;
        private TextView eventType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.row_events_calendar_title);
            eventCategory = itemView.findViewById(R.id.row_events_calendar_category);
            eventLocation = itemView.findViewById(R.id.row_events_calendar_location);
            eventTime = itemView.findViewById(R.id.row_events_calendar_time);
            eventType = itemView.findViewById(R.id.row_events_calendar_type);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventCalenderClickInterface.onEventCalenderClick(getAdapterPosition());
                }
            });
        }
    }

    public interface EventCalenderClickInterface {
        void onEventCalenderClick(int position);
    }

    public interface EventCalenderExtraInterface {
        void onEventCalenderExtraClick(int position);
    }

}
