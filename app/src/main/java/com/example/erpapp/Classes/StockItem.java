package com.example.erpapp.Classes;

import java.util.Date;

public class StockItem {
    private String productName;
    private int quantity;
    private double price;
    private Date purchaseDate;

    public StockItem() {

    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    private OnQuantityChangeListener onQuantityChangeListener;

    public StockItem(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity( int newQuantity) {
        this.quantity = newQuantity;

    }



    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(int position, int newQuantity);

        void onItemDeleted(int position, StockItem deletedItem);
    }


    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.onQuantityChangeListener = listener;
    }

    // Calculate the total price of this stock item
    public int calculateTotalPrice() {
        return (int) (quantity * price);
    }
    public interface OnItemDeletedListener {
        void onItemDeleted(int position, StockItem deletedItem);
    }
}
