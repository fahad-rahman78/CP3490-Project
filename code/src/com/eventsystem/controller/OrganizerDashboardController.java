package com.eventsystem.controller;

import com.eventsystem.model.Event;
import com.eventsystem.model.EventOrganizer;
import com.eventsystem.storage.DataStorageService;
import com.eventsystem.storage.EventDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
 * Controller for the Organizer Dashboard.
 * Handles viewing owned events, canceling events, viewing registrations,
 * and approving/rejecting pending requests.
 */
public class OrganizerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<String> eventListView;   // My events
    @FXML private ListView<String> pendingListView; // Pending requests (New)
    @FXML private Label messageLabel;
    @FXML private Button cancelButton;
    @FXML private Button viewRegButton;
    @FXML private Button approveButton; // New
    @FXML private Button rejectButton; // New
    @FXML private Button logoutButton; // New

    private EventOrganizer currentUser;
    private EventDatabase database;
    private DataStorageService storageService;
    private ObservableList<Event> myEventsList;
    private ObservableList<Event> pendingEventsList; // New list for pending

    /**
     * Initializes the controller with data passed from the LoginController.
     */
    public void initData(EventOrganizer organizer, EventDatabase db, DataStorageService storage) {
        this.currentUser = organizer;
        this.database = db;
        this.storageService = storage;

        welcomeLabel.setText("Welcome, " + currentUser.getName() + " (Event Organizer)");
        refreshEventList();
    }

    /**
     * Handles canceling an ACTIVE event.
     * Only works on the "My Created Events" list.
     */
    @FXML
    protected void onCancelEventClick() {
        int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex == -1) {
            // If nothing selected in main list, check if they are trying to cancel a pending request
            // (This is a common user mistake, so we guide them)
            if (pendingListView.getSelectionModel().getSelectedIndex() != -1) {
                messageLabel.setText("Use 'Reject Request' for pending items.");
            } else {
                messageLabel.setText("Please select an active event to cancel.");
            }
            messageLabel.setTextFill(Color.RED);
            return;
        }

        Event selectedEvent = myEventsList.get(selectedIndex);

        if (selectedEvent.getStatus().equals("Cancelled")) {
            messageLabel.setText("This event is already cancelled.");
            messageLabel.setTextFill(Color.BLUE);
            return;
        }

        // Cancel the event
        currentUser.cancelEvent(selectedEvent);
        storageService.saveData(database);

        messageLabel.setText("Event Cancelled: " + selectedEvent.getTitle());
        messageLabel.setTextFill(Color.GREEN);
        refreshEventList();
    }

    /**
     * Handles viewing registrations for a selected event.
     * Works for both Active and Pending lists.
     */
    @FXML
    protected void onViewRegistrationsClick() {
        // Can view registrations for EITHER list
        Event selectedEvent = getSelectedEvent();
        if (selectedEvent == null) {
            messageLabel.setText("Please select an event first.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/view-registrations-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find view-registrations-view.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load());
            ViewRegistrationsController controller = fxmlLoader.getController();
            controller.initData(selectedEvent);
            Stage popupStage = new Stage();
            popupStage.setTitle("Registrations: " + selectedEvent.getTitle());
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading registrations window.");
        }
    }

    /**
     * Handles approving a pending event request.
     */
    @FXML
    protected void onApproveEventClick() {
        int selectedIndex = pendingListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            messageLabel.setText("Select a pending request to approve.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        Event eventToApprove = pendingEventsList.get(selectedIndex);

        // Change status to Active
        eventToApprove.setStatus("Active");
        // Assign this organizer as the "owner" / approver so they can manage it
        eventToApprove.setOrganizer(this.currentUser);
        this.currentUser.getCreatedEvents().add(eventToApprove);

        storageService.saveData(database);

        messageLabel.setText("Approved: " + eventToApprove.getTitle());
        messageLabel.setTextFill(Color.GREEN);

        refreshEventList();
    }

    /**
     * Handles rejecting a pending event request.
     */
    @FXML
    protected void onRejectEventClick() {
        int selectedIndex = pendingListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            messageLabel.setText("Select a pending request to reject.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        Event eventToReject = pendingEventsList.get(selectedIndex);

        // Change status to Rejected (or Cancelled)
        eventToReject.setStatus("Rejected");

        // Release the room booking since it won't happen
        eventToReject.getRoom().releaseBooking(eventToReject);

        storageService.saveData(database);

        messageLabel.setText("Rejected: " + eventToReject.getTitle());
        messageLabel.setTextFill(Color.RED); // Red to signify rejection

        refreshEventList();
    }

    /**
     * Handles logging out the user.
     */
    @FXML
    protected void onLogoutButtonClick() {
        try {
            // Load the Login View
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/login-view.fxml");
            if (fxmlUrl == null) {
                messageLabel.setText("Error: Cannot find login-view.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);

            // Create a new stage for the login window
            Stage loginStage = new Stage();
            loginStage.setTitle("Campus Event System - Login");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.show();

            // Close the current dashboard window
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error logging out.");
        }
    }

    /**
     * Refreshes the data in both ListViews.
     */
    public void refreshEventList() {
        // 1. Populate "My Created Events"
        this.myEventsList = FXCollections.observableArrayList(currentUser.getCreatedEvents());
        eventListView.setItems(
                myEventsList.stream()
                        .map(this::formatEventString)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList))
        );

        // 2. Populate "Pending Requests" (All events with status "Pending")
        this.pendingEventsList = FXCollections.observableArrayList(
                database.getEvents().stream()
                        .filter(e -> "Pending".equals(e.getStatus()))
                        .collect(Collectors.toList())
        );

        pendingListView.setItems(
                pendingEventsList.stream()
                        .map(this::formatEventString)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList))
        );
    }

    private String formatEventString(Event event) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd h:mm a");
        String dateStr = (event.getStartTime() != null) ? sdf.format(event.getStartTime()) : "TBD";
        return String.format("%s (%s) - %s", event.getTitle(), event.getStatus(), dateStr);
    }

    /**
     * Helper to get the selected event from EITHER list.
     * This allows the "Cancel" and "View Reg" buttons to work for both lists.
     */
    private Event getSelectedEvent() {
        int idx1 = eventListView.getSelectionModel().getSelectedIndex();
        if (idx1 != -1) return myEventsList.get(idx1);

        int idx2 = pendingListView.getSelectionModel().getSelectedIndex();
        if (idx2 != -1) return pendingEventsList.get(idx2);

        return null;
    }
}