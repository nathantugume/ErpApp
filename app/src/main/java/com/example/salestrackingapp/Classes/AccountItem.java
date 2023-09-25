package com.example.salestrackingapp.Classes;

public class AccountItem {
    private String accountName;
    private String transaction_type;
    private String accountId;

    public AccountItem(String accountName, String transaction_type, String accountId) {
        this.accountName = accountName;
        this.transaction_type = transaction_type;
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }
}
