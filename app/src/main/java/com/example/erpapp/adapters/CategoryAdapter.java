package com.example.erpapp.adapters;

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

import com.example.erpapp.Classes.Category;
import com.example.erpapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private Context context;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        String categoryName = category.getName();
        String desc = category.getDescription();
        String categoryId = category.getCategoryId();

        holder.categoryNameTextView.setText(categoryName);
        holder.categoryDesc.setText(desc);
        holder.menuIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuIcon);
            popupMenu.inflate(R.menu.card_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.editOption) {
                    showEditCategoryDialog(categoryId, categoryName, desc);
                    return true;
                } else if (itemId == R.id.deleteOption) {
                    // Handle delete option
                    AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(context);
                    confirmDialogBuilder.setMessage("Are you sure you want to delete this category?");
                    confirmDialogBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        // User confirmed the delete action

                        // Delete the category document from Firestore
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                        firestore.collection("categories").document(categoryId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Data deleted successfully from Firestore
                                    categoryList.remove(position);// Remove the category from the list
                                    notifyDataSetChanged(); // Refresh the RecyclerView
                                    Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to delete data in Firestore
                                    Toast.makeText(context, "Error deleting category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void addCategory(Category category) {
        categoryList.add(category);
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
    private void showEditCategoryDialog(String categoryId, String currentName, String currentDesc) {
        // Implement your edit category dialog here
        // You can pass categoryId, currentName, and currentDesc as parameters
        // and open a dialog to edit category details
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_category_dialogue, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("EDIT " + currentName);

        EditText categoryNameEditText = dialogView.findViewById(R.id.categoryNameEditText);
        EditText categoryDescEditText = dialogView.findViewById(R.id.categoryDescEditText);


        // Populate the input fields with the existing product details
        categoryNameEditText.setText(currentName);
      categoryDescEditText.setText(currentDesc);

        dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            // Retrieve the edited values from the input fields
            String updatedCatName = categoryNameEditText.getText().toString();
            String updatedCatDesc = categoryDescEditText.getText().toString();


            // Update the category
            // Update the Firestore document
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("categories").document(categoryId)
                    .update("name", updatedCatName,
                            "description", updatedCatDesc

                    )
                    .addOnSuccessListener(aVoid -> {
                        // Data updated successfully in Firestore
                        notifyDataSetChanged(); // Refresh the RecyclerView
                        Toast.makeText(context, "Category updated successfully", Toast.LENGTH_SHORT).show();
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
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView, categoryDesc;
        ImageView menuIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            categoryDesc = itemView.findViewById(R.id.categoryDescList);
            menuIcon = itemView.findViewById(R.id.menuIcon);
        }
    }

}
