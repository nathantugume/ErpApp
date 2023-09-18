package com.example.erpapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.LoadCategoriesTask;
import com.example.erpapp.Classes.Product;
import com.example.erpapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> productList;
    private final Context context;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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

        holder.productNameTextView.setText(product.getProduct_name());
        holder.barcodeTextView.setText(product.getBarcode());
        holder.category.setText(product.getCategory());
        holder.quantity.setText(String.valueOf(product.getQuantity()));
        holder.buying_price.setText(String.valueOf(product.getBuying_price()));
        holder.price.setText(String.valueOf(product.getPrice()));
        holder.description.setText(product.getProduct_desc());
        holder.wholeSale.setText(String.valueOf(product.getWholeSalePrice()));

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
        dialogBuilder.setTitle("EDIT " + product.getProduct_name().toUpperCase());

        EditText productNameEditText = dialogView.findViewById(R.id.productNameEditText);
        EditText productDescEditText = dialogView.findViewById(R.id.productDescEditText);
        EditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        EditText barcodeEditText = dialogView.findViewById(R.id.barcodeEditText);
        EditText BpriceEditText = dialogView.findViewById(R.id.BpriceEditText);



        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        new LoadCategoriesTask(context, categorySpinner,product.getCategory()).execute();


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
            firestore.collection("products").document(product.getProductId())
                    .update("barcode", product.getBarcode(),
                            "buying_price", product.getBuying_price(),
                            "categoty", product.getCategory(),
                            "price", product.getPrice(),
                            "product_desc", product.getProduct_desc(),
                            "product_name", product.getProduct_name(),
                            "quantity", product.getQuantity()
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
        TextView barcodeTextView, quantity, price, buying_price, category, description,wholeSale;
        ImageView menuIcon;

        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            barcodeTextView = itemView.findViewById(R.id.barcodeTextView);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            buying_price = itemView.findViewById(R.id.buying_price);
            category = itemView.findViewById(R.id.category);
            wholeSale = itemView.findViewById(R.id.wholesale_price);
            description = itemView.findViewById(R.id.description);
            menuIcon = itemView.findViewById(R.id.menuIcon);
        }
    }

    // Add this method to add a new product to the dataset
    public void addProduct(Product product) {
        productList.add(product);
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}
