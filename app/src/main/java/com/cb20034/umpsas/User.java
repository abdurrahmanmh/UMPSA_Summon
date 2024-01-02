package com.cb20034.umpsas;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String name;
    private String id;
    private String phoneNo;
    private String email;
    private String icNumber;
    private String userType;
    private String fcmToken;


    public User() {
        // Default constructor required for Firestore
    }

    public User(String uid,String name, String id, String phoneNo, String email, String icNumber, String userType,String fcmToken) {
        this.uid = uid;
        this.name = name;
        this.id = id;
        this.phoneNo = phoneNo;
        this.email = email;
        this.icNumber = icNumber;
        this.userType = userType;
        this.fcmToken=fcmToken;

    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.name = uid;
    }
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

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}