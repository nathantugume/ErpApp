package com.example.erpapp.Classes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.adapters.SalesProductAdapter;

import java.util.List;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final List<Product> productList;
    private final SalesProductAdapter productAdapter;
    private final List<Product> salesList;

    public SwipeToDeleteCallback(List<Product> productList, SalesProductAdapter productAdapter, List<Product> salesList) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.productList = productList;
        this.productAdapter = productAdapter;
        this.salesList = salesList;
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
      productAdapter.notifyDataSetChanged();
        productAdapter.updateData(salesList);


    }
}
