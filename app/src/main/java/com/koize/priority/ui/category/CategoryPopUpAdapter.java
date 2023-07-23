package com.koize.priority.ui.category;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.koize.priority.R;

import java.util.ArrayList;

public class CategoryPopUpAdapter extends RecyclerView.Adapter<CategoryPopUpAdapter.ViewHolder> {
    private ArrayList<CategoryData> categoryDataArrayList;
    private Context context;
    private CategoryClickInterface categoryClickInterface;
    int lastPos = -1;

    public CategoryPopUpAdapter(ArrayList<CategoryData> categoryDataArrayList, Context context, CategoryClickInterface categoryClickInterface) {
        this.categoryDataArrayList = categoryDataArrayList;
        this.context = context;
        this.categoryClickInterface = categoryClickInterface;
    }

    @NonNull
    @Override
    public CategoryPopUpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryPopUpAdapter.ViewHolder holder, int position) {
        CategoryData categoryData = categoryDataArrayList.get(position);
        holder.rowCardTitle.setText(categoryData.getCategoryTitle());
        holder.rowCard.setCardBackgroundColor(ColorStateList.valueOf(categoryData.getCategoryColor()));
        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClickInterface.onCategoryClick(position);
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

    @Override
    public int getItemCount() {
        return categoryDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView rowCard;
        TextView rowCardTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCard = itemView.findViewById(R.id.row_category_list_card);
            rowCardTitle = itemView.findViewById(R.id.text_category_list);

        }
    }

    public interface CategoryClickInterface {
        void onCategoryClick(int position);
    }

}
