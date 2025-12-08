package com.eventsystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Room implements Serializable {

    private String roomID;
    private String name;
    private String location;
    private int capacity;
    private List<Event> bookings;

    public Room() {
        this.bookings = new ArrayList<>();
    }

    public Room(String roomID, String name, String location, int capacity) {
        this.roomID = roomID;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.bookings = new ArrayList<>();
    }

    public boolean isAvailable(Date newStart, Date newEnd) {
        for (Event existingBooking : bookings) {
            if (newStart.before(existingBooking.getEndTime()) && newEnd.after(existingBooking.getStartTime())) {
                return false;
            }
        }
        return true;
    }

    public void bookRoom(Event event) {
        if (isAvailable(event.getStartTime(), event.getEndTime())) {
            this.bookings.add(event);
        }
    }

    public void releaseBooking(Event event) {
        this.bookings.remove(event);
    }

    public String getRoomID() { return roomID; }
    public void setRoomID(String roomID) { this.roomID = roomID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<Event> getBookings() { return bookings; }
    public void setBookings(List<Event> bookings) { this.bookings = bookings; }
}