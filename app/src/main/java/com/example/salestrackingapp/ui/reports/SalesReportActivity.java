package com.example.salestrackingapp.ui.reports;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salestrackingapp.Classes.SalesItem;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.adapters.SalesAdapter;
import com.example.salestrackingapp.ui.Users.LoginActivity;
import com.example.salestrackingapp.ui.settings.SettingsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesReportActivity extends AppCompatActivity {

    private Button fromDateButton;
    private Button toDateButton;
    private RecyclerView salesRecyclerView;
    private SalesAdapter salesAdapter;
    private Calendar fromDateCalendar = Calendar.getInstance();
    private Calendar toDateCalendar = Calendar.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()); // You can specify the desired locale
   private MaterialToolbar toolbar;
    private String companyId;
    private ExtendedFloatingActionButton printBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        companyId = sharedPreferences.getString("companyId", null);
        printBtn = findViewById(R.id.print_blc);
        printBtn.setOnClickListener(view -> printDoc());

        //        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        salesRecyclerView = findViewById(R.id.salesRecyclerView);

        // Initialize RecyclerView
        salesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        salesAdapter = new SalesAdapter(new ArrayList<>());
        salesRecyclerView.setAdapter(salesAdapter);

        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromDateDatePickerDialog();
            }
        });

        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToDateDatePickerDialog();
            }
        });

        // Fetch and display initial sales data
        updateSalesReportData();
    }

    private void showFromDateDatePickerDialog() {
        DatePickerDialog fromDateDatePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fromDateCalendar.set(year, month, dayOfMonth);
                        fromDateButton.setText("From Date: " + formatDate(fromDateCalendar.getTime()));
                        updateSalesReportData();
                    }
                },
                fromDateCalendar.get(Calendar.YEAR),
                fromDateCalendar.get(Calendar.MONTH),
                fromDateCalendar.get(Calendar.DAY_OF_MONTH)
        );
        fromDateDatePickerDialog.show();
    }

    private void showToDateDatePickerDialog() {
        DatePickerDialog toDateDatePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        toDateCalendar.set(year, month, dayOfMonth);
                        toDateButton.setText("To Date: " + formatDate(toDateCalendar.getTime()));
                        updateSalesReportData();
                    }
                },
                toDateCalendar.get(Calendar.YEAR),
                toDateCalendar.get(Calendar.MONTH),
                toDateCalendar.get(Calendar.DAY_OF_MONTH)
        );
        toDateDatePickerDialog.show();
    }

    private void updateSalesReportData() {
        // Fetch and display sales data based on the selected date range
        String fromDate = formatDate(fromDateCalendar.getTime());
        String toDate = formatDate(toDateCalendar.getTime());

        Source source = Source.CACHE;

        firestore.collection("sales")
                .whereGreaterThanOrEqualTo("saleDate", fromDate)
                .whereLessThanOrEqualTo("saleDate", toDate)
                .get(source)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<SalesItem> salesItemList = new ArrayList<>();
                        double totalSales = 0.0; // Initialize total sales

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.getString("companyId").equals(companyId)){
                                String saleDate = document.getString("saleDate");
                                String productName = document.getString("productName");
                                double saleAmount = document.getDouble("productPrice");
                                Long quantity =  document.getLong("quantity");
                                String companyId = document.getString("companyId");

                                Log.d("product", "name "+productName);

                                SalesItem salesItem = new SalesItem(saleDate, productName, saleAmount, quantity,companyId);
                                salesItemList.add(salesItem);
                                totalSales += saleAmount*quantity;
                            }

                        }
                        // Display the total sales in a TextView
                        TextView totalSalesTextView = findViewById(R.id.totalSalesAmountTextView);
                        totalSalesTextView.setText("Total Sales: Ugx" + numberFormat.format(totalSales));

                        TextView totalCount = findViewById(R.id.totalSalesCountTextView);
                        totalCount.setText("Total Count: "+salesItemList.size());

                        // Update the adapter with the new data
                        salesAdapter.setData(salesItemList);
                        salesAdapter.notifyDataSetChanged();
                    }
                });
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    private void printDoc() {
        toolbar.setVisibility(View.GONE);
        printBtn.setVisibility(View.GONE);
//        bottomNavigationView.setVisibility(View.GONE);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        // Convert the layout to a bitmap
        Bitmap bitmap = loadBitmapFromView(rootView);

        // Use PrintHelper to print the bitmap
        PrintHelper printHelper = new PrintHelper(this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        printHelper.printBitmap("cash_flow.pdf", bitmap);

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

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // you can navigate to the login screen or perform any cleanup
        // After sign-out, you can navigate to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
