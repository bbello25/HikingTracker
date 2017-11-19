package com.example.brano.hikingtracker;


import java.util.ArrayList;
import java.util.List;

class Track {
    public String name;
    public List<PointHolder> points;

    public Track(String name, List<PointHolder> points) {
        this.name = name;
        this.points = points;
    }

    public Track () {
        points = new ArrayList<>();
    }
}
