package com.eventsystem.storage;

import com.eventsystem.model.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventDatabase implements Serializable {

    private List<Student> students;
    private List<EventOrganizer> organizers;
    private List<Administrator> administrators;
    private List<Event> events;
    private List<Room> rooms;

    public EventDatabase() {
        this.students = new ArrayList<>();
        this.organizers = new ArrayList<>();
        this.administrators = new ArrayList<>();
        this.events = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public List<EventOrganizer> getOrganizers() { return organizers; }
    public void setOrganizers(List<EventOrganizer> organizers) { this.organizers = organizers; }

    public List<Administrator> getAdministrators() { return administrators; }
    public void setAdministrators(List<Administrator> administrators) { this.administrators = administrators; }

    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }

    public void addStudent(Student s) { this.students.add(s); }
    public void addOrganizer(EventOrganizer o) { this.organizers.add(o); }
    public void addAdministrator(Administrator a) { this.administrators.add(a); }
    public void addEvent(Event e) { this.events.add(e); }
    public void addRoom(Room r) { this.rooms.add(r); }
}