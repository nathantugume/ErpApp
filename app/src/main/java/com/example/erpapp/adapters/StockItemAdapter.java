package com.example.erpapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.StockItem;
import com.example.erpapp.R;

import java.util.List;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.StockItemViewHolder>  {

    private List<StockItem> stockItemList;
    private StockItem.OnQuantityChangeListener onQuantityChangeListener;


    public StockItemAdapter(List<StockItem> stockItemList, StockItem.OnQuantityChangeListener onQuantityChangeListener) {
        this.stockItemList = stockItemList;
        this.onQuantityChangeListener = onQuantityChangeListener;

    }

    @NonNull
    @Override
    public StockItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new StockItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StockItem stockItem = stockItemList.get(position);

        holder.bind(stockItem);

        holder.quantityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    int newQuantity = Integer.parseInt(holder.quantityEditText.getText().toString());
                    stockItem.setQuantity(newQuantity);

                    // Notify the listener when the quantity changes
                    if (onQuantityChangeListener != null) {
                        onQuantityChangeListener.onQuantityChange(position, newQuantity);
                    }
                }
            }
        });
    }




    // Implement this method to update the UI with the new total price
//    private void updateTotalPriceUI(double totalPrice) {
//        // You should implement this method based on your UI structure
//        // For example, if you have a TextView to display the total price:
//
//         totalPrice.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));
//    }


    @Override
    public int getItemCount() {
        return stockItemList.size();
    }

    public static class StockItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        EditText quantityEditText;
        EditText priceEditText;

        public StockItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityEditText = itemView.findViewById(R.id.quantityEditText);
            priceEditText = itemView.findViewById(R.id.priceEditText);
        }

        public void bind(StockItem stockItem) {
            // Update UI elements with data from the StockItem
            productNameTextView.setText(stockItem.getProductName());
            quantityEditText.setText(String.valueOf(stockItem.getQuantity()));
            priceEditText.setText(String.valueOf(stockItem.getPrice()));
        }
    }
}
