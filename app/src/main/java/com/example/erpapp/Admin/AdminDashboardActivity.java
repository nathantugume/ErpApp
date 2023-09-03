package com.example.erpapp.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.erpapp.R;
import com.example.erpapp.ui.Users.UsersActivity;
import com.example.erpapp.ui.expenses.ExpensesActivity;
import com.example.erpapp.ui.products.CategoryActivity;
import com.example.erpapp.ui.products.ProductsActivity;
import com.example.erpapp.ui.reports.ReportsActivity;
import com.example.erpapp.ui.sales.SalesActivity;
import com.example.erpapp.ui.settings.SettingsActivity;
import com.example.erpapp.ui.stock.StockActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView sales_card,products_card,category_card,report_card,settings_card,users_card,stock_card,expenses_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar topAppbar = findViewById(R.id.topAppBar);
        topAppbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        sales_card = findViewById(R.id.sales_card);
        products_card = findViewById(R.id.products_card);
        category_card = findViewById(R.id.categories_card);
        stock_card = findViewById(R.id.stock_card);
        expenses_card = findViewById(R.id.expenses_card);
        report_card = findViewById(R.id.reports_card);
        settings_card = findViewById(R.id.settings_card);
        users_card = findViewById(R.id.users_card);
        category_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
              startActivity(intent);
            }
        });

        products_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductsActivity.class);
                startActivity(intent);
            }
        });
        sales_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, SalesActivity.class);
                startActivity(intent);
            }
        });

        stock_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, StockActivity.class);
                startActivity(intent);
            }
        });

        report_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, ReportsActivity.class);
                startActivity(intent);
            }
        });

        settings_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        expenses_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });

        users_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });

    }


}