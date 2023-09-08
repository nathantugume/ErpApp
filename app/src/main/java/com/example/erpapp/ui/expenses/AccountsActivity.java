package com.example.erpapp.ui.expenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.erpapp.Classes.AccountItem;
import com.example.erpapp.Fragments.AddAccountsDialogFragment;
import com.example.erpapp.Fragments.AddCategoryDialogFragment;
import com.example.erpapp.R;
import com.example.erpapp.adapters.AccountAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
                AddAccountsDialogFragment addAccountsDialogFragment = new AddAccountsDialogFragment();
                addAccountsDialogFragment.show(getSupportFragmentManager(),"AddAccountDialog");
            }
        });

    }

    private void fetchAccountsFromFirestore() {
        firestore.collection("accounts")
                .get()
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