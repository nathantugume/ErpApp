package com.example.erpapp.adapters;

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

import com.example.erpapp.Classes.Product;
import com.example.erpapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void updateData(List<Product> newProductList) {
        productList.clear();
        productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        int qty = product.getQuantity();
        int pri = product.getPrice();
        int byp = product.getBuying_price();
        String buyingPrice = String.valueOf(byp);
        String price = String.valueOf(pri);
        String quantity = String.valueOf(qty);
        holder.productNameTextView.setText(product.getProduct_name());
        holder.barcodeTextView.setText(product.getBarcode());
        holder.category.setText(product.getCategory());
        holder.quantity.setText(quantity);
        holder.buying_price.setText(buyingPrice);
        holder.price.setText(price);
        holder.description.setText(product.getProduct_desc());

        holder.menuIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuIcon);
            popupMenu.inflate(R.menu.card_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.editOption) {
                    showEditProductDialog(product);
                    return true;
                } else if (itemId == R.id.deleteOption) {
                    // Handle delete option
                    AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(context);
                    confirmDialogBuilder.setMessage("Are you sure you want to delete this product?");
                    confirmDialogBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        // User confirmed the delete action

                        // Delete the product document from Firestore
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("products").document(product.getProductId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Data deleted successfully from Firestore
                                    productList.remove(product); // Remove the product from the list
                                    notifyDataSetChanged(); // Refresh the RecyclerView
                                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to delete data in Firestore
                                    Toast.makeText(context, "Error deleting product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
//editing the products
    private void showEditProductDialog(Product product) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_product_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Edit Product");

        EditText productNameEditText = dialogView.findViewById(R.id.productNameEditText);
        EditText productDescEditText = dialogView.findViewById(R.id.productDescEditText);
        EditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        EditText barcodeEditText = dialogView.findViewById(R.id.barcodeEditText);
        EditText BpriceEditText = dialogView.findViewById(R.id.BpriceEditText);


        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);


        // Initialize the spinner with categories
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categoryNames = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String categoryName = documentSnapshot.getString("name");
                        categoryNames.add(categoryName);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item,
                            categoryNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);

                    // Set the selected category based on the product's category
                    String currentCategory = product.getCategory();
                    int categoryPosition = categoryNames.indexOf(currentCategory);
                    categorySpinner.setSelection(categoryPosition);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });

        // Populate the input fields with the existing product details
        productNameEditText.setText(product.getProduct_name());
        quantityEditText.setText(String.valueOf(product.getQuantity()));
        priceEditText.setText(String.valueOf(product.getPrice()));
        productDescEditText.setText(product.getProduct_desc());
        barcodeEditText.setText(product.getBarcode());
        BpriceEditText.setText(String.valueOf(product.getBuying_price()));

        dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            // Retrieve the edited values from the input fields
            String updatedProductName = productNameEditText.getText().toString();
            String updatedProductDesc = productDescEditText.getText().toString();
            String editedBarcode = barcodeEditText.getText().toString();
            int editedBprice = Integer.parseInt(BpriceEditText.getText().toString());
            int updatedQuantity = Integer.parseInt(quantityEditText.getText().toString());
            int updatedPrice = Integer.parseInt(priceEditText.getText().toString());

            // Update the product details (you can update the product in your list or Firestore here)
            product.setProduct_name(updatedProductName);
            product.setQuantity(updatedQuantity);
            product.setPrice(updatedPrice);
            product.setProduct_desc(updatedProductDesc);
            product.setBarcode(editedBarcode);
            product.setBuying_price(editedBprice);
            // Update the category
            String updatedCategory = categorySpinner.getSelectedItem().toString();
            product.setCategory(updatedCategory);
            // Update the Firestore document
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("products").document(product.getProductId())
                    .update("barcode",product.getBarcode(),
                            "buying_price",product.getBuying_price(),
                            "categoty",product.getCategory(),
                            "price",product.getPrice(),
                            "product_desc",product.getProduct_desc(),
                            "product_name",product.getProduct_name(),
                            "quantity",product.getQuantity()
                    )
                    .addOnSuccessListener(aVoid -> {
                        // Data updated successfully in Firestore
                        notifyDataSetChanged(); // Refresh the RecyclerView
                        Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show();
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
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView barcodeTextView, quantity, price, buying_price, category, description;
        ImageView menuIcon;

        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            barcodeTextView = itemView.findViewById(R.id.barcodeTextView);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            buying_price = itemView.findViewById(R.id.buying_price);
            category = itemView.findViewById(R.id.category);
            description = itemView.findViewById(R.id.description);
            menuIcon = itemView.findViewById(R.id.menuIcon);
        }
    }
}
