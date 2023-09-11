package com.example.erpapp.ui.reports;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.erpapp.R;



import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.erpapp.Classes.StockItem;
import com.example.erpapp.R;
import com.example.erpapp.adapters.StockReportAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
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
