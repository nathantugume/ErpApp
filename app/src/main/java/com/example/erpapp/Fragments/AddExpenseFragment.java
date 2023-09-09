package com.example.erpapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.erpapp.Classes.LoadExpensesTask;
import com.example.erpapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddExpenseFragment extends DialogFragment {
    private EditText paidToEditText, paymentDescEditText, amountEditText, referenceNoEditText;
    private Spinner expenseAccountSpinner, paymentTypeSpinner;
    private String paidTo, paymentDesc, amount, reference;
    private String selectedPaymentMethod;
    private String selectedAccount;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_add_expense, null);

        paidToEditText = view.findViewById(R.id.paidToEditText);
        paymentDescEditText = view.findViewById(R.id.paymentDescEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        referenceNoEditText = view.findViewById(R.id.referenceNoEditText);

        //  spinners
        paymentTypeSpinner = view.findViewById(R.id.payment_typeSpinner);
        expenseAccountSpinner = view.findViewById(R.id.expenseAccountSpinner);
        // payment Type Spinner
        // Create an ArrayAdapter using the custom layout for the Spinner items
        String[] payment_types= {"Cash","Credit card","Cheque","Mobile money"};
        ArrayAdapter<CharSequence> payment  = new ArrayAdapter<CharSequence>(getContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                ,payment_types);
        payment.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        paymentTypeSpinner.setAdapter(payment);

        LoadExpensesTask loadExpensesTask = new LoadExpensesTask(getContext(), expenseAccountSpinner);
        loadExpensesTask.execute();

            // Getting spinner selected item
        expenseAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAccount = (String) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //      do nothing
            }
        });
        paymentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPaymentMethod = (String) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //        setting the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Add Expenses")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view1 -> {
                paidTo = paidToEditText.getText().toString();
                paymentDesc = paymentDescEditText.getText().toString();
                amount = amountEditText.getText().toString();
                reference = referenceNoEditText.getText().toString();
                    //  set accounts spinner


                // Get the current date and time
                Date currentDate = new Date();
                // Create a SimpleDateFormat or any date/time formatting method to format the date and time as needed
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate);
                String formattedTime = timeFormat.format(currentDate);

                if (validateInput()) {
                    String expenseId = firestore.collection("expenses").document().getId();
                    DocumentReference expenseRef = firestore.collection("expenses").document(expenseId);

                    Double amount_paid = Double.valueOf(amount);
                    Map<String, Object> expenseData = new HashMap<>();
                    expenseData.put("paid_to",paidTo);
                    expenseData.put("expenseId", expenseId);
                    expenseData.put("payment_desc", paymentDesc);
                    expenseData.put("payment_type", selectedPaymentMethod);
                    expenseData.put("amount", amount_paid);
                    expenseData.put("reference", reference);
                    expenseData.put("expense_account", selectedAccount);
                    expenseData.put("date",formattedDate);
                    expenseData.put("time",formattedTime);

                    expenseRef.set(expenseData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Expense Added Successfully", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            });
                }

            });
        });


        return alertDialog;
    }

    private boolean validateInput() {

        if (paidTo.isEmpty()) {
            paidToEditText.setError("please enter details");
            paidToEditText.requestFocus();
        } else if (paymentDesc.isEmpty()) {
            paymentDescEditText.setError("please enter description");
            paymentDescEditText.requestFocus();

        } else if (amount.isEmpty()) {
            amountEditText.setError("please enter payment amount");
            amountEditText.requestFocus();

        } else if (reference.isEmpty()) {
            referenceNoEditText.setError("please enter reference no");
            referenceNoEditText.requestFocus();
        } else {
            return true;
        }
        return false;
    }


}


