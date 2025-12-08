package com.eventsystem.model;

import java.io.Serializable;

/**
 * Represents an Administrator. Extends User and is Serializable.
 */
public class Administrator extends User implements Serializable {

    /**
     * Public no-arg constructor (REQUIRED for XMLDecoder)
     */
    public Administrator() {
        super();
    }

    public Administrator(String userID, String name, String email, String password) {
        super(userID, name, email, password);
    }

    public Room manageRooms_addRoom(String roomID, String name, String location, int capacity) {
        System.out.println(this.getName() + " added new room: " + name);
        Room newRoom = new Room(roomID, name, location, capacity);
        return newRoom;
    }

    public void manageUsers_changeRole(User user, String newRole) {
        System.out.println(this.getName() + " changed " + user.getName() + "'s role to " + newRole);
    }

    public Report generateReports() {
        System.out.println(this.getName() + " is generating a report...");
        String reportData = "Event Report:\n- Java Tech Talk: 100 Registrations\n- Code Jam: 2 Registrations";
        Report newReport = new Report("R" + System.currentTimeMillis(), "Event Participation Report", reportData);
        return newReport;
    }
}