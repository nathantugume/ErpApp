package com.example.erpapp.ui.products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator; // Import this

import android.os.Bundle;
import android.view.View;

import com.example.erpapp.R;
import com.example.erpapp.adapters.CategoryAdapter;
import com.example.erpapp.Fragments.AddCategoryDialogFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
        firestore.collection("categories")
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
