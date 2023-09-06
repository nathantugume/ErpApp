package com.example.erpapp.ui.stock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.erpapp.R;
import com.google.android.material.card.MaterialCardView;

public class StockActivity extends AppCompatActivity {
    MaterialCardView add_stock_card, modify_stock_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        add_stock_card = findViewById(R.id.add_stock_card);
        modify_stock_card = findViewById(R.id.modify_stock_card);

        add_stock_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddStockActivity.class);
                startActivity(intent);
            }
        });

        modify_stock_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ModifyStockActivity.class);
                startActivity(intent);
            }
        });
    }
}