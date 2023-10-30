package com.example.salestrackingapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.salestrackingapp.Classes.LoadCategoriesTask;
import com.example.salestrackingapp.Classes.Product;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.adapters.ProductAdapter;
import com.example.salestrackingapp.ui.products.ProductsActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class AddProductDialogFragment extends DialogFragment {


    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private TextInputEditText productNameEditText;
    private TextInputEditText productDescEditText;
    private TextInputEditText priceEditText, BpriceEditText, wholeSalePriceEdt;
    private TextInputEditText quantityEditText;
    private TextInputEditText barcodeEditText;
    private Source source = Source.DEFAULT;

    private ProductAdapter productsAdapter;
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            barcodeEditText.setText(result.getContents());
        }
    });
    private String selectedCategory = "";
    private Spinner categorySpinner;
    private String productName, productDesc, price, quantity, barcode, buyingPrice, wholesalePrice;
    // Constructor to pass the productsAdapter reference
    public AddProductDialogFragment(ProductAdapter productsAdapter) {
        this.productsAdapter = productsAdapter;
    }
    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_add_product_dialog, null);

        productNameEditText = view.findViewById(R.id.productNameEditText);
        productDescEditText = view.findViewById(R.id.productDescEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        barcodeEditText = view.findViewById(R.id.barcodeEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        wholeSalePriceEdt = view.findViewById(R.id.wholeSaleEditText);
        BpriceEditText = view.findViewById(R.id.BpriceEditText);

        LoadCategoriesTask loadCategoriesTask = new LoadCategoriesTask(getContext(), categorySpinner);
        loadCategoriesTask.execute();
//        barcode
        barcodeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanner();
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
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
                productName = productNameEditText.getText().toString();
                productDesc = productDescEditText.getText().toString();
                price = priceEditText.getText().toString();
                quantity = quantityEditText.getText().toString();
                barcode = barcodeEditText.getText().toString();
                buyingPrice = BpriceEditText.getText().toString();
                wholesalePrice = wholeSalePriceEdt.getText().toString();

                // Set the item selection listener

                // Retrieve companyId from SharedPreferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String companyId = sharedPreferences.getString("companyId", null);


                if (!companyId.isEmpty() && validateInput()) {
                    // Check if a product with the same name and companyId exists
                    firestore.collection("products")
                            .whereEqualTo("product_name", productName)
                            .whereEqualTo("companyId", companyId)
                            .get(source)
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    // No existing product with the same name and companyId found, proceed to add
                                    String productId = firestore.collection("products").document().getId(); // Generate product ID
                                    DocumentReference productRef = firestore.collection("products").document(productId);

                                    // Add the new product to the productsAdapter
                                    Product newProduct = new Product(productId, productName, productDesc, Integer.parseInt(price), Integer.parseInt(quantity), barcode, Integer.parseInt(buyingPrice), selectedCategory, Integer.parseInt(wholesalePrice), companyId);
                                    productsAdapter.addProduct(newProduct);
                                    productRef.set(newProduct)
                                            .addOnSuccessListener(aVoid -> {
                                                // Success
                                                Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();

                                               productsAdapter.notifyDataSetChanged();

                                                alertDialog.dismiss();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Error handling
                                                Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                } else {
                                    // Product with the same name and companyId already exists
                                    Toast.makeText(getContext(), "Product with the same name already exists"+productName, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Error handling
                                Toast.makeText(getContext(), "error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }

            });
        });


        return alertDialog;
    }

    private boolean validateInput() {
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
        }
        if (selectedCategory.isEmpty()) {
            // Show an error message to the user
            Log.d("category","name "+selectedCategory);
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;

    }

    private void scanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up for flash light ");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }


}
