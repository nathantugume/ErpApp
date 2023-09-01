package com.example.erpapp.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.erpapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddProductDialogFragment extends DialogFragment {


    private TextInputEditText productNameEditText;
    private TextInputEditText productDescEditText;
    private TextInputEditText priceEditText, BpriceEditText;
    private TextInputEditText quantityEditText;
    private TextInputEditText barcodeEditText;
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            barcodeEditText.setText(result.getContents());
        }
    });
    private String selectedCategory = "";
    private Spinner categorySpinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        FirebaseApp.initializeApp(getContext());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_add_product_dialog, null);

        productNameEditText = view.findViewById(R.id.productNameEditText);
        productDescEditText = view.findViewById(R.id.productDescEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        barcodeEditText = view.findViewById(R.id.barcodeEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        BpriceEditText = view.findViewById(R.id.BpriceEditText);

//        barcode
        barcodeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanner();
            }
        });


        firestore.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categoryNames = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String categoryName = documentSnapshot.getString("name");
                        Log.d("Categories", "name" + categoryName);
                        categoryNames.add(categoryName);
                    }

                    setupCategorySpinner(categoryNames);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Add Product")
                .setPositiveButton("Add", null) // Set the positive button later
                .setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String productName = productNameEditText.getText().toString();
                String productDesc = productDescEditText.getText().toString();
                String price = priceEditText.getText().toString();
                String quantity = quantityEditText.getText().toString();
                String barcode = barcodeEditText.getText().toString();
                String buyingPrice = BpriceEditText.getText().toString();

                // Set the item selection listener
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        selectedCategory = (String) parent.getItemAtPosition(position);
                        selectedCategory = (String) parent.getSelectedItem();
                        Log.d("selectedCategory", "category" + selectedCategory);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });


                if (productName.isEmpty()) {
                    productNameEditText.setError("Please enter product name");
                    productNameEditText.requestFocus();
                } else if (productDesc.isEmpty()) {
                    productDescEditText.setError("Please enter product description");
                    productDescEditText.requestFocus();
                } else if (price.isEmpty()) {
                    priceEditText.setError("Please enter product price");
                    priceEditText.requestFocus();
                } else if (buyingPrice.isEmpty()) {
                    BpriceEditText.setError("please enter Buying price");
                    BpriceEditText.requestFocus();

                } else if (quantity.isEmpty()) {
                    quantityEditText.setError("Please enter product quantity");
                    quantityEditText.requestFocus();
                } else if (barcode.isEmpty()) {
                    barcodeEditText.setError("Please enter product barcode");
                    barcodeEditText.requestFocus();
                }
                if (selectedCategory.isEmpty()) {
                    // Show an error message to the user
                    Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                } else {
                    // Add category to Firestore with custom category ID
                    String productId = firestore.collection("products").document().getId(); // Generate category ID
                    DocumentReference productRef = firestore.collection("products").document(productId);

                    int pricedata = Integer.parseInt(price);
                    int qty = Integer.parseInt(quantity);
                    int bPrice = Integer.parseInt(buyingPrice);
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productId", productId); // Save product ID
                    productData.put("product_name", productName);
                    productData.put("product_desc", productDesc);
                    productData.put("price", pricedata);
                    productData.put("quantity", qty);
                    productData.put("barcode", barcode);
                    productData.put("buying_price", bPrice);
                    productData.put("category", selectedCategory);

                    productRef.set(productData)
                            .addOnSuccessListener(aVoid -> {
                                // Success
                                Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
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

    private void scanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up for flash light ");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void setupCategorySpinner(List<String> categoryNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

}
