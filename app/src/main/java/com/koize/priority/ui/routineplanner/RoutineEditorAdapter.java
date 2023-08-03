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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.koize.priority.R;

import java.util.ArrayList;

public class RoutineEditorAdapter extends RecyclerView.Adapter<RoutineEditorAdapter.ViewHolder> {
    private ArrayList<HabitsData> routineEditorDataArrayList;
    private Context context;
    private RoutineEditorListener routineEditorListener;
    private RoutineEditorListener2 routineEditorListener2;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



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

        String Title = habitsData.getHabitsTitle();
        int Duration = habitsData.getHabitsDuration();
        holder.rowCardTitle.setText(Title);
        holder.rowCardDuration.setText(Duration + "m");

        holder.rowConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutineEditorAdapter.this.onRoutineHabitClick(holder.getAdapterPosition());
            }
        });
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

    public void onRoutineHabitClick(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name != "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + name + "_" + user.getUid().substring(1,5) + "/routine");
            } else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasants/" + "peasant_" + user.getUid() + "/routine");
            } else {
                throw new IllegalStateException("Unexpected value: " + name);
            }
        }
        databaseReference.child(RoutinePlannerPage.routineDataMain.getRoutineTextId()).child("routineHabitsList").child(Integer.toString(position)).removeValue();
        RoutinePlannerPage.routineHabits.remove(position);
        notifyDataSetChanged();
    }

}