package com.cb20034.umpsas;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class Summon implements Serializable {
    private String plateNumber;
    private String offence;
    private String location;
    private String fineAmount;
    private String imagePath;  // Path to the uploaded image
    private String userId;
    private String status;
    private String date;
    private String timestamp;// New field to store the user ID

    // Constructors
    public Summon() {
        // Default constructor
    }

    public Summon(String plateNumber, String offence, String location, String fineAmount, String imagePath, String userId, String status, String date, String timestamp) {
        this.plateNumber = plateNumber;
        this.offence = offence;
        this.location = location;
        this.fineAmount = fineAmount;
        this.imagePath = imagePath;
        this.userId = userId;
        this.status = status;
        this.date = date;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getOffence() {
        return offence;
    }

    public void setOffence(String offence) {
        this.offence = offence;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(String fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {return date;}
    public String getStatus() {return status;}
    public void setStatus(String status) {
        this.status = status;
    }
    public String getTimestamp() {return timestamp;}
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String toString() {
        return date +'\n'+ plateNumber + '\n' +
                "offence :" + offence +"  | Status :"+status + '\n'
         ;
    }
}
