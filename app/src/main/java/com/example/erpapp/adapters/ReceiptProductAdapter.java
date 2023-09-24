package com.example.erpapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.R;

import java.util.List;
import java.util.Locale;

public class ReceiptProductAdapter extends RecyclerView.Adapter<ReceiptProductAdapter.ViewHolder> {
    private List<Product> productList;

    public ReceiptProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productNameTextView.setText(product.getProduct_name());
        holder.quantityTextView.setText(String.valueOf(product.getQuantity()));
        holder.priceTextView.setText(String.format(Locale.getDefault(), "Ugx %.2f", product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTextView;
        public TextView quantityTextView;
        public TextView priceTextView;

        public ViewHolder(View view) {
            super(view);
            productNameTextView = view.findViewById(R.id.productNameTextView);
            quantityTextView = view.findViewById(R.id.quantityTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
        }
    }
}
