package com.eventsystem.controller;

import com.eventsystem.model.Administrator;
import com.eventsystem.model.EventOrganizer;
import com.eventsystem.model.Student;
import com.eventsystem.model.User;
import com.eventsystem.storage.DataStorageService;
import com.eventsystem.storage.EventDatabase;
import com.eventsystem.programs.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private DataStorageService storageService;
    private EventDatabase database;

    @FXML
    public void initialize() {
        this.storageService = new DataStorageService();
        this.database = storageService.loadData();

        if (database.getAdministrators().isEmpty()) {
            System.out.println("Database is empty. Populating with initial data...");
            Main.populateInitialData(database);
            storageService.saveData(database);
            messageLabel.setText("Database populated. Please log in.");
        } else {
            messageLabel.setText("Database loaded. Please log in.");
            messageLabel.setTextFill(Color.BLACK);
        }
    }

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email and password cannot be empty.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        for (User admin : database.getAdministrators()) {
            if (admin.login(email, password)) {
                loginSuccess("Admin", admin);
                return;
            }
        }

        for (User organizer : database.getOrganizers()) {
            if (organizer.login(email, password)) {
                loginSuccess("Organizer", organizer);
                return;
            }
        }

        for (User student : database.getStudents()) {
            if (student.login(email, password)) {
                loginSuccess("Student", student);
                return;
            }
        }

        loginFailure();
    }

    @FXML
    protected void onCreateAccountClick() {
        try {
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/register-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find register-view.fxml");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load());

            RegisterController controller = fxmlLoader.getController();
            controller.initData(this.database, this.storageService);

            Stage registerStage = new Stage();
            registerStage.setTitle("Create New Account");
            registerStage.setScene(scene);
            registerStage.initModality(Modality.APPLICATION_MODAL);
            registerStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading registration form.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void loginSuccess(String role, User user) {
        messageLabel.setText("Login Successful! Welcome, " + user.getName() + " (" + role + ").");
        messageLabel.setTextFill(Color.GREEN);

        if (role.equals("Student")) {
            openStudentDashboard((Student) user);
        } else if (role.equals("Organizer")) {
            openOrganizerDashboard((EventOrganizer) user);
        } else if (role.equals("Admin")) {
            // --- UPDATED FOR ADMIN DASHBOARD ---
            openAdminDashboard((Administrator) user);
        }
    }

    private void loginFailure() {
        messageLabel.setText("Login Failed. Invalid email or password.");
        messageLabel.setTextFill(Color.RED);
    }

    private void openStudentDashboard(Student loggedInStudent) {
        try {
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/student-dashboard-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find student-dashboard-view.fxml");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            StudentDashboardController controller = fxmlLoader.getController();
            controller.initData(loggedInStudent, this.database, this.storageService);

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Student Dashboard - Welcome " + loggedInStudent.getName());
            dashboardStage.setScene(scene);
            dashboardStage.show();

            Stage loginStage = (Stage) messageLabel.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void openOrganizerDashboard(EventOrganizer loggedInOrganizer) {
        try {
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/organizer-dashboard-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find organizer-dashboard-view.fxml");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);

            OrganizerDashboardController controller = fxmlLoader.getController();
            controller.initData(loggedInOrganizer, this.database, this.storageService);

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Organizer Dashboard - Welcome " + loggedInOrganizer.getName());
            dashboardStage.setScene(scene);
            dashboardStage.show();

            Stage loginStage = (Stage) messageLabel.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    /**
     * --- NEW METHOD FOR ADMIN DASHBOARD ---
     */
    private void openAdminDashboard(Administrator loggedInAdmin) {
        try {
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/admin-dashboard-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find admin-dashboard-view.fxml");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load());

            AdminDashboardController controller = fxmlLoader.getController();
            controller.initData(loggedInAdmin, this.database, this.storageService);

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Administrator Dashboard");
            dashboardStage.setScene(scene);
            dashboardStage.show();

            ((Stage) messageLabel.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading admin dashboard.");
            messageLabel.setTextFill(Color.RED);
        }
    }
}