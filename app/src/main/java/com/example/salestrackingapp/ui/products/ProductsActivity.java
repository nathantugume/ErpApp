package com.example.salestrackingapp.ui.products;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.example.salestrackingapp.Classes.Product;
import com.example.salestrackingapp.Fragments.AddProductDialogFragment;
import com.example.salestrackingapp.Fragments.CaptureAct;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.adapters.ProductAdapter;
import com.example.salestrackingapp.ui.Users.LoginActivity;
import com.example.salestrackingapp.ui.categories.CategoryActivity;
import com.example.salestrackingapp.ui.reports.ReportsActivity;
import com.example.salestrackingapp.ui.sales.SalesActivity;
import com.example.salestrackingapp.ui.settings.SettingsActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();
    private SearchView searchView;

    private  ShimmerFrameLayout shimmerFrameLayout;
    private TextView noProductsTextView;
    private Source source = Source.DEFAULT;

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            searchView.setQuery(result.getContents(),true);

        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        noProductsTextView = findViewById(R.id.noProductsTextView);

//        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //floating button for adding categories
        FloatingActionButton fab = findViewById(R.id.fab_add_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProductDialogFragment addProductDialogFragment = new AddProductDialogFragment(productAdapter);
                addProductDialogFragment.show(getSupportFragmentManager(), "AddProductDialog");
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(ProductsActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(ProductsActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(ProductsActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(ProductsActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });


        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search_view);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);


        // Initialize RecyclerView and set its adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this,productList);
        recyclerView.setAdapter(productAdapter);



        // Load products from Firestore
        loadProductsFromFirestore();

        // Set up search functionality
        setupSearchView();

    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Reload all items if the search query is empty
                    productAdapter.updateData(productList);
                } else {
                    // Filter the products based on the search query
                    List<Product> filteredProducts = filterProducts(productList, newText);
                    productAdapter.updateData(filteredProducts);
                    productAdapter.notifyDataSetChanged();
                }
                return true;
            }

        });
        searchView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                scanner();
                return true;
            }
        });

    }
    private void scanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up for flash light ");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private List<Product> filterProducts(List<Product> products, String query) {
        query = query.toLowerCase(Locale.getDefault());
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getProduct_name().toLowerCase(Locale.getDefault()).contains(query) ||
                    product.getBarcode().toLowerCase(Locale.getDefault()).contains(query)) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProductsFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        // Retrieve companyId from SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String companyId = sharedPreferences.getString("companyId", null);

        firestore.collection("products")
                .whereEqualTo("companyId", companyId)
                .get(source)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear(); // Clear the existing list before populating with new data

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        productList.add(product);
                    }

                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    // Check if there are no products
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Show the "No Products" TextView and hide the RecyclerView
                        recyclerView.setVisibility(View.GONE);
                        noProductsTextView.setVisibility(View.VISIBLE);
                    } else {
                        // Show the RecyclerView and hide the "No Products" TextView
                        recyclerView.setVisibility(View.VISIBLE);
                        noProductsTextView.setVisibility(View.GONE);

                        // Notify the adapter of the data change
                        productAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
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
