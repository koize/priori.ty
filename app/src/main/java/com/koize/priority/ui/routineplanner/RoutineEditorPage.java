package com.koize.priority.ui.routineplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koize.priority.R;

public class RoutineEditorPage extends AppCompatActivity {
    public static final int INPUT_METHOD_NEEDED = 1;
    EditText habitTitle;
    Chip habitPickerNewHabitChip;
    private FloatingActionButton habitPickerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_editor_page);
        habitPickerButton = findViewById(R.id.button_habitPicker_add);
        habitPickerButton.setOnClickListener(addHabitListener);
    }

    View.OnClickListener addHabitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupWindow(v);}

        public void showPopupWindow(final View view) {

            ConstraintLayout routineView;
            //Create a View object yourself through inflater
            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_habit_picker, null);
            routineView = popupView.findViewById(R.id.habitPickerPopUpLayout);

            habitPickerNewHabitChip = popupView.findViewById(R.id.habit_add_new_habit);

            //Specify the length and width through constants

            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

            //Make Inactive Items Outside Of PopupWindow
            boolean focusable = true;

            //Create a window with our parameters
            final PopupWindow popupWindow = new PopupWindow(routineView, width, height, focusable);
            // Closes the popup window when touch outside
            //Handler for clicking on the inactive zone of the window



            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });

            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.setAnimationStyle(com.google.android.material.R.style.Animation_AppCompat_Dialog);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            //Set the location of the window on the screen
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            View container = popupWindow.getContentView().getRootView();
            if (container != null) {
                WindowManager wm = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                if (wm != null) {
                    wm.updateViewLayout(container, p);
                }
            }
            //Initialize the elements of our window, install the handler
            habitPickerNewHabitChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewHabitPopUp newHabitPopUp = new NewHabitPopUp();
                    newHabitPopUp.showPopupWindow(v);
                }
            });



        }
    };
}