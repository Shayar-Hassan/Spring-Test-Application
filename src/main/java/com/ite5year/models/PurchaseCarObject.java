package com.ite5year.models;

import java.util.Date;

public class PurchaseCarObject {
    private String payerName;
    private Date dateOfSale;
    private double priceOfSale;
    private long waitPeriod;

    public PurchaseCarObject() {
    }


    public long getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(long waitPeriod) {
        this.waitPeriod = waitPeriod;
    }

    public PurchaseCarObject(String payerName, Date dateOfSale, double priceOfSale) {
        this.payerName = payerName;
        this.dateOfSale = dateOfSale;
        this.priceOfSale = priceOfSale;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public Date getDateOfSale() {
        return dateOfSale;
    }

    public void setDateOfSale(Date dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    public double getPriceOfSale() {
        return priceOfSale;
    }

    public void setPriceOfSale(double priceOfSale) {
        this.priceOfSale = priceOfSale;
    }
}
