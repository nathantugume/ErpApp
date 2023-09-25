package com.example.salestrackingapp.ui.expenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.salestrackingapp.Admin.AdminDashboardActivity;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.ui.categories.CategoryActivity;
import com.example.salestrackingapp.ui.products.ProductsActivity;
import com.example.salestrackingapp.ui.reports.ReportsActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;

public class ExpensesActivity extends AppCompatActivity {

    private MaterialCardView expenses_card, accounts_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

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
                    Intent intent = new Intent(ExpensesActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(ExpensesActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(ExpensesActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(ExpensesActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        expenses_card = findViewById(R.id.expenses_card);
        accounts_card = findViewById(R.id.accounts_card);
        expenses_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesActivity.this, AddExpensesActivity.class);
                startActivity(intent);
            }
        });

        accounts_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesActivity.this, AccountsActivity.class);
                startActivity(intent);
            }
        });
    }
}