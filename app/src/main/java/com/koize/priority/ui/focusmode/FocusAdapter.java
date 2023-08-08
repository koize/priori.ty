package com.koize.priority.ui.focusmode;

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
import com.koize.priority.ui.routineplanner.HabitsData;
import com.koize.priority.ui.routineplanner.NestedAdapter;
import com.koize.priority.ui.routineplanner.RoutineAdapter;
import com.koize.priority.ui.routineplanner.RoutineData;

import java.util.ArrayList;

public class FocusAdapter extends RecyclerView.Adapter<FocusAdapter.ViewHolder> {
    private ArrayList<RoutineData> focusModeDataArrayList;
    private ArrayList<HabitsData> focusModeHabitsArrayList;
    private Context context;
    private FocusListener FocusListener;
    private NestedAdapter NestedAdapter;
    public FocusAdapter(ArrayList<RoutineData> focusModeDataArrayList, Context context, FocusListener FocusListener){
        this.focusModeDataArrayList = focusModeDataArrayList;
        this.context = context;
        this.FocusListener = FocusListener;
    }
    @NonNull
    @Override
    public FocusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_focusmode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FocusAdapter.ViewHolder holder, int position) {
        RoutineData routineData =  focusModeDataArrayList.get(position);
        holder.rowCardTitle.setText(routineData.getRoutineTitle());
        focusModeHabitsArrayList = routineData.getRoutineHabitsList();
        int totalDuration = 0;
        if(focusModeHabitsArrayList != null){
            for(HabitsData habits : focusModeHabitsArrayList){
                totalDuration += habits.getHabitsDuration();
            }
        }
        holder.rowCardDuration.setText(totalDuration+"m");

        NestedAdapter = new NestedAdapter(focusModeHabitsArrayList,context);
        holder.rowCardRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
        holder.rowCardRV.setAdapter(NestedAdapter);
        holder.rowCardImage.setImageResource(R.drawable.baseline_access_time_24);
        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FocusListener.onFocusClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return focusModeDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        MaterialCardView rowCard;
        TextView rowCardMainText;
        TextView rowCardTitle;
        ImageView rowCardImage;
        TextView rowCardDuration;
        RecyclerView rowCardRV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_focusMode_card);
            rowCardMainText = itemView.findViewById(R.id.journal_main_text);
            rowCardTitle = itemView.findViewById(R.id.row_focusMode_Title);
            rowCardImage = itemView.findViewById(R.id.row_focusMode_icon);
            rowCardDuration = itemView.findViewById(R.id.row_focusMode_duration);
            rowCardRV = itemView.findViewById(R.id.row_focusMode_RV);
        }
        @Override
        public boolean onLongClick(View v) {

            return false;
        }

    }

    public interface FocusListener{
        void onFocusClick(int position);
    }
}
