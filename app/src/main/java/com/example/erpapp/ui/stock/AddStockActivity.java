package com.example.erpapp.ui.stock;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.Classes.Purchase;
import com.example.erpapp.Classes.StockItem;
import com.example.erpapp.Fragments.CaptureAct;
import com.example.erpapp.R;
import com.example.erpapp.adapters.StockItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddStockActivity extends AppCompatActivity implements StockItem.OnQuantityChangeListener {

    private RecyclerView recyclerView;
    private StockItemAdapter stockItemAdapter;
    private List<StockItem> stockItemList = new ArrayList<>();
    private EditText etBarcodeOrSearch;
    private TextView totalPriceEdt;

    private double totalPrice = 0.0;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference productsCollection = firestore.collection("products");
    private CollectionReference purchasesCollection = firestore.collection("purchases");


    private Product product;

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            etBarcodeOrSearch.setText(result.getContents());
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockItemAdapter = new StockItemAdapter(stockItemList, this);

        recyclerView.setAdapter(stockItemAdapter);
        totalPriceEdt = findViewById(R.id.totalStock);

        // ... Other initialization code ...

        FloatingActionButton fabAddItem = findViewById(R.id.fabAddStock);
        fabAddItem.setOnClickListener(view -> showAddProductDialog());

        FloatingActionButton fabSaveStock = findViewById(R.id.fabSaveStock);
        fabSaveStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calculate the total price and perform save operation
                for (StockItem item : stockItemList) {
                    totalPrice += item.getQuantity() * item.getPrice();
                }
                // Perform the save operation here
                saveStock();
            }
        });

        FloatingActionButton fabRefresh = findViewById(R.id.fabRefresh);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onClick(View view) {
                // Clear the stockItemList and update UI
                stockItemList.clear();
                stockItemAdapter.notifyDataSetChanged();
                totalPrice = 0.0;
                totalPriceEdt.setText("Total: Ugx 0.0");
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_sales, null);
        dialogBuilder.setView(dialogView);

        etBarcodeOrSearch = dialogView.findViewById(R.id.etBarcodeOrSearch);
        Button btnFetchProductDetails = dialogView.findViewById(R.id.btnFetchProductDetails);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        etBarcodeOrSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions options = new ScanOptions();
                options.setPrompt("volume up for flash light ");
                options.setBeepEnabled(true);
                options.setOrientationLocked(true);
                options.setCaptureActivity(CaptureAct.class);
                barLauncher.launch(options);
            }
        });

        btnFetchProductDetails.setOnClickListener(v -> {
            String searchTerm = etBarcodeOrSearch.getText().toString().trim();
            if (!searchTerm.isEmpty()) {
                if (isNumeric(searchTerm)) {
                    // If the search term is numeric (potential barcode), perform barcode search
                    barCodeSearch(searchTerm);
                } else {
                    // If the search term is not numeric, perform text search
                    fetchProducts(searchTerm);
                }
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter search details", Toast.LENGTH_SHORT).show();
                etBarcodeOrSearch.setError("Enter a product to search");
                etBarcodeOrSearch.requestFocus();
            }
        });
    }

    // Check if a string is numeric (contains only digits)
    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private void fetchProducts(String barcodeOrSearchText) {
        // Fetch product details based on barcode or search text
        // You need to implement this part by querying your database or API
        // Create a Product object and add it to the stockItemList
        // Update the RecyclerView and total price

        firestore.collection("products")
                .whereEqualTo("product_name", barcodeOrSearchText) // Adjust the field name based on your Firestore structure
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            StockItem stockItem = new StockItem(product.getProduct_name(), 1, product.getBuying_price());
                            addItemToStock(stockItem);
                        }
                        etBarcodeOrSearch.setText("");
                        etBarcodeOrSearch.requestFocus();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e, Toast.LENGTH_SHORT).show();
                    // Handle error
                });
    }

    public void barCodeSearch(String barcodeOrSearchText) {
        firestore.collection("products")
                .whereEqualTo("barcode", barcodeOrSearchText)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            StockItem stockItem = new StockItem(product.getProduct_name(), 1, product.getBuying_price());
                            addItemToStock(stockItem);
                        }
                        etBarcodeOrSearch.setText("");
                        etBarcodeOrSearch.requestFocus();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e, Toast.LENGTH_SHORT).show();
                    // Handle error
                });
    }

    private void addItemToStock(StockItem stockItem) {
        // Add the StockItem to the list and update the RecyclerView
        stockItem.setPurchaseDate(new Date());

        stockItemList.add(stockItem);
        stockItemAdapter.notifyDataSetChanged();

        // Calculate the total price after adding the item
        calculateTotalPrice();
    }

    @SuppressLint("SetTextI18n")
    private void calculateTotalPrice() {
        totalPrice = 0.0;
        for (StockItem item : stockItemList) {
            totalPrice += item.getQuantity() * item.getPrice();
        }
        // Update the UI with the new total price
        totalPriceEdt.setText("Total: Ugx " + totalPrice);
    }

    // Update quantity of an existing stock item
    private void updateStockItemQuantity(int position, int newQuantity) {
        StockItem stockItem = stockItemList.get(position);
        stockItem.setQuantity(newQuantity);

        // Update the RecyclerView to reflect the new quantity
        stockItemAdapter.notifyItemChanged(position);

        // Recalculate the total price and update the UI
        calculateTotalPrice();
    }

    private void saveStock() {
        for (StockItem item : stockItemList) {
            int newQuantity = item.getQuantity();
            String productName = item.getProductName();

            // 1. Update the product quantity in the "products" collection
            updateProductQuantityInDatabase(productName, newQuantity);

            // 2. Add a purchase record to the "purchases" collection
            addPurchaseRecord(productName, newQuantity, item.getPurchaseDate());
        }

        // Clear the stockItemList (if needed)
        stockItemList.clear();
        stockItemAdapter.notifyDataSetChanged();

        // Calculate the total price again (UI update)
        calculateTotalPrice();

        // Inform the user about the successful save
        Toast.makeText(this, "Stock saved successfully!", Toast.LENGTH_SHORT).show();

    }
    private void updateProductQuantityInDatabase(String productName, int newQuantity) {
        // Update the product quantity in the "products" collection
        productsCollection
                .whereEqualTo("product_name", productName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String documentId = documentSnapshot.getId();
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            int currentQuantity = product.getQuantity();
                            int updatedQuantity = currentQuantity + newQuantity;

                            // Update the product quantity
                            productsCollection                                    .document(documentId)
                                    .update("quantity", updatedQuantity);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating product quantity: " + e, Toast.LENGTH_SHORT).show();
                });
    }

    private void addPurchaseRecord(String productName, int quantity, Date purchaseDate) {
        // Create a new purchase record and add it to the "purchases" collection
        Purchase purchase = new Purchase(productName, quantity, purchaseDate);

        purchasesCollection.add(purchase)
                .addOnSuccessListener(documentReference -> {
                    // Handle success if needed
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding purchase record: " + e, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onQuantityChange(int position, int newQuantity) {
        updateStockItemQuantity(position, newQuantity);

    }
}
