package com.example.erpapp.ui.products;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.erpapp.Admin.AdminDashboardActivity;
import com.example.erpapp.Classes.Product;
import com.example.erpapp.Fragments.AddCategoryDialogFragment;
import com.example.erpapp.Fragments.AddProductDialogFragment;
import com.example.erpapp.Fragments.CaptureAct;
import com.example.erpapp.R;
import com.example.erpapp.adapters.ProductAdapter;
import com.example.erpapp.ui.categories.CategoryActivity;
import com.example.erpapp.ui.reports.ReportsActivity;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            searchView.setQuery(result.getContents(),true);

        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

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
                AddProductDialogFragment addProductDialogFragment = new AddProductDialogFragment();
                addProductDialogFragment.show(getSupportFragmentManager(), "AddProductDialog");
            }
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

    private void loadProductsFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        firestore.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear(); // Clear the existing list before populating with new data

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        productList.add(product);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }


                    productAdapter.hasObservers();
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }


}
