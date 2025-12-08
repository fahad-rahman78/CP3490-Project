package com.eventsystem.storage;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DataStorageService {

    private static final String FILENAME = "event_data.xml";

    public void saveData(EventDatabase database) {
        try (XMLEncoder encoder = new XMLEncoder(
                new BufferedOutputStream(
                        new FileOutputStream(FILENAME)))) {

            encoder.writeObject(database);
            System.out.println("--- Data successfully saved to " + FILENAME + " ---");

        } catch (Exception e) {
            System.out.println("!!! ERROR saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public EventDatabase loadData() {
        try (XMLDecoder decoder = new XMLDecoder(
                new BufferedInputStream(
                        new FileInputStream(FILENAME)))) {

            EventDatabase database = (EventDatabase) decoder.readObject();
            System.out.println("--- Data successfully loaded from " + FILENAME + " ---");
            return database;

        } catch (Exception e) {
            System.out.println("!!! No save file found or error loading data. Creating new database... !!!");
            return new EventDatabase();
        }
    }
}