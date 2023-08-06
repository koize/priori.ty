package com.koize.priority.ui.routineplanner;

public class HabitsData {
    private int habitsId;
    private String habitsTextId;
    private String habitsTitle;
    private int habitsDuration;
    private String habitsDescription;

    public HabitsData() {
        // Default constructor required for calls to DataSnapshot.getValue(RemindersData.class)
    }

    public HabitsData(String habitsTitle,int habitsDuration,String habitsDescription) {
        this.habitsTitle = habitsTitle;
        this.habitsDuration = habitsDuration;
        this.habitsDescription = habitsDescription;
    }

    //title
    public String setHabitsTitle(String habitsTitle) {
        this.habitsTitle = habitsTitle;
        return habitsTitle;
    }

    public String getHabitsTitle() {
        return habitsTitle;
    }

    //duration
    public int setHabitsDuration(int habitsDuration) {
        this.habitsDuration = habitsDuration;
        return habitsDuration;
    }

    public int getHabitsDuration() {
        return habitsDuration;
    }

    public void setHabitsDescription(String habitsDescription) {
        this.habitsDescription = habitsDescription;}

    public String getHabitsDescription() {
        return habitsDescription;
    }

    public void setHabitsId(int habitsId){
        this.habitsId = habitsId;
    }

    public int getHabitsId(){return habitsId;}

    public void setHabitsTextId(String habitsTextId){
        this.habitsTextId = habitsTextId;
    }

    public String getHabitsTextId(){return habitsTextId;}
}
