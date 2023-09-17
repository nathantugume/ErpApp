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
    private String saleBy;
    private String saleType;


    public Sale(){}

    public Sale(String saleId, String productId, String productName, double productPrice, int quantity, String saleDate, String saleTime,String saleBy, String saleType, String companyId) {
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.saleTime = saleTime;
        this.saleBy = saleBy;
        this.saleType = saleType;
        this.companyId = companyId;
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

    public String getSaleBy() {
        return saleBy;
    }

    public void setSaleBy(String saleBy) {
        this.saleBy = saleBy;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }
}
