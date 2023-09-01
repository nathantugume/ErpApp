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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {


    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
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
        // Bind other data as needed
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView barcodeTextView,quantity,price,buying_price,category,description;


        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            barcodeTextView = itemView.findViewById(R.id.barcodeTextView);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            buying_price = itemView.findViewById(R.id.buying_price);
            category = itemView.findViewById(R.id.category);
            description = itemView.findViewById(R.id.description);
            // Find other views here
        }
    }
}

