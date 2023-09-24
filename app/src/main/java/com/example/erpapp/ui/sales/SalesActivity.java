package com.example.erpapp.ui.sales;

// Import statements...

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.Classes.Sale;
import com.example.erpapp.Classes.SaleManager;
import com.example.erpapp.Classes.SalesReceiptPDFGenerator;
import com.example.erpapp.Classes.SettingsManager;
import com.example.erpapp.Classes.SwipeToDeleteCallback;
import com.example.erpapp.Fragments.CaptureAct;
import com.example.erpapp.R;
import com.example.erpapp.adapters.ReceiptProductAdapter;
import com.example.erpapp.adapters.SalesProductAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SalesActivity extends AppCompatActivity implements SalesProductAdapter.OnItemClickListener {

    private final List<Product> productList = new ArrayList<>();
    private final List<Product> salesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView totalPriceTextView;
    private SalesProductAdapter productAdapter;
    private FirebaseFirestore firestore;
    private EditText etBarcodeOrSearch;
    private SalesProductAdapter.OnItemRemovedListener onItemRemovedListener;
    private String companyId;
    private Source source = Source.SERVER;
    private SettingsManager settingsManager;
    private SaleManager saleManager;
    private long minimumStock = Long.valueOf(0);

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            barCodeSearch(result.getContents());
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        firestore = FirebaseFirestore.getInstance();
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        FloatingActionButton fabSaveProduct = findViewById(R.id.fabSaveProduct);
        FloatingActionButton fabPrint = findViewById(R.id.fabPrint);
        fabAddProduct.setOnClickListener(view -> showAddProductDialog());
        fabSaveProduct.setOnClickListener(view -> saveProduct());
        fabPrint.setOnClickListener(view -> printReceipt());
        recyclerView = findViewById(R.id.recyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        companyId = sharedPreferences.getString("companyId", null);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new SalesProductAdapter(productList, this, onItemRemovedListener, totalPriceTextView);
        productAdapter.setSalesList(salesList);
        recyclerView.setAdapter(productAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(productList, productAdapter, salesList, totalPriceTextView));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        settingsManager = new SettingsManager(companyId, this);
        saleManager = new SaleManager(this, true);
        fetchMinimumStockSetting();
    }

    private void printReceipt() {
        // Inflate the custom receipt layout
        View receiptView = getLayoutInflater().inflate(R.layout.receipt_layout, null);

// Find views in the layout
        TextView companyNameTextView = receiptView.findViewById(R.id.companyNameTextView);
        TextView companyAddressTextView = receiptView.findViewById(R.id.companyAddressTextView);
        RecyclerView receiptRecyclerView = receiptView.findViewById(R.id.receiptRecyclerView);
        TextView totalPriceTextView = receiptView.findViewById(R.id.totalPriceTextView);

// Populate the views with data
        companyNameTextView.setText("Your Company Name");
        companyAddressTextView.setText("123 Main St, City, Country");

// Set up a RecyclerView adapter for the product list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        receiptRecyclerView.setLayoutManager(layoutManager);
        ReceiptProductAdapter productAdapter = new ReceiptProductAdapter(salesList);
        receiptRecyclerView.setAdapter(productAdapter);

// Calculate and set the total price
        double total = calculateTotalPrice();
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: Ugx%.2f", total));

// Create a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

// Check if there is a printer available
        if (printManager != null) {
            // Create a print job
            PrintJob printJob = printManager.print("SalesReceipt",new SalesReceiptPDFGenerator(this,salesList) , null);

            // Check if the print job was successfully created
            if (printJob != null) {
                // Notify the user that printing has started
                Toast.makeText(this, "Printing receipt...", Toast.LENGTH_SHORT).show();
            } else {
                // Notify the user if printing failed
                Toast.makeText(this, "Failed to start printing", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Notify the user if no printing service is available
            Toast.makeText(this, "Printing service is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProduct() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference salesRef = db.collection("sales");
        CollectionReference productsRef = db.collection("products");

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        String formattedTime = timeFormat.format(currentDate);

        for (Product product : salesList) {
            saleManager.checkSaleSettingsAndMonitorStock(product, new SaleManager.SalePermissionCallback() {
                @Override
                public void onSalePermissionCheck(boolean isAllowed) {
                    if (isAllowed) {
                        int quantityToSubtract = product.getQuantity();
                        DocumentReference productRef = productsRef.document(product.getProductId());
                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("quantity", FieldValue.increment(-quantityToSubtract));

                        productRef.update(updateMap)
                                .addOnSuccessListener(aVoid -> {
                                    DocumentReference saleDocRef = salesRef.document();
                                    Sale sale = new Sale();
                                    sale.setSaleId(saleDocRef.getId());
                                    sale.setProductId(product.getProductId());
                                    sale.setProductName(product.getProduct_name());
                                    sale.setProductPrice(product.getPrice());
                                    sale.setQuantity(quantityToSubtract);
                                    sale.setSaleTime(formattedTime);
                                    sale.setSaleDate(formattedDate);
                                    sale.setSaleBy(product.getSaleBy());
                                    sale.setSaleType(product.getSaleType());
                                    sale.setCompanyId(product.getCompanyId());

                                    saleDocRef.set(sale)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(SalesActivity.this, "Sale data uploaded successfully", Toast.LENGTH_SHORT).show();
                                                productList.clear();
                                                productAdapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(SalesActivity.this, "Error uploading sale data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SalesActivity.this, "Error updating quantity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        salesList.remove(product);
                        productAdapter.notifyDataSetChanged();
                        Toast.makeText(SalesActivity.this, "Item removed due to insufficient stock " + product.getProduct_name(), Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_sales, null);
        dialogBuilder.setView(dialogView);

        etBarcodeOrSearch = dialogView.findViewById(R.id.etBarcodeOrSearch);
        Button btnFetchProductDetails = dialogView.findViewById(R.id.btnFetchProductDetails);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        etBarcodeOrSearch.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("volume up for flash light ");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);
            barLauncher.launch(options);
        });

        btnFetchProductDetails.setOnClickListener(v -> {
            String searchTerm = etBarcodeOrSearch.getText().toString().trim();
            if (!searchTerm.isEmpty()) {
                if (isNumeric(searchTerm)) {
                    barCodeSearch(searchTerm);
                } else {
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

    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private void fetchProducts(String barcodeOrSearchText) {
        firestore.collection("products")
                .orderBy("product_name")
                .startAt(barcodeOrSearchText)
                .endAt(barcodeOrSearchText + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String txt = documentSnapshot.getString("companyId");
                        if (Objects.equals(companyId, txt)) {
                            Product product = documentSnapshot.toObject(Product.class);
                            addItemToSales(product);
                            etBarcodeOrSearch.setText("");
                            etBarcodeOrSearch.requestFocus();
                        }

                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e, Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateSalesList() {
        productAdapter.updateData(salesList);
        productAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (Product product : salesList) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    private void updateTotalPrice() {
        double totalPrice = calculateTotalPrice();
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));
    }

    private void addItemToSales(Product product) {
        // Check if the product already exists in the salesList
        boolean productExists = false;
        for (Product p : salesList) {
            if (p.getProductId().equals(product.getProductId())) {
                // Product already exists, update its quantity
                p.setQuantity(p.getQuantity() + 1);
                productExists = true;
                Toast.makeText(SalesActivity.this, "Product quantity updated", Toast.LENGTH_SHORT).show();
                break; // No need to continue searching
            }
        }

        if (!productExists) {
            // Product doesn't exist in the list, add it
            product.setQuantity(1); // Set initial quantity to 1
            salesList.add(product);
            Toast.makeText(SalesActivity.this, "Product added to sales", Toast.LENGTH_SHORT).show();
        }

        // Update the total price after adding or updating an item
        updateSalesList();
        updateTotalPrice();
    }


    public void barCodeSearch(String barcodeOrSearchText) {
        firestore.collection("products")
                .whereEqualTo("barcode", barcodeOrSearchText)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String id = documentSnapshot.getString("companyId");
                        if (companyId.equals(id)) {
                            Product product = documentSnapshot.toObject(Product.class);
                            addItemToSales(product);
                            etBarcodeOrSearch.setText("");
                            etBarcodeOrSearch.requestFocus();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemClick(Product product) {
        Toast.makeText(this, "Clicked on: " + product.getProduct_name(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuantityChange(Product product, int newQuantity) {
        saleManager.checkSaleSettingsAndMonitorStock(product, new SaleManager.SalePermissionCallback() {
            @Override
            public void onSalePermissionCheck(boolean isAllowed) {
                if (isAllowed) {
                    // Sale is allowed, proceed with updating the product's quantity
                    for (Product p : salesList) {
                        if (p.getProductId().equals(product.getProductId())) {
                            p.setQuantity(newQuantity);
                            break;
                        }
                    }

                    // Update the UI and total price
                    updateSalesList();
                    updateTotalPrice();
                } else {
                    // Sale is not allowed, show a message or take appropriate action
                    Toast.makeText(SalesActivity.this, "Sale not allowed due to insufficient stock.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public SalesProductAdapter.OnItemRemovedListener getOnItemRemovedListener() {
        calculateTotalPrice();
        return onItemRemovedListener;
    }

    public void setOnItemRemovedListener(SalesProductAdapter.OnItemRemovedListener onItemRemovedListener) {
        this.onItemRemovedListener = onItemRemovedListener;
    }

    @Override
    public int getQuantity(Product product) {
        return 0;
    }

    @Override
    public int getPrice(Product product) {
        return product.getPrice();
    }

    @Override
    public void onPriceChange(Product product, double newPrice) {
        updateTotalPrice();
    }

    // Method to fetch the minimum stock setting from Firestore
    private void fetchMinimumStockSetting() {
        settingsManager.fetchSettingsFromFirestore(new SettingsManager.OnSettingsFetchedListener() {
            @Override
            public void onSettingsFetched(Map<String, Object> settings) {
                if (settings.containsKey("minimumStock")) {
                    minimumStock = (long) settings.get("minimumStock");
                    // Now you have the minimum stock value, and you can use it as needed
                    // For example, you can check this value against your product quantities
                    checkProductQuantitiesAgainstMinimumStock();
                }
            }

            @Override
            public void onSettingsNotFound() {
                // Handle the case where settings document doesn't exist
                // You may want to use a default value in this case
                minimumStock = 5; // Use a default value
                checkProductQuantitiesAgainstMinimumStock();
            }

            @Override
            public void onSettingsFetchError(Exception e) {
                // Handle the error case when fetching settings
                // You may want to use a default value in this case
                minimumStock = 5; // Use a default value
                checkProductQuantitiesAgainstMinimumStock();
            }
        });
    }

    // Method to check product quantities against the minimum stock setting
    private void checkProductQuantitiesAgainstMinimumStock() {
        for (Product product : salesList) {
            if (product.getQuantity() < minimumStock) {
                // Product quantity is below the minimum stock
                // Show a notification (in this example, using Toast)
                String notificationMessage = "Product " + product.getProduct_name() + " is below minimum stock!";
                Toast.makeText(this, notificationMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
