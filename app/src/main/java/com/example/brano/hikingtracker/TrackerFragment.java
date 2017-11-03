package com.example.brano.hikingtracker;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeEvent;

public class TrackerFragment extends Fragment {

    String TAG = TrackerFragment.class.getSimpleName();

    public LocationService locationService;
    private View view;
    private Context context;
    private Activity activity;
    private Button btnStartLogging;
    private Button btnStopLogging;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public TrackerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tracker, container, false);
        context = view.getContext();
        activity = this.getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        btnStartLogging = view.findViewById(R.id.btnStartLogging);
        btnStopLogging = view.findViewById(R.id.btnStopLogging);

        //for future use
        editor = preferences.edit();
        editor.putBoolean("needBound", false);
        editor.apply();

        btnStartLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToLoacationService();
            }
        });

        btnStopLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
            }
        });
        return view;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();

            if (name.endsWith("LocationService")) {
                locationService = ((LocationService.LocationServiceBinder) service).getService();

                locationService.startUpdatingLocation();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("LocationService")) {
                locationService = null;
            }
        }
    };

    //TODO Create permissions utils
    private void connectToLoacationService() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_LOCATION_PERMISSION_CODE);
        } else {
            if (preferences.getBoolean("needBound", false)) {
                bindToLocationService();
            } else {
                startLoactionService();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission was granted");
                    if (preferences.getBoolean("needBound", false)) {
                        bindToLocationService();
                    } else {
                        startLoactionService();
                    }
                } else {
                    //Not work ... why?
                    Toast.makeText(context, "Location service will not track your position", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Location permission was not granted");
                }
            }
        }
    }

    private void bindToLocationService() {
        Toast.makeText(context, "Binding to location service.", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Binding to location service.");
        final Intent serviceStart = new Intent(this.getActivity(), LocationService.class);
        activity.startService(serviceStart);
        activity.bindService(serviceStart, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startLoactionService() {
        Toast.makeText(context, "Starting location service.", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Starting location service.");
        Intent startIntent = new Intent(activity, LocationService.class);
        startIntent.putExtra(Constants.START_LOGGING, true);
        context.startService(startIntent);
    }

    private void stopLocationService() {
        Toast.makeText(context, "Stopping location service.", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Stopping location service.");
        Intent startIntent = new Intent(activity, LocationService.class);
        startIntent.putExtra(Constants.STOP_LOGGING, true);
        context.startService(startIntent);
    }


}
