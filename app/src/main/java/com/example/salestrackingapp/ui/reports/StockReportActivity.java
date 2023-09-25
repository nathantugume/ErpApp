package com.example.salestrackingapp.ui.reports;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.example.salestrackingapp.R;


import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.salestrackingapp.Classes.StockItem;
import com.example.salestrackingapp.adapters.StockReportAdapter;
import com.example.salestrackingapp.ui.categories.CategoryActivity;
import com.example.salestrackingapp.ui.products.ProductsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockReportActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private List<StockItem> stockItemList = new ArrayList<>();
    private StockReportAdapter stockReportAdapter;
    private Source source = Source.DEFAULT;
    private String companyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        companyId = sharedPreferences.getString("companyId", null);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(StockReportActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(StockReportActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(StockReportActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(StockReportActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        MaterialToolbar toolbar;

        //        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Initialize RecyclerView and adapter
        RecyclerView stockRecyclerView = findViewById(R.id.stockRecyclerView);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockReportAdapter = new StockReportAdapter(stockItemList);
        stockRecyclerView.setAdapter(stockReportAdapter);

        // Fetch stock data from Firestore
        CollectionReference stockRef = firestore.collection("products");
        stockRef.whereEqualTo("companyId",companyId)
                .get(source)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Clear existing data
                        stockItemList.clear();

                        // Iterate through the documents
                        double totalPrice = 0;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String productName = document.getString("product_name");
                            int quantity = document.getLong("quantity").intValue(); // Convert Long to int
                            double price = document.getDouble("price");

                            totalPrice += price*quantity;

                            // Create a StockItem object
                            StockItem stockItem = new StockItem(productName, quantity, price);

                            // Add the item to the list

                            stockItemList.add(stockItem);
                        }

                        // Update the adapter
                        TextView count = findViewById(R.id.stockCount);
                        TextView totalPriceTextView = findViewById(R.id.stockTotal);
                        count.setText("Count: "+ (long) stockItemList.size());

                        NumberFormat number = NumberFormat.getInstance(Locale.getDefault());
                        totalPriceTextView.setText("Net Amount: Ugx "+number.format(totalPrice));
//                        stockReportAdapter.setData(stockItemList);
                        stockReportAdapter.notifyDataSetChanged();
                    }
                });
    }
}
