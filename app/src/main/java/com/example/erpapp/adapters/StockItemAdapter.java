package com.example.erpapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.StockItem;
import com.example.erpapp.R;

import java.util.List;
import java.util.Objects;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.StockItemViewHolder> {
    private List<StockItem> stockItemList;
    private StockItem.OnQuantityChangeListener onQuantityChangeListener;
    private StockItem.OnItemDeletedListener onItemDeletedListener;
    private OnPriceChangeListener onPriceChangeListener;

    private StockItemAdapter stockItemAdapter;

    public StockItemAdapter(List<StockItem> stockItemList, StockItem.OnQuantityChangeListener onQuantityChangeListener, StockItem.OnItemDeletedListener onItemDeletedListener, OnPriceChangeListener onPriceChangeListener) {
        this.stockItemList = stockItemList;
        this.onQuantityChangeListener = onQuantityChangeListener;
        this.onItemDeletedListener = onItemDeletedListener;
        this.onPriceChangeListener = onPriceChangeListener;

        this.stockItemAdapter = this;
    }

    @NonNull
    @Override
    public StockItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        if (stockItemList.isEmpty()) {
            // The list is empty
            Log.d("StockAdapter", "The list is empty in onCreateViewHolder");
        }

        return new StockItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StockItem stockItem = stockItemList.get(position);

        holder.bind(stockItem);
        if (stockItemList.isEmpty()) {
            // The list is empty
            Log.d("StockAdapter", "The list is empty");
        } else {
            // The list contains items
            Log.d("StockAdapter", "The list contains items");
        }


        // Quantity EditText listener
        holder.quantityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    int newQuantity = Integer.parseInt(holder.quantityEditText.getText().toString());
                    stockItem.setQuantity(newQuantity);

                    if (onQuantityChangeListener != null) {
                        onQuantityChangeListener.onQuantityChange(position, newQuantity);
                    }

                    // Use a Handler to post the notifyItemChanged call to the main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            stockItemAdapter.notifyItemChanged(position);
                        }
                    });
                }
            }
        });

        // Price EditText listener
        holder.priceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    double price = Double.parseDouble(holder.priceEditText.getText().toString());
                    int newPrice = (int) price;
                    stockItem.setPrice(newPrice);

                    if (onPriceChangeListener != null) {
                        onPriceChangeListener.onPriceChange(position, newPrice);
                    }

                    // Use a Handler to post the notifyItemChanged call to the main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            stockItemAdapter.notifyItemChanged(position);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockItemList.size();
    }

    public class StockItemViewHolder extends RecyclerView.ViewHolder {
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
            productNameTextView.setText(stockItem.getProductName());
            quantityEditText.setText(String.valueOf(stockItem.getQuantity()));
            priceEditText.setText(String.valueOf(stockItem.getPrice()));
            Log.d("StockAdapter","item "+stockItem.getProductName());
        }
    }

    public interface OnPriceChangeListener {
        void onPriceChange(int position, int newPrice);
    }

    // Add a method to attach swipe-to-delete functionality to the RecyclerView
    public void attachItemTouchHelperToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // Get the position of the swiped item
                        int position = viewHolder.getAdapterPosition();
                        // Remove the item from the list
                        StockItem deletedItem = stockItemList.remove(position);
                        // Notify the adapter of the removal
                        notifyItemRemoved(position);

                        // Notify the listener that an item was deleted
                        if (onItemDeletedListener != null) {
                            onItemDeletedListener.onItemDeleted(position, deletedItem);
                        }
                    }

                    @Override
                    public void onChildDraw(
                            @NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive
                    ) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                        // Add background for swipe-to-delete action
                        View itemView = viewHolder.itemView;
                        int iconMargin = (itemView.getHeight() - Objects.requireNonNull(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_delete)).getIntrinsicHeight()) / 2;
                        if (dX > 0) {
                            // Swiping to the right
                            c.clipRect(itemView.getLeft(), itemView.getTop(), (int) (itemView.getLeft() + dX), itemView.getBottom());
                        } else {
                            // Swiping to the left
                            c.clipRect((int) (itemView.getRight() + dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        }
                    }
                };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }
}
