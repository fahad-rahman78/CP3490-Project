package com.eventsystem.model;

import java.io.Serializable;

public abstract class User implements Serializable {

    private String userID;
    private String name;
    private String email;
    private String passwordHash;

    public User() {
    }

    public User(String userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.passwordHash = password;
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.passwordHash.equals(password);
    }

    public void logout() {
        System.out.println(this.name + " has logged out.");
    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}