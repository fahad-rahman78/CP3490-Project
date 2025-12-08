package com.eventsystem.programs;

import com.eventsystem.model.*;
import com.eventsystem.storage.DataStorageService;
import com.eventsystem.storage.EventDatabase;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Campus Event Management System (Console Mode) ---");

        DataStorageService storage = new DataStorageService();
        EventDatabase db = storage.loadData();

        // Check if the database is empty (or specifically if there are no admins)
        if (db.getAdministrators().isEmpty()) {
            System.out.println("Database is empty. Populating with initial data...");
            populateInitialData(db);
            storage.saveData(db);
        } else {
            System.out.println("Loaded data from event_data.xml");
        }
    }

    public static void populateInitialData(EventDatabase db) {
        // 1. Create Administrator
        Administrator admin = new Administrator("A1", "Admin Chris", "admin@campus.com", "admin123");
        db.addAdministrator(admin);

        // 2. Create Rooms
        Room hall = admin.manageRooms_addRoom("R101", "Main Hall", "Building A", 100);
        Room lab = admin.manageRooms_addRoom("R205", "Computer Lab", "Building B", 2);
        db.addRoom(hall);
        db.addRoom(lab);

        // 3. Create Users
        // --- UPDATED: Use "Fahad" as the Organizer ---
        EventOrganizer prof = new EventOrganizer("O1", "Fahad", "fahad@campus.com", "pass123");
        Student studentA = new Student("S1", "S-12345", "Anna", "anna@campus.com", "pass456");
        Student studentB = new Student("S2", "S-67890", "Bob", "bob@campus.com", "pass789");

        db.addOrganizer(prof);
        db.addStudent(studentA);
        db.addStudent(studentB);

        // 4. Create Events
        Date now = new Date();
        Date oneHourFromNow = new Date(now.getTime() + TimeUnit.HOURS.toMillis(1));
        Date twoHoursFromNow = new Date(now.getTime() + TimeUnit.HOURS.toMillis(2));
        Date threeHoursFromNow = new Date(now.getTime() + TimeUnit.HOURS.toMillis(3));


        Event techTalk = prof.createEvent("Java Tech Talk", "A talk on Java", now, oneHourFromNow, 100, hall);
        Event codeJam = prof.createEvent("Code Jam", "A coding competition", oneHourFromNow, threeHoursFromNow, 2, lab);

        if (techTalk != null) {
            db.addEvent(techTalk);
        }
        if (codeJam != null) {
            db.addEvent(codeJam);
        }
    }}