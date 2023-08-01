package com.koize.priority.ui.journal;

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

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder>{
    private ArrayList<JournalData> journalDataArrayList;
    private Context context;
    private JournalListener JournalListener;
    private JournalListener2 JournalListener2;

    public JournalAdapter(ArrayList<JournalData> journalDataArrayList, Context context, JournalListener JournalListener, JournalListener2 JournalListener2) {
        this.journalDataArrayList = journalDataArrayList;
        this.context = context;
        this.JournalListener = JournalListener;
        this.JournalListener2 = JournalListener2;
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
        holder.rowCardJournalDate.setText(journalData.getJournalDate());
        holder.rowCardJournalDay.setText(journalData.getJournalDay());
        if(journalData.getJournalMood().equals("mood1")){
            holder.rowCardImage.setImageResource(R.drawable.radio_ic_face1_checked);
        }else if(journalData.getJournalMood().equals("mood2")){
            holder.rowCardImage.setImageResource(R.drawable.radio_ic_face2_checked);
        }else if(journalData.getJournalMood().equals("mood3")){
            holder.rowCardImage.setImageResource(R.drawable.radio_ic_face3_checked);
        }else if(journalData.getJournalMood().equals("mood4")){
            holder.rowCardImage.setImageResource(R.drawable.radio_ic_face4_checked);
        }else if(journalData.getJournalMood().equals("mood5")){
            holder.rowCardImage.setImageResource(R.drawable.radio_ic_face5_checked);
        }


        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalListener.onJournalClick(holder.getAdapterPosition());
            }

        });

        holder.rowCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                JournalListener2.onJournalLongClick(holder.getAdapterPosition());
                return true;
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

    public interface JournalListener2 {
        void onJournalLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        MaterialCardView rowCard;
        TextView rowCardMainText;
        TextView rowCardTitle;
        ImageView rowCardImage;
        TextView rowCardJournalDate;
        TextView rowCardJournalDay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_journal_card);
            rowCardMainText = itemView.findViewById(R.id.journal_main_text);
            rowCardTitle = itemView.findViewById(R.id.journal_main_title);
            rowCardImage = itemView.findViewById(R.id.journal_image);
            rowCardJournalDate = itemView.findViewById(R.id.journal_main_date);
            rowCardJournalDay = itemView.findViewById(R.id.journal_main_day);
        }
        @Override
        public boolean onLongClick(View v) {

            return false;
        }

    }
}
