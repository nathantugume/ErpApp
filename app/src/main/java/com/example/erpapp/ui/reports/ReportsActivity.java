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
        cash_flow_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportsActivity.this,CashFlowReportActivity.class);
                startActivity(intent);
            }
        });

    }
}