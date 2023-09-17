package com.example.erpapp.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.erpapp.Classes.Product;
import com.example.erpapp.R;

import java.util.List;

public class SalesProductAdapter extends RecyclerView.Adapter<SalesProductAdapter.ViewHolder> {

    private final OnItemClickListener itemClickListener;
    private final OnItemRemovedListener itemRemovedListener;
    private final List<Product> productList;
    private List<Product> salesList;
    TextView totalPriceTextView;



    public SalesProductAdapter(List<Product> productList, OnItemClickListener itemClickListener, OnItemRemovedListener itemRemovedListener,TextView totalPriceTextView) {
        this.productList = productList;
        this.itemClickListener = itemClickListener;
        this.itemRemovedListener = itemRemovedListener;
        this.totalPriceTextView = totalPriceTextView;
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
        int qty = product.getQuantity();
        String quantity = String.valueOf(qty);
        product.setSaleBy("Cash");

        holder.productNameTextView.setText(product.getProduct_name());
        holder.quantityTextView.setText(quantity);

        holder.saleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                // Check the selected sale type (retail or wholesale)
                String selectedSaleType = holder.saleTypeSpinner.getSelectedItem().toString();
                Log.d("selected","item"+selectedSaleType);
                if (selectedSaleType.equals("Retail")) {
                    // Display retail price
                    product.setSaleType(selectedSaleType);
                    holder.productPriceEditText.setText(String.valueOf(product.getPrice()));
                    holder.productPriceEditText.requestFocus();

                } else {
                    // Display wholesale price
                    product.setSaleType(selectedSaleType);
                    holder.productPriceEditText.setText(String.valueOf(product.getWholeSalePrice()));
                    holder.productPriceEditText.requestFocus();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Set onClick listeners for increment and decrement buttons
        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(holder.quantityTextView.getText().toString());
                currentQuantity++;
                holder.quantityTextView.setText(String.valueOf(currentQuantity));
                Product product = productList.get(holder.getAdapterPosition());
                product.setQuantity(currentQuantity);
                itemClickListener.onQuantityChange(product, currentQuantity);
            }
        });

        holder.decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(holder.quantityTextView.getText().toString());
                if (currentQuantity > 0) {
                    currentQuantity--;
                    holder.quantityTextView.setText(String.valueOf(currentQuantity));
                    Product product = productList.get(holder.getAdapterPosition());
                    product.setQuantity(currentQuantity);
                    itemClickListener.onQuantityChange(product, currentQuantity);
                }
            }
        });
        holder.productPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String priceStr = charSequence.toString();
                if (!priceStr.isEmpty()) {
                    double newPrice = Double.parseDouble(priceStr);
                    product.setPrice((int) newPrice); // Update the price in the product
                    itemClickListener.onPriceChange(product, newPrice); // Notify the listener
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.saleCreditSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Get the product associated with this item in the RecyclerView


            if (isChecked) {
                // Credit sale selected
                product.setSaleBy("Credit");

            } else {
                // Cash sale selected
                product.setSaleBy("Cash");             }


        });

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
        TextView incrementButton;
        TextView decrementButton;
        EditText productPriceEditText;

        Spinner saleTypeSpinner;
        SwitchCompat saleCreditSwitch;


        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            productPriceEditText = itemView.findViewById(R.id.productPriceEditText);
            saleTypeSpinner = itemView.findViewById(R.id.saleTypeSpinner);
            saleCreditSwitch = itemView.findViewById(R.id.saleCreditSwitch);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);

        void onQuantityChange(Product product, int newQuantity);

        int getQuantity(Product product);

        int getPrice(Product product);

        void onPriceChange(Product product, double newPrice);
    }

    public interface OnItemRemovedListener {

        boolean onItemRemoved();
    }

}
