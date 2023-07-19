package com.koize.priority;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

public class ReminderPopUp {
    public static final int INPUT_METHOD_NEEDED = 1;
    public void showPopupWindow(final View view) {

        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_reminders_add, null);
        reminderView = popupView.findViewById(R.id.reminderPopUpLayout);

        //Specify the length and width through constants

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(reminderView, width, height, focusable);
        // Closes the popup window when touch outside
        //Handler for clicking on the inactive zone of the window

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
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


        /* final View root = popupView.getRootView();
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                root.getWindowVisibleDisplayFrame(r);

                // Calculate the difference between the original height and the new height
                int heightDiff = r.height() - root.getHeight();

                // Now update the Popup's position
                // The first value is the x-axis, which stays the same.
                // Second value is the y-axis. We still want it centered, so move it up by 50% of the height
                // change
                // The third and the fourth values are default values to keep the width/height
                popupWindow.update(0, heightDiff / 2, -1, -1);
            }
        });*/


        //Set the location of the window on the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        View container = popupWindow.getContentView().getRootView();
        if(container != null) {
            WindowManager wm = (WindowManager)container.getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams)container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.3f;
            if(wm != null) {
                wm.updateViewLayout(container, p);
            }
        }
        //Initialize the elements of our window, install the handler

        EditText reminderTitle = popupView.findViewById(R.id.new_reminder_title);
        Chip firstReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_1);
        Chip secondReminderChip = popupView.findViewById(R.id.button_new_reminder_choose_date_2);
        EditText reminderLocationText = popupView.findViewById(R.id.new_reminder_location_text);
        Chip reminderLocationChip = popupView.findViewById(R.id.button_new_reminder_getLocation);
        Chip reminderCategoryChip = popupView.findViewById(R.id.button_new_reminder_choose_category);
        Chip reminderSaveChip = popupView.findViewById(R.id.button_new_reminder_save);
        firstReminderChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        secondReminderChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        reminderLocationChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        reminderCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp categoryPopUp = new CategoryPopUp();
                categoryPopUp.showPopupWindow(v);
            }
        });
        reminderSaveChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}