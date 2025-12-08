package com.eventsystem.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Association Class for Student/Event. Implements Serializable.
 */
public class Registration implements Serializable {

    private String registrationID;
    private Date timestamp;
    private Student student;
    private Event event;

    /**
     * Public no-arg constructor (REQUIRED for XMLDecoder)
     */
    public Registration() {
    }

    public Registration(Student student, Event event) {
        this.registrationID = "R-" + System.currentTimeMillis();
        this.timestamp = new Date();
        this.student = student;
        this.event = event;
    }

    // --- Getters and Setters (REQUIRED for XMLEncoder) ---

    public String getRegistrationID() { return registrationID; }
    public void setRegistrationID(String registrationID) { this.registrationID = registrationID; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}