package com.example.erpapp.ui.reports;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.erpapp.R;
import com.google.android.material.card.MaterialCardView;

public class ReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

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