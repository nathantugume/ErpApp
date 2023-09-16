package com.example.erpapp.Classes;

import java.util.Date;

public class Sale {
    private String saleId;
    private String productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private String saleDate;
    private String saleTime;
    private String companyId;

    public Sale(){}

    public Sale(String saleId, String productId, String productName, double productPrice, int quantity, String saleDate, String saleTime) {
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.saleTime = saleTime;
    }

    public Sale(String id, String productId, String product_name, int price, int quantityToSubtract, String formattedDate, String formattedTime, String saleBy, String saleType, String CompanyId) {
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(String saleTime) {
        this.saleTime = saleTime;
    }


}
