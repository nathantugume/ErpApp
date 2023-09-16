package com.example.erpapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.SalesItem;
import com.example.erpapp.R;

import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {

    private List<SalesItem> salesItemList;

    public SalesAdapter(List<SalesItem> salesItemList) {
        this.salesItemList = salesItemList;
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sales, parent, false);
        return new SalesViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
        SalesItem salesItem = salesItemList.get(position);

        // Bind data to the views
        holder.salesDateTextView.setText("Sale Date: " + salesItem.getSaleDate());
        holder.productNameTextView.setText("Product: " + salesItem.getProductName());
        holder.saleAmountTextView.setText("Amount: Ugx" + salesItem.getSaleAmount());
        holder.quantityTextView.setText("Quantity: "+salesItem.getQuantity());
    }

    @Override
    public int getItemCount() {
        return salesItemList.size();
    }

    // Define the setData method to update the dataset
    public void setData(List<SalesItem> updatedList) {
        salesItemList.clear();
        salesItemList.addAll(updatedList);
        notifyDataSetChanged();
    }

    public class SalesViewHolder extends RecyclerView.ViewHolder {
        public TextView salesDateTextView;
        public TextView productNameTextView;
        public TextView saleAmountTextView;
        public  TextView quantityTextView;

        public SalesViewHolder(@NonNull View itemView) {
            super(itemView);
            salesDateTextView = itemView.findViewById(R.id.salesDateTextView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            saleAmountTextView = itemView.findViewById(R.id.saleAmountTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
        }


    }
}
