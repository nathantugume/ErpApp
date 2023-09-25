package com.example.salestrackingapp.Classes;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.example.salestrackingapp.R;
import com.example.salestrackingapp.ui.Users.LoginActivity;
import com.example.salestrackingapp.ui.settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class OptionsMenuHelper {
    private final Activity activity;

    public OptionsMenuHelper(Activity activity) {
        this.activity = activity;
    }

    public void onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.admin_top_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            // Start the SettingsActivity
            startActivity(SettingsActivity.class);
            return true;
        } else if (id == R.id.sign_out) {
            // Sign out the user
            signOut();
            return true;
        }
        return false;
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        // Add any additional sign-out logic here
        // For example, you can navigate to the login screen or perform any cleanup
        // After sign-out, you can navigate to the LoginActivity
        startActivity(LoginActivity.class);
        activity.finish(); // Close the current activity
    }

    // Add any additional methods or functionality as needed
}
