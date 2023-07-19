package com.koize.priority;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;
import java.util.List;

public class AccountSettings extends AppCompatActivity {
    private TextView userName;
    private TextView userEmail;
    private CoordinatorLayout coordinatorLayout;

    FirebaseUser user;

    private static final int RC_SIGN_IN = 123;
    String name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);




    }

    @Override

            protected void onResume() {
                super.onResume();

        userName = findViewById(R.id.account_settings_username);
        userEmail = findViewById(R.id.account_settings_email);

        Chip loginSignOutChip = findViewById(R.id.button_account_settings_login_signout);
        loginSignOutChip.setOnClickListener(onLoginSignOut);

        Chip deleteAccountChip = findViewById(R.id.button_account_settings_delete_account);
        deleteAccountChip.setOnClickListener(onDeleteAccount);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            name = user.getDisplayName();
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                name = profile.getDisplayName();
                email = profile.getEmail();
            }
            userName.setText("Current user: " + name);
            email = user.getEmail();
            userEmail.setText("Email: " + email);

        } else {
            userName.setText("Current user: Not logged in");
            userEmail.setText("");
        }

        if (user != null) {
            loginSignOutChip.setText("Sign out");
            deleteAccountChip.setVisibility(View.VISIBLE);
        } else {
            loginSignOutChip.setText("Login");
            deleteAccountChip.setVisibility(View.GONE);
        }
            }


     View.OnClickListener onLoginSignOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (user != null) {
                FirebaseAuth.getInstance().signOut();
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Signed out",Snackbar.LENGTH_SHORT);
                snackbar.show();
                onResume();
            } else {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.AnonymousBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.AppTheme)
                                .build(),
                        RC_SIGN_IN);
                        onResume();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                onResume();


            } else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Login failed",Snackbar.LENGTH_SHORT);
                snackbar.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onLoginSignOut.onClick(view);
                    }
                });
                snackbar.show();
            }
        }
    }

    View.OnClickListener onDeleteAccount = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);

            // Set the message show for the Alert time
            builder.setMessage("WARNING! This will delete your account and all your data. Are you sure?");

            // Set Alert Title
            builder.setTitle("WARNING!!!");

            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
            builder.setCancelable(true);

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                // When the user click yes button then app will close
                AuthUI.getInstance().delete(getApplicationContext());
                AuthUI.getInstance().delete(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Account deleted successfully",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        /*Intent main_firebaseUI = new Intent(getApplicationContext(), SettingsActivity.class);
                        main_firebaseUI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main_firebaseUI);*/
                        onResume();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Failed to delete account",Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onDeleteAccount.onClick(view);
                            }
                        });
                        snackbar.show();

                    }
                });
            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        }
    };

}