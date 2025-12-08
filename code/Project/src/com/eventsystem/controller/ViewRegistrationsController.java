package com.eventsystem.controller;

import com.eventsystem.model.Event;
import com.eventsystem.model.Registration;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.stream.Collectors;

/**
 * Controller for the View Registrations popup.
 * Displays a list of students registered for a specific event.
 */
public class ViewRegistrationsController {

    @FXML
    private ListView<String> registrationListView;

    /**
     * Called by OrganizerDashboardController to pass in the selected event.
     * Populates the ListView with student names and IDs.
     */
    public void initData(Event event) {
        if (event.getRegistrations().isEmpty()) {
            registrationListView.setItems(FXCollections.observableArrayList("No registrations for this event."));
        } else {
            registrationListView.setItems(
                    event.getRegistrations().stream()
                            .map(reg -> {
                                // Format: "Anna (S-12345) - anna@campus.com"
                                return String.format("%s (%s) - %s",
                                        reg.getStudent().getName(),
                                        reg.getStudent().getStudentID(),
                                        reg.getStudent().getEmail());
                            })
                            .collect(Collectors.toCollection(FXCollections::observableArrayList))
            );
        }
    }
}