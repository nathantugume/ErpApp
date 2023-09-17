package com.example.erpapp.Classes;

import java.util.Date;

public class Purchase {
    private String productName;
    private int quantity;
    private Date purchaseDate;
    private int price;

    String companyId;

    public Purchase() {
        // Default constructor required for Firestore
    }

    public Purchase(String productName, int quantity, Date purchaseDate) {
        this.productName = productName;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
    }

    public Purchase(String productName, int quantity, Date purchaseDate, int newPrice,String companyId) {
        this.productName = productName;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.price = newPrice;
        this.companyId = companyId;
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

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
