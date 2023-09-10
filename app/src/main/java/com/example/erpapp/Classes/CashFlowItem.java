package com.example.erpapp.Classes;

public class CashFlowItem {
    private String date;
    private double cashFlowAmount;
    private  String paymentDec;

    public CashFlowItem(String date, double cashFlowAmount, String paymentDec) {
        this.date = date;
        this.cashFlowAmount = cashFlowAmount;
        this.paymentDec = paymentDec;
    }


    public String getDate() {
        return date;
    }

    public double getCashFlowAmount() {
        return cashFlowAmount;
    }

    public String getPaymentDec() {
        return paymentDec;
    }


}


