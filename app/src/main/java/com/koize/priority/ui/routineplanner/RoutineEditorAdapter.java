package com.koize.priority.ui.routineplanner;

import android.content.Context;
import android.util.Log;
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
    private ArrayList<HabitsData> routineEditorDataArrayList;
    //do aft lunch, each habit click adds title to a string, then can convert into array to get all habits, prob just delete routine
    //editorData and just make a routineData
    private Context context;
    private RoutineEditorListener routineEditorListener;
    private RoutineEditorListener2 routineEditorListener2;

    public RoutineEditorAdapter(ArrayList<HabitsData> routineEditorDataArrayList, Context context, RoutineEditorListener routineEditorListener, RoutineEditorListener2 routineEditorListener2){
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
        HabitsData habitsdata1 = new HabitsData();
        routineEditorDataArrayList.add(habitsdata1);
        HabitsData habitsData = routineEditorDataArrayList.get(position);
        /*
        ArrayList testsmth = routineData1.getRoutineHabitsList();
        testsmth.get(0);
        const { habitsDescription, habitsDuration } = testsmth.get(0);
        Log.d("test",testsmth.get(0).habitsDescription);*/
        //ArrayList<HabitsData> testarray = routineData1.getRoutineHabitsList();
        //testarray.get(0).getHabitsDescription();
        //String Title = RoutinePlannerPage.routineHabits.get(0).getHabitsDescription();
        //String Title = habitsData.getHabitsTitle();
        //int Duration = habitsData.getHabitsDuration();

        String Title = habitsData.getHabitsTitle();
        int Duration = habitsData.getHabitsDuration();
        holder.rowCardTitle.setText(Title);
        holder.rowCardDuration.setText(Duration + "m");
    }

    @Override
    public int getItemCount() {
        return RoutinePlannerPage.routineHabits.size();
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