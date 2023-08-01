package com.koize.priority.ui.routineplanner;

import java.util.ArrayList;

public class RoutineData {
    public int routineId;
    public String routineTextId;
    private String routineIcon;
    private String routineTitle;
    private ArrayList<HabitsData> routineHabitsList;
    private int routineTotalDuration;

    public RoutineData(){

    }

    public RoutineData(String routineIcon,String routineTitle,ArrayList<HabitsData> routineHabits,int routineTotalDuration){
        this.routineIcon = routineIcon;
        this.routineTitle = routineTitle;
        this.routineHabitsList = routineHabits;
        this.routineTotalDuration = routineTotalDuration;
    }

    public void setRoutineIcon(String routineIcon){
        this.routineIcon = routineIcon;
    }

    public String getRoutineIcon(){return routineIcon;}

    public void setRoutineTitle(String routineTitle){
        this.routineTitle = routineTitle;
    }

    public String getRoutineTitle(){return routineTitle;}

    public void setRoutineHabitsList(ArrayList<HabitsData> routineHabitsList){
       this.routineHabitsList = routineHabitsList;
    }

    public ArrayList<HabitsData> getRoutineHabitsList(){return routineHabitsList;}

    public void setRoutineTotalDuration(int routineTotalDuration){
        this.routineTotalDuration = routineTotalDuration;
    }

    public int getRoutineTotalDuration(){return routineTotalDuration;}

    public void setRoutineId(int routineId){
        this.routineId = routineId;
    }

    public int getRoutineId(){return routineId;}

    public void setRoutineTextId(String routineTextId){
        this.routineTextId = routineTextId;
    }

    public String getRoutineTextId(){return routineTextId;}
}
