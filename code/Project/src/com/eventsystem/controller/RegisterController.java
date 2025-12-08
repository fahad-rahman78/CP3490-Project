package com.eventsystem.controller;

import com.eventsystem.model.Student;
import com.eventsystem.storage.DataStorageService;
import com.eventsystem.storage.EventDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField studentIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private DataStorageService storageService;
    private EventDatabase database;

    public void initData(EventDatabase db, DataStorageService storage) {
        this.database = db;
        this.storageService = storage;
    }

    @FXML
    protected void onRegisterButtonClick() {
        String name = nameField.getText();
        String email = emailField.getText();
        String studentID = studentIdField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || studentID.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (database.getStudents().stream().anyMatch(s -> s.getEmail().equals(email))) {
            messageLabel.setText("Email already registered.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        Student newStudent = new Student(studentID, studentID, name, email, password);
        database.addStudent(newStudent);
        storageService.saveData(database);

        messageLabel.setText("Registration successful! You can now login.");
        messageLabel.setTextFill(Color.GREEN);
    }
}