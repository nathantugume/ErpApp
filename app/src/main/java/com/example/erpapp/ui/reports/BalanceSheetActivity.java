package com.example.erpapp.ui.reports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.erpapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BalanceSheetActivity extends AppCompatActivity {


    private TextView totalAssetsTextView;
    private TextView totalLiabilitiesTextView;
    private TextView equityTextView;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Button monthYearButton; // Button to select the "Month and Year"
    private Calendar selectedMonthYearCalendar = Calendar.getInstance();
    private TextView balanceSheetHeading;
    private double totalAssets;
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()); // You can specify the desired locale


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_sheet);

        // Initialize UI components

// Initialize UI components
        monthYearButton = findViewById(R.id.monthYearButton);
        totalAssetsTextView = findViewById(R.id.totalAssetsTextView);
        totalAssetsTextView = findViewById(R.id.totalAssetsTextView);
        totalLiabilitiesTextView = findViewById(R.id.totalLiabilitiesTextView);
        equityTextView = findViewById(R.id.equityTextView);
        balanceSheetHeading = findViewById(R.id.balanceSheetHeading);

        // Calenders
        // Set click listeners for From Date and To Date buttons
        // Set click listener for the "Month and Year" button
        monthYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a DatePickerDialog to select the month and year
                showMonthYearDatePickerDialog();
            }
        });


        // Fetch and display data for assets, liabilities, and equity

    }



    private void fetchAndDisplayAssetsData() {
        firestore.collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot productSnapshot) {
                        double totalAssets = 0.0;

                        for (QueryDocumentSnapshot document : productSnapshot) {
                            double price = document.getDouble("price");
                            double quantity = document.getDouble("quantity");
                            double assetValue = price * quantity;
                            totalAssets += assetValue;
                        }

                        // Display totalAssets and update the UI
                        totalAssetsTextView.setText("Total Assets: Ugx" + numberFormat.format(totalAssets) );
                    }
                });
    }


    private void updateBalanceSheet() {
        // Get the selected end date (last day of the selected month and year)
        String endDate = getDateFromDateCalendar(selectedMonthYearCalendar);

        // Query your Firestore collections with the selected end date
        // Replace "products", "sales", and "expenses" with your actual collection names
        CollectionReference productsRef = firestore.collection("products");
        CollectionReference salesRef = firestore.collection("sales");
        CollectionReference expensesRef = firestore.collection("expenses");

        // Fetch and display data for assets, liabilities, and equity
        fetchAndDisplayAssetsData(productsRef, endDate);
        fetchAndDisplayEquityData(expensesRef, endDate);
    }

    private void fetchAndDisplayAssetsData(CollectionReference productsRef, String endDate) {
        productsRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot productSnapshot) {
                         totalAssets = 0.0;

                        for (QueryDocumentSnapshot document : productSnapshot) {
                            double price = document.getDouble("price");
                            double quantity = document.getDouble("quantity");
                            double assetValue = price * quantity;
                            totalAssets += assetValue;
                        }

                        // Display totalAssets and update the UI
                        totalAssetsTextView.setText("Total Assets: Ugx" + numberFormat.format(totalAssets));
                    }
                });
    }

    private void fetchAndDisplayEquityData(CollectionReference expensesRef, String endDate) {
        expensesRef
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot expenseSnapshot) {
                        double totalExpenses = 0.0;

                        for (QueryDocumentSnapshot document : expenseSnapshot) {
                            double amount = document.getDouble("amount");
                            totalExpenses += amount;
                        }

                        // Calculate equity as (Total Assets - Total Expenses)

                        double equity = totalAssets - totalExpenses;

                        // Display equity and update the UI
                        // Display totalLiabilities and update the UI

                        equityTextView.setText("Equity: Ugx" + numberFormat.format(equity));
                        totalLiabilitiesTextView.setText("Total Liabilities: Ugx" + numberFormat.format(totalExpenses));
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private String getDateFromDateCalendar(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return dateFormat.format(calendar.getTime());
    }


    private void showMonthYearDatePickerDialog() {
        // Create a DatePickerDialog for Month and Year selection
        DatePickerDialog monthYearDatePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle the selected Month and Year
                        selectedMonthYearCalendar.set(Calendar.YEAR, year);
                        selectedMonthYearCalendar.set(Calendar.MONTH, month);

                        // Set the selected Month and Year as the "To Date"
                        setEndDateToLastDayOfMonth();

                        // Update the button text
                        updateMonthYearButtonText();

                        // After selecting the Month and Year, update the balance sheet
                        updateBalanceSheet();
                    }
                },
                selectedMonthYearCalendar.get(Calendar.YEAR),
                selectedMonthYearCalendar.get(Calendar.MONTH),
                selectedMonthYearCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show only the year and month (hide the day)
        monthYearDatePickerDialog.getDatePicker().setCalendarViewShown(false);
        monthYearDatePickerDialog.show();
    }

    private void setEndDateToLastDayOfMonth() {
        selectedMonthYearCalendar.set(Calendar.DAY_OF_MONTH, selectedMonthYearCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    @SuppressLint("SetTextI18n")
    private void updateMonthYearButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String monthYearText = dateFormat.format(selectedMonthYearCalendar.getTime());
        String fullDate = dateFormat2.format(selectedMonthYearCalendar.getTime());

        monthYearButton.setText("End of: " + monthYearText);
        balanceSheetHeading.setText("Balance Sheet as of "+fullDate);
        balanceSheetHeading.setVisibility(View.VISIBLE);
        balanceSheetHeading.setPaintFlags(balanceSheetHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


    }

}
