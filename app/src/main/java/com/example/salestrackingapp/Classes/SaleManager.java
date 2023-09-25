package com.example.salestrackingapp.Classes;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class SaleManager {
    private Context context;
    private FirebaseFirestore db;
    private boolean allowNegativeStock; // Add a field to store the allowNegativeStock setting

    public SaleManager(Context context, boolean allowNegativeStock) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.allowNegativeStock = allowNegativeStock; // Initialize the allowNegativeStock setting
    }

    public void checkSaleSettingsAndMonitorStock(Product product, SalePermissionCallback callback) {
        // Check if the sale is allowed based on the setting and available stock
        if (allowNegativeStock || product.getQuantity() <= 0) {
            // If allowNegativeStock is true or product quantity is non-positive, allow the sale
            callback.onSalePermissionCheck(true);
        } else {
            // If allowNegativeStock is false and product quantity is positive, check stock
            isSaleAllowed(product, callback);
        }
    }


    // Define a callback interface for sale permission
    public interface SalePermissionCallback {
        void onSalePermissionCheck(boolean isAllowed);
    }

    // Public method to check if a sale is allowed
    private void isSaleAllowed(Product product, SalePermissionCallback callback) {
        // Replace "products" with the actual name of your Firestore collection
        String productId = product.getProductId(); // Replace with your product ID

        // Fetch the current quantity for the product from Firestore
        db.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int currentQuantity = documentSnapshot.getLong("quantity").intValue();
                        int requestedQuantity = product.getQuantity();

                        // Check if the sale is allowed based on available stock
                        boolean isAllowed = currentQuantity >= requestedQuantity;

                        // Invoke the callback with the result
                        callback.onSalePermissionCheck(isAllowed);
                    } else {
                        // Handle the case where the product document does not exist
                        Toast.makeText(context, "Product not found in Firestore.", Toast.LENGTH_SHORT).show();

                        // Invoke the callback with the result indicating not allowed
                        callback.onSalePermissionCheck(false);
                        Toast.makeText(context, "Sale not allowed due to insufficient stock balance!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to fetch data from Firestore
                    Toast.makeText(context, "Error fetching product data: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Invoke the callback with the result indicating not allowed
                    callback.onSalePermissionCheck(false);
                });
    }

    // Implement the logic to send a stock notification here if needed
    private void sendStockNotification(Product product) {
        // This can be done using Android's notification system or a third-party library like Firebase Cloud Messaging (FCM)

        // For simplicity, I'll show a Toast notification as an example
        String notificationMessage = "Minimum stock reached for product: " + product.getProduct_name();
        Toast.makeText(context, notificationMessage, Toast.LENGTH_LONG).show();
    }
}
