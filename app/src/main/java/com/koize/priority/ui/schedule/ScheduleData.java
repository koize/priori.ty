package com.koize.priority.ui.schedule;

import com.koize.priority.ui.category.CategoryData;

public class ScheduleData {
    private int scheduleId;
    private String scheduleTextId;
    private String scheduleTitle;
    private int startTimeHr;
    private int startTimeMin;
    private int endTimeHr;
    private int endTimeMin;
    private CategoryData scheduleCategory;

    public ScheduleData(){

    }

    public ScheduleData(int scheduleId, String scheduleTextId, String scheduleTitle,int startTimeHr,int startTimeMin, int endTimeHr,int endTimeMin, CategoryData scheduleCategory){
        this.scheduleId = scheduleId;
        this.scheduleTextId = scheduleTextId;
        this.scheduleTitle = scheduleTitle;
        this.startTimeHr = startTimeHr;
        this.startTimeMin = startTimeMin;
        this.endTimeHr = endTimeHr;
        this.endTimeMin = endTimeMin;
        this.scheduleCategory = scheduleCategory;
    }

    public void setScheduleId(int scheduleId){
        this.scheduleId = scheduleId;
    }

    public int getScheduleId(){return scheduleId;}

    public void setScheduleTextId(String scheduleTextId) {
        this.scheduleTextId = scheduleTextId;
    }

    public String getScheduleTextId() {
        return scheduleTextId;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setStartTimeHr(int startTimeHr) {
        this.startTimeHr = startTimeHr;
    }

    public int getStartTimeHr() {
        return startTimeHr;
    }

    public void setStartTimeMin(int startTimeMin) {
        this.startTimeMin = startTimeMin;
    }

    public int getStartTimeMin() {
        return startTimeMin;
    }

    public void setEndTimeHr(int endTimeHr) {
        this.endTimeHr = endTimeHr;
    }

    public int getEndTimeHr() {
        return endTimeHr;
    }

    public void setEndTimeMin(int endTimeMin) {
        this.endTimeMin = endTimeMin;
    }

    public int getEndTimeMin() {
        return endTimeMin;
    }

    public void setScheduleCategory(CategoryData scheduleCategory) {
        this.scheduleCategory = scheduleCategory;
    }

    public CategoryData getScheduleCategory() {
        return scheduleCategory;
    }
}



