package com.example.salestrackingapp.Classes;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    private FirebaseFirestore firestore;
    private String companyId;
    private Context context;

    public SettingsManager(String companyId, Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.companyId = companyId;
        this.context = context;
    }

    // Method to update a boolean setting
    public void updateBooleanSetting(String settingName, boolean value) {
        Map<String, Object> data = new HashMap<>();
        data.put(settingName, value);
        // Update the setting in Firestore
        firestore.collection("settings").document(companyId).set(data, SetOptions.merge()).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to update settings "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to update an integer setting
    public void updateIntegerSetting(String settingName, int value) {
        Map<String, Object> data = new HashMap<>();
        data.put(settingName, value);
        // Update the setting in Firestore
        firestore.collection("settings").document(companyId).set(data, SetOptions.merge()).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to update settings "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Method to fetch settings from Firestore
    public void fetchSettingsFromFirestore(final OnSettingsFetchedListener listener) {
        DocumentReference settingsRef = firestore.collection("settings").document(companyId);
        settingsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, fetch the settings data
                        Map<String, Object> settings = document.getData();
                        if (listener != null) {
                            listener.onSettingsFetched(settings);
                        }
                    } else {
                        // Document does not exist, handle this case
                        if (listener != null) {
                            listener.onSettingsNotFound();
                        }
                    }
                } else {
                    // Handle the error case
                    if (listener != null) {
                        listener.onSettingsFetchError(task.getException());
                    }
                }
            }
        });
    }

    public void initializeDefaultSettings(String companyId) {
        DocumentReference settingsRef = firestore.collection("settings").document(companyId);

        Map<String, Object> defaultSettings = new HashMap<>();
        defaultSettings.put("allowNegativeStockSales", false);
        defaultSettings.put("warnStockOn", false);
        defaultSettings.put("darkMode", false);
        defaultSettings.put("minimumStock", 5); // Set your default value here

        settingsRef.set(defaultSettings)
                .addOnSuccessListener(aVoid -> {
                    // Default settings added successfully

                })
                .addOnFailureListener(e -> {
                    // Error handling
                    Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public interface OnSettingsFetchedListener {
        void onSettingsFetched(Map<String, Object> settings);

        void onSettingsNotFound();

        void onSettingsFetchError(Exception e);
    }
}
