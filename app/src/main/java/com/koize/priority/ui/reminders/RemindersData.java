package com.koize.priority.ui.reminders;

import com.koize.priority.ui.category.CategoryData;

public class RemindersData {
    private String reminderTitle;
    private int firstReminderTimeHr;
    private int firstReminderTimeMin;
    private int secondReminderTimeHr;
    private int secondReminderTimeMin;
    private long firstReminderDateTime;
    private long secondReminderDateTime;
    private double reminderLatitude;
    private double reminderLongitude;
    private String reminderLocationName;
    private CategoryData reminderCategory;

    public RemindersData() {
        // Default constructor required for calls to DataSnapshot.getValue(RemindersData.class)
    }

    public RemindersData(String reminderTitle, int firstReminderTimeHr, int firstReminderTimeMin, int secondReminderTimeHr, int secondReminderTimeMin, int firstReminderDateTime, int secondReminderDateTime, double reminderLatitude, double reminderLongitude, String reminderLocationName, CategoryData reminderCategory) {
        this.reminderTitle = reminderTitle;
        this.firstReminderTimeHr = firstReminderTimeHr;
        this.firstReminderTimeMin = firstReminderTimeMin;
        this.secondReminderTimeHr = secondReminderTimeHr;
        this.secondReminderTimeMin = secondReminderTimeMin;
        this.firstReminderDateTime = firstReminderDateTime;
        this.secondReminderDateTime = secondReminderDateTime;
        this.reminderLatitude = reminderLatitude;
        this.reminderLongitude = reminderLongitude;
        this.reminderLocationName = reminderLocationName;
        this.reminderCategory = reminderCategory;
    }

    public String setReminderTitle(String reminderTitle) {
        this.reminderTitle = reminderTitle;
        return reminderTitle;
    }

    public int setFirstReminderTimeHr(int firstReminderTimeHr) {
        this.firstReminderTimeHr = firstReminderTimeHr;
        return firstReminderTimeHr;
    }

    public int setFirstReminderTimeMin(int firstReminderTimeMin) {
        this.firstReminderTimeMin = firstReminderTimeMin;
        return firstReminderTimeMin;
    }

    public int setSecondReminderTimeHr(int secondReminderTimeHr) {
        this.secondReminderTimeHr = secondReminderTimeHr;
        return secondReminderTimeHr;
    }

    public int setSecondReminderTimeMin(int secondReminderTimeMin) {
        this.secondReminderTimeMin = secondReminderTimeMin;
        return secondReminderTimeMin;
    }

    public long setFirstReminderDateTime(long firstReminderDateTime) {
        this.firstReminderDateTime = firstReminderDateTime;
        return firstReminderDateTime;
    }

    public long setSecondReminderDateTime(long secondReminderDateTime) {
        this.secondReminderDateTime = secondReminderDateTime;
        return secondReminderDateTime;
    }

    public double setReminderLatitude(double reminderLatitude) {
        this.reminderLatitude = reminderLatitude;
        return reminderLatitude;
    }

    public double setReminderLongitude(double reminderLongitude) {
        this.reminderLongitude = reminderLongitude;
        return reminderLongitude;
    }

    public String setReminderLocationName(String reminderLocationName) {
        this.reminderLocationName = reminderLocationName;
        return reminderLocationName;
    }

    public CategoryData setReminderCategory(CategoryData reminderCategory) {
        this.reminderCategory = reminderCategory;
        return reminderCategory;
    }

    public String getReminderTitle() {
        return reminderTitle;
    }

    public int getFirstReminderTimeHr() {
        return firstReminderTimeHr;
    }

    public int getFirstReminderTimeMin() {
        return firstReminderTimeMin;
    }

    public int getSecondReminderTimeHr() {
        return secondReminderTimeHr;
    }

    public int getSecondReminderTimeMin() {
        return secondReminderTimeMin;
    }

    public long getFirstReminderDateTime() {
        return firstReminderDateTime;
    }

    public long getSecondReminderDateTime() {
        return secondReminderDateTime;
    }

    public double getReminderLatitude() {
        return reminderLatitude;
    }

    public double getReminderLongitude() {
        return reminderLongitude;
    }

    public String getReminderLocationName() {
        return reminderLocationName;
    }

    public CategoryData getReminderCategory() {
        return reminderCategory;
    }

}
