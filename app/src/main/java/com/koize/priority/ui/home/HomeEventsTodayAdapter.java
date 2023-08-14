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
import com.koize.priority.ui.reminders.RemindersData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeEventsTodayAdapter extends RecyclerView.Adapter<HomeEventsTodayAdapter.ViewHolder> {
    private ArrayList<EventData> eventsTodayDataArrayList;
    private Context context;
    private EventsTodayClickInterface eventsTodayClickInterface;
    long daysSinceNow;
    long hoursSinceNow;
    long minutesSinceNow;
    long secondsSinceNow;
    private int[] colors = {Color.RED, Color.parseColor("#FFB4AB")};


    public HomeEventsTodayAdapter(ArrayList<EventData> eventsTodayDataArrayList, Context context, EventsTodayClickInterface eventsTodayClickInterface) {
        this.eventsTodayDataArrayList = eventsTodayDataArrayList;
        this.context = context;
        this.eventsTodayClickInterface = eventsTodayClickInterface;
    }

    @NonNull
    @Override
    public HomeEventsTodayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_events_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeEventsTodayAdapter.ViewHolder holder, int position) {
        EventData eventData = eventsTodayDataArrayList.get(position);
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
        holder.rowCardTimeLeft.setText(convertTimestampBot(eventData.getEventStartDateTime()));
        if (eventData.getEventAllDay() == true) {
            holder.rowTimeCard.setVisibility(View.GONE);
        } else {
            holder.rowCardTimeLeft.setVisibility(View.VISIBLE);
            holder.rowTimeLeftCard.setVisibility(View.VISIBLE);
            holder.rowTimeCard.setVisibility(View.VISIBLE);
            holder.rowCardTime.setText(convertTimestampTop(eventData.getEventStartDateTime()));
        }

        if (secondsSinceNow < 0 && minutesSinceNow <=0 && hoursSinceNow <= 0 && daysSinceNow <= 0){
            holder.rowCardTimeLeft.setText("Now");
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsTodayClickInterface.onEventTodayClick(holder.getAdapterPosition());
            }

        });
    }

    public String convertTimestampTop(long timestamp) {
        timestamp = timestamp - 28800000;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Asia/Singapore"));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("h:mm a");
        String formattedDate = dateFormat.format(dateTime);

        // Calculate the number of days since the current date
        return formattedDate;
    }

    public String convertTimestampBot(long timestamp) {
        timestamp = timestamp - 28800000;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = formatter.format(dateTime);

        // Calculate the number of hours since the current time
        hoursSinceNow = (timestamp - System.currentTimeMillis()) / (3600 * 1000);
        minutesSinceNow = (timestamp - System.currentTimeMillis()) / (60 * 1000);
        secondsSinceNow = (timestamp - System.currentTimeMillis()) / (1000);

        // Get the hour of the timestamp
        int timestampHour = dateTime.getHour();

        if (hoursSinceNow == 0) {
            return minutesSinceNow + "min";
        } else {
            return hoursSinceNow + "hr" ;
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
        return eventsTodayDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView rowCardTitle;
        private Chip rowCardCategoryChip;
        private Chip rowCardLocationChip;
        private Chip rowCardEventTypeChip;
        private TextView rowCardTimeLeft;
        private TextView rowCardTime;
        private MaterialCardView rowTimeLeftCard;

        private MaterialCardView rowTimeCard;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            rowCardTitle = view.findViewById(R.id.row_home_events_today_title);
            rowCardCategoryChip = view.findViewById(R.id.row_home_events_today_category_chip);
            rowCardLocationChip = view.findViewById(R.id.row_home_events_today_location_chip);
            rowCardEventTypeChip = view.findViewById(R.id.row_home_events_today_event_type_chip);
            rowCardTimeLeft = view.findViewById(R.id.row_home_events_today_time_left);
            rowCardTime = view.findViewById(R.id.row_home_events_today_time);
            rowTimeLeftCard = view.findViewById(R.id.row_home_events_today_time_left_card);
            rowTimeCard = view.findViewById(R.id.row_home_events_today_time_card);
        }
    }

    public interface EventsTodayClickInterface {
        void onEventTodayClick(int position);
    }



}
