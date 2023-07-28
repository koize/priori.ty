package com.koize.priority.ui.monthlyplanner;

import com.koize.priority.ui.category.CategoryData;

public class EventData {
    private String eventTitle;
    private String eventType;
    private String eventStartDate;
    private String eventEndDate;
    private long eventStartDateEpoch;
    private long eventEndDateEpoch;
    private long eventStartDateTime;
    private long eventEndDateTime;
    private int eventStartHr;
    private int eventStartMin;
    private int eventEndHr;
    private int eventEndMin;
    private String eventReminderDate;
    private long eventReminderDateTime;
    private int eventReminderHr;
    private int eventReminderMin;
    private Boolean eventAllDay;
    private String eventLocationName;
    private double eventLatitude;
    private double eventLongitude;
    private CategoryData eventCategory;
    private String eventDesc;

    public EventData() {
        // Default constructor required for calls to DataSnapshot.getValue(EventData.class)
    }

    public EventData(String eventTitle, String eventStartDate, String eventEndDate, long eventStartDateTime, long eventEndDateTime, int eventStartHr, int eventStartMin, int eventEndHr, int eventEndMin, String eventReminderDate, long eventReminderDateTime, int eventReminderHr, int eventReminderMin, Boolean eventAllDay, String eventLocationName, double eventLatitude, double eventLongitude, CategoryData eventCategory, String eventDesc) {
        this.eventTitle = eventTitle;
        this.eventType = eventType;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventStartDateEpoch = eventStartDateEpoch;
        this.eventEndDateEpoch = eventEndDateEpoch;
        this.eventStartDateTime = eventStartDateTime;
        this.eventEndDateTime = eventEndDateTime;
        this.eventStartHr = eventStartHr;
        this.eventStartMin = eventStartMin;
        this.eventEndHr = eventEndHr;
        this.eventEndMin = eventEndMin;
        this.eventReminderDate = eventReminderDate;
        this.eventReminderDateTime = eventReminderDateTime;
        this.eventReminderHr = eventReminderHr;
        this.eventReminderMin = eventReminderMin;
        this.eventAllDay = eventAllDay;
        this.eventLocationName = eventLocationName;
        this.eventLatitude = eventLatitude;
        this.eventLongitude = eventLongitude;
        this.eventCategory = eventCategory;
        this.eventDesc = eventDesc;
    }

    public String setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
        return eventTitle;
    }

    public String setEventType(String eventType) {
        this.eventType = eventType;
        return eventType;
    }

    public String setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
        return eventStartDate;
    }

    public String setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
        return eventEndDate;
    }

    public long setEventStartDateEpoch(long eventStartDateEpoch) {
        this.eventStartDateEpoch = eventStartDateEpoch;
        return eventStartDateEpoch;
    }

    public long setEventEndDateEpoch(long eventEndDateEpoch) {
        this.eventEndDateEpoch = eventEndDateEpoch;
        return eventEndDateEpoch;
    }

    public long setEventStartDateTime(long eventStartDateTime) {
        this.eventStartDateTime = eventStartDateTime;
        return eventStartDateTime;
    }

    public long setEventEndDateTime(long eventEndDateTime) {
        this.eventEndDateTime = eventEndDateTime;
        return eventEndDateTime;
    }

    public int setEventStartHr(int eventStartHr) {
        this.eventStartHr = eventStartHr;
        return eventStartHr;
    }

    public int setEventStartMin(int eventStartMin) {
        this.eventStartMin = eventStartMin;
        return eventStartMin;
    }

    public int setEventEndHr(int eventEndHr) {
        this.eventEndHr = eventEndHr;
        return eventEndHr;
    }

    public int setEventEndMin(int eventEndMin) {
        this.eventEndMin = eventEndMin;
        return eventEndMin;
    }

    public String setEventReminderDate(String eventReminderDate) {
        this.eventReminderDate = eventReminderDate;
        return eventReminderDate;
    }

    public long setEventReminderDateTime(long eventReminderDateTime) {
        this.eventReminderDateTime = eventReminderDateTime;
        return eventReminderDateTime;
    }

    public int setEventReminderHr(int eventReminderHr) {
        this.eventReminderHr = eventReminderHr;
        return eventReminderHr;
    }

    public int setEventReminderMin(int eventReminderMin) {
        this.eventReminderMin = eventReminderMin;
        return eventReminderMin;
    }

    public Boolean setEventAllDay(Boolean eventAllDay) {
        this.eventAllDay = eventAllDay;
        return eventAllDay;
    }

    public String setEventLocationName(String eventLocationName) {
        this.eventLocationName = eventLocationName;
        return eventLocationName;
    }

    public double setEventLatitude(double eventLatitude) {
        this.eventLatitude = eventLatitude;
        return eventLatitude;
    }

    public double setEventLongitude(double eventLongitude) {
        this.eventLongitude = eventLongitude;
        return eventLongitude;
    }

    public CategoryData setEventCategory(CategoryData eventCategory) {
        this.eventCategory = eventCategory;
        return eventCategory;
    }

    public String setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
        return eventDesc;
    }

    public String getEventTitle() {
        return eventTitle;
    }
    public String getEventType() { return eventType; }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public long getEventStartDateEpoch() {
        return eventStartDateEpoch;
    }

    public long getEventEndDateEpoch() {
        return eventEndDateEpoch;
    }

    public long getEventStartDateTime() {
        return eventStartDateTime;
    }

    public long getEventEndDateTime() {
        return eventEndDateTime;
    }

    public int getEventStartHr() {
        return eventStartHr;
    }

    public int getEventStartMin() {
        return eventStartMin;
    }

    public int getEventEndHr() {
        return eventEndHr;
    }

    public int getEventEndMin() {
        return eventEndMin;
    }

    public String getEventReminderDate() {
        return eventReminderDate;
    }

    public long getEventReminderDateTime() {
        return eventReminderDateTime;
    }

    public int getEventReminderHr() {
        return eventReminderHr;
    }

    public int getEventReminderMin() {
        return eventReminderMin;
    }

    public Boolean getEventAllDay() {
        return eventAllDay;
    }

    public String getEventLocationName() {
        return eventLocationName;
    }

    public double getEventLatitude() {
        return eventLatitude;
    }

    public double getEventLongitude() {
        return eventLongitude;
    }

    public CategoryData getEventCategory() {
        return eventCategory;
    }

    public String getEventDesc() {
        return eventDesc;
    }




}
