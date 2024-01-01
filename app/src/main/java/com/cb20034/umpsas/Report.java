package com.cb20034.umpsas;

import java.io.Serializable;

public class Report implements Serializable {
    private String userId;
    private String reportId;
    private String reportDetail;
    private String imagePath;
    private String date;

    // Default (no-argument) constructor
    public Report() {
        // Default constructor is needed for deserialization
    }

    // Parameterized constructor
    public Report(String userId, String reportId, String reportDetail, String imagePath, String date) {
        this.userId = userId;
        this.reportId = reportId;
        this.reportDetail = reportDetail;
        this.imagePath = imagePath;
        this.date = date;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportDetail() {
        return reportDetail;
    }

    public void setReportDetail(String reportDetail) {
        this.reportDetail = reportDetail;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String toString() {
        return '\n'+ reportId + '\n' +
                "Date :" + date + '\n'
                ;
    }
    // Additional methods can be added as needed
}
