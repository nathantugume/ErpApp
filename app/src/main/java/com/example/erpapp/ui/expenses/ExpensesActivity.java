package com.example.erpapp.ui.expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.erpapp.R;
import com.google.android.material.card.MaterialCardView;

public class ExpensesActivity extends AppCompatActivity {

    private MaterialCardView expenses_card, accounts_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

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