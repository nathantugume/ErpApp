package com.example.erpapp.Classes;

import java.util.Date;

public class Expense {
    private String expenseId;
    private String payment_desc;
    private String payment_type;
    private Double amount;
    private String reference;
    private String expense_account;

    private String paid_to;
    private String date;
    private String time;

    public Expense() {
        // Default constructor required for Firestore
    }

    public Expense(String expenseId, String payment_desc, String payment_type, Double amount, String reference, String expense_account, String paid_to, String date, String time) {
        this.expenseId = expenseId;
        this.payment_desc = payment_desc;
        this.payment_type = payment_type;
        this.amount = amount;
        this.reference = reference;
        this.expense_account = expense_account;
        this.paid_to = paid_to;
        this.date = date;
        this.time = time;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getPayment_desc() {
        return payment_desc;
    }

    public void setPayment_desc(String payment_desc) {
        this.payment_desc = payment_desc;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getExpense_account() {
        return expense_account;
    }

    public void setExpense_account(String expense_account) {
        this.expense_account = expense_account;
    }

    public String getPaid_to() {
        return paid_to;
    }

    public void setPaid_to(String paid_to) {
        this.paid_to = paid_to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
