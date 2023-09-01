package com.example.erpapp.Classes;

public class Product {
    private String productId;
    private String product_name;
    private String product_desc;
    private int price;
    private int quantity;
    private String barcode;
    private int buying_price;
    private String category;

    // Constructors, getters, and setters

    public Product() {
        // Default constructor required for Firebase Firestore
    }

    public Product(String productId, String product_name, String product_desc, int price, int quantity, String barcode, int buying_price, String category) {
        this.productId = productId;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.buying_price = buying_price;
        this.category = category;
    }
// Other getters and setters

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
}
