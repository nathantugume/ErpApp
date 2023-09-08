package com.example.erpapp.ui.expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.erpapp.Fragments.AddExpenseFragment;
import com.example.erpapp.Fragments.AddProductDialogFragment;
import com.example.erpapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddExpensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        FloatingActionButton fab = findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddExpenseFragment addExpenseFragment = new AddExpenseFragment();
                addExpenseFragment.show(getSupportFragmentManager(),"AddExpenseFragment");
            }
        });
    }
}