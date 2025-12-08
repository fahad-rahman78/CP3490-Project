package com.eventsystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Serializable {

    private String eventID;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private int capacity;
    private String status;

    private EventOrganizer organizer;
    private Room room;
    private List<Registration> registrations;
    private List<Notification> notifications;

    public Event() {
        this.registrations = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public Event(String eventID, String title, String description, Date startTime, Date endTime, int capacity, EventOrganizer organizer, Room room) {
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.organizer = organizer;
        this.room = room;
        this.status = "Active";
        this.registrations = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public boolean isFull() {
        return this.registrations.size() >= this.capacity;
    }

    public void addRegistration(Registration reg) {
        if (!isFull()) {
            this.registrations.add(reg);
        }
    }

    public void removeRegistration(Registration reg) {
        this.registrations.remove(reg);
    }

    public void cancel() {
        this.status = "Cancelled";
    }

    public String getEventID() { return eventID; }
    public void setEventID(String eventID) { this.eventID = eventID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getStartTime() { return this.startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return this.endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public EventOrganizer getOrganizer() { return organizer; }
    public void setOrganizer(EventOrganizer organizer) { this.organizer = organizer; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }

    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
}