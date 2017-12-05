package com.example.brano.hikingtracker.uploadJobScheduler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.brano.hikingtracker.LocationService;
import com.example.brano.hikingtracker.Session;


public class UploadSchedulerService extends Service {
    public static final String LOG_TAG = LocationService.class.getSimpleName();

//todo probably don't need this now
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!Session.isSessionStarted()) {
            Session.createSession(getApplicationContext());
        }

        return START_NOT_STICKY;
    }

}
