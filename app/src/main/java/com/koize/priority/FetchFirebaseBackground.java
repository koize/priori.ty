package com.koize.priority;

import android.provider.ContactsContract;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FetchFirebaseBackground {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reminderRef;
    DatabaseReference eventRef;
    FirebaseUser user;


    public FetchFirebaseBackground() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");

        if (user != null) {
            String name = user.getDisplayName();
            if ((name != null) && name!="") {
                reminderRef = firebaseDatabase.getReference("users/" + name + "#" + user.getUid().substring(1,5) + "/reminders");
                eventRef = firebaseDatabase.getReference("users/" + name + "#" + user.getUid().substring(1,5) + "/events");

            }
            else if (name=="") {
                reminderRef = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/reminders");
                eventRef = firebaseDatabase.getReference("users/" + "peasant" + user.getUid() + "/events");
            }
            else {
                throw new IllegalStateException("Unexpected value: " + name);
            }

        } else {
            throw new IllegalStateException("Unexpected value: " + user);
        }
    }

}
