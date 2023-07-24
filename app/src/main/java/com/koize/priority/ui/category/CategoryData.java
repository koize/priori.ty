package com.koize.priority.ui.category;

import android.graphics.Color;

public class CategoryData {
    private String categoryTitle;
    private int categoryColor;

    public CategoryData() {
    }

    public CategoryData(String categoryTitle, int categoryColor) {
        this.categoryTitle = categoryTitle;
        this.categoryColor = categoryColor;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(int categoryColor) {
        this.categoryColor = categoryColor;
    }
}
