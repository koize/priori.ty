package com.koize.priority.ui.routineplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.koize.priority.R;

import java.util.ArrayList;

public class RoutineEditorAdapter extends RecyclerView.Adapter<RoutineEditorAdapter.ViewHolder> {
    private ArrayList<RoutineData> routineEditorDataArrayList;
    //do aft lunch, each habit click adds title to a string, then can convert into array to get all habits, prob just delete routine
    //editorData and just make a routineData
    private Context context;
    private RoutineEditorListener routineEditorListener;
    private RoutineEditorListener2 routineEditorListener2;

    public RoutineEditorAdapter(ArrayList<RoutineData> routineEditorDataArrayList, Context context, RoutineEditorListener routineEditorListener, RoutineEditorListener2 routineEditorListener2){
        this.routineEditorDataArrayList = routineEditorDataArrayList;
        this.context = context;
        this.routineEditorListener = routineEditorListener;
        this.routineEditorListener2 = routineEditorListener2;
    }

    @NonNull
    @Override
    public RoutineEditorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_routineeditor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineEditorAdapter.ViewHolder holder, int position) {
        RoutineData routineData1 = routineEditorDataArrayList.get(position);
        //holder.rowCardTitle.setText(routineData1.getRoutineHabits().getHabitsTitle());
        //holder.rowCardDuration.setText(routineData1.getRoutineHabits().getHabitsDuration()+"m");
        holder.rowCardTitle.setText("Test");
        holder.rowCardDuration.setText("test 1"+"m");
    }

    @Override
    public int getItemCount() {
        return routineEditorDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ConstraintLayout rowConstraintLayout;
        TextView rowCardTitle;
        TextView rowCardDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowConstraintLayout = itemView.findViewById(R.id.rowCL_routineEditor);
            rowCardTitle = itemView.findViewById(R.id.row_routine_editor_habitTitle);
            rowCardDuration = itemView.findViewById(R.id.row_routine_editor_habitDuration);
        }

        @Override
        public boolean onLongClick(View v) {

            return false;
        }

    }

    public interface RoutineEditorListener {
        void onRoutineHabitClick(int position);
    }

    public interface RoutineEditorListener2 {
        void onRoutineHabitLongClick(int position);
    }
}