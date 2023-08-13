package com.koize.priority.ui.reminders;

import android.app.PendingIntent;

import com.koize.priority.ui.category.CategoryData;

public class RemindersData {
    private int reminderId;
    private String reminderTextId;
    private String reminderTitle;
    private int firstReminderTimeHr;
    private int firstReminderTimeMin;
    private int secondReminderTimeHr;
    private int secondReminderTimeMin;
    private long firstReminderDateTime;
    private long secondReminderDateTime;
    private String firstReminderPartOfDay;
    private String secondReminderPartOfDay;
    private long firstReminderDateEpoch;
    private long secondReminderDateEpoch;
    private double reminderLatitude;
    private double reminderLongitude;
    private String reminderLocationName;
    private CategoryData reminderCategory;
    private PendingIntent reminderPendingIntent;

    public RemindersData() {
        // Default constructor required for calls to DataSnapshot.getValue(RemindersData.class)
    }

    public RemindersData(String reminderTitle, int firstReminderTimeHr, int firstReminderTimeMin, int secondReminderTimeHr, int secondReminderTimeMin, int firstReminderDateTime, int secondReminderDateTime, String firstReminderPartOfDay, String secondReminderPartOfDay, long firstReminderDateEpoch, long secondReminderDateEpoch,double reminderLatitude, double reminderLongitude, String reminderLocationName, CategoryData reminderCategory) {
        this.reminderTitle = reminderTitle;
        this.firstReminderTimeHr = firstReminderTimeHr;
        this.firstReminderTimeMin = firstReminderTimeMin;
        this.secondReminderTimeHr = secondReminderTimeHr;
        this.secondReminderTimeMin = secondReminderTimeMin;
        this.firstReminderDateTime = firstReminderDateTime;
        this.secondReminderDateTime = secondReminderDateTime;
        this.firstReminderPartOfDay = firstReminderPartOfDay;
        this.secondReminderPartOfDay = secondReminderPartOfDay;
        this.firstReminderDateEpoch = firstReminderDateEpoch;
        this.secondReminderDateEpoch = secondReminderDateEpoch;
        this.reminderLatitude = reminderLatitude;
        this.reminderLongitude = reminderLongitude;
        this.reminderLocationName = reminderLocationName;
        this.reminderCategory = reminderCategory;
    }

    public int setReminderId(int reminderId) {
        this.reminderId = reminderId;
        return reminderId;
    }

    public String setReminderTextId(String reminderTextId) {
        this.reminderTextId = reminderTextId;
        return reminderTextId;
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

    public String setFirstReminderPartOfDay(String firstReminderPartOfDay) {
        this.firstReminderPartOfDay = firstReminderPartOfDay;
        return firstReminderPartOfDay;
    }

    public String setSecondReminderPartOfDay(String secondReminderPartOfDay) {
        this.secondReminderPartOfDay = secondReminderPartOfDay;
        return secondReminderPartOfDay;
    }

    public long setFirstReminderDateEpoch(long firstReminderDateEpoch) {
        this.firstReminderDateEpoch = firstReminderDateEpoch;
        return firstReminderDateEpoch;
    }

    public long setSecondReminderDateEpoch(long secondReminderDateEpoch) {
        this.secondReminderDateEpoch = secondReminderDateEpoch;
        return secondReminderDateEpoch;
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

    public PendingIntent setReminderPendingIntent(PendingIntent reminderPendingIntent) {
        this.reminderPendingIntent = reminderPendingIntent;
        return reminderPendingIntent;
    }

    public int getReminderId() {
        return reminderId;
    }

    public String getReminderTextId() {
        return reminderTextId;
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

    public String getFirstReminderPartOfDay() {
        return firstReminderPartOfDay;
    }

    public String getSecondReminderPartOfDay() {
        return secondReminderPartOfDay;
    }

    public long getFirstReminderDateEpoch() {
        return firstReminderDateEpoch;
    }

    public long getSecondReminderDateEpoch() {
        return secondReminderDateEpoch;
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

    public PendingIntent getReminderPendingIntent() {
        return reminderPendingIntent;
    }

}
