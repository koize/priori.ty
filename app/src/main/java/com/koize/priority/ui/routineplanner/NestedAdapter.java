package com.koize.priority.ui.routineplanner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.koize.priority.R;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.ViewHolder> {

    @NonNull
    @Override
    public NestedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NestedAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        MaterialCardView rowCard;
        TextView rowCardMainText;
        TextView rowCardTitle;
        ImageView rowCardImage;
        TextView rowCardDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_focusMode_card);
            rowCardMainText = itemView.findViewById(R.id.journal_main_text);
            rowCardTitle = itemView.findViewById(R.id.row_focusMode_Title);
            rowCardImage = itemView.findViewById(R.id.row_focusMode_icon);
            rowCardDuration = itemView.findViewById(R.id.row_focusMode_duration);
        }
        @Override
        public boolean onLongClick(View v) {

            return false;
        }

    }
}
