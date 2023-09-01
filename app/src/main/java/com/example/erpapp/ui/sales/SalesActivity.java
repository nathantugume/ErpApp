package com.example.erpapp.ui.sales;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.Fragments.CaptureAct;
import com.example.erpapp.R;
import com.example.erpapp.adapters.SalesProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesActivity extends AppCompatActivity implements  SalesProductAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private TextView totalPriceTextView;

    private List<Product> productList = new ArrayList<>();
    private List<Product> salesList = new ArrayList<>(); // List to store selected products
    private SalesProductAdapter productAdapter;
    private FirebaseFirestore firestore; // Declare Firestore instance
    private EditText etBarcodeOrSearch;
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            etBarcodeOrSearch.setText(result.getContents());
            fetchProducts();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        firestore = FirebaseFirestore.getInstance();

        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(view -> showAddProductDialog());
        recyclerView = findViewById(R.id.recyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new SalesProductAdapter(productList,this);

        recyclerView.setAdapter(productAdapter);

        // Set up search functionality

    }



    private void showAddProductDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_sales, null);
        dialogBuilder.setView(dialogView);

        etBarcodeOrSearch = dialogView.findViewById(R.id.etBarcodeOrSearch);
        Button btnFetchProductDetails = dialogView.findViewById(R.id.btnFetchProductDetails);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        barCodeSearch();

        btnFetchProductDetails.setOnClickListener(v -> {
            fetchProducts();
            dialog.dismiss();
        });
    }

    private void fetchProducts() {
        String barcodeOrSearchText = etBarcodeOrSearch.getText().toString();
        // Fetch product details based on barcode or search text
        // You need to implement this part by querying your database or API
        // Create a Product object and add it to the sales list
        // Update the RecyclerView and total price

        Log.d("seachText", barcodeOrSearchText);
        firestore.collection("products")
                .whereEqualTo("barcode", barcodeOrSearchText) // Adjust the field name based on your Firestore structure

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
       double totalPrice = calculateTotalPrice();
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (Product product : salesList) {
            // Assuming product.getPrice() returns a double representing the price
            total += product.getPrice();
        }
        return total;
    }

    private void addItemToSales(Product product) {
        // Add the selected product to the sales list
        // Update the RecyclerView and total price
        salesList.add(product);
        updateSalesList();
    }

    public void barCodeSearch() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_sales, null);
        dialogBuilder.setView(dialogView);

        etBarcodeOrSearch = dialogView.findViewById(R.id.etBarcodeOrSearch);

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

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onItemClick(Product product) {
        Toast.makeText(this, "Clicked on: " + product.getProduct_name(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onQuantityChange(Product product, int newQuantity) {
        // Handle quantity change here
        // For example, you can update the total price or perform other operations
        double totalPrice = calculateTotalPrice(); // Recalculate total price
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));

        // You can also update the product's quantity in the salesList
        // Find the product in the salesList and update its quantity
        for (Product p : salesList) {
            if (p.getProductId().equals(product.getProductId())) {
                p.setQuantity(newQuantity);
                break; // No need to continue searching
            }
        }
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
