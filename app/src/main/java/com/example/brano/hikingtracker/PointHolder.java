package com.example.brano.hikingtracker;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Date;

public class PointHolder {

    public String name;
    public Date date;
    public LatLng latLng;
    public Double altitude;
    public Double accurracy;
    public String provider;
    public Double distance;

    public PointHolder() {
    }

}

