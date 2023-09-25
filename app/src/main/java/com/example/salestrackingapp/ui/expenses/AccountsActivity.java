package com.example.salestrackingapp.ui.expenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.example.salestrackingapp.Classes.AccountItem;
import com.example.salestrackingapp.Fragments.AddAccountsDialogFragment;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.adapters.AccountAdapter;
import com.example.salestrackingapp.ui.categories.CategoryActivity;
import com.example.salestrackingapp.ui.products.ProductsActivity;
import com.example.salestrackingapp.ui.reports.ReportsActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class AccountsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccountAdapter accountAdapter;
    private List<AccountItem> accountItemList = new ArrayList<>();
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

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
                    Intent intent = new Intent(AccountsActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(AccountsActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(AccountsActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(AccountsActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.accountsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        accountAdapter = new AccountAdapter(accountItemList);
        recyclerView.setAdapter(accountAdapter);


        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Fetch accounts from Firestore
        fetchAccountsFromFirestore();
        FloatingActionButton fabAddAccounts = findViewById(R.id.fab_add_accounts);

        fabAddAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAccountsDialogFragment addAccountsDialogFragment = new AddAccountsDialogFragment(accountAdapter);
                addAccountsDialogFragment.show(getSupportFragmentManager(),"AddAccountDialog");
            }
        });

    }

    private void fetchAccountsFromFirestore() {

        Source source = Source.DEFAULT;
        // Retrieve companyId from SharedPreferences
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String companyId = sharedPreferences.getString("companyId", null);
        firestore.collection("accounts")
                .whereEqualTo("companyId",companyId)
                .get(source)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        accountItemList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String accountName = document.getString("account_name");
                            String transaction_type = document.getString("transaction_type");
                            String accountId = document.getString("accountsId");

                            // Create an AccountItem and add it to the list
                            AccountItem accountItem = new AccountItem(accountName, transaction_type,accountId);
                            accountItemList.add(accountItem);
                        }
                        accountAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    } else {
                        // Handle errors here
                        Toast.makeText(this, "Failed to fetch account details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}