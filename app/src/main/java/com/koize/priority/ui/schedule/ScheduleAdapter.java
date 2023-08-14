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
        ScheduleData scheduleData = scheduleDataArrayList.get(position);
        holder.rowCardTime.setText(scheduleData.getStartTimeHr()+":"+scheduleData.getStartTimeMin()+ " - "+scheduleData.getEndTimeHr()+":"+scheduleData.getEndTimeMin());
        holder.icon.setImageResource(R.drawable.baseline_access_time_24);
        holder.rowCardTitle.setText(scheduleData.getScheduleTitle());
        holder.rowCardCategoryChip.setText(scheduleData.getScheduleCategory().getCategoryTitle());
        holder.rowCardCategoryChip.setChipBackgroundColor(ColorStateList.valueOf(scheduleData.getScheduleCategory().getCategoryColor()));

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



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            rowCardTime = view.findViewById(R.id.schedule_row_timing);
            rowCardTitle = view.findViewById(R.id.schedule_row_activity);
            rowCardCategoryChip = view.findViewById(R.id.row_schedule_category_chip);
            rowCard = view.findViewById(R.id.schedule_row_card);
            icon = view.findViewById(R.id.row_schedule_card_icon);

        }
    }
    public interface ScheduleListener {
        void onScheduleClick(int position);
    }
}
