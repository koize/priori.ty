package com.koize.priority;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

public class AboutScreen extends AppCompatActivity {

    Chip webButton;
    TextView versionNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_screen);
        versionNo = findViewById(R.id.home_version_no);
        webButton = findViewById(R.id.button_github);
        PackageManager pm = getPackageManager();
        String pkgName = getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String ver = pkgInfo.versionName;
        versionNo.setText("priori.ty v" + ver);
        webButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            startActivity(intent);
    });
    }
}