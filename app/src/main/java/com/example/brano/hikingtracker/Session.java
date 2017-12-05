package com.example.brano.hikingtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;

import java.io.File;

public class Session {

    private final String TAG = Session.class.getSimpleName();

    private static Session instance = null;
    private SharedPreferences prefs;
    private static Context context;

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
            context = ctx.getApplicationContext();
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

    public void userLogin(int id, String name) {
        String userDir = context.getFilesDir().getPath() + "/" + String.valueOf(id);
        File folder = new File(context.getFilesDir().getPath() + File.separator + String.valueOf(id));
        boolean success;
        if (!folder.exists()) {
            success = folder.mkdirs();
            if (success) {
                Log.d(TAG, "User folder " + folder.getAbsolutePath() + " created successfully.");
            } else {
                Log.d(TAG, "User folder " + folder.getAbsolutePath() + " creation failed.");
            }
        }
        prefs.edit().putString("userName", name).apply();
        prefs.edit().putInt("userId", id).apply();
        prefs.edit().putString("userFileDir", userDir).apply();
    }

    public void userLogout() {
        prefs.edit().remove("userName").apply();
        prefs.edit().remove("userId").apply();
        prefs.edit().remove("userFileDir").apply();
    }

    public boolean getLoginStatus() {
        return !((prefs.getString("userName", null) == null) ||
                (prefs.getInt("userId", -1) == -1));
    }

    public String getUserName() {
        return prefs.getString("userName", null);
    }

    public String getUserFileDir() {
        return prefs.getString("userFileDir", context.getFilesDir().getPath());
    }
}
