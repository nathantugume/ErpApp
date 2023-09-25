package com.example.salestrackingapp.Classes;

public class Product {
    private String productId;
    private String product_name;
    private String product_desc;
    private int wholeSalePrice;
    private int price;
    private int quantity;
    private String barcode;
    private int buying_price;
    private String category;
    private String companyId;

    private String saleType;
    private String saleBy;



    // Constructors, getters, and setters

    public Product() {
        // Default constructor required for Firebase Firestore
    }

    public Product(String productId, String product_name, String product_desc, int price, int quantity,
                   String barcode, int buying_price, String category,int wholeSalePrice, String companyId) {
        this.productId = productId;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.buying_price = buying_price;
        this.category = category;
        this.wholeSalePrice = wholeSalePrice;
        this.companyId = companyId;
    }
// Other getters and setters


    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getSaleBy() {
        return saleBy;
    }

    public void setSaleBy(String saleBy) {
        this.saleBy = saleBy;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getBuying_price() {
        return buying_price;
    }

    public void setBuying_price(int buying_price) {
        this.buying_price = buying_price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getWholeSalePrice() {
        return wholeSalePrice;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setWholeSalePrice(int wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }
}
