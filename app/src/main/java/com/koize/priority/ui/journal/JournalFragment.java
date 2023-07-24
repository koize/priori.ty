package com.koize.priority.ui.journal;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.databinding.FragmentJournalBinding;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koize.priority.R;

import java.util.ArrayList;


public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;
    private FloatingActionButton addJournalButton;
    public static final int INPUT_METHOD_NEEDED = 1;

    EditText journalTitle;
    EditText journalEditor;
    Chip journalSaveChip;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    JournalData journalData;
    FirebaseUser user;
    RadioGroup journalMood;
    private RecyclerView journalRV;
    private JournalAdapter JournalAdapter;
    private FirebaseAuth firebaseAuth;
    private ArrayList<JournalData> journalDataArrayList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        JournalViewModel journalViewModel =
                new ViewModelProvider(this).get(JournalViewModel.class);

        binding = FragmentJournalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addJournalButton = root.findViewById(R.id.button_journal_add);
        addJournalButton.setOnClickListener(addJournalListener);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name != "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + name + "/journal");
            } else if (name == "") {
                firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/journal");
            } else {
                throw new IllegalStateException("Unexpected value: " + name);
            }


    }
        else{
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Not signed in!", Snackbar.LENGTH_SHORT)
                    .show();
        }

        journalRV = root.findViewById(R.id.recycler_journal);
        journalDataArrayList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();


        JournalAdapter = new JournalAdapter(journalDataArrayList, getContext(), this::onJournalClick);
        journalRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        journalRV.setAdapter(JournalAdapter);
        getJournal();

        return root;
    }

    private void getJournal() {
        journalDataArrayList.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                journalDataArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    JournalData journalData = dataSnapshot.getValue(JournalData.class);
                    journalDataArrayList.add(journalData);
                }
                JournalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onJournalClick(int i) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener addJournalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //JournalPopUp journalPopUp = new JournalPopUp();
            //journalPopUp.showPopupWindow(v);
            showPopupWindow(v);}

        public void showPopupWindow(final View view) {

            ConstraintLayout journalView;
            //Create a View object yourself through inflater
            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_journal_add, null);
            journalView = popupView.findViewById(R.id.journalPopUpLayout);

            //Specify the length and width through constants

            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

            //Make Inactive Items Outside Of PopupWindow
            boolean focusable = true;

            //Create a window with our parameters
            final PopupWindow popupWindow = new PopupWindow(journalView, width, height, focusable);
            // Closes the popup window when touch outside
            //Handler for clicking on the inactive zone of the window

            journalTitle = popupView.findViewById(R.id.title_new_journal);
            journalEditor = popupView.findViewById(R.id.journal_editor);
            journalSaveChip = popupView.findViewById(R.id.button_new_journal_save);
            journalMood = popupView.findViewById(R.id.journalMood);

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

            journalSaveChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null){
                        journalData = new JournalData();
                        journalData.setJournalTitle(journalTitle.getText().toString());
                        journalData.setJournalEditor(journalEditor.getText().toString());

                        String mood = "";
                        if(journalMood.getCheckedRadioButtonId() == R.id.radio_mood1){
                            mood = "mood1";
                        }else if(journalMood.getCheckedRadioButtonId() == R.id.radio_mood2){
                            mood = "mood2";
                        }else if(journalMood.getCheckedRadioButtonId() == R.id.radio_mood3){
                            mood = "mood3";
                        } else if(journalMood.getCheckedRadioButtonId() == R.id.radio_mood4){
                            mood = "mood4";
                        } else if(journalMood.getCheckedRadioButtonId() == R.id.radio_mood5){
                            mood = "mood5";
                        }
                        journalData.setJournalMood(mood);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                databaseReference.child(journalData.getJournalTitle()).setValue(journalData);
                                Snackbar.make(view, "Journal Saved", Snackbar.LENGTH_SHORT)
                                        .show();
                                popupWindow.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                    else{
                        Snackbar.make(view, "Please sign in to save journal", Snackbar.LENGTH_SHORT)
                                .show();
                    }

                }
            });

        }
    };

}