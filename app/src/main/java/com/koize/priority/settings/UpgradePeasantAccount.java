package com.koize.priority.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.R;

import java.util.Arrays;
import java.util.List;

public class UpgradePeasantAccount extends AppCompatActivity {
    private TextView userName;
    private TextView userEmail;
    private CoordinatorLayout coordinatorLayout;


    private static final int RC_SIGN_IN = 456;
    FirebaseUser user;
    String name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        user = FirebaseAuth.getInstance().getCurrentUser();
        showFirebaseUI();

    }
    private void showFirebaseUI() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.AppTheme)
                        .enableAnonymousUsersAutoUpgrade()
                        .setLogo(R.mipmap.ic_whack)
                        .setTosAndPrivacyPolicyUrls("https://www.twitch.tv/chocofwog",
                                "https://www.twitch.tv/koizee_")
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                user.reload();
                Intent homeIntent = new Intent(this, AccountSettings.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");
                name = user.getDisplayName();
                DatabaseReference fromPath = database.getReference("users/" + "peasants/" + "peasant_" + user.getUid());
                DatabaseReference toPath = database.getReference("users/" + name + "_" + user.getUid().substring(1,5));
                moveRecord(fromPath, toPath);
                startActivity(homeIntent);
                finish();

            } else {
                finish();
            }
        }
    }

    private void moveRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Snackbar.make(findViewById(android.R.id.content), "Data transfer successful!", Snackbar.LENGTH_SHORT).show();
                            fromPath.removeValue();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "lol unluck ur data gone", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(findViewById(android.R.id.content), "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
            }


        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }








}