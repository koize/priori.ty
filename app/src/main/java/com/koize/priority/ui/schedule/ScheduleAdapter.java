package com.koize.priority.ui.schedule;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.koize.priority.R;
import com.koize.priority.ui.monthlyplanner.ImageChooser;
import com.koize.priority.ui.reminders.RemindersAdapter;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    ArrayList<ScheduleData> scheduleDataArrayList;
    private Context context;
    private ScheduleListener ScheduleListener;

    public ScheduleAdapter(ArrayList<ScheduleData> scheduleDataArrayList,Context context,ScheduleListener ScheduleListener){
        this.scheduleDataArrayList = scheduleDataArrayList;
        this.context = context;
        this.ScheduleListener = ScheduleListener;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        String StartTimeHr;
        String StartTimeMin;
        String EndTimeMin;
        String EndTimeHr;
        ScheduleData scheduleData = scheduleDataArrayList.get(position);
        if(scheduleData.getEndTimeMin()<10){
            EndTimeMin =  "0"+ Integer.toString(scheduleData.getEndTimeMin());
        }else{
            EndTimeMin = Integer.toString(scheduleData.getEndTimeMin());
        }
        if(scheduleData.getEndTimeHr()<10){
            EndTimeHr =  "0"+ Integer.toString(scheduleData.getEndTimeHr());
        }else{
            EndTimeHr = Integer.toString(scheduleData.getEndTimeHr());
        }
        if(scheduleData.getStartTimeHr()<10){
            StartTimeHr =  "0"+ Integer.toString(scheduleData.getStartTimeHr());
        }else{
            StartTimeHr = Integer.toString(scheduleData.getStartTimeHr());
        }
        if(scheduleData.getStartTimeMin()<10){
            StartTimeMin =  "0"+ Integer.toString(scheduleData.getStartTimeMin());
        }else{
            StartTimeMin = Integer.toString(scheduleData.getStartTimeMin());
        }
        String FinalTime = StartTimeHr + ":" + StartTimeMin+ " - " + EndTimeHr + ":"+ EndTimeMin;
        holder.rowCardTime.setText(FinalTime);
        holder.icon.setImageResource(R.drawable.baseline_access_time_24);
        holder.rowCardTitle.setText(scheduleData.getScheduleTitle());
        holder.rowCardCategoryChip.setText(scheduleData.getScheduleCategory().getCategoryTitle());
        holder.rowCardCategoryChip.setChipBackgroundColor(ColorStateList.valueOf(scheduleData.getScheduleCategory().getCategoryColor()));

        long totalDuration = scheduleData.getEndTimeTime() - scheduleData.getStartTimeTime();
        long totalSeconds = totalDuration / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        holder.duration.setText(hours+"hr "+ minutes+"min");

        holder.rowCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ScheduleListener.onScheduleClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView rowCardTime;
        private TextView rowCardTitle;
        private Chip rowCardCategoryChip;
        private MaterialCardView rowCard;
        private ImageView icon;
        private TextView duration;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            rowCardTime = view.findViewById(R.id.schedule_row_timing);
            rowCardTitle = view.findViewById(R.id.schedule_row_activity);
            rowCardCategoryChip = view.findViewById(R.id.row_schedule_category_chip);
            rowCard = view.findViewById(R.id.schedule_row_card);
            icon = view.findViewById(R.id.row_schedule_card_icon);
            duration = view.findViewById(R.id.schedule_row_duration);
        }
    }
    public interface ScheduleListener {
        void onScheduleClick(int position);
    }
}
