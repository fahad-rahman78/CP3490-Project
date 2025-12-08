package com.eventsystem.controller;

import com.eventsystem.model.*;
import com.eventsystem.storage.DataStorageService;
import com.eventsystem.storage.EventDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Updated Controller for the Admin Dashboard.
 * Handles Event filtering, Exporting, User CRUD, Room CRUD, and Analytics.
 */
public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label messageLabel;
    @FXML private Label statsLabel;

    // -- Events Tab --
    @FXML private ListView<String> eventListView;
    @FXML private TextField searchField;

    // -- Users Tab --
    @FXML private ListView<String> userListView;

    // -- Rooms Tab --
    @FXML private ListView<String> roomListView;
    @FXML private TextField roomNameField;
    @FXML private TextField roomLocationField;
    @FXML private TextField roomCapacityField;

    // -- Analytics Tab --
    @FXML private PieChart eventPieChart;

    // Data
    private Administrator currentUser;
    private EventDatabase database;
    private DataStorageService storageService;

    // For filtering events
    private ObservableList<Event> masterEventList;
    private FilteredList<Event> filteredEventList;

    public void initData(Administrator admin, EventDatabase db, DataStorageService storage) {
        this.currentUser = admin;
        this.database = db;
        this.storageService = storage;

        welcomeLabel.setText("Admin Dashboard: " + currentUser.getName());

        // Initialize all tabs
        setupEventTab();
        setupUserTab();
        setupRoomTab();
        setupAnalyticsTab();
    }

    // =================================================
    // TAB 1: EVENT MANAGEMENT (Search, Filter, Export)
    // =================================================

    private void setupEventTab() {
        // Wrap the database list in an ObservableList
        this.masterEventList = FXCollections.observableArrayList(database.getEvents());

        // Wrap that in a FilteredList (initially show all)
        this.filteredEventList = new FilteredList<>(masterEventList, p -> true);

        // Listen for text changes in the search bar
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEventList.setPredicate(event -> {
                // If filter text is empty, display all events.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Check if title or organizer name contains the filter text
                if (event.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (event.getOrganizer() != null &&
                        event.getOrganizer().getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
            // Re-render list after filtering
            renderEventListView();
        });

        renderEventListView();
    }

    private void renderEventListView() {
        // Convert the filtered Event objects into readable Strings
        ObservableList<String> displayList = FXCollections.observableArrayList();
        for (Event e : filteredEventList) {
            String orgName = (e.getOrganizer() != null) ? e.getOrganizer().getName() : "Pending";
            displayList.add(String.format("%s [%s] - Org: %s", e.getTitle(), e.getStatus(), orgName));
        }
        eventListView.setItems(displayList);
    }

    @FXML
    protected void onGenerateReportClick() {
        // Use the filtered list to find the actual selected event object
        int index = eventListView.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            messageLabel.setText("Select an event to generate a report.");
            return;
        }

        // Because the list is filtered, index maps to filteredEventList
        Event selected = filteredEventList.get(index);

        // Generate report (Simple alert for now)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Event Report");
        alert.setHeaderText("Report for: " + selected.getTitle());
        alert.setContentText("Status: " + selected.getStatus() +
                "\nRegistrations: " + selected.getRegistrations().size() +
                "/" + selected.getCapacity());
        alert.showAndWait();
    }

    @FXML
    protected void onExportEventsClick() {
        String filename = "events_export_" + System.currentTimeMillis() + ".csv";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("Event ID,Title,Status,Organizer,Capacity,Registered\n");

            for (Event e : database.getEvents()) {
                String org = (e.getOrganizer() != null) ? e.getOrganizer().getName() : "None";
                writer.append(e.getEventID()).append(",")
                        .append(e.getTitle()).append(",")
                        .append(e.getStatus()).append(",")
                        .append(org).append(",")
                        .append(String.valueOf(e.getCapacity())).append(",")
                        .append(String.valueOf(e.getRegistrations().size())).append("\n");
            }
            messageLabel.setText("Data exported to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error exporting data.");
        }
    }

    // =================================================
    // TAB 2: USER MANAGEMENT (CRUD)
    // =================================================

    private void setupUserTab() {
        refreshUserListView();
    }

    private void refreshUserListView() {
        ObservableList<String> users = FXCollections.observableArrayList();
        database.getStudents().forEach(s -> users.add("[Student] " + s.getName() + " (" + s.getEmail() + ")"));
        database.getOrganizers().forEach(o -> users.add("[Organizer] " + o.getName() + " (" + o.getEmail() + ")"));
        userListView.setItems(users);
    }

    @FXML
    protected void onDeleteUserClick() {
        String selectedString = userListView.getSelectionModel().getSelectedItem();
        if (selectedString == null) {
            messageLabel.setText("Select a user to delete.");
            return;
        }

        // Identify if it's a student or organizer and remove
        boolean removed = false;
        if (selectedString.startsWith("[Student]")) {
            removed = database.getStudents().removeIf(s -> selectedString.contains(s.getEmail()));
        } else if (selectedString.startsWith("[Organizer]")) {
            removed = database.getOrganizers().removeIf(o -> selectedString.contains(o.getEmail()));
        }

        if (removed) {
            storageService.saveData(database);
            refreshUserListView();
            messageLabel.setText("User deleted successfully.");
        } else {
            messageLabel.setText("Error deleting user.");
        }
    }

    // =================================================
    // TAB 3: ROOM MANAGEMENT (CRUD)
    // =================================================

    private void setupRoomTab() {
        refreshRoomListView();
    }

    private void refreshRoomListView() {
        ObservableList<String> rooms = FXCollections.observableArrayList();
        database.getRooms().forEach(r -> rooms.add(r.getName() + " (" + r.getLocation() + ") - Cap: " + r.getCapacity()));
        roomListView.setItems(rooms);
    }

    @FXML
    protected void onAddRoomClick() {
        String name = roomNameField.getText();
        String loc = roomLocationField.getText();
        String capStr = roomCapacityField.getText();

        if (name.isEmpty() || loc.isEmpty() || capStr.isEmpty()) {
            messageLabel.setText("Please fill all room fields.");
            return;
        }

        try {
            int cap = Integer.parseInt(capStr);
            Room newRoom = new Room("R-" + System.currentTimeMillis(), name, loc, cap);

            database.addRoom(newRoom);
            storageService.saveData(database);

            // Clear fields
            roomNameField.clear();
            roomLocationField.clear();
            roomCapacityField.clear();

            refreshRoomListView();
            messageLabel.setText("Room added: " + name);

        } catch (NumberFormatException e) {
            messageLabel.setText("Capacity must be a number.");
        }
    }

    @FXML
    protected void onDeleteRoomClick() {
        int index = roomListView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Room removed = database.getRooms().remove(index);
            storageService.saveData(database);
            refreshRoomListView();
            messageLabel.setText("Deleted room: " + removed.getName());
        } else {
            messageLabel.setText("Select a room to delete.");
        }
    }

    // =================================================
    // TAB 4: ANALYTICS (Charts)
    // =================================================

    private void setupAnalyticsTab() {
        long activeCount = database.getEvents().stream().filter(e -> "Active".equals(e.getStatus())).count();
        long pendingCount = database.getEvents().stream().filter(e -> "Pending".equals(e.getStatus())).count();
        long cancelledCount = database.getEvents().stream().filter(e -> "Cancelled".equals(e.getStatus())).count();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Active", activeCount),
                new PieChart.Data("Pending", pendingCount),
                new PieChart.Data("Cancelled", cancelledCount)
        );

        eventPieChart.setData(pieChartData);

        statsLabel.setText("Total Students: " + database.getStudents().size() +
                " | Total Organizers: " + database.getOrganizers().size() +
                " | Total Rooms: " + database.getRooms().size());
    }

    // =================================================
    // GLOBAL ACTIONS
    // =================================================

    @FXML
    protected void onLogoutButtonClick() {
        try {
            URL fxmlUrl = getClass().getResource("/com/eventsystem/view/login-view.fxml");
            if (fxmlUrl == null) return;
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Stage stage = new Stage();
            stage.setTitle("Campus Event System - Login");
            stage.setScene(scene);
            stage.show();
            ((Stage) welcomeLabel.getScene().getWindow()).close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}