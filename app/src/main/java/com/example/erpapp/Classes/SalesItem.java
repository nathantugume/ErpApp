package com.example.erpapp.Classes;

public class SalesItem {
    private String saleDate;
    private String productName;
    private double saleAmount;
    private Long quantity;

    public SalesItem(String saleDate, String productName, double saleAmount, Long quantity) {
        this.saleDate = saleDate;
        this.productName = productName;
        this.saleAmount = saleAmount;
        this.quantity = quantity;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public String getProductName() {
        return productName;
    }

    public double getSaleAmount() {
        return saleAmount;
    }

    public Long getQuantity() {
        return quantity;
    }
}
