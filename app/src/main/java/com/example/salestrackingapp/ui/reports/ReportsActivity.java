package com.example.salestrackingapp.ui.reports;

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;

public class ReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    Intent intent = new Intent(ReportsActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.category){
                    Intent intent = new Intent(ReportsActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.analytics){
                    Intent intent = new Intent(ReportsActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.product){
                    Intent intent = new Intent(ReportsActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        MaterialToolbar toolbar;

        //        toolbar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        MaterialCardView cash_flow_report = findViewById(R.id.cashFlowCard);
        MaterialCardView balance_sheet = findViewById(R.id.balanceSheet_card);
        MaterialCardView sales_report = findViewById(R.id.sales_report);
        MaterialCardView stock_report = findViewById(R.id.stock_report_card);
        cash_flow_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportsActivity.this,CashFlowReportActivity.class);
                startActivity(intent);
            }
        });

        // balance sheet
        balance_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportsActivity.this, BalanceSheetActivity.class);
                startActivity(intent);
            }
        });
        // sales report
        sales_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportsActivity.this, SalesReportActivity.class);
                startActivity(intent);
            }
        });

        stock_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportsActivity.this,StockReportActivity.class);
                startActivity(intent);
            }
        });





    }
}