package com.koize.priority.ui.reminders;

import android.content.Context;
import android.content.res.ColorStateList;
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

import java.util.ArrayList;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {
    private ArrayList<RemindersData> remindersDataArrayList;
    private Context context;
    private RemindersClickInterface remindersClickInterface;
    private RemindersCheckBoxDeleteInterface remindersCheckBoxDeleteInterface;

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

    public int getItemCount() {
        return remindersDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private CheckBox rowCardCheckBox;
        private TextView rowCardTitle;
        private Chip rowCardCategoryChip;
        private Chip rowCardLocationChip;
        private TextView rowCardDateTime;
        private TextView rowCardTimeLeft;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            rowCardCheckBox = view.findViewById(R.id.check_row_reminders);
            rowCardTitle = view.findViewById(R.id.row_reminders_title);
            rowCardCategoryChip = view.findViewById(R.id.row_reminders_category_chip);
            rowCardLocationChip = view.findViewById(R.id.row_reminders_location_chip);
            rowCardDateTime = view.findViewById(R.id.row_reminders_date);
            rowCardTimeLeft = view.findViewById(R.id.row_reminder_timeleft);

        }
    }

    public interface RemindersClickInterface {
        void onRemindersClick(int position);
    }

    public interface RemindersCheckBoxDeleteInterface {
        void onRemindersCheckBoxDelete(int position);
    }

}
