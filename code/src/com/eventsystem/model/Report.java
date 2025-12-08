package com.eventsystem.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a Report. Implements Serializable.
 */
public class Report implements Serializable {

    private String reportID;
    private Date generatedDate;
    private String title;
    private String data;

    /**
     * Public no-arg constructor (REQUIRED for XMLDecoder)
     */
    public Report() {
    }

    public Report(String reportID, String title, String data) {
        this.reportID = reportID;
        this.title = title;
        this.data = data;
        this.generatedDate = new Date();
    }

    public void print() {
        System.out.println("--- REPORT: " + this.title + " ---");
        System.out.println("Generated on: " + this.generatedDate);
        System.out.println(this.data);
        System.out.println("--- END OF REPORT ---");
    }

    // --- Getters and Setters (REQUIRED for XMLEncoder) ---

    public String getReportID() { return reportID; }
    public void setReportID(String reportID) { this.reportID = reportID; }

    public Date getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(Date generatedDate) { this.generatedDate = generatedDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}