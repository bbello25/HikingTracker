package com.example.brano.hikingtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private static Session instance = null;
    private SharedPreferences prefs;


    private Session() {
    }

    public static synchronized Session getInstance() {
        if (instance == null) {
            return null;
        }
        return instance;
    }

    public static void createSession(Context ctx) {
        if (instance == null) {
            instance = new Session();
            instance.prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        }
    }

    public static boolean isSessionStarted() {
        return instance != null;
    }


    public boolean getLoggingStatus() {
        return prefs.getBoolean("isLogging", false);
    }

    public void setLoggingStatus(boolean status) {
        prefs.edit().putBoolean("isLogging", status).apply();
    }

    public String getSessionName() {
        return prefs.getString("sessionName", "unknown");
    }

    public void setSessionName(String name) {
        prefs.edit().putString("sessionName", name).apply();
    }
}
