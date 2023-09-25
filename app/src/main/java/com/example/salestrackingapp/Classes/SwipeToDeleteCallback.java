package com.example.salestrackingapp.Classes;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salestrackingapp.adapters.SalesProductAdapter;

import java.util.List;
import java.util.Locale;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final List<Product> productList;
    private final SalesProductAdapter productAdapter;
    private final List<Product> salesList;
    TextView totalPriceTextView;

    public SwipeToDeleteCallback(List<Product> productList, SalesProductAdapter productAdapter, List<Product> salesList, TextView totalPriceTextView) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.productList = productList;
        this.productAdapter = productAdapter;
        this.salesList = salesList;
        this.totalPriceTextView = totalPriceTextView;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            // Handle swipe to remove here
            removeItem(position);

        }

    }

    private void removeItem(int position) {
        Product removedProduct = productList.get(position);
        productList.remove(position);
        productAdapter.notifyItemRemoved(position);

        // Remove the same product from the salesList
        for (int i = 0; i < salesList.size(); i++) {
            if (salesList.get(i).getProductId().equals(removedProduct.getProductId())) {
                salesList.remove(i);

                break;
            }

        }
        // Recalculate the total price
        double totalPrice = calculateTotalPrice();

// Update the total price TextView
        if (totalPriceTextView != null) {
            totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));
        }
      productAdapter.notifyDataSetChanged();
        productAdapter.updateData(salesList);


    }

    private double calculateTotalPrice() {
        double total = 0;
        for (Product product : salesList) {
            total += product.getPrice() * product.getQuantity(); // Calculate total price for each item
        }
        return total;
    }
}
