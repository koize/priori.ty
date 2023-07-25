package com.koize.priority.ui.category;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.koize.priority.R;

import java.util.ArrayList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CategoryPopUpFirebaseAdapter extends FirebaseRecyclerAdapter<CategoryData, CategoryPopUpFirebaseAdapter.categoryViewHolder> {
    private ArrayList<CategoryData> categoryDataArrayList;
    private Context context;
    private CategoryClickInterface categoryClickInterface;
    private CategoryClickInterface1 categoryClickInterface1;
    int lastPos = -1;

    public CategoryPopUpFirebaseAdapter(@NonNull FirebaseRecyclerOptions<CategoryData> options, CategoryClickInterface categoryClickInterface, CategoryClickInterface1 categoryClickInterface1) {
        super(options);
        this.categoryClickInterface = categoryClickInterface;
        this.categoryClickInterface1 = categoryClickInterface1;

    }



    @Override
    public void onBindViewHolder(@NonNull categoryViewHolder holder, int position, @NonNull CategoryData categoryData) {
        String refKey = getRef(holder.getAbsoluteAdapterPosition()).getKey();
        holder.rowCardTitle.setText(categoryData.getCategoryTitle());
        holder.rowCard.setCardBackgroundColor(ColorStateList.valueOf(categoryData.getCategoryColor()));
        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClickInterface.onCategoryClick(holder.getAbsoluteAdapterPosition());
            }

        });
        holder.rowCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                categoryClickInterface1.onCategoryLongClick(holder.getAbsoluteAdapterPosition(), refKey);
                return true;
            }

        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPos) {
            viewToAnimate.setAlpha(0.0f);
            viewToAnimate.animate().alpha(1.0f).setDuration(500);
            lastPos = position;
        }
    }



    @NonNull
    @Override
    public categoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_list, parent, false);
        return new CategoryPopUpFirebaseAdapter.categoryViewHolder(view);
    }

    public static class categoryViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        MaterialCardView rowCard;
        TextView rowCardTitle;
        public categoryViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_category_list_card);
            rowCardTitle = itemView.findViewById(R.id.text_category_list);

        }
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public interface CategoryClickInterface {
        void onCategoryClick(int position);
    }

    public interface CategoryClickInterface1 {
        boolean onCategoryLongClick(int position, String refKey);
    }

}
