package com.example.brano.hikingtracker.uploadJobScheduler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.brano.hikingtracker.GeoJSONHandler;
import com.example.brano.hikingtracker.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class UploadTracksTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = UploadTracksTask.class.getSimpleName();
    private final Context mApplicationContext;
    private final Session session;
    private final String path;
    private final GeoJSONHandler geoJSONHandler;
    RequestQueue requestQueue;
    String URL = "https://webhook.site/d8813111-239a-4b75-ac26-6e3361fe1753";


    public UploadTracksTask(Context context) {
        mApplicationContext = context.getApplicationContext();
        if (!Session.isSessionStarted()) {
            Session.createSession(mApplicationContext);
        }
        session = Session.getInstance();
        path = session.getUserFileDir();
        geoJSONHandler = new GeoJSONHandler(mApplicationContext);
        requestQueue = Volley.newRequestQueue(mApplicationContext);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            File tracksDir = new File(path);
            File[] files = tracksDir.listFiles();

            for (final File file : files) {
                final String requestBody = new String(geoJSONHandler.getFileContent(file));

                /*final HashMap<String, String> postParams = new HashMap<>();
                postParams.put("fileName", name)*/


                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        super.getParams();
                        Map<String, String> params = new HashMap<String, String>();


                        params.put("fileName", file.getName());

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        super.getHeaders();
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("fileName", file.getName());
                        return params;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            Log.d(TAG, response.toString());
                        }


                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


}
