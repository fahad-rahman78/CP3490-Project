package com.eventsystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a Student. Extends User and is Serializable.
 */
public class Student extends User implements Serializable {

    private List<Registration> registrations;
    private String studentID;

    /**
     * Public no-arg constructor (REQUIRED for XMLDecoder)
     */
    public Student() {
        super(); // Call parent's empty constructor
        this.registrations = new ArrayList<>();
    }

    public Student(String userID, String studentID, String name, String email, String password) {
        super(userID, name, email, password);
        this.studentID = studentID;
        this.registrations = new ArrayList<>();
    }

    public Registration registerForEvent(Event event) {
        if (event.isFull()) {
            System.out.println("Registration failed: Event '" + event.getTitle() + "' is full.");
            return null;
        }
        // Check if event is active before registering
        if (!event.getStatus().equals("Active")) {
            System.out.println("Registration failed: Event is not active.");
            return null;
        }

        Registration newReg = new Registration(this, event);
        this.registrations.add(newReg);
        event.addRegistration(newReg);
        System.out.println(this.getName() + " successfully registered for " + event.getTitle());
        return newReg;
    }

    public void cancelRegistration(Registration reg) {
        if (reg == null) return;
        this.registrations.remove(reg);
        reg.getEvent().removeRegistration(reg);
        System.out.println(this.getName() + " cancelled registration for " + reg.getEvent().getTitle());
    }

    /**
     * Creates a new Event and associates it with this student.
     * (Added for Sprint 2)
     */
    public Event createEvent(String title, String description, Date startTime, Date endTime, int capacity, Room room) {
        if (!room.isAvailable(startTime, endTime)) {
            System.out.println("Event creation failed: Room '" + room.getName() + "' is not available.");
            return null;
        }
        // Note: We pass 'null' for the organizer because this is a student-created event
        // In a real app, we might want to make 'User' hold the relationship instead of 'EventOrganizer'
        // or create a new constructor. For now, this works.
        Event newEvent = new Event("E" + System.currentTimeMillis(), title, description, startTime, endTime, capacity, null, room);
        newEvent.setStatus("Pending"); // Default status for student events is Pending

        room.bookRoom(newEvent);

        // Ideally, we should add this event to a list in Student too, but for now
        // we just return it so it can be added to the main database.
        System.out.println(this.getName() + " created event: " + title);
        return newEvent;
    }

    // --- Getters and Setters (REQUIRED for XMLEncoder) ---

    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }
}