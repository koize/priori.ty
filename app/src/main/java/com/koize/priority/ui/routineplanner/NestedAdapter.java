package com.koize.priority.ui.routineplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.koize.priority.R;

import java.util.ArrayList;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.ViewHolder> {
    private ArrayList<HabitsData>  routineHabitsArrayList;
    private Context context;

    public NestedAdapter(ArrayList<HabitsData> routineHabitsArrayList, Context context){
        this.routineHabitsArrayList = routineHabitsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public NestedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_nestedrv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedAdapter.ViewHolder holder, int position) {
        HabitsData habitsData = routineHabitsArrayList.get(position);
        holder.rowCardTitle.setText(habitsData.getHabitsTitle());
    }

    @Override
    public int getItemCount() {
        if(routineHabitsArrayList != null){
            return routineHabitsArrayList.size();
        }else{
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ConstraintLayout rowConstraintLayout;
        TextView rowCardTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowConstraintLayout = itemView.findViewById(R.id.nestedrv_CL);
            rowCardTitle = itemView.findViewById(R.id.nestedrv_title);
        }

        @Override
        public boolean onLongClick(View v) {

            return false;
        }

    }
}
