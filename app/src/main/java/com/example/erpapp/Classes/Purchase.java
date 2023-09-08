package com.example.erpapp.Classes;

import java.util.Date;

public class Purchase {
    private String productName;
    private int quantity;
    private Date purchaseDate;
    private int price;

    public Purchase() {
        // Default constructor required for Firestore
    }

    public Purchase(String productName, int quantity, Date purchaseDate) {
        this.productName = productName;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
    }

    public Purchase(String productName, int quantity, Date purchaseDate, int newPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.price = newPrice;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }
}
