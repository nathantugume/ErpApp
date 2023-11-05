package com.example.salestrackingapp.ui.stock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import com.example.salestrackingapp.Classes.Product;
import com.example.salestrackingapp.Classes.Purchase;
import com.example.salestrackingapp.Classes.StockItem;
import com.example.salestrackingapp.Fragments.CaptureAct;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.adapters.OnPriceChangeListener;
import com.example.salestrackingapp.adapters.StockItemAdapter;
import com.example.salestrackingapp.ui.Users.LoginActivity;
import com.example.salestrackingapp.ui.settings.SettingsActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddStockActivity extends AppCompatActivity implements StockItem.OnQuantityChangeListener,OnPriceChangeListener {

    private final Source source = Source.DEFAULT;
    // Retrieve companyId from SharedPreferences

    private RecyclerView recyclerView;
    private StockItemAdapter stockItemAdapter;
    private List<StockItem> stockItemList = new ArrayList<>();
    private EditText etBarcodeOrSearch;
    private TextView totalPriceEdt;
    private double totalPrice = 0.0;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
//            etBarcodeOrSearch.setText(result.getContents());
            barCodeSearch(result.getContents());
        }
    });
    private CollectionReference productsCollection = firestore.collection("products");
    private CollectionReference purchasesCollection = firestore.collection("purchases");
    private Product product;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);
        // Retrieve companyId from SharedPreferences

        MaterialToolbar toolbar;

        //        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.sign_out){
                logout();
                return true;
            } else if (item.getItemId() == R.id.settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

            }
            return false;
        });
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        companyId = sharedPreferences.getString("companyId", null);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockItemAdapter = new StockItemAdapter(stockItemList, this, this::onItemDeleted, this::onPriceChange);

        recyclerView.setAdapter(stockItemAdapter);
        totalPriceEdt = findViewById(R.id.totalStock);
        // Attach swipe-to-delete functionality to the RecyclerView
        stockItemAdapter.attachItemTouchHelperToRecyclerView(recyclerView);
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

    @SuppressLint("NotifyDataSetChanged")
    private void fetchProducts(String barcodeOrSearchText) {
        // Fetch product details based on barcode or search text
        // You need to implement this part by querying your database or API
        // Create a Product object and add it to the stockItemList
        // Update the RecyclerView and total price

        firestore.collection("products")
                .orderBy("product_name")
                .startAt(barcodeOrSearchText)
                .endAt(barcodeOrSearchText+"\uf8ff")
                .get(source)
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Product product = documentSnapshot.toObject(Product.class);

                        if (companyId.equals(product.getCompanyId())) {
                            StockItem stockItem = new StockItem();
                            stockItem.setProductName(product.getProduct_name());
                            stockItem.setQuantity(1);
                            stockItem.setPrice(product.getBuying_price());
                            stockItemList.add(stockItem);
                            addItemToStock(stockItem);
                            stockItemAdapter.notifyDataSetChanged();

                            etBarcodeOrSearch.setText("");
                            etBarcodeOrSearch.requestFocus();
                        }


                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Log.d("listSum","sum"+stockItemList.size());
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Handle error
                });
    }

    private void addItemToStock(StockItem stockItem) {
        // Add the StockItem to the list and update the RecyclerView
        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        // Get the current date in the desired format
        String formattedDate = dateFormat.format(new Date());

        // Parse the formatted date string to obtain a Date object
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(formattedDate);
        } catch (ParseException e) {
            // Handle the parse exception, if needed
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Set the StockItem's purchase date using the parsed Date
        stockItem.setPurchaseDate(parsedDate);

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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                stockItemAdapter.notifyItemChanged(position);
            }
        });


        // Recalculate the total price and update the UI
        calculateTotalPrice();
    }

    private void saveStock() {
        for (StockItem item : stockItemList) {
            int newQuantity = item.getQuantity();
            String productName = item.getProductName();
            int newPrice = (int) item.getPrice();

            // 1. Update the product quantity in the "products" collection
            updateProductQuantityInDatabase(productName, newQuantity,newPrice);

            // 2. Add a purchase record to the "purchases" collection
            addPurchaseRecord(productName, newQuantity, item.getPurchaseDate(),newPrice);
        }

        // Clear the stockItemList (if needed)
        stockItemList.clear();
        stockItemAdapter.notifyDataSetChanged();

        // Calculate the total price again (UI update)
        calculateTotalPrice();

        // Inform the user about the successful save
        Toast.makeText(this, "Stock saved successfully!", Toast.LENGTH_SHORT).show();

    }

    private void updateProductQuantityInDatabase(String productName, int newQuantity, int newPrice) {
        // Update the product quantity in the "products" collection
        productsCollection
                .whereEqualTo("product_name", productName)
                .get(source)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String documentId = documentSnapshot.getId();
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            int currentQuantity = product.getQuantity();
                            int updatedQuantity = currentQuantity + newQuantity;


                            // Update the product quantity
                            productsCollection.document(documentId)
                                    .update("quantity", updatedQuantity,
                                            "buying_price",newPrice);

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating product quantity: " + e, Toast.LENGTH_SHORT).show();
                });
    }

    private void addPurchaseRecord(String productName, int quantity, Date purchaseDate,  int newPrice) {
        // Create a new purchase record and add it to the "purchases" collection
        Purchase purchase = new Purchase();
        purchase.setProductName(productName);
        purchase.setQuantity(quantity);
        purchase.setPurchaseDate(purchaseDate);
        purchase.setPrice(newPrice);
        purchase.setCompanyId(companyId);

        purchasesCollection.add(purchase)
                .addOnSuccessListener(documentReference -> {
                    // Handle success if needed
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding purchase record: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onQuantityChange(int position, int newQuantity) {
        updateStockItemQuantity(position, newQuantity);

    }

    @Override
    public void onItemDeleted(int position, StockItem deletedItem) {

            // Store the deleted item temporarily
            final StockItem deletedItemTemp = deletedItem;
            final int deletedItemPosition = position;


            // Show an "Undo" Snackbar
            Snackbar snackbar = Snackbar.make(findViewById(R.id.recyclerView),"Item deleted", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User clicked "Undo," so add the item back to the list at the same position
                    if (deletedItemPosition >= 0 && deletedItemPosition <= stockItemList.size()) {
                        stockItemList.add(deletedItemPosition, deletedItemTemp);
                        stockItemAdapter.notifyItemInserted(deletedItemPosition);
                        // Recalculate the total price again
                     calculateTotalPrice();
                    }
                }
            });



            snackbar.show();
            calculateTotalPrice();
        }

    @Override
    public void onPriceChange(int position, int newPrice) {
        try {
            StockItem item = stockItemList.get(position);

            // Update the price for the item at the given position
            item.setPrice(newPrice);

            // Recalculate the total price
            calculateTotalPrice();

            // Notify the adapter that the data at the specified position has changed
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    stockItemAdapter.notifyItemChanged(position);
                }
            });
        } catch (IndexOutOfBoundsException e) {
            // Handle the exception (e.g., log or display an error message)
            Log.d("Error","sorry"+e.getMessage());
        }
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // you can navigate to the login screen or perform any cleanup
        // After sign-out, you can navigate to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
