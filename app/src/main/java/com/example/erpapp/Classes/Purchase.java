package com.example.erpapp.Classes;

import java.util.Date;

public class Purchase {
    private String productName;
    private int quantity;
    private Date purchaseDate;

    public Purchase() {
        // Default constructor required for Firestore
    }

    public Purchase(String productName, int quantity, Date purchaseDate) {
        this.productName = productName;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
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
