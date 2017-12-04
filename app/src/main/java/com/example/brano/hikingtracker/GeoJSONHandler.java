package com.example.brano.hikingtracker;

import android.content.Context;
import android.location.Location;

import com.mapbox.services.commons.geojson.FeatureCollection;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class GeoJSONHandler {
    final static Object lock = new Object();
    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128));
    private File file = null;
    private Context context = null;

    public GeoJSONHandler(File file) {
        this.file = file;
    }

    public GeoJSONHandler(Context ctx) {
        context = ctx;
    }

    public void logIntoFile(String description, Location location) {
        Runnable r = new GeoJSONWriter(file, location, description);
        EXECUTOR.execute(r);
    }

    public byte[] getFileContent(File file) throws IOException {
        synchronized (GeoJSONHandler.lock) {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            byte[] buffer = new byte[(int) raf.length()];
            raf.read(buffer);
            raf.close();
            return buffer;
        }

    }

    public LinkedHashMap<String, FeatureCollection> getAllTracks() {

        File tracksDir = context.getFilesDir();
        File[] files = tracksDir.listFiles();

        LinkedHashMap<String, FeatureCollection> tracks = new LinkedHashMap<String, FeatureCollection>();
        for (File file : files) {
            try {
                tracks.put(file.getName(), FeatureCollection.fromJson(new String(getFileContent(file))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tracks;
    }



    public List<String> getTrackList() {

        List<String> trackNames = new ArrayList<>();

        File tracksDir = context.getFilesDir();
        File[] files = tracksDir.listFiles();
        for (File file : files) {
            String filename = file.getName();
            if (filename.indexOf(".") > 0) {
                filename = filename.substring(0, filename.lastIndexOf("."));
            }
            trackNames.add(filename);
        }

        return trackNames;
    }

}
