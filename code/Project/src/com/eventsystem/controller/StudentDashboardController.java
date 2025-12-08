package com.eventsystem.controller;

import com.eventsystem.model.Event;
import com.eventsystem.model.Registration;
import com.eventsystem.model.Student;
import com.eventsystem.storage.DataStorageService;
import com.eventsystem.storage.EventDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * Controller for the Student Dashboard.
 * Handles viewing events, registering for events, creating new events, and viewing notifications.
 */
public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<String> eventListView;

    @FXML
    private Label messageLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button notificationsButton; // New Button

    // Local data to hold the current state
    private Student currentUser;
    private EventDatabase database;
    private DataStorageService storageService;
    private ObservableList<Event> allEventsList;

    /**
     * Initializes the controller with data passed from the LoginController.
     * This sets up the user context and loads the initial list of events.
     */
    public void initData(Student student, EventDatabase db, DataStorageService storage) {
        this.currentUser = student;
        this.database = db;
        this.storageService = storage;

        welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");

        // Load events from the database into our observable list
        this.allEventsList = FXCollections.observableArrayList(database.getEvents());

        refreshEventList();
    }

    /**
     * Handles the "Register for Selected Event" button click.
     */
    @FXML
    protected void onRegisterButtonClick() {
        int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex == -1) {
            messageLabel.setText("Please select an event to register for.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Get the actual Event object from our list based on the selection
        // Note: We filter the list first to ensure the index matches the visual list
        Event selectedEvent = allEventsList.stream()
                .filter(event -> event.getStatus().equals("Active"))
                .collect(Collectors.toList())
                .get(selectedIndex);

        // Check if the student is already registered
        for (Registration reg : currentUser.getRegistrations()) {
            if (reg.getEvent().getEventID().equals(selectedEvent.getEventID())) {
                messageLabel.setText("You are already registered for this event.");
                messageLabel.setTextFill(Color.BLUE);
                return;
            }
        }

        // Check if the event is full
        if (selectedEvent.isFull()) {
            messageLabel.setText("Sorry, this event is full.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Perform registration
        currentUser.registerForEvent(selectedEvent);

        // Save changes to the XML file
        storageService.saveData(database);

        messageLabel.setText("Successfully registered for: " + selectedEvent.getTitle());
        messageLabel.setTextFill(Color.GREEN);

        // Refresh the list to show the new capacity
        refreshEventList();
    }

    /**
     * Handles the "Create New Event" button click.
     * Opens the event creation popup window.
     */
    @FXML
    protected void onCreateEventButtonClick() {
        try {
            // Load the FXML for the popup
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/event-creation-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find event-creation-view.fxml");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load());

            // Pass data to the new controller
            EventCreationController controller = fxmlLoader.getController();
            // We pass 'this' (the dashboard controller) so the popup can refresh our list when done
            controller.initData(this.currentUser, this.database, this.storageService, this);

            // Create and show the popup stage
            Stage formStage = new Stage();
            formStage.setTitle("Create New Event");
            formStage.setScene(scene);

            // Block interaction with the dashboard until the popup closes
            formStage.initModality(Modality.APPLICATION_MODAL);

            formStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading event creation form.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Handles the "Notifications" button click.
     * Shows a simple alert with recent notifications.
     * (In a real app, this would be a separate list view).
     */
    @FXML
    protected void onNotificationsButtonClick() {
        // For now, we simulate fetching notifications since we haven't built a Notification Center UI.
        // We check all events the student registered for. If any are cancelled, we show a message.

        StringBuilder notificationText = new StringBuilder();
        boolean hasNotifications = false;

        for (Registration reg : currentUser.getRegistrations()) {
            if (reg.getEvent().getStatus().equals("Cancelled")) {
                notificationText.append("- ALERT: The event '")
                        .append(reg.getEvent().getTitle())
                        .append("' has been CANCELLED by the organizer.\n");
                hasNotifications = true;
            }
        }

        if (!hasNotifications) {
            notificationText.append("No new notifications.");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Your Messages");
        alert.setContentText(notificationText.toString());
        alert.showAndWait();
    }

    /**
     * Handles the "Logout" button click.
     * Closes the dashboard and returns to the login screen.
     */
    @FXML
    protected void onLogoutButtonClick() {
        try {
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/login-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find login-view.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);

            Stage loginStage = new Stage();
            loginStage.setTitle("Campus Event System - Login");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.show();

            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error logging out.");
        }
    }

    /**
     * Updates the visual list of events.
     * Can be called from this class or external controllers (like EventCreationController).
     */
    public void refreshEventList() {
        // Re-fetch the list to ensure we have the latest data (including new events)
        this.allEventsList = FXCollections.observableArrayList(database.getEvents());

        // Filter and format the list for display
        eventListView.setItems(
                allEventsList.stream()
                        .filter(event -> event.getStatus().equals("Active")) // Only show Active events
                        .map(event -> {
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd 'at' h:mm a");
                            String dateStr = sdf.format(event.getStartTime());
                            return String.format("%s (Registered: %d / %d) - %s",
                                    event.getTitle(),
                                    event.getRegistrations().size(),
                                    event.getCapacity(),
                                    dateStr);
                        })
                        .collect(Collectors.toCollection(FXCollections::observableArrayList))
        );
    }
}