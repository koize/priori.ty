package com.koize.priority.ui.schedule;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

import com.koize.priority.R;


public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final View parentView;
    public final TextView calendarText;
    public final CardView calendarCellCard;
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;
    private final ArrayList<LocalDate> days;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {

        super(itemView);
        calendarCellCard = itemView.findViewById(R.id.calendarCellCard);
        calendarText = itemView.findViewById(R.id.cellDayText);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days = days;
    }

    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}
