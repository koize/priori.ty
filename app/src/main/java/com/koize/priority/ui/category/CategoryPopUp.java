package com.koize.priority.ui.category;

import static android.provider.Settings.Global.getString;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.MainActivity;
import com.koize.priority.R;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;


public class CategoryPopUp {
    Chip colorChip;
    public static final int INPUT_METHOD_NEEDED = 1;
    ColorEnvelope colorEnvelope;
    PopupWindow popupWindow;
    int color;
    Chip addCategoryChip;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;
    CategoryData categoryData;
    private RecyclerView categoryRV;
    private FirebaseAuth firebaseAuth;
    private CategoryPopUpAdapter categoryPopUpAdapter;
    private ArrayList<CategoryData> categoryDataArrayList;
    public interface CategoryCallBack {
        void sendCategory(CategoryData categoryData);
    }
    private CategoryCallBack categoryCallBack;

    public CategoryPopUp(CategoryCallBack categoryCallBack) {
        this.categoryCallBack = categoryCallBack;
    }

    public void showPopupWindow(final View view) {

        ConstraintLayout reminderView;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_category_add, null);


        colorChip = popupView.findViewById(R.id.category_chose_color);
        addCategoryChip = popupView.findViewById(R.id.button_new_reminder_save);

        //Specify the length and width through constants

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        // Closes the popup window when touch outside
        //Handler for clicking on the inactive zone of the window


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name!="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + name + "/categories");
            }
            else if (name=="") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/categories");
            }
            else {
                throw new IllegalStateException("Unexpected value: " + name);
            }

        } else {
            Snackbar.make(view, "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        categoryRV = popupView.findViewById(R.id.recycler_row_category_list);
        firebaseAuth = FirebaseAuth.getInstance();
        categoryDataArrayList = new ArrayList<>();
        categoryPopUpAdapter = new CategoryPopUpAdapter(categoryDataArrayList, popupView.getContext(), this::onCategoryClick, this::onCategoryLongClick);
        categoryRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(popupView.getContext()));
        categoryRV.setAdapter(categoryPopUpAdapter);
        getCategories();
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


        colorChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(popupView.getContext())
                        .setTitle("ColorPicker Dialog")
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton("Ok",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        color = envelope.getColor();
                                        // colorChip.setChipBackgroundColor(ColorStateList.valueOf(Color.rgb(envelope.getArgb()[1], envelope.getArgb()[2], envelope.getArgb()[3])));
                                        colorChip.setChipBackgroundColor(ColorStateList.valueOf(color));
                                        addCategoryChip.setVisibility(View.VISIBLE);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachAlphaSlideBar(true) // the default value is true.
                        .attachBrightnessSlideBar(true)  // the default value is true.
                        .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                        .show();
            }
        });

        addCategoryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText catTitle = popupView.findViewById(R.id.new_category_title);
                String categoryName = catTitle.getText().toString();
                if (categoryName.isEmpty()) {
                    catTitle.setError("Set title!");
                    catTitle.requestFocus();
                }
                if (user != null) {
                    categoryData = new CategoryData();
                    categoryData.setCategoryTitle(categoryName);
                    categoryData.setCategoryColor(color);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            databaseReference.child(categoryData.getCategoryTitle()).setValue(categoryData);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Snackbar.make(view, "Not signed in!", Snackbar.LENGTH_SHORT)
                            .show();
                }

            }


        });

    }



    private void getCategories() {
        categoryDataArrayList.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryData categoryData = dataSnapshot.getValue(CategoryData.class);
                    categoryDataArrayList.add(categoryData);
                }
                categoryPopUpAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void onCategoryClick(int position) {
        categoryData = categoryDataArrayList.get(position);
        categoryCallBack.sendCategory(categoryData);
        popupWindow.dismiss();

    }
    public boolean onCategoryLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(categoryRV.getContext());

        // Set the message show for the Alert time
        builder.setMessage("Delete the following category: " + categoryDataArrayList.get(position).getCategoryTitle() + "? ");

        // Set Alert Title
        builder.setTitle("Warning!");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            databaseReference.child(categoryDataArrayList.get(position).getCategoryTitle()).removeValue();
            Snackbar.make(categoryRV, "Category deleted!", Snackbar.LENGTH_SHORT)
                    .show();
            dialog.dismiss();
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
        return false;
    }


}
