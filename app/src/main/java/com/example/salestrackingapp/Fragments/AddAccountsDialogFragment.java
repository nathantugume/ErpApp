package com.example.salestrackingapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.salestrackingapp.Classes.AccountItem;
import com.example.salestrackingapp.R;
import com.example.salestrackingapp.adapters.AccountAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAccountsDialogFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String selectedTransactionType;
    private AccountAdapter accountAdapter;
    private final String[] transaction = {""};
    public AddAccountsDialogFragment() {
        // Required empty public constructor
    }
    public AddAccountsDialogFragment(AccountAdapter accountAdapter) {
        this.accountAdapter = accountAdapter;

    }

    public static AddAccountsDialogFragment newInstance(String param1, String param2) {
        AddAccountsDialogFragment fragment = new AddAccountsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // ...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_accounts_dialog, container, false);

        // Return the inflated rootView
        return rootView;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for the dialog
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_add_accounts_dialog, null);

        Spinner spinnerTransactionType = view.findViewById(R.id.spinnerTransactionType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.transaction_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransactionType.setAdapter(adapter);


        spinnerTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected transaction type (Expenses or Income)
                selectedTransactionType = parentView.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected, if needed
            }
        });

        EditText transaction_name = view.findViewById(R.id.account_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Add Transaction Type")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view1 -> {
                String transactionName = transaction_name.getText().toString();

                if (transactionName.isEmpty()) {
                    transaction_name.setError("please add transaction name");
                    transaction_name.requestFocus();
                } else {

                    // Retrieve companyId from SharedPreferences
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    String companyId = sharedPreferences.getString("companyId", null);
                    Log.d("selected item","item"+selectedTransactionType);
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    String accountId = firestore.collection("Accounts").document().getId(); // Generate Unique Id
                    DocumentReference accountsRef = firestore.collection("accounts").document(accountId);
                    Map<String, Object> accountsData = new HashMap<>();
                    accountsData.put("accountsId", accountId);
                    accountsData.put("transaction_type", selectedTransactionType); // Changed to transaction[0]
                    accountsData.put("account_name", transactionName);
                    accountsData.put("companyId",companyId);
                    AccountItem accounts = new AccountItem(transactionName,selectedTransactionType,accountId);
                    accountAdapter.add(accounts);

                    accountsRef.set(accountsData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Account Added Successfully", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Sorry, transaction failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            });
                }
            });
        });

        return alertDialog; // Return the created AlertDialog
    }

}
