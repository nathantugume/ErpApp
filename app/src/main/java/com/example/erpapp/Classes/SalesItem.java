package com.example.erpapp.Classes;

public class SalesItem {
    private String saleDate;
    private String productName;
    private double saleAmount;
    private Long quantity;
    private String companyId;

    public SalesItem(String saleDate, String productName, double saleAmount, Long quantity,String companyId) {
        this.saleDate = saleDate;
        this.productName = productName;
        this.saleAmount = saleAmount;
        this.quantity = quantity;
        this.companyId = companyId;

    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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
