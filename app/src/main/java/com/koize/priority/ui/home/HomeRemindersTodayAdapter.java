package com.koize.priority.ui.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.koize.priority.R;
import com.koize.priority.ui.reminders.RemindersData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeRemindersTodayAdapter extends RecyclerView.Adapter<HomeRemindersTodayAdapter.ViewHolder> {
    private ArrayList<RemindersData> remindersDataArrayList;
    private Context context;
    private RemindersClickInterface remindersClickInterface;
    private RemindersCheckBoxDeleteInterface remindersCheckBoxDeleteInterface;
    long daysSinceNow;
    long hoursSinceNow;
    long minutesSinceNow;
    long secondsSinceNow;
    private int[] colors = {Color.RED, Color.parseColor("#FFB4AB")};


    public HomeRemindersTodayAdapter(ArrayList<RemindersData> remindersDataArrayList, Context context, RemindersClickInterface remindersClickInterface) {
        this.remindersDataArrayList = remindersDataArrayList;
        this.context = context;
        this.remindersClickInterface = remindersClickInterface;
    }

    @NonNull
    @Override
    public HomeRemindersTodayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_reminders_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRemindersTodayAdapter.ViewHolder holder, int position) {
        RemindersData remindersData = remindersDataArrayList.get(position);
        holder.rowCardTitle.setText(remindersData.getReminderTitle());
        holder.rowCardCategoryChip.setText(remindersData.getReminderCategory().getCategoryTitle());
        holder.rowCardCategoryChip.setChipBackgroundColor(ColorStateList.valueOf(remindersData.getReminderCategory().getCategoryColor()));
        holder.rowCardLocationChip.setText(remindersData.getReminderLocationName());

        if (holder.rowCardLocationChip.getText().toString().equals("")){
            holder.rowCardLocationChip.setVisibility(View.GONE);
        } else {
            holder.rowCardLocationChip.setVisibility(View.VISIBLE);
        }

        holder.rowCardTimeLeft.setText(convertTimestampBot(remindersData.getFirstReminderDateTime()));
        holder.rowCardTime.setText(convertTimestampTop(remindersData.getFirstReminderDateTime()));

        if (secondsSinceNow < 0 && minutesSinceNow <=0 && hoursSinceNow <= 0 && daysSinceNow <= 0){
            holder.rowCardTimeLeft.setText("Overdue");
            startChangingColors(holder.rowTimeLeftCard);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindersClickInterface.onRemindersClick(holder.getAdapterPosition());
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
        return remindersDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView rowCardTitle;
        private Chip rowCardCategoryChip;
        private Chip rowCardLocationChip;
        private TextView rowCardTimeLeft;
        private TextView rowCardTime;
        private MaterialCardView rowTimeLeftCard;

        private MaterialCardView rowTimeCard;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            rowCardTitle = view.findViewById(R.id.row_home_reminders_today_title);
            rowCardCategoryChip = view.findViewById(R.id.row_home_reminders_today_category_chip);
            rowCardLocationChip = view.findViewById(R.id.row_home_reminders_today_location_chip);
            rowCardTimeLeft = view.findViewById(R.id.row_home_reminders_today_time_left);
            rowCardTime = view.findViewById(R.id.row_home_reminders_today_time);
            rowTimeLeftCard = view.findViewById(R.id.row_home_reminders_today_time_left_card);
            rowTimeCard = view.findViewById(R.id.row_home_reminders_today_time_card);
        }
    }

    public interface RemindersClickInterface {
        void onRemindersClick(int position);
    }

    public interface RemindersCheckBoxDeleteInterface {
        void onRemindersCheckBoxDelete(int position);
    }

}
