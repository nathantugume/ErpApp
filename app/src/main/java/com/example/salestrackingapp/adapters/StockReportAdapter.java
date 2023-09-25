package com.example.salestrackingapp.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salestrackingapp.Classes.StockItem;
import com.example.salestrackingapp.R;
import java.util.List;

public class StockReportAdapter extends RecyclerView.Adapter<StockReportAdapter.ViewHolder> {

    private final List<StockItem> stockItemList;

    public StockReportAdapter(List<StockItem> stockItemList) {
        this.stockItemList = stockItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_report, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockItem stockItem = stockItemList.get(position);
        holder.productNameTextView.setText(stockItem.getProductName());
        holder.quantityTextView.setText("Quantity: " + stockItem.getQuantity());
        holder.priceTextView.setText("Price: Ugx" + stockItem.getPrice());

        Log.d("holder", "name"+stockItem.getProductName());
    }

    @Override
    public int getItemCount() {
        return stockItemList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<StockItem> newStockItemList) {
//        stockItemList.clear();
        stockItemList.addAll(newStockItemList);
        notifyDataSetChanged();
      }



    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        TextView priceTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
