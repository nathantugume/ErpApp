package com.example.salestrackingapp.ui.reports;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.example.salestrackingapp.Classes.PrintBitmapDocumentAdapter;
import com.example.salestrackingapp.R;


import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.salestrackingapp.Classes.StockItem;
import com.example.salestrackingapp.adapters.CustomPrintDocumentAdapter;
import com.example.salestrackingapp.adapters.StockReportAdapter;
import com.example.salestrackingapp.ui.categories.CategoryActivity;
import com.example.salestrackingapp.ui.products.ProductsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    MaterialToolbar toolbar;
    private ExtendedFloatingActionButton printBtn;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);

         printBtn = findViewById(R.id.print_btn);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        companyId = sharedPreferences.getString("companyId", null);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
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

        printBtn.setOnClickListener(view -> printDoc());
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

    private void printDoc() {
        toolbar.setVisibility(View.GONE);
        printBtn.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        // Convert the layout to a bitmap
        Bitmap bitmap = loadBitmapFromView(rootView);

        // Use PrintHelper to print the bitmap
        PrintHelper printHelper = new PrintHelper(this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        printHelper.printBitmap("Stock_Report.pdf", bitmap);

    }

    public static Bitmap loadBitmapFromView(View view) {

        // Remove the top and bottom app bars from the view
        view.setPadding(0, 10, 0, 10);


        // Measure and layout the view
        view.measure(
                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        // Create a bitmap of the layout
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


}
