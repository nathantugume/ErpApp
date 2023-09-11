package com.example.erpapp.ui.reports;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.SalesItem;
import com.example.erpapp.R;
import com.example.erpapp.adapters.SalesAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

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

        firestore.collection("sales")
                .whereGreaterThanOrEqualTo("saleDate", fromDate)
                .whereLessThanOrEqualTo("saleDate", toDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<SalesItem> salesItemList = new ArrayList<>();
                        double totalSales = 0.0; // Initialize total sales

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String saleDate = document.getString("saleDate");
                            String productName = document.getString("productName");
                            double saleAmount = document.getDouble("productPrice");
                            Long quantity =  document.getLong("quantity");

                            Log.d("product", "name "+productName);

                            SalesItem salesItem = new SalesItem(saleDate, productName, saleAmount, quantity);
                            salesItemList.add(salesItem);
                            totalSales += saleAmount*quantity;
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
}
