package com.koize.priority.ui.routineplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.koize.priority.R;

import java.util.ArrayList;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.ViewHolder> {
    private ArrayList<HabitsData> habitsDataArrayList;
    private Context context;
    private HabitsListener HabitsListener;
    private HabitsListener2 HabitsListener2;

    public HabitsAdapter(ArrayList<HabitsData> habitsDataArrayList, Context context, HabitsListener HabitsListener, HabitsListener2 HabitsListener2) {
        this.habitsDataArrayList = habitsDataArrayList;
        this.context = context;
        this.HabitsListener = HabitsListener;
        this.HabitsListener2 = HabitsListener2  ;
    }

    @NonNull
    @Override
    public HabitsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_habits, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitsData habitsData = habitsDataArrayList.get(position);
        holder.rowCardTitle.setText(habitsData.getHabitsTitle());
        //int duration_int = habitsData.getHabitsDuration();
        //String duration_string = duration_int.toString();

        holder.rowCardDuration.setText(habitsData.getHabitsDuration()+"m");
        //or holder.rowCardDuration.setText(Integer.toString(habitsData.getHabitsDuration()));

        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitsListener.onHabitClick(holder.getAdapterPosition());
            }

        });

        holder.rowCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HabitsListener2.onHabitLongClick(holder.getAdapterPosition());
                return true;
            }

        });
    }

    @Override
    public int getItemCount() {
        return habitsDataArrayList.size();
    }

    public interface HabitsListener {
        void onHabitClick(int position);
    }

    public interface HabitsListener2 {
        void onHabitLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        MaterialCardView rowCard;
        TextView rowCardTitle;
        TextView rowCardDuration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_habits_card);
            rowCardTitle = itemView.findViewById(R.id.row_habits_title);
            rowCardDuration = itemView.findViewById(R.id.row_habits_duration);
        }
        @Override
        public boolean onLongClick(View v) {

            return false;
        }

    }

}
