package com.koize.priority.ui.journal;

public class JournalData {
    private String journalTitle;
    private String journalEditor;

    public JournalData() {
        // Default constructor required for calls to DataSnapshot.getValue(RemindersData.class)
    }

    public JournalData(String reminderTitle, int firstReminderTimeHr, int firstReminderTimeMin, int secondReminderTimeHr, int secondReminderTimeMin, int firstReminderDateTime, int secondReminderDateTime, double reminderLatitude, double reminderLongitude, String reminderLocationName, String reminderCategory) {
        this.journalTitle = journalEditor;
        this.journalEditor = journalEditor;

    }

    public String setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
        return journalTitle;
    }

    public String setJournalEditor(String journalEditor) {
        this.journalEditor = journalEditor;
        return journalEditor;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public String getJournalEditor() {
        return journalEditor;
    }



}
