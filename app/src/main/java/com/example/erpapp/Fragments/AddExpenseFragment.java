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

import java.util.HashMap;
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
        paymentTypeSpinner = view.findViewById(R.id.payment_typeSpinner);
        paymentDescEditText = view.findViewById(R.id.paymentDescEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        referenceNoEditText = view.findViewById(R.id.referenceNoEditText);
        expenseAccountSpinner = view.findViewById(R.id.expenseAccountSpinner);

        LoadExpensesTask loadExpensesTask = new LoadExpensesTask(getContext(), expenseAccountSpinner);
        loadExpensesTask.execute();

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
                    //  set accounts spiner
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

                // payment Type Spinner
                // Create an ArrayAdapter using the custom layout for the Spinner items
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        view.getContext(),
                       R.array.payment_types,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Set the adapter to the Spinner
                paymentTypeSpinner.setAdapter(adapter);

                // Set an item selection listener if needed
                paymentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Handle the selected payment method
                        selectedPaymentMethod = parentView.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Handle nothing selected, if needed
                    }
                });

                if (validateInput()) {
                    String expenseId = firestore.collection("expenses").document().getId();
                    DocumentReference expenseRef = firestore.collection("expenses").document(expenseId);

                    Map<String, Object> expenseData = new HashMap<>();
                    expenseData.put("expenseId", expenseId);
                    expenseData.put("payment_desc", paymentDesc);
                    expenseData.put("payment_type", selectedPaymentMethod);
                    expenseData.put("amount", amount);
                    expenseData.put("reference", reference);
                    expenseData.put("expense_account", selectedAccount);

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


