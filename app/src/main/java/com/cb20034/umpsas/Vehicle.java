package com.cb20034.umpsas;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private String vehicleType;
    private String brand;
    private String model;
    private String color;
    private String licenseValidDate;
    private String plateNo;
    private String academicYear;
    private String userId; // New field to store the user ID

    // Default constructor (required for Firestore)
    public Vehicle() {
        // Default constructor is necessary for Firestore to map data to objects
    }

    // Constructor with parameters
    public Vehicle(String vehicleType, String brand, String model, String color,
                   String licenseValidDate, String plateNo, String academicYear) {
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.licenseValidDate = licenseValidDate;
        this.plateNo = plateNo;
        this.academicYear = academicYear;
    }

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter methods for existing fields
    public String getVehicleType() {
        return vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public String getLicenseValidDate() {
        return licenseValidDate;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    // Setter methods for existing fields
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setLicenseValidDate(String licenseValidDate) {
        this.licenseValidDate = licenseValidDate;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    // Other methods, if needed

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleType='" + vehicleType + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", licenseValidDate='" + licenseValidDate + '\'' +
                ", plateNo='" + plateNo + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
