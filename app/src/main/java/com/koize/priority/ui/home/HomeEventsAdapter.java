package com.koize.priority.ui.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.koize.priority.R;
import com.koize.priority.ui.monthlyplanner.EventData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.ViewHolder> {
    private ArrayList<EventData> eventsDataArrayList;
    private Context context;
    private EventsClickInterface eventsClickInterface;
    long daysSinceNow;
    long hoursSinceNow;
    long minutesSinceNow;
    long secondsSinceNow;
    private int[] colors = {Color.RED, Color.parseColor("#FFB4AB")};


    public HomeEventsAdapter(ArrayList<EventData> eventsDataArrayList, Context context, EventsClickInterface eventsClickInterface) {
        this.eventsDataArrayList = eventsDataArrayList;
        this.context = context;
        this.eventsClickInterface = eventsClickInterface;
    }

    @NonNull
    @Override
    public HomeEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_events, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeEventsAdapter.ViewHolder holder, int position) {
        EventData eventData = eventsDataArrayList.get(position);
        holder.rowCardTitle.setText(eventData.getEventTitle());
        holder.rowCardCategoryChip.setText(eventData.getEventCategory().getCategoryTitle());
        holder.rowCardCategoryChip.setChipBackgroundColor(ColorStateList.valueOf(eventData.getEventCategory().getCategoryColor()));
        holder.rowCardLocationChip.setText(eventData.getEventLocationName());

        if (holder.rowCardLocationChip.getText().toString().equals("")){
            holder.rowCardLocationChip.setVisibility(View.GONE);
        } else {
            holder.rowCardLocationChip.setVisibility(View.VISIBLE);
        }
        holder.rowCardEventTypeChip.setText(eventData.getEventType());
        holder.rowCardTime.setText(convertTimestampTop(eventData.getEventStartDateTime()));
        holder.rowCardDay.setText(convertTimestampToDay(eventData.getEventStartDateTime(), eventData.getEventEndDateTime()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsClickInterface.onEventClick(holder.getAdapterPosition());
            }

        });
    }

    public String convertTimestampTop(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Asia/Singapore"));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("h:mm a");
        String formattedDate = dateFormat.format(dateTime);

        // Calculate the number of days since the current date
        return formattedDate;
    }

    public String convertTimestampToDay(long timestamp1, long timestamp2) {

        LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp1), ZoneId.systemDefault());
        LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        String formattedTime1 = formatter.format(dateTime1);
        String formattedTime2 = formatter.format(dateTime2);

        if (formattedTime1.equals(formattedTime2)){
            return formattedTime1;
        } else {
            return formattedTime1 + " - " + formattedTime2;
        }
    }
    public void startChangingColors(MaterialCardView rowDateTimeCard) {
        CountDownTimer timer = new CountDownTimer(600000, 750) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Get the current color index
                int currentColorIndex = (int) (millisUntilFinished / 750) % colors.length;

                // Set the cardview background color
                rowDateTimeCard.setCardBackgroundColor((colors[currentColorIndex]));
            }

            @Override
            public void onFinish() {
                // Start the timer again
                startChangingColors(rowDateTimeCard);
            }
        };

        timer.start();
    }

    public int getItemCount() {
        return eventsDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView rowCardTitle;
        private Chip rowCardCategoryChip;
        private Chip rowCardLocationChip;
        private Chip rowCardEventTypeChip;
        private TextView rowCardTime;
        private TextView rowCardDay;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            rowCardTitle = view.findViewById(R.id.row_home_events_title);
            rowCardCategoryChip = view.findViewById(R.id.row_home_events_category_chip);
            rowCardLocationChip = view.findViewById(R.id.row_home_events_location_chip);
            rowCardEventTypeChip = view.findViewById(R.id.row_home_events_event_type_chip);
            rowCardTime = view.findViewById(R.id.row_home_events_time);
            rowCardDay = view.findViewById(R.id.row_home_events_day);
        }
    }

    public interface EventsClickInterface {
        void onEventClick(int position);
    }



}
