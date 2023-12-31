package com.koize.priority

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.AppBarConfiguration.Builder
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.koize.priority.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val isNotiGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        val isFineLocationGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isCoarseLocationGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isNotiGranted) {
            //sendNotification(this)
            //Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
            )
        }

        if (isFineLocationGranted) {
            //Toast.makeText(this, "Fine location permission granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        if (isCoarseLocationGranted) {
            //Toast.makeText(this, "Coarse location permission granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration: AppBarConfiguration = Builder(
                R.id.navigation_home, R.id.navigation_reminders,  /*R.id.navigation_goals,*/R.id.navigation_schedule, R.id.navigation_journal)
                .build()
        val navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        setupWithNavController(binding!!.navView, navController)
    }



    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
            sendNotification(this)
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }
    companion object {
        const val TAG = "MainActivity"
        const val NOTIFICATION_MESSAGE_TAG = "message from notification"
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
            putExtra(
                    NOTIFICATION_MESSAGE_TAG, "Hi ☕\uD83C\uDF77\uD83C\uDF70"
            )
        }
    }
}