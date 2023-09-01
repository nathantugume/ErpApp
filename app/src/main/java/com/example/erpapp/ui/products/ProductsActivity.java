package com.example.erpapp.ui.products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.Fragments.AddCategoryDialogFragment;
import com.example.erpapp.Fragments.AddProductDialogFragment;
import com.example.erpapp.R;
import com.example.erpapp.adapters.ProductAdapter;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private SearchView searchView;

    private  ShimmerFrameLayout shimmerFrameLayout;

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

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search_view);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);


        // Initialize RecyclerView and set its adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList);
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
                }
                return true;
            }

        });
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
