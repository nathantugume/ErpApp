package com.example.erpapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.erpapp.Classes.OptionsMenuHelper;
import com.example.erpapp.R;
import com.example.erpapp.ui.Users.LoginActivity;
import com.example.erpapp.ui.Users.UsersActivity;
import com.example.erpapp.ui.expenses.ExpensesActivity;
import com.example.erpapp.ui.categories.CategoryActivity;
import com.example.erpapp.ui.products.ProductsActivity;
import com.example.erpapp.ui.reports.ReportsActivity;
import com.example.erpapp.ui.sales.SalesActivity;
import com.example.erpapp.ui.settings.SettingsActivity;
import com.example.erpapp.ui.stock.AddStockActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar topAppbar = findViewById(R.id.topAppBar);
        topAppbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.drawer_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()== R.id.drawer_logout) {
                    logout();
                } else if (item.getItemId() == R.id.home) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }else if (item.getItemId() == R.id.category) {
                    Intent intent = new Intent(AdminDashboardActivity.this, CategoryActivity.class);
                    startActivity(intent);
                }else if (item.getItemId() == R.id.analytics) {
                    Intent intent = new Intent(AdminDashboardActivity.this, ReportsActivity.class);
                    startActivity(intent);
                }else if (item.getItemId() == R.id.product) {
                    Intent intent = new Intent(AdminDashboardActivity.this, ProductsActivity.class);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        CardView sales_card = findViewById(R.id.sales_card);
        CardView products_card = findViewById(R.id.products_card);
        CardView category_card = findViewById(R.id.categories_card);
        CardView stock_card = findViewById(R.id.stock_card);
        CardView expenses_card = findViewById(R.id.expenses_card);
        CardView report_card = findViewById(R.id.reports_card);
        CardView settings_card = findViewById(R.id.settings_card);
        CardView users_card = findViewById(R.id.users_card);
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
                Intent intent = new Intent(getApplicationContext(), AddStockActivity.class);
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

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // you can navigate to the login screen or perform any cleanup
        // After sign-out, you can navigate to the LoginActivity
        Intent intent = new Intent(AdminDashboardActivity.this,LoginActivity.class);
                startActivity(intent);
        finish(); // Close the current activity
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("menu", "loaded"+item.getTitle());
        if (item.getItemId() == R.id.sign_out) {
            Toast.makeText(this, "sign-out", Toast.LENGTH_SHORT).show();
            Log.d("menu", "loaded"+item.getTitle());
            return true;
        } else if (item.getItemId() == R.id.settings) {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            Log.d("menu", "loaded"+item.getTitle());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}