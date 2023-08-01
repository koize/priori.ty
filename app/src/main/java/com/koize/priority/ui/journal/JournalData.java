package com.koize.priority.ui.journal;

public class JournalData {
    private String journalTitle;
    private String journalEditor;
    private String journalMood;
    private String journalDate;
    private String journalDay;

    public JournalData() {
        // Default constructor required for calls to DataSnapshot.getValue(RemindersData.class)
    }

    public JournalData(String journalTitle,String journalEditor,String journalMood,String journalDate,String journalDay) {
        this.journalTitle = journalTitle;
        this.journalEditor = journalEditor;
        this.journalMood = journalMood;
        this.journalDate = journalDate;
        this.journalDay = journalDay;
    }

    public String setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
        return journalTitle;
    }

    public String setJournalEditor(String journalEditor) {
        this.journalEditor = journalEditor;
        return journalEditor;
    }

    public String setJournalMood(String journalMood) {
        this.journalMood = journalMood;
        return journalMood;
    }
    public String setJournalDate(String journalDate) {
        this.journalDate = journalDate;
        return journalDate;
    }
    public String setJournalDay(String journalDay) {
        this.journalDay = journalDay;
        return journalDay;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public String getJournalEditor() {
        return journalEditor;
    }

    public String getJournalMood() {
        return journalMood;
    }

    public String getJournalDate() {
        return journalDate;
    }

    public String getJournalDay() {
        return journalDay;
    }

}
