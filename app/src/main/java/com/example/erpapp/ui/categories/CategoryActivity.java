package com.example.erpapp.ui.categories;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator; // Import this

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.erpapp.Admin.AdminDashboardActivity;
import com.example.erpapp.R;
import com.example.erpapp.adapters.CategoryAdapter;
import com.example.erpapp.Fragments.AddCategoryDialogFragment;
import com.example.erpapp.ui.products.ProductsActivity;
import com.example.erpapp.ui.reports.ReportsActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private final List<DocumentSnapshot> categoryList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(CategoryActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(CategoryActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(CategoryActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(CategoryActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set custom item animator for RecyclerView
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500); // Set the animation duration in milliseconds
        recyclerView.setItemAnimator(itemAnimator);

        categoryAdapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(categoryAdapter);

        //load categories from firestore database
        loadCategoriesFromFirestore();

        //floating button for adding categories
        FloatingActionButton fab = findViewById(R.id.fab_add_category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCategoryDialogFragment addCategoryDialogFragment = new AddCategoryDialogFragment();
                addCategoryDialogFragment.show(getSupportFragmentManager(), "AddCategoryDialog");
            }
        });
    }

    private void loadCategoriesFromFirestore() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // Retrieve companyId from SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String companyId = sharedPreferences.getString("companyId", null);
        firestore.collection("categories")
                .whereEqualTo("companyId",companyId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int initialSize = categoryList.size();
                    categoryList.clear();
                    categoryList.addAll(queryDocumentSnapshots.getDocuments());
                    int finalSize = categoryList.size();
                    if (finalSize > initialSize) {
                        int startPosition = initialSize;
                        int itemCount = finalSize - initialSize;
                        categoryAdapter.notifyItemRangeInserted(startPosition, itemCount);
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();

                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}
