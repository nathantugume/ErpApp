package com.example.erpapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.R;

import java.util.List;

public class SalesProductAdapter extends RecyclerView.Adapter<SalesProductAdapter.ViewHolder> {

    private final OnItemClickListener itemClickListener;
    private final OnItemRemovedListener itemRemovedListener;
    private final List<Product> productList;
    private List<Product> salesList;

    public SalesProductAdapter(List<Product> productList, OnItemClickListener itemClickListener, OnItemRemovedListener itemRemovedListener) {
        this.productList = productList;
        this.itemClickListener = itemClickListener;
        this.itemRemovedListener =itemRemovedListener;
    }
    // Add a setter method for salesList
    public void setSalesList(List<Product> salesList) {
        this.salesList = salesList;
    }

    // onCreateViewHolder is required to be implemented
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        int p_price = product.getPrice();
        String productPrice = String.valueOf(p_price);

        int qty = product.getQuantity();
        String quantity = String.valueOf(qty);

        holder.productNameTextView.setText(product.getProduct_name());
        holder.productPriceTextView.setText(productPrice);
        holder.quantityTextView.setText(quantity);


    }




    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newProductList) {
        productList.clear();
        productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        Button incrementButton;
        Button decrementButton;
        TextView productPriceTextView;

        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);

            incrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());
                    currentQuantity++;
                    quantityTextView.setText(String.valueOf(currentQuantity));
                    Product product = productList.get(getAdapterPosition());
                    product.setQuantity(currentQuantity);
                    itemClickListener.onQuantityChange(product, currentQuantity);
                }
            });

            decrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());
                    if (currentQuantity > 0) {
                        currentQuantity--;
                        quantityTextView.setText(String.valueOf(currentQuantity));
                        Product product = productList.get(getAdapterPosition());
                        product.setQuantity(currentQuantity);
                        itemClickListener.onQuantityChange(product, currentQuantity);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
        void onQuantityChange(Product product, int newQuantity);

        int getQuantity(Product product);

        int getPrice(Product product);
    }
    public interface OnItemRemovedListener {


        boolean onItemRemoved();
    }

}
