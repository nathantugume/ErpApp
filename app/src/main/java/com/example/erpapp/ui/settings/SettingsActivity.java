package com.example.erpapp.ui.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.erpapp.Classes.SettingsManager;
import com.example.erpapp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private SettingsManager settingsManager;
    private SwitchMaterial allowNegativeStockSalesSwitch, warnStockOnSwitch, darkModeSwitch;
    private EditText minimumStockEditText;
    private LinearLayout stockLayout;
    private SharedPreferences sharedPreferences;
    private Button testNetworkButton;
    private String companyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize all the switches and EditText elements
        allowNegativeStockSalesSwitch = findViewById(R.id.allowNegativeStockSalesSwitch);
        warnStockOnSwitch = findViewById(R.id.warnStockOnSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        minimumStockEditText = findViewById(R.id.minimumStockEditText);
        stockLayout = findViewById(R.id.minimumStockLayout);
        sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        companyId = sharedPreferences.getString("companyId", null);

        settingsManager = new SettingsManager(companyId,this);

        // Fetch and initialize settings from Firestore
        fetchAndInitializeSettings();

        testNetworkButton = findViewById(R.id.testNetworkButton);

        // Set an OnClickListener for the "Test Network" button
        testNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connectivity
                if (isNetworkConnected()) {
                    // Network is connected
                    Toast.makeText(SettingsActivity.this, "Network is connected", Toast.LENGTH_SHORT).show();
                } else {
                    // Network is not connected
                    Toast.makeText(SettingsActivity.this, "Network is not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add an OnCheckedChangeListener to the allowNegativeStockSalesSwitch
        allowNegativeStockSalesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsManager.updateBooleanSetting("allowNegativeStockSales", isChecked);
            }
        });

        // Add an OnCheckedChangeListener to the warnStockOnSwitch
        warnStockOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsManager.updateBooleanSetting("warnStockOn", isChecked);
                stockLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        // Add an OnCheckedChangeListener to the darkModeSwitch
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsManager.updateBooleanSetting("darkMode", isChecked);
            }
        });

        // Add a TextChangedListener to the minimumStockEditText
        minimumStockEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed in this case
            }

            @Override
            public void afterTextChanged(Editable s) {
                String minimumStockValue = s.toString();
                // Update the setting in Firestore whenever the text is edited
                settingsManager.updateIntegerSetting("minimumStock", Integer.parseInt(minimumStockValue));
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    // Fetch and initialize settings from Firestore
    // Fetch and initialize settings from Firestore
    private void fetchAndInitializeSettings() {
        settingsManager.fetchSettingsFromFirestore(new SettingsManager.OnSettingsFetchedListener() {
            @Override
            public void onSettingsFetched(Map<String, Object> settings) {
                if (settings != null && !settings.isEmpty()) {
                    allowNegativeStockSalesSwitch.setChecked((boolean) settings.get("allowNegativeStockSales"));
                    warnStockOnSwitch.setChecked((boolean) settings.get("warnStockOn"));
                    darkModeSwitch.setChecked((boolean) settings.get("darkMode"));
                    // Check the "darkMode" setting
                    boolean darkMode = (boolean) settings.get("darkMode");
                    // Set dark mode based on the retrieved setting
                    setDarkMode(darkMode);
                    Object minimumStockObj = settings.get("minimumStock");

                    if (minimumStockObj instanceof Long) {
                        long minimumStockLong = (Long) minimumStockObj;
                        int minimumStock = (int) minimumStockLong;
                        minimumStockEditText.setText(String.valueOf(minimumStock));
                    } else if (minimumStockObj instanceof Integer) {
                        int minimumStock = (Integer) minimumStockObj;
                        minimumStockEditText.setText(String.valueOf(minimumStock));
                    }
                } else {
                    // Settings are empty, set default settings
                    settingsManager.initializeDefaultSettings(companyId);
                }
            }

            @Override
            public void onSettingsNotFound() {
                // Handle the case where settings document is not found
                Toast.makeText(SettingsActivity.this, "settings not set", Toast.LENGTH_SHORT).show();
                settingsManager.initializeDefaultSettings(companyId);
            }

            @Override
            public void onSettingsFetchError(Exception e) {
                // Handle the error case during settings fetch
                Toast.makeText(SettingsActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            // Enable dark mode
            // You can implement your dark mode logic here
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            fetchAndInitializeSettings();

        } else {
            // Disable dark mode
            // You can implement your light mode logic here
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            fetchAndInitializeSettings();
        }
    }


}

