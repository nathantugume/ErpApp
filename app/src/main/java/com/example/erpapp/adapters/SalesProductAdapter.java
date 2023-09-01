package com.example.erpapp.adapters;

import android.util.Log;
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
import java.util.Locale;

public class SalesProductAdapter extends RecyclerView.Adapter<SalesProductAdapter.ViewHolder> {

    public static SalesProductAdapter.OnItemClickListener itemClickListener;
    private List<Product> productList;
//    private static OnItemClickListener itemClickListener;

    private static Product product;

    private String total_price;



    public SalesProductAdapter(List<Product> productList, OnItemClickListener itemClickListener) {
        this.productList = productList;
        SalesProductAdapter.itemClickListener = itemClickListener;
    }

    public void updateData(List<Product> newProductList) {
        productList.clear();
        productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product = productList.get(position);
        int p_price = product.getPrice();
        String productPrice = String.valueOf(p_price);
        total_price = productPrice;

        holder.productNameTextView.setText(product.getProduct_name());
        holder.productPriceTextView.setText(productPrice);
        holder.totalPriceTextView.setText(total_price);

       // holder.barcodeTextView.setText(product.getBarcode());

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements com.example.erpapp.adapters.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        TextView totalPriceTextView;
        Button incrementButton;
        Button decrementButton;

        TextView productPriceTextView;



        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);


            // Find other views here
            incrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());
                    currentQuantity++;
                    quantityTextView.setText(String.valueOf(currentQuantity));

                    // Update the total price when quantity increases
                    String total = productPriceTextView.getText().toString();
                    double total_double = Double.parseDouble(total);

                    double totalPrice = calculateTotalPrice(product, itemClickListener);
                    totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));

                    // Notify the listener about the quantity change
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

                        // Update the total price when quantity decreases
                        double totalPrice = calculateTotalPrice(product, itemClickListener);
                        Log.d("total","price"+totalPrice);

                        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f", totalPrice));

                        // Notify the listener about the quantity change
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


    private static double calculateTotalPrice(Product product, OnItemClickListener itemClickListener) {


        return itemClickListener.getPrice(product) * itemClickListener.getQuantity(product);
    }

}
