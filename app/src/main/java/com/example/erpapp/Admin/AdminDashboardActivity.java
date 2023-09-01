package com.example.erpapp.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.erpapp.R;
import com.example.erpapp.ui.products.CategoryActivity;
import com.example.erpapp.ui.products.ProductsActivity;
import com.example.erpapp.ui.sales.SalesActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView sales_card,products_card,category_card,report_card,settings_card,users_card;

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
    }


}