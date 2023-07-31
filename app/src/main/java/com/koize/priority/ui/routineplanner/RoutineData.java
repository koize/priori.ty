package com.koize.priority.ui.routineplanner;

public class RoutineData {
    private String routineIcon;
    private String routineTitle;
    private HabitsData routineHabits;
    private int routineTotalDuration;

    public RoutineData(){

    }

    public RoutineData(String routineIcon,String routineTitle,HabitsData routineHabits,int routineTotalDuration){
        this.routineIcon = routineIcon;
        this.routineTitle = routineTitle;
        this.routineHabits = routineHabits;
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

    public void setRoutineHabits(HabitsData routineHabits){
       this.routineHabits = routineHabits;
    }

    public HabitsData getRoutineHabits(){return routineHabits;}

    public void setRoutineTotalDuration(int routineTotalDuration){
        this.routineTotalDuration = routineTotalDuration;
    }

    public int getRoutineTotalDuration(){return routineTotalDuration;}
}
