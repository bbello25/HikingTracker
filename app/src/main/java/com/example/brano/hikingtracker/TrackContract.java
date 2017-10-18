package com.example.brano.hikingtracker;


import android.provider.BaseColumns;

public final class TrackContract {

    private TrackContract() {
    }

    public final class TrackEntry implements BaseColumns {
        public final static String TABLE_NAME = "track";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_LAT = "lat";
        public final static String COLUMN_LON = "lon";
        public final static String COLUMN_TIMESTAMP = "timestamp";
    }

}
