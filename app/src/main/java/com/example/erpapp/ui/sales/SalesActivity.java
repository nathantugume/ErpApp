package com.example.erpapp.ui.sales;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.Classes.Sale;
import com.example.erpapp.Classes.SwipeToDeleteCallback;
import com.example.erpapp.Fragments.CaptureAct;
import com.example.erpapp.R;
import com.example.erpapp.adapters.SalesProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesActivity extends AppCompatActivity implements SalesProductAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private TextView totalPriceTextView;

    private List<Product> productList = new ArrayList<>();
    private List<Product> salesList = new ArrayList<>(); // List to store selected products
    private SalesProductAdapter productAdapter;
    private FirebaseFirestore firestore; // Declare Firestore instance
    private EditText etBarcodeOrSearch;
    private SalesProductAdapter.OnItemRemovedListener onItemRemovedListener;
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            etBarcodeOrSearch.setText(result.getContents());

        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        firestore = FirebaseFirestore.getInstance();

        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        FloatingActionButton fabSaveProduct = findViewById(R.id.fabSaveProduct);
        fabAddProduct.setOnClickListener(view -> showAddProductDialog());
        fabSaveProduct.setOnClickListener(view -> saveProduct());
        recyclerView = findViewById(R.id.recyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new SalesProductAdapter(productList, this, onItemRemovedListener);
        productAdapter.setSalesList(salesList);

        recyclerView.setAdapter(productAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(productList, productAdapter, salesList));
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    private void saveProduct() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new Firestore collection reference for sales
        CollectionReference salesRef = db.collection("sales");
        CollectionReference productsRef = db.collection("products");

        // Get the current date and time
        Date currentDate = new Date();
        // Create a SimpleDateFormat or any date/time formatting method to format the date and time as needed
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        String formattedTime = timeFormat.format(currentDate);

        // Iterate through the salesList and save each product as a document in the sales collection
        for (Product product : salesList) {
            int quantityToSubtract = product.getQuantity(); // Specify the quantity to subtract

            // Define the Firestore document reference for the product
            DocumentReference productRef = productsRef.document(product.getProductId());

            // Create a map to update the quantity field with the subtraction
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("quantity", FieldValue.increment(-quantityToSubtract));

            // Update the Firestore document to subtract the quantity
            productRef.update(updateMap)
                    .addOnSuccessListener(aVoid -> {
                        // Quantity updated successfully
                        // Now you can proceed with creating the sale record

                        // Create a new document with a unique ID (Firestore will generate one)
                        DocumentReference saleDocRef = salesRef.document();

                        // Create a Sale object representing the sale, including the product details, date, and time
                        Sale sale = new Sale(
                                saleDocRef.getId(),   // Use the document ID as the sale ID
                                product.getProductId(),
                                product.getProduct_name(),
                                product.getPrice(),
                                quantityToSubtract, // Use the specified quantity to sell
                                formattedDate, // Set the saleDate to the current date
                                formattedTime  // Set the saleTime to the current time
                        );

                        // Use the set method to save the Sale object as a document in the sales collection
                        saleDocRef.set(sale)
                                .addOnSuccessListener(documentReference -> {
                                    // Successfully uploaded sale data to Firestore
                                    Toast.makeText(this, "Sale data uploaded successfully", Toast.LENGTH_SHORT).show();

                                    // Clear the productList and notify the adapter of the change
                                    productList.clear();
                                    productAdapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to upload sale data
                                    Toast.makeText(this, "Error uploading sale data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update the quantity
                        Toast.makeText(this, "Error updating quantity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        // Create a Product object and add it to the sales list
        // Update the RecyclerView and total price

        firestore.collection("products")
                .whereEqualTo("product_name", barcodeOrSearchText) // Adjust the field name based on your Firestore structure
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String txt = "";
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        // Assuming you have a Product class with appropriate setters/getters

                        txt = documentSnapshot.getString("product_name");
                        Product product = documentSnapshot.toObject(Product.class);
                        addItemToSales(product);

                        etBarcodeOrSearch.setText("");
                        etBarcodeOrSearch.requestFocus();
                    }
                    Log.d("txt", txt);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e, Toast.LENGTH_SHORT).show();
                    // Handle error
                });


    }


    private void updateSalesList() {
        productAdapter.updateData(salesList);
        productAdapter.notifyDataSetChanged(); // Notify the adapter that data has changed

        // Update the total price display based on the current sales list
        updateTotalPrice();
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (Product product : salesList) {
            total += product.getPrice() * product.getQuantity(); // Calculate total price for each item

            Log.d("toto", "totalqty" + total);
        }
        return total;
    }


    private void addItemToSales(Product product) {
        // Check if the product already exists in the salesList
        boolean productExists = false;
        for (Product p : salesList) {
            if (p.getProductId().equals(product.getProductId())) {
                // Product already exists, update its quantity
                p.setQuantity(p.getQuantity() + 1);
                productExists = true;
                Toast.makeText(this, "product already added", Toast.LENGTH_SHORT).show();


                break; // No need to continue searching
            }
        }

        if (!productExists) {
            // Product doesn't exist in the list, add it
            product.setQuantity(1); // Set initial quantity to 1
            salesList.add(product);

        }

        // Update the total price after adding or updating an item
        updateSalesList();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double totalPrice = calculateTotalPrice();
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));
    }


    public void barCodeSearch(String barcodeOrSearchText) {
        firestore.collection("products")
                .whereEqualTo("barcode", barcodeOrSearchText)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String txt = "";
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        // Assuming you have a Product class with appropriate setters/getters

                        txt = documentSnapshot.getString("product_name");
                        Product product = documentSnapshot.toObject(Product.class);
                        addItemToSales(product);
                        etBarcodeOrSearch.setText("");
                        etBarcodeOrSearch.requestFocus();
                    }
                    Log.d("txt", txt);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching the product" + e, Toast.LENGTH_SHORT).show();
                    // Handle error
                });
    }

    @Override
    public void onItemClick(Product product) {
        Toast.makeText(this, "Clicked on: " + product.getProduct_name(), Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onQuantityChange(Product product, int newQuantity) {
        // Handle quantity change here
        // For example, you can update the total price or perform other operations


        // You can also update the product's quantity in the salesList
        // Find the product in the salesList and update its quantity
        for (Product p : salesList) {
            if (p.getProductId().equals(product.getProductId())) {
                p.setQuantity(newQuantity);
                break; // No need to continue searching
            }
        }
        updateTotalPrice();
    }


    @Override
    public int getQuantity(Product product) {
        return 0;
    }

    @Override
    public int getPrice(Product product) {
        return product.getPrice();
    }

}
