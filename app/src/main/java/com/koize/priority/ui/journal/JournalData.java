package com.koize.priority.ui.journal;

public class JournalData {
    private String journalTitle;
    private String journalEditor;
    private String journalMood;

    public JournalData() {
        // Default constructor required for calls to DataSnapshot.getValue(RemindersData.class)
    }

    public JournalData(String journalTitle,String journalEditor,String journalMood) {
        this.journalTitle = journalTitle;
        this.journalEditor = journalEditor;
        this.journalMood = journalMood;
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

    public String getJournalTitle() {
        return journalTitle;
    }

    public String getJournalEditor() {
        return journalEditor;
    }

    public String getJournalMood() {
        return journalMood;
    }

}
