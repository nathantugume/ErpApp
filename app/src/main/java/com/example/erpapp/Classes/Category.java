package com.example.erpapp.Classes;

public class Category {
    private String categoryId;
    private String companyId;
    private String name;
    private String description;


    public Category(){

    }
    public Category(String categoryId, String companyId, String name, String description) {
        this.categoryId = categoryId;
        this.companyId = companyId;
        this.name = name;
        this.description = description;

    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
