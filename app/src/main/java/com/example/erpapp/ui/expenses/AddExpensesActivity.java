package com.example.erpapp.ui.expenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.erpapp.Admin.AdminDashboardActivity;
import com.example.erpapp.Classes.Expense;
import com.example.erpapp.Fragments.AddExpenseFragment;
import com.example.erpapp.Fragments.AddProductDialogFragment;
import com.example.erpapp.R;
import com.example.erpapp.adapters.ExpenseAdapter;
import com.example.erpapp.ui.categories.CategoryActivity;
import com.example.erpapp.ui.products.ProductsActivity;
import com.example.erpapp.ui.reports.ReportsActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddExpensesActivity extends AppCompatActivity {
    private RecyclerView expenseRecyclerView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseItemList;
    // Initialize Firestore
    private  FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference expensesCollection = firestore.collection("expenses");
    private ShimmerFrameLayout shimmerFrameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        MaterialToolbar toolbar;

        //        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(AddExpensesActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(AddExpensesActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(AddExpensesActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(AddExpensesActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });
        // Initialize the RecyclerView
        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list of expenses and adapter
                expenseItemList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseItemList);
        expenseRecyclerView.setAdapter(expenseAdapter);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);

        // Query the expenses collection
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        expensesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Get data from the document
                String expenseId = documentSnapshot.getString("expenseId");
                String paymentDesc = documentSnapshot.getString("payment_desc");
                String paymentType = documentSnapshot.getString("payment_type");
                Double amount = documentSnapshot.getDouble("amount");
                String reference = documentSnapshot.getString("reference");
                String expenseAccount = documentSnapshot.getString("expense_account");
                String date = String.valueOf(documentSnapshot.getString("date"));
                String time = String.valueOf(documentSnapshot.getString("time"));
                String paidTo = documentSnapshot.getString("paid_to");



                // Create an ExpenseItem instance with the retrieved data
                Expense expenseItem = new Expense(expenseId, paymentDesc, paymentType, amount, reference, expenseAccount,paidTo, date, time);

                // Now you can use the expenseItem object as needed
                expenseItemList.add(expenseItem);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
            // Notify the adapter that the data has changed
            expenseAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle the failure to retrieve data from Firestore
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddExpenseFragment addExpenseFragment = new AddExpenseFragment();
                addExpenseFragment.show(getSupportFragmentManager(), "AddExpenseFragment");
            }
        });
    }
}