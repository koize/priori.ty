package com.koize.priority.ui.routineplanner;

public class RoutineEditorData {
    private String routineHabitsTitle;
    private int routineHabitsDuration;
    private String routineHabitsDescription;

    public RoutineEditorData(){

    }

    public RoutineEditorData(String routineHabitsTitle,int routineHabitsDuration, String routineHabitsDescription){
        this.routineHabitsTitle = routineHabitsTitle;
        this.routineHabitsDuration = routineHabitsDuration;
        this.routineHabitsDescription = routineHabitsDescription;
    }

    public void setRoutineHabitsTitle(String routineHabitsTitle){
        this.routineHabitsTitle = routineHabitsTitle;
    }

    public String getRoutineHabitsTitle(){
        return routineHabitsTitle;
    }

    public int setRoutineHabitsDuration(int routineHabitsDuration){
        this.routineHabitsDuration = routineHabitsDuration;
        return routineHabitsDuration;
    }

    public int getRoutineHabitsDuration(){
        return routineHabitsDuration;
    }

    public void setRoutineHabitsDescription(String routineHabitsDescription){
        this.routineHabitsDescription = routineHabitsDescription;
    }

    public String getRoutineHabitsDescription(){
        return routineHabitsDescription;
    }
}

