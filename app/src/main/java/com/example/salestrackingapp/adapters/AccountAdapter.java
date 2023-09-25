package com.example.salestrackingapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salestrackingapp.Classes.AccountItem;
import com.example.salestrackingapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<AccountItem> accountItemList;
    private Context context;
    String  selectedTransactionType;

    public AccountAdapter(List<AccountItem> accountItemList) {
        this.accountItemList = accountItemList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        AccountItem accountItem = accountItemList.get(position);

        holder.accountNameTextView.setText(accountItem.getAccountName());
        holder.transactionType.setText(accountItem.getTransaction_type());
        holder.menuIcon.setOnClickListener(view -> {
            AccountItem accountSnapshot = accountItemList.get(position);
            String accountName = (String) accountSnapshot.getAccountName();
            String transactionType = (String) accountSnapshot.getTransaction_type();
            String accountId = accountSnapshot.getAccountId();
            PopupMenu popupMenu = new PopupMenu(context, holder.menuIcon);
            popupMenu.inflate(R.menu.card_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.editOption) {
//                    showEditAccountsDialog(accountName,transactionType,accountId);

                    // Ensure accountId is not null or empty before calling the dialog
                    if (accountId != null && !accountId.isEmpty()) {
                        showEditAccountsDialog(accountName, transactionType, accountId);
                    } else {
                        // Handle the case where accountId is null or empty
                        Toast.makeText(context, "Invalid account ID"+accountId, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (itemId == R.id.deleteOption) {
                    // Handle delete option
                    AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(context);
                    confirmDialogBuilder.setMessage("Are you sure you want to delete this category?");
                    confirmDialogBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        // User confirmed the delete action

                        // Delete the category document from Firestore
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                        firestore.collection("accounts").document(accountId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Data deleted successfully from Firestore
                                    accountItemList.remove(position);// Remove the category from the list
                                    notifyDataSetChanged(); // Refresh the RecyclerView
                                    Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to delete data in Firestore
                                    Toast.makeText(context, "Error deleting account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                    confirmDialogBuilder.setNegativeButton("No", (dialogInterface, i) -> {
                        // User canceled the delete action
                    });
                    AlertDialog confirmDialog = confirmDialogBuilder.create();
                    confirmDialog.show();
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showEditAccountsDialog(String accountName, String transactionType, String accountId) {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_account_dialogue, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("EDIT " + accountName);

        EditText accountNameEditText = dialogView.findViewById(R.id.account_name);
        Spinner spinnerTransactionType = dialogView.findViewById(R.id.spinnerTransactionType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
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

        // Populate the input fields with the existing product details
        accountNameEditText.setText(accountName);


        dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            // Retrieve the edited values from the input fields
            String updatedAccName = accountNameEditText.getText().toString();
            String updatedTransactionType = selectedTransactionType;


            // Update the category
            // Update the Firestore document
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("accounts").document(accountId)
                    .update("account_name", updatedAccName,
                            "transaction_type", updatedTransactionType

                    )
                    .addOnSuccessListener(aVoid -> {
                        // Data updated successfully in Firestore
                        
                        notifyDataSetChanged(); // Refresh the RecyclerView
                        Toast.makeText(context, "Account updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update data in Firestore
                        Toast.makeText(context, "Error updating product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            // Close the dialog
            dialog.dismiss();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            // Close the dialog without making any changes
            dialog.dismiss();
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return accountItemList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void add(AccountItem accounts) {
        accountItemList.add(accounts);
        notifyDataSetChanged();
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView accountNameTextView;
        TextView transactionType;

        ImageView menuIcon;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            accountNameTextView = itemView.findViewById(R.id.accountNameTextView);
            transactionType = itemView.findViewById(R.id.transTypeTextView);
            menuIcon = itemView.findViewById(R.id.menuIcon);
        }
    }
}
