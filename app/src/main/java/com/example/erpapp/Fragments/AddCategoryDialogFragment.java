package com.example.erpapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.erpapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddCategoryDialogFragment extends DialogFragment {

    private TextInputEditText categoryNameEditText,categoryDesc;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        FirebaseApp.initializeApp(getContext());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_add_category_dialog, null);

        categoryNameEditText = view.findViewById(R.id.categoryNameEditText);
        categoryDesc = view.findViewById(R.id.categoryDescEditText);


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Add Category")
                .setPositiveButton("Add", null) // Set the positive button later
                .setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String categoryName = Objects.requireNonNull(categoryNameEditText.getText()).toString();
                String desc = Objects.requireNonNull(categoryDesc.getText()).toString();
                // Retrieve companyId from SharedPreferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String companyId = sharedPreferences.getString("companyId", null);

                if (desc.isEmpty()){
                    categoryDesc.setError("please enter category details");
                    categoryDesc.setFocusable(true);
                    categoryDesc.requestFocus();
                } else if (categoryName.isEmpty()) {
                    categoryNameEditText.setError("please enter category name");
                    categoryNameEditText.setFocusable(true);
                    categoryNameEditText.requestFocus();
                } else if (companyId.isEmpty()) {
                    Toast.makeText(getContext(), "Company Id is empty please contact your Administrator to fix this", Toast.LENGTH_LONG).show();

                } else {
                    // Add category to Firestore with custom category ID
                    String categoryId = firestore.collection("categories").document().getId(); // Generate category ID
                    DocumentReference categoryRef = firestore.collection("categories").document(categoryId);

                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("categoryId", categoryId); // Save category ID
                    categoryData.put("companyId", companyId);
                    categoryData.put("name", categoryName);
                    categoryData.put("description",desc);

                    categoryRef.set(categoryData)
                            .addOnSuccessListener(aVoid -> {
                                // Success
                                Toast.makeText(getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                // Error handling
                            });
                }


            });
            });


        return alertDialog;
    }
}
