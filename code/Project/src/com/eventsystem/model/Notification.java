package com.eventsystem.model;

import java.io.Serializable;

/**
 * Represents a Notification. Implements Serializable.
 */
public class Notification implements Serializable {

    private String notificationID;
    private String message;
    private Event event;

    /**
     * Public no-arg constructor (REQUIRED for XMLDecoder)
     */
    public Notification() {
    }

    public Notification(String notificationID, String message, Event event) {
        this.notificationID = notificationID;
        this.message = message;
        this.event = event;
    }

    public void send() {
        System.out.println("--- NOTIFICATION ---");
        System.out.println("For Event: " + event.getTitle());
        System.out.println("Message: " + message);
        System.out.println("--------------------");
    }

    // --- Getters and Setters (REQUIRED for XMLEncoder) ---

    public String getNotificationID() { return notificationID; }
    public void setNotificationID(String notificationID) { this.notificationID = notificationID; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}