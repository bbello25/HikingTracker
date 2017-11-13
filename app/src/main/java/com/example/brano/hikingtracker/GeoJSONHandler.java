package com.example.brano.hikingtracker;

import android.location.Location;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class GeoJSONHandler {
    final static Object lock = new Object();
    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128));
    private final File file;

    public GeoJSONHandler(File file) {
        this.file = file;
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


}
