package com.koize.priority.ui.focusmode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.koize.priority.R;
import com.koize.priority.ui.routineplanner.HabitsData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class FocusStart extends AppCompatActivity implements Serializable {
    TextView currentActivityTV;
    ProgressBar progressBar;
    TextView nextActivityTV;
    TextView focusTimer;
    Chip focusStartBtn;
    Chip focusEndBtn;
    public int counter;
    public int progressCounter;
    TextToSpeech t1;
    static ArrayList<HabitsData> focusHabitsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_start);

        Intent intent = getIntent();

        focusHabitsList = (ArrayList<HabitsData>) intent.getSerializableExtra("focusHabitsList");

        currentActivityTV = findViewById(R.id.focusCurrentActivity);
        progressBar = findViewById(R.id.focus_progressBar);

        nextActivityTV = findViewById(R.id.focusNextActivity);
        focusTimer = findViewById(R.id.focusTimer);

        focusStartBtn = findViewById(R.id.focus_startBtn);
        focusStartBtn.setOnClickListener(startRoutine);

        focusEndBtn = findViewById(R.id.focus_endBtn);
        focusEndBtn.setOnClickListener(endRoutine);

        currentActivityTV.setText("");
        nextActivityTV.setText("");
        focusTimer.setText("Press Button to start");

        progressBar.setProgress(100);

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.JAPAN);
            }
        });
    }

    View.OnClickListener startRoutine = new View.OnClickListener() {

        int totalDuration = 0;
        int currenthabitDuration = 0;
        @Override
        public void onClick(View v) {

            focusStartBtn.setVisibility(View.INVISIBLE);
            focusEndBtn.setVisibility(View.VISIBLE);
            /*
            for(HabitsData habits : focusHabitsList){
               totalDuration += habits.getHabitsDuration();
            }
            */
            int i;
            currentActivityTV.setText(focusHabitsList.get(0).getHabitsTitle());
            nextActivityTV.setText(focusHabitsList.get(1).getHabitsTitle());
            String tts = "Activity Started," + focusHabitsList.get(0).getHabitsTitle();
            t1.speak(tts, TextToSpeech.QUEUE_FLUSH, null);
            for(i=0;i<focusHabitsList.size();i++ ){
                totalDuration += focusHabitsList.get(i).getHabitsDuration();
                currenthabitDuration = currenthabitDuration + focusHabitsList.get(i).getHabitsDuration();
                int finalI = i;
                int listSize = focusHabitsList.size();
                new CountDownTimer(currenthabitDuration*60000, 1000) { //orginal 60000 / 1000
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        int test = finalI;
                        if(test == listSize-2){
                            currentActivityTV.setText(focusHabitsList.get(finalI + 1).getHabitsTitle());
                            nextActivityTV.setText("No more activities");
                            String tts = "Last Activity";
                            t1.speak(tts, TextToSpeech.QUEUE_FLUSH, null);
                        } else if(test < listSize-1) {
                            currentActivityTV.setText(focusHabitsList.get(finalI + 1).getHabitsTitle());
                            nextActivityTV.setText(focusHabitsList.get(finalI + 2).getHabitsTitle());
                            String tts = "Next Activity" + focusHabitsList.get(finalI + 1).getHabitsTitle();
                            t1.speak(tts, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }.start();
            }
            counter = totalDuration*60;
            new CountDownTimer(totalDuration*60000, 1000) { //orginal 60000 / 1000 for demo purposes play at 100x speed
                int totalDurationSec = 0;
                int totalDurationMin = totalDuration;

                public void onTick(long millisUntilFinished) {
                    //int time_left = ((totalDurationSec + (totalDurationMin*60))/(totalDuration*60))*100;
                    if(totalDurationSec == 0){
                        totalDurationMin -= 1;
                        totalDurationSec = 59;
                    }
                    if(totalDurationSec < 10){
                        focusTimer.setText(totalDurationMin + " : 0" + totalDurationSec);
                    }else if(totalDurationMin == 0 && totalDurationSec == 0){
                        focusTimer.setText("FINISH!!");
                    }else{
                        focusTimer.setText(totalDurationMin + " : " + totalDurationSec);
                    }
                    int time_left_milli = Math.toIntExact(millisUntilFinished);
                    int progress = (time_left_milli/(totalDuration*600));
                    //int time_left = (totalDurationSec + (totalDurationMin*60))*100;
                    totalDurationSec--;

                    progressBar.setProgress(progress);
                    //progressCounter = (counter / 30) * 100;
                    //progressBar.setProgress(progressCounter);
                }

                public void onFinish() {
                    focusTimer.setText("FINISH!!");
                    currentActivityTV.setText("SESSION COMPLETE!");
                    nextActivityTV.setText("SESSION COMPLETE");
                    String tts = "Routine Ended";
                    t1.speak(tts, TextToSpeech.QUEUE_FLUSH, null);

                }
            }.start();
        }
    };

    View.OnClickListener endRoutine = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tts = "Exit";
            t1.speak(tts, TextToSpeech.QUEUE_FLUSH, null);
            finish();
        }
    };

}