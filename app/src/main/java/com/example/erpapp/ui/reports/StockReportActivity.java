package com.example.erpapp.ui.reports;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.erpapp.Admin.AdminDashboardActivity;
import com.example.erpapp.R;



import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.erpapp.Classes.StockItem;
import com.example.erpapp.R;
import com.example.erpapp.adapters.StockReportAdapter;
import com.example.erpapp.ui.categories.CategoryActivity;
import com.example.erpapp.ui.products.ProductsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockReportActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private List<StockItem> stockItemList = new ArrayList<>();
    private StockReportAdapter stockReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);

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
        stockRef
                .get()
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

                            Log.d("stock", "name"+price);
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
                        stockReportAdapter.setData(stockItemList);
                        stockReportAdapter.notifyDataSetChanged();
                    }
                });
    }
}
