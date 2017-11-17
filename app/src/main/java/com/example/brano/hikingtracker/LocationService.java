package com.example.brano.hikingtracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class LocationService extends Service implements LocationListener, GpsStatus.Listener {
    public static final String LOG_TAG = LocationService.class.getSimpleName();

    LocationManager locationManager;
    String filename;
    File file;
    GeoJSONHandler geoJSONHandler;

    private final LocationServiceBinder binder = new LocationServiceBinder();

    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startUpdatingLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        //This flag enables LocationManager to exchange “data packet”
        // with 3G/4G network base stations in order to get better location.
        // This additional data packet exchange may increase the cost
        // for the user’s monthly or temporary data plan.
        criteria.setCostAllowed(false);
        criteria.setBearingRequired(false);

        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        Integer gpsFreqInMillis = 3000;
        Integer gpsFreqInDistance = 1;  // in meters

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission to access location was not granted.");
            return;
        }
        if (locationManager != null) {
            locationManager.addGpsStatusListener(this);
            locationManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, this, null);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_STICKY;
    }

    private void handleIntent(Intent intent) {

        if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                if (bundle.getBoolean(Constants.START_LOGGING)) {
                    Log.i(LOG_TAG, "Intent received - Start Logging Now");
                    startLogging();
                }

                if (bundle.getBoolean(Constants.STOP_LOGGING)) {
                    Log.i(LOG_TAG, "Intent received - Stop Logging Now");
                    stopLogging();
                }
            }
        } else {
            //todo service has been killed or restarted
        }
    }

    private void startLogging() {
        try {
            startForeground(Constants.NOTIFICATION_ID, new Notification());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not start LocationService in foreground. ", e);
        }

        filename = "test123.geojson";
        file = new File(getApplicationContext().getFilesDir(), filename);
        geoJSONHandler = new GeoJSONHandler(file);

        showNotification();
        startUpdatingLocation();
    }

    private void stopLogging() {
        try {
            stopForeground(true);
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
            Log.i(LOG_TAG, new String(geoJSONHandler.getFileContent(file)));
            removeNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //todo implement notfication channel
    private void showNotification() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.btn_moreinfo);
        mBuilder.setContentTitle("Hiking Tracker");
        mBuilder.setContentText("Tracker is running.");
        mBuilder.setOngoing(true);

        /*Intent contentIntent = new Intent(this, TrackerFragment.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TrackerFragment.class);

        stackBuilder.addNextIntent(contentIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);*/

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
        }
    }

    private void removeNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy.");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOG_TAG, "onTaskRemoved.");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onGpsStatusChanged(int i) {
        //annoying af
        //Log.d(LOG_TAG, "onGpsStatusChanged: " + i);
    }

    @Override
    public void onLocationChanged(Location location) {
        geoJSONHandler.logIntoFile(null, location);
        Log.d(LOG_TAG, "(" + location.getLatitude() + "," + location.getLongitude() + ")");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(LOG_TAG, "onStatusChanged:" + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(LOG_TAG, "onProviderEnabled: " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(LOG_TAG, "onProviderDisabled: " + s);
    }
}
