package com.koize.priority.ui.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.koize.priority.R;

import java.util.ArrayList;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder>{
    private ArrayList<JournalData> journalDataArrayList;
    private Context context;
    private JournalListener JournalListener;

    public JournalAdapter(ArrayList<JournalData> journalDataArrayList, Context context, JournalListener JournalListener) {
        this.journalDataArrayList = journalDataArrayList;
        this.context = context;
        this.JournalListener = JournalListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_journal_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalAdapter.ViewHolder holder, int position) {
        JournalData journalData = journalDataArrayList.get(position);
        holder.rowCardTitle.setText(journalData.getJournalTitle());
        holder.rowCardMainText.setText(journalData.getJournalEditor());
        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalListener.onJournalClick(position);
            }

        });

    }

    @Override
    public int getItemCount() {
        return journalDataArrayList.size();
    }

    public interface JournalListener {
        void onJournalClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView rowCard;
        TextView rowCardMainText;
        TextView rowCardTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_journal_card);
            rowCardMainText = itemView.findViewById(R.id.journal_main_text);
            rowCardTitle = itemView.findViewById(R.id.journal_main_title);
        }

    }
}
