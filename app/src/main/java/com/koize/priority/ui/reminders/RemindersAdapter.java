package com.koize.priority.ui.reminders;

import static java.time.Instant.ofEpochMilli;

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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {
    private ArrayList<RemindersData> remindersDataArrayList;
    private Context context;
    private RemindersClickInterface remindersClickInterface;
    private RemindersCheckBoxDeleteInterface remindersCheckBoxDeleteInterface;
    long daysSinceNow;
    long hoursSinceNow;
    long minutesSinceNow;
    long secondsSinceNow;
    private int[] colors = {Color.RED, Color.parseColor("#FFB4AB")};


    public RemindersAdapter(ArrayList<RemindersData> remindersDataArrayList, Context context, RemindersClickInterface remindersClickInterface, RemindersCheckBoxDeleteInterface remindersCheckBoxDeleteInterface) {
        this.remindersDataArrayList = remindersDataArrayList;
        this.context = context;
        this.remindersClickInterface = remindersClickInterface;
        this.remindersCheckBoxDeleteInterface = remindersCheckBoxDeleteInterface;
    }

    @NonNull
    @Override
    public RemindersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reminders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemindersAdapter.ViewHolder holder, int position) {
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

        if (remindersData.getFirstReminderDateTime() == 0) {
           holder.rowDateTimeCard.setVisibility(View.INVISIBLE);
        }

        holder.rowCardDateTime.setText(convertTimestampTop(remindersData.getFirstReminderDateTime()));
        holder.rowCardTimeLeft.setText(convertTimestampBot(remindersData.getFirstReminderDateTime()));

        if (secondsSinceNow <= 0 && minutesSinceNow <=0 && hoursSinceNow <= 0 && daysSinceNow <= 0){
            holder.rowCardTimeLeft.setText("Overdue");
            startChangingColors(holder.rowDateTimeCard);
        }

        //holder.rowCardDateTime.setText(remindersData.getReminderDateTime());
        //holder.rowCardTimeLeft.setText(remindersData.getReminderTimeLeft());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindersClickInterface.onRemindersClick(holder.getAdapterPosition());
            }

        });
        holder.rowCardCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindersCheckBoxDeleteInterface.onRemindersCheckBoxDelete(holder.getAdapterPosition());
            }
        });

    }

    public String convertTimestampTop(long timestamp) {
        timestamp = timestamp - 28800000;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Asia/Singapore"));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("h:mm a");
        String formattedDate = dateFormat.format(dateTime);

        // Calculate the number of days since the current date
        daysSinceNow = (timestamp - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);

        // If the timestamp is today, return "Today, 10.30am"
        if (daysSinceNow == 0) {
            return "Today, " + formattedDate;
        } else if (daysSinceNow == 1){
            return "Tomorrow, " + formattedDate;
        } else {
            return daysSinceNow + " Days, " + formattedDate;
        }
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
        private CheckBox rowCardCheckBox;
        private TextView rowCardTitle;
        private Chip rowCardCategoryChip;
        private Chip rowCardLocationChip;
        private TextView rowCardDateTime;
        private TextView rowCardTimeLeft;
        private MaterialCardView rowDateTimeCard;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            rowCardCheckBox = view.findViewById(R.id.check_row_reminders);
            rowCardTitle = view.findViewById(R.id.row_reminders_title);
            rowCardCategoryChip = view.findViewById(R.id.row_reminders_category_chip);
            rowCardLocationChip = view.findViewById(R.id.row_reminders_location_chip);
            rowCardDateTime = view.findViewById(R.id.row_reminders_date);
            rowCardTimeLeft = view.findViewById(R.id.row_reminder_timeleft);
            rowDateTimeCard = view.findViewById(R.id.row_reminders_date_time_card);

        }
    }

    public interface RemindersClickInterface {
        void onRemindersClick(int position);
    }

    public interface RemindersCheckBoxDeleteInterface {
        void onRemindersCheckBoxDelete(int position);
    }

}
