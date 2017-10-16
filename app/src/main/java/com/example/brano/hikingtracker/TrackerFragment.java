package com.example.brano.hikingtracker;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackerFragment extends Fragment {

    String TAG = TrackerFragment.class.getSimpleName();
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1234;

    public LocationService locationService;
    private View view;
    private Context context;
    private Activity activity;

    public TrackerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tracker, container, false);
        context = view.getContext();
        activity = this.getActivity();

        connectToLoacationService();

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
                    REQUEST_LOCATION_PERMISSION_CODE);
        } else {
            bindToLocationService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bindToLocationService();
                } else {
                    //Not work ... why?
                    Toast.makeText(context, "Location service will not track your position", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Location permission was not granted");
                }
            }
        }
    }

    private void bindToLocationService() {
        Toast.makeText(context, "Starting location service", Toast.LENGTH_LONG).show();
        final Intent serviceStart = new Intent(this.getActivity(), LocationService.class);
        activity.startService(serviceStart);
        activity.bindService(serviceStart, serviceConnection, Context.BIND_AUTO_CREATE);
    }


}
