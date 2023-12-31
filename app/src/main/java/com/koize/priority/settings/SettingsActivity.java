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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.koize.priority.R;
import com.koize.priority.SendNotiTestKt;

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
        String name;
        Preference accountSettingsPreference;
        Preference convertGuestToFull;
        Preference offlineSync;
        Preference testNoti;
        Preference deleteAccount;
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://priority-135fc-default-rtdb.asia-southeast1.firebasedatabase.app/");

        DatabaseReference databaseReference;
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://priority-135fc.appspot.com");
        StorageReference storageReference;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            if (user != null){
                name = user.getDisplayName();
                databaseReference = database.getReference("users/" + name + "_" + user.getUid().substring(1, 5));
                storageReference = storage.getReference("users/" + name + "_" + user.getUid().substring(1, 5));
            }
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            accountSettingsPreference = findPreference("account_settings");
            if ((name != null) && name != "") {
                accountSettingsPreference.setSummary("Current user: " + user.getDisplayName());
            } else if (name == ""){
                accountSettingsPreference.setSummary("Current user: Guest");
            } else {
                accountSettingsPreference.setSummary("Not signed in");
            }
            convertGuestToFull = findPreference("convert_guest_to_full");
            if (name == "" && name != null) {
                convertGuestToFull.setVisible(true);
            } else {
                convertGuestToFull.setVisible(false);
            }
            testNoti = findPreference("test_noti");
            deleteAccount = findPreference("delete_account");

            accountSettingsPreference.setOnPreferenceClickListener(preference -> {
                Intent accountSettings = new Intent(requireContext(), AccountSettings.class);
                startActivity(accountSettings);
                return true;
            });

            convertGuestToFull.setOnPreferenceClickListener(preference -> {
                if (name == "") {
                    Intent intent = new Intent(requireContext(), UpgradePeasantAccount.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(requireView(), "Error: You already have a full account", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            });



            testNoti.setOnPreferenceClickListener(preference -> {
                SendNotiTestKt.sendNotification(requireContext());
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
                            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar.make(requireView(), "You are a peasant now", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar.make(requireView(), "You are a peasant now", Snackbar.LENGTH_SHORT).show();
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