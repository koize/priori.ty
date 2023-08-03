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

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {
    private ArrayList<RoutineData> routinePlannerDataArrayList;
    private Context context;
    private RoutineListener RoutineListener;
    public RoutineAdapter(ArrayList<RoutineData> routinePlannerDataArrayList, Context context, RoutineListener RoutinerListener){
        this.routinePlannerDataArrayList = routinePlannerDataArrayList;
        this.context = context;
        this.RoutineListener = RoutinerListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_focusmode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return routinePlannerDataArrayList.size(); //change
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        MaterialCardView rowCard;
        TextView rowCardMainText;
        TextView rowCardTitle;
        ImageView rowCardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_focusMode_card);
            rowCardMainText = itemView.findViewById(R.id.journal_main_text);
            rowCardTitle = itemView.findViewById(R.id.row_focusMode_Title);
            rowCardImage = itemView.findViewById(R.id.row_focusMode_icon);
        }
        @Override
        public boolean onLongClick(View v) {

            return false;
        }

    }

    public interface RoutineListener {
        void onRoutineClick(int position);
    }
}
