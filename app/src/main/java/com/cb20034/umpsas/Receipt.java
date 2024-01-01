package com.cb20034.umpsas;

import java.io.Serializable;
import java.util.Date;

public class Receipt implements Serializable {
    private String plateNo;
    private String summonId;
    private String offenceDate;
    private String offenceLocation;
    private String payDate;
    private String offence;
    private String paymentMethod;
    private String amountPaid;
    private String userId;
    private String receiptId;

    // Constructors
    public Receipt() {
        // Default constructor is needed for deserialization
    }

    public Receipt(String plateNo, String summonId, String offenceDate, String offenceLocation,
                   String payDate, String offence, String paymentMethod, String amountPaid,
                   String userId, String receiptId) {
        this.plateNo = plateNo;
        this.summonId = summonId;
        this.offenceDate = offenceDate;
        this.offenceLocation = offenceLocation;
        this.payDate = payDate;
        this.offence = offence;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.userId = userId;
        this.receiptId = receiptId;
    }

    // Getters and Setters

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getSummonId() {
        return summonId;
    }

    public void setSummonId(String summonId) {
        this.summonId = summonId;
    }

    public String getOffenceDate() {
        return offenceDate;
    }

    public void setOffenceDate(String offenceDate) {
        this.offenceDate = offenceDate;
    }

    public String getOffenceLocation() {
        return offenceLocation;
    }

    public void setOffenceLocation(String offenceLocation) {
        this.offenceLocation = offenceLocation;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getOffence() {
        return offence;
    }

    public void setOffence(String offence) {
        this.offence = offence;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    // Additional methods can be added as needed
    @Override
    public String toString() {
        return receiptId + '\n' + plateNo +'\n'+payDate+'\n' ;
    }
}
