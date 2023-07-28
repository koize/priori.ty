package com.koize.priority.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.koize.priority.R;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private static final int RC_SIGN_IN = 123;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference accountSettingsPreference = findPreference("account_settings");
            Preference convertGuestToFull = findPreference("convert_guest_to_full");
            Preference offlineSync = findPreference("offline_sync");
            Preference deleteAccount = findPreference("delete_account");

            accountSettingsPreference.setOnPreferenceClickListener(preference -> {
                Intent accountSettings = new Intent(requireContext(), AccountSettings.class);
                startActivity(accountSettings);
                return true;
            });

            convertGuestToFull.setOnPreferenceClickListener(preference -> {
                if (user.isAnonymous()) {
                    showFirebaseUI();
                } else {
                    Toast.makeText(requireContext(), "You are already a full user!", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            offlineSync.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.toString().equals("true")) {
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                    Snackbar.make(requireView(), "Offline sync enabled", Snackbar.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().setPersistenceEnabled(false);
                    Snackbar.make(requireView(), "Offline sync disabled", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            });

            deleteAccount.setOnPreferenceClickListener(preference -> {
                if (user.isAnonymous()) {
                    Snackbar.make(requireView(), "You are a Peasant", Snackbar.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("WARNING!!!! YOU ARE DELETING ALL YOUR DATA");
                    builder.setMessage("ARE YOU SURE YOU WANT TO CONTINUE???");
                    builder.setPositiveButton("Yes (dont complain ah)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar.make(requireView(), "Account deleted", Snackbar.LENGTH_SHORT).show();
                                    Snackbar.make(requireView(), "You are now a peasant", Snackbar.LENGTH_SHORT).show();
                                    showFirebaseUI();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(requireView(), "Account deletion failed", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).setNegativeButton("No.. im scared", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Snackbar.make(requireView(), "u scared ah", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return true;
            });







        }
        public void showFirebaseUI(){
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.AnonymousBuilder().build());

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(R.style.AppTheme)
                    .build(), RC_SIGN_IN);
        }
        }


       }