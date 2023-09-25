package com.example.salestrackingapp.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LoadCategoriesTask extends AsyncTask<Void, Void, List<String>> {
    private WeakReference<Context> contextWeakReference;
    private Spinner categorySpinner;
    private String currentCategory;

    public LoadCategoriesTask(Context context, Spinner categorySpinner, String currentCategory) {
        this.contextWeakReference = new WeakReference<>(context);
        this.categorySpinner = categorySpinner;
        this.currentCategory = currentCategory;
    }

    public LoadCategoriesTask(Context context, Spinner categorySpinner) {
        this.contextWeakReference = new WeakReference<>(context);
        this.categorySpinner = categorySpinner;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        // Load categories from Firestore
        List<String> categoryNames = new ArrayList<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        try {
            Thread.sleep(0); // Simulate a delay (remove this in your actual code)
            // Retrieve companyId from SharedPreferences
            SharedPreferences sharedPreferences = contextWeakReference.get().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String companyId = sharedPreferences.getString("companyId", null);
            firestore.collection("categories")
                    .whereEqualTo("companyId",companyId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            String categoryName = documentSnapshot.getString("name");
                            categoryNames.add(categoryName);
                        }

                        // Call the onPostExecute method with the loaded category names
                        onPostExecute(categoryNames);
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                        e.printStackTrace();
                    });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return categoryNames;
    }

    @Override
    protected void onPostExecute(List<String> categoryNames) {
        Context context = contextWeakReference.get();
        if (context != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item,
                    categoryNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);

            // Set the selected category based on the product's category
            int categoryPosition = categoryNames.indexOf(currentCategory);
            categorySpinner.setSelection(categoryPosition);
        }
    }
}
