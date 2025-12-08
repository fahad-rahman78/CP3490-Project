package com.eventsystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an Event Organizer. Extends User and is Serializable.
 */
public class EventOrganizer extends User implements Serializable {

    private List<Event> createdEvents;

    /**
     * Public no-arg constructor (REQUIRED for XMLDecoder)
     */
    public EventOrganizer() {
        super();
        this.createdEvents = new ArrayList<>();
    }

    public EventOrganizer(String userID, String name, String email, String password) {
        super(userID, name, email, password);
        this.createdEvents = new ArrayList<>();
    }

    public Event createEvent(String title, String description, Date startTime, Date endTime, int capacity, Room room) {
        if (!room.isAvailable(startTime, endTime)) {
            System.out.println("Event creation failed: Room '" + room.getName() + "' is not available.");
            return null;
        }
        Event newEvent = new Event("E" + System.currentTimeMillis(), title, description, startTime, endTime, capacity, this, room);
        room.bookRoom(newEvent);
        this.createdEvents.add(newEvent);
        System.out.println(this.getName() + " created event: " + title);
        return newEvent;
    }

    /**
     * Cancels an event and notifies all registered students.
     */
    public void cancelEvent(Event event) {
        event.cancel();
        event.getRoom().releaseBooking(event);
        System.out.println(this.getName() + " cancelled event: " + event.getTitle());

        // --- NEW NOTIFICATION LOGIC ---
        String message = "Event '" + event.getTitle() + "' has been CANCELLED by the organizer.";

        // Create a general notification attached to the event
        Notification note = new Notification("N" + System.currentTimeMillis(), message, event);
        note.send(); // This prints to console for now


    }

    // --- Getters and Setters (REQUIRED for XMLEncoder) ---

    public List<Event> getCreatedEvents() { return createdEvents; }
    public void setCreatedEvents(List<Event> createdEvents) { this.createdEvents = createdEvents; }
}