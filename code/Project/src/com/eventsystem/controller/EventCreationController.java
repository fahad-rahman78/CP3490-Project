package com.eventsystem.controller;

import com.eventsystem.model.Event;
import com.eventsystem.model.Room;
import com.eventsystem.model.Student;
import com.eventsystem.storage.DataStorageService;
import com.eventsystem.storage.EventDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

public class EventCreationController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Integer> startHourCombo;

    @FXML
    private ComboBox<Integer> endHourCombo;

    @FXML
    private ComboBox<Room> roomCombo;

    @FXML
    private Spinner<Integer> capacitySpinner;

    @FXML
    private Label messageLabel;

    private Student currentUser;
    private EventDatabase database;
    private DataStorageService storageService;
    private StudentDashboardController dashboardController;

    public void initData(Student student, EventDatabase db, DataStorageService storage, StudentDashboardController dash) {
        this.currentUser = student;
        this.database = db;
        this.storageService = storage;
        this.dashboardController = dash;

        for (int i = 0; i < 24; i++) {
            startHourCombo.getItems().add(i);
            endHourCombo.getItems().add(i);
        }
        startHourCombo.setValue(12);
        endHourCombo.setValue(13);

        datePicker.setValue(LocalDate.now());

        // Debug: Print rooms to console
        System.out.println("DEBUG: Loading rooms into combo box...");
        for(Room r : database.getRooms()) {
            System.out.println(" - Found room: " + r.getName());
        }

        roomCombo.setItems(
                database.getRooms().stream()
                        // removed filter for debugging purposes, let's see all rooms first
                        // .filter(room -> !room.getName().equals("Main Hall"))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList))
        );

        roomCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Room room) {
                return room == null ? "" : room.getName() + " (Cap: " + room.getCapacity() + ")";
            }

            @Override
            public Room fromString(String string) {
                return null;
            }
        });

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10);
        capacitySpinner.setValueFactory(valueFactory);
    }

    @FXML
    protected void onCreateEventSubmit() {
        System.out.println("DEBUG: Create Event button clicked.");

        String title = titleField.getText();
        String description = descriptionArea.getText();
        Room selectedRoom = roomCombo.getValue();
        // Spinner value factory might be null if not initialized, so get value safely
        int capacity = capacitySpinner.getValue() != null ? capacitySpinner.getValue() : 0;
        LocalDate localDate = datePicker.getValue();

        // 1. Check for empty fields
        if (title == null || title.isEmpty() ||
                description == null || description.isEmpty() ||
                selectedRoom == null || localDate == null) {

            System.out.println("DEBUG: Validation failed - Empty fields.");
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        int startHour = startHourCombo.getValue();
        int endHour = endHourCombo.getValue();

        // 2. Check time validity
        if (startHour >= endHour) {
            System.out.println("DEBUG: Validation failed - Start time ("+startHour+") >= End time ("+endHour+")");
            messageLabel.setText("Start time must be before end time.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        Date startTime = Date.from(localDate.atTime(startHour, 0)
                .atZone(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(localDate.atTime(endHour, 0)
                .atZone(ZoneId.systemDefault()).toInstant());

        // 3. Check room availability
        if (!selectedRoom.isAvailable(startTime, endTime)) {
            System.out.println("DEBUG: Validation failed - Room conflict for " + selectedRoom.getName());
            messageLabel.setText("Sorry, " + selectedRoom.getName() + " is already booked at this time.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // 4. Create the event
        System.out.println("DEBUG: Creating event '" + title + "' in " + selectedRoom.getName());
        Event newEvent = currentUser.createEvent(title, description, startTime, endTime, capacity, selectedRoom);

        if (newEvent == null) {
            System.out.println("DEBUG: Event creation returned null.");
            messageLabel.setText("Error creating event.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // 5. Add to database and save
        database.addEvent(newEvent);
        storageService.saveData(database);
        System.out.println("DEBUG: Event saved to database.");

        // 6. Refresh dashboard
        if (dashboardController != null) {
            dashboardController.refreshEventList();
            System.out.println("DEBUG: Dashboard refreshed.");
        } else {
            System.out.println("DEBUG: ERROR - Dashboard controller is null!");
        }

        // 7. Close window
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}