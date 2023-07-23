package com.koize.priority.settings;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
            Preference signInPreference = findPreference("sign_in");
            Preference signOutPreference = findPreference("sign_out");
            Preference deleteAccountPreference = findPreference("delete_account");
            Preference accountSettingsPreference = findPreference("account_settings");

            signInPreference.setOnPreferenceClickListener(preference -> {
                showFirebaseUI();

                return true;
            });

            signOutPreference.setOnPreferenceClickListener(preference -> {
                AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Intent main_firebaseUI = new Intent(requireContext(), SettingsActivity.class);
                        main_firebaseUI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main_firebaseUI);

                        Toast.makeText(requireContext(), "You have been signed out", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(requireContext(), "Failed to sign out, please try again later", Toast.LENGTH_SHORT).show();

                    }
                });
                return true;
            });

            deleteAccountPreference.setOnPreferenceClickListener(preference -> {
                AuthUI.getInstance().delete(requireContext());
                AuthUI.getInstance().delete(requireContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(requireContext(), "Your account has been deleted", Toast.LENGTH_SHORT).show();

                        Intent main_firebaseUI = new Intent(requireContext(), SettingsActivity.class);
                        main_firebaseUI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main_firebaseUI);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(requireContext(), "Failed to delete account, please try again later", Toast.LENGTH_SHORT).show();

                    }
                });
                return true;
            });

            accountSettingsPreference.setOnPreferenceClickListener(preference -> {
                Intent accountSettings = new Intent(requireContext(), AccountSettings.class);
                startActivity(accountSettings);
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