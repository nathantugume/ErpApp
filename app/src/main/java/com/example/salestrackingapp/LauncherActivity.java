package com.example.salestrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_KEY = "isFirstTime";
    private static final int PROGRESS_DELAY = 2000; // Delay in milliseconds

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Initialize views
      //  LinearProgressIndicator launcherProgress = findViewById(R.id.launcherProgress);
        ProgressBar launcherProgress = findViewById(R.id.launcherProgress);

        // Hide the progress bar initially
        launcherProgress.setVisibility(View.GONE);

        // Display the progress bar after a delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launcherProgress.setVisibility(View.VISIBLE);

                // Check if the user is logged in to Firebase or if it's the first app launch
                // You can add your authentication logic here

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Check if it's the user's first time using the app
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        boolean isFirstTime = settings.getBoolean(FIRST_TIME_KEY, true);

                        if (isFirstTime) {
                            // It's the user's first time, navigate to registration or login activity
                            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                        } else {
                            // User has used the app before
                            // Check if the user is authenticated with Firebase
                            if (mAuth.getCurrentUser() != null) {
                                // User is authenticated, navigate to the dashboard
                                startActivity(new Intent(LauncherActivity.this, AdminDashboardActivity.class));

                            } else {
                                // User is not authenticated, navigate to the login activity
                                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                            }
                        }

                        // Set the flag to false (not the user's first time) for future launches
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(FIRST_TIME_KEY, false);
                        editor.apply();

                        finish();

                    }
                }, PROGRESS_DELAY); // Delay for progress bar (simulated)
            }
        }, 1000); // Initial delay for displaying the app name and tagline
    }
}
