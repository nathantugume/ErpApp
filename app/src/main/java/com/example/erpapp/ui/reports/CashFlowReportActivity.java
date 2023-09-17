package com.example.erpapp.ui.reports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.erpapp.Classes.CashFlowItem;
import com.example.erpapp.R;
import com.example.erpapp.adapters.CashFlowAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CashFlowReportActivity extends AppCompatActivity {

    private Spinner timePeriodSpinner;
    private RecyclerView cashFlowRecyclerView;
    private CashFlowAdapter cashFlowAdapter;
    private Button fromDateButton; // Button to open the DatePicker dialog for fromDate
    private Button toDateButton; // Button to open the DatePicker dialog for toDate
    private Calendar fromDateCalendar = Calendar.getInstance(); // Calendar instance for fromDate
    private Calendar toDateCalendar = Calendar.getInstance(); // Calendar instance for toDate
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private   List<CashFlowItem> expensesData = new ArrayList<>();
    private     List<CashFlowItem> salesData = new ArrayList<>();

    private TextView netCashFlow;
    private TextView totalInFlow;
    private TextView totalOutFlow;
   private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_flow_report);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
         companyId = sharedPreferences.getString("companyId", null);

        MaterialToolbar toolbar;

        //        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Initialize UI components
        timePeriodSpinner = findViewById(R.id.timePeriodSpinner);
        totalInFlow = findViewById(R.id.totalInFlowTextView);
        totalOutFlow = findViewById(R.id.totalOutFlowTextView);
        netCashFlow = findViewById(R.id.netCashFlowTextView);
        cashFlowRecyclerView = findViewById(R.id.cashFlowRecyclerView);
        fromDateButton = findViewById(R.id.fromDateButton); // Initialize the fromDate button
        toDateButton = findViewById(R.id.toDateButton); // Initialize the toDate button

        // Initialize RecyclerView
        cashFlowRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cashFlowAdapter = new CashFlowAdapter(new ArrayList<>());
        cashFlowRecyclerView.setAdapter(cashFlowAdapter);

        // Initialize the Spinner with time period options (e.g., Daily, Weekly, Monthly, Yearly)
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_periods, // Define this array in your resources
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timePeriodSpinner.setAdapter(spinnerAdapter);

        // Set a listener to fetch and display data based on the selected time period
        timePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTimePeriod = parent.getItemAtPosition(position).toString();
                // Implement logic to fetch and display cash flow data based on selectedTimePeriod
                updateCashFlowData(selectedTimePeriod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected, if needed
            }
        });


        // Set an OnClickListener for the fromDate button to open the DatePicker dialog for fromDate
        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromDateDatePickerDialog();
            }
        });

        // Set an OnClickListener for the toDate button to open the DatePicker dialog for toDate
        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToDateDatePickerDialog();
            }
        });


    }

    private void showFromDateDatePickerDialog() {
        // Create a DatePickerDialog for fromDate
        DatePickerDialog fromDateDatePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // Handle the selected fromDate (year, month, day)
                        // Update the fromDateCalendar
                        fromDateCalendar.set(year, month, day);
                        // Set the fromDate text on the button (e.g., "From: yyyy-MM-dd")
                        fromDateButton.setText("From: " + year + "-" + (month + 1) + "-" + day);
                        // After selecting fromDate, update cash flow data
                        updateCashFlowData(getSelectedTimePeriod());
                    }
                },
                fromDateCalendar.get(Calendar.YEAR),
                fromDateCalendar.get(Calendar.MONTH),
                fromDateCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePicker dialog for fromDate
        fromDateDatePickerDialog.show();
    }

    private void showToDateDatePickerDialog() {
        // Create a DatePickerDialog for toDate
        DatePickerDialog toDateDatePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // Handle the selected toDate (year, month, day)
                        // Update the toDateCalendar
                        toDateCalendar.set(year, month, day);
                        // Set the toDate text on the button (e.g., "To: yyyy-MM-dd")
                        toDateButton.setText("To: " + year + "-" + (month + 1) + "-" + day);
                        // After selecting toDate, update cash flow data
                        updateCashFlowData(getSelectedTimePeriod());
                    }
                },
                toDateCalendar.get(Calendar.YEAR),
                toDateCalendar.get(Calendar.MONTH),
                toDateCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePicker dialog for toDate
        toDateDatePickerDialog.show();
    }

    private String getSelectedTimePeriod() {
        return timePeriodSpinner.getSelectedItem().toString();
    }

    private void updateCashFlowData(String selectedTimePeriod) {
        // Implement the logic to fetch and update cash flow data based on the selected time period or date range
        // You may fetch data from your Firestore database and calculate cash flow
        String fromDate = getDateFromDateCalendar(fromDateCalendar);
        String toDate = getDateFromDateCalendar(toDateCalendar);

        // Query expenses and sales collections in Firestore
        CollectionReference expensesRef = firestore.collection("expenses");
        CollectionReference salesRef = firestore.collection("sales");

        expensesRef
                .whereGreaterThanOrEqualTo("date", fromDate)
                .whereLessThanOrEqualTo("date", toDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot expenseSnapshot) {
                        // Fetch and process expense data
                        List<CashFlowItem> cashFlowData = new ArrayList<>();
                        for (QueryDocumentSnapshot document : expenseSnapshot) {
                            if (companyId.equals(document.getString("companyId"))){
                                String paymentDesc = document.getString("payment_desc");
                                double amount = document.getDouble("amount");
                                String date = document.getString("date");

                                cashFlowData.add(new CashFlowItem( date,-amount,paymentDesc));
                            }


                        }

                        // After fetching expense data, fetch sales data
                        salesRef
                                .whereGreaterThanOrEqualTo("saleDate", fromDate)
                                .whereLessThanOrEqualTo("saleDate", toDate)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onSuccess(QuerySnapshot salesSnapshot) {
                                        // Fetch and process sales data
                                        for (QueryDocumentSnapshot document : salesSnapshot) {

                                            if (companyId.equals(document.getString("companyId"))){
                                                String productName = document.getString("productName");
                                                Long amount = document.getLong("productPrice");
                                                String date = document.getString("saleDate");
                                                cashFlowData.add(new CashFlowItem(date,amount,productName ));
                                                Log.d("saleAmount","amount"+amount);
                                            }

                                        }

                                        // Calculate the total cash flow
                                        double totalCashFlow = 0.0;
                                        for (CashFlowItem item : cashFlowData) {
                                            totalCashFlow += item.getCashFlowAmount();
                                        }

                                        netCashFlow.setText("Net Cash Flow: Ugx "+totalCashFlow);

                                        // calculate net cash flow
                                        double outflow = 0.0;
                                        for (CashFlowItem item : cashFlowData) {
                                            if (item.getCashFlowAmount() < 0){
                                                outflow += item.getCashFlowAmount();

                                            }
                                        }
                                        // calculate the cash inflow
                                        double inFlow;
                                        totalOutFlow.setText("Out Flow: Ugx"+outflow);
                                        inFlow = totalCashFlow - outflow;
                                        totalInFlow.setText("In Flow: Ugx"+inFlow);

                                        // You can display the totalCashFlow or other calculated values as needed
                                        // For example, you can update a TextView to display the totalCashFlow
                                        cashFlowAdapter.setData(cashFlowData);
                                        cashFlowAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });
    }

    private String getDateFromDateCalendar(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
