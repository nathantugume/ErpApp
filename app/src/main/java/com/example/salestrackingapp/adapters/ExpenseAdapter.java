package com.example.salestrackingapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.salestrackingapp.Classes.Expense;
import com.example.salestrackingapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseItemList;
    private Context context;

    public ExpenseAdapter(List<Expense> expenseItemList) {
        this.expenseItemList = expenseItemList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expenseItem = expenseItemList.get(position);

        String eAmount = String.valueOf(expenseItem.getAmount());

        // Bind expense data to the ViewHolder
        holder.paidToTextView.setText(expenseItem.getPaid_to());
        holder.exAmountTextView.setText(eAmount);
        holder.eAccountTextView.setText(expenseItem.getExpense_account());
        holder.dateTextView.setText(expenseItem.getDate());
        holder.timeTextView.setText(expenseItem.getTime());
        holder.paidByTextView.setText(expenseItem.getPayment_type());
        holder.referenceTextView.setText(expenseItem.getReference());
        holder.expenseDescTextView.setText(expenseItem.getPayment_desc());


        // Add any click listeners or other actions here if required
        holder.menuIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuIcon);
            popupMenu.inflate(R.menu.card_menu);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.editOption){
                    showExpenseEditDialog(expenseItem);
                    return true;
                } else if (itemId == R.id.deleteOption) {
                    // Handle delete option
                    AlertDialog.Builder confirmDeleteBuilder = new AlertDialog.Builder(context);
                    confirmDeleteBuilder.setMessage("Are you sure you want to delete this product?");
                    confirmDeleteBuilder.setPositiveButton("Yes",((dialogInterface, i) -> {
                        // User confirmed the delete action

                        //Delete the expense from firestore
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("expenses").document(expenseItem.getExpenseId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        expenseItemList.remove(expenseItem);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Expense Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to delete expense"+e, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }));
                    confirmDeleteBuilder.setNegativeButton("No", ((dialogInterface1, i1) -> {
                        // user cancelled delete action

                    }));
                    AlertDialog confirmDialog = confirmDeleteBuilder.create();
                    confirmDeleteBuilder.show();
                    return true;

                } else {
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    private void showExpenseEditDialog(Expense expenseItem) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_expense_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Edit Expense");

        EditText paidToEditText = dialogView.findViewById(R.id.editPaidTo);
        EditText amountEditText = dialogView.findViewById(R.id.editAmount);
        EditText descriptionEditText = dialogView.findViewById(R.id.editDescription);
        EditText referenceEditText = dialogView.findViewById(R.id.editReference);

        // Set the initial values in the dialog
        paidToEditText.setText(expenseItem.getPaid_to());
        amountEditText.setText(String.valueOf(expenseItem.getAmount()));
        descriptionEditText.setText(expenseItem.getPayment_desc());
        referenceEditText.setText(expenseItem.getReference());

        dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            // Retrieve the edited values from the input fields
            String updatedPaidTo = paidToEditText.getText().toString();
            double updatedAmount = Double.parseDouble(amountEditText.getText().toString());
            String updatedDescription = descriptionEditText.getText().toString();
            String updatedReference = referenceEditText.getText().toString();

            // Update the expense item
            expenseItem.setPaid_to(updatedPaidTo);
            expenseItem.setAmount(updatedAmount);
            expenseItem.setPayment_desc(updatedDescription);
            expenseItem.setReference(updatedReference);

            // Update the Firestore document
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("expenses").document(expenseItem.getExpenseId())
                    .set(expenseItem)
                    .addOnSuccessListener(aVoid -> {
                        // Data updated successfully in Firestore
                        notifyDataSetChanged(); // Refresh the RecyclerView
                        Toast.makeText(context, "Expense updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update data in Firestore
                        Toast.makeText(context, "Error updating expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
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
        return expenseItemList.size();
    }

    public void add(Expense expense) {
        expenseItemList.add(expense);
        notifyDataSetChanged();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView expenseDescTextView,exAmountTextView, eAccountTextView, paidToTextView, dateTextView, timeTextView, paidByTextView, referenceTextView;
        ImageView menuIcon;


        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseDescTextView =itemView.findViewById(R.id.description);
            exAmountTextView = itemView.findViewById(R.id.amountTextView);
            eAccountTextView = itemView.findViewById(R.id.eAccountTextView);
            paidToTextView = itemView.findViewById(R.id.paidTo);
            dateTextView = itemView.findViewById(R.id.eDate);
            timeTextView = itemView.findViewById(R.id.time);
            paidByTextView = itemView.findViewById(R.id.paidBy);
            referenceTextView = itemView.findViewById(R.id.eReference);

            menuIcon = itemView.findViewById(R.id.menuIcon);

        }
    }
}
