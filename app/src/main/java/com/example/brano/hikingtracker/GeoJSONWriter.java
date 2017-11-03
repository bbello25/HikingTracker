package com.example.brano.hikingtracker;

import android.location.Location;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;


public class GeoJSONWriter implements Runnable {
    private final String TAG = GeoJSONWriter.class.getSimpleName();

    private final static String HEADER = "{\"type\": \"FeatureCollection\",\"features\": [\n";
    private final static String TRAILER = "]}";
    private final static int TRAILER_LENGTH = -TRAILER.length();
    private final static String TEMPLATE =
            "{\"type\": \"Feature\"," +
                    "\"properties\":{" +
                    "%s" +
                    "}," +
                    "\"geometry\":{" +
                    "\"type\":\"Point\",\"coordinates\":" +
                    "%s}}\n";
    private final static String ATTRIBUTE_TEMPLATE = ",\"%s\":\"%s\"";
    private final static String COORD_TEMPLATE = "[%s,%s]";
    private final static String DIVIDER = ",";

    private String description;
    private File file;
    private Location location;

    public GeoJSONWriter(File file, Location location, String description) {
        this.file = file;
        this.location = location;
        this.description = description;
    }

    @Override
    public void run() {
        try {
            synchronized (GeoJSONHandler.lock) {
                byte[] value = getString(file.exists()).getBytes();

                RandomAccessFile raf;
                if (!file.exists()) {
                    file.createNewFile();
                    raf = new RandomAccessFile(file, "rw");
                } else {
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(file.length() + TRAILER_LENGTH);
                }
                raf.write(value);
                raf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getString(boolean append) {
        String coords = String.format(COORD_TEMPLATE, location.getLongitude(), location.getLatitude());
        StringBuilder value = new StringBuilder();
        if (append) {
            value.append(DIVIDER);
        } else {
            value.append(HEADER);
        }
        String dateTimeString = String.valueOf(new Date(location.getTime()));
        String extra = "";
        StringBuilder attributes = new StringBuilder();
        attributes.append("\"time\":\"").append(dateTimeString).append("\"");
        attributes.append(String.format(ATTRIBUTE_TEMPLATE, "provider", location.getProvider()));
        attributes.append(String.format(ATTRIBUTE_TEMPLATE, "time_long", location.getTime()));
        /*if (!Strings.isNullOrEmpty(desc)) {
            attributes.append(String.format(ATTRIBUTE_TEMPLATE, "description", Strings.cleanDescriptionForJson(desc)));
        }*/
        if (location.hasAccuracy()) {
            attributes.append(String.format(ATTRIBUTE_TEMPLATE, "accuracy", location.getAccuracy()));
        }
        if (location.hasAltitude()) {
            attributes.append(String.format(ATTRIBUTE_TEMPLATE, "altitude", location.getAltitude()));
        }
        if (location.hasBearing()) {
            attributes.append(String.format(ATTRIBUTE_TEMPLATE, "bearing", location.getBearing()));
        }
        if (location.hasSpeed()) {
            attributes.append(String.format(ATTRIBUTE_TEMPLATE, "speed", location.getSpeed()));
        }

        extra = attributes.toString();
        value.append(String.format(TEMPLATE,
                extra,
                coords)).append(TRAILER);
        return value.toString();
    }
}
