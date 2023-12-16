package com.cb20034.umpsas;

public class User {
    private String name;
    private String id;
    private String phoneNo;
    private String email;
    private String icNumber;
    private String userType;


    public User() {
        // Default constructor required for Firestore
    }

    public User(String name, String id, String phoneNo, String email, String icNumber, String userType) {
        this.name = name;
        this.id = id;
        this.phoneNo = phoneNo;
        this.email = email;
        this.icNumber = icNumber;
        this.userType = userType;

    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }



}