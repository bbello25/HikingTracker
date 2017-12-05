package com.example.brano.hikingtracker.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.brano.hikingtracker.GeoJSONHandler;
import com.example.brano.hikingtracker.PointHolder;
import com.example.brano.hikingtracker.R;
import com.example.brano.hikingtracker.Session;
import com.example.brano.hikingtracker.Track;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackDetailFragment extends Fragment {

    Bundle data;
    JSONObject trackData;
    JSONArray features;
    List<PointHolder> points;
    private View view;
    private Session session;

    public TrackDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_track_detail, container, false);

        if (!Session.isSessionStarted()) {
            Session.createSession(view.getContext());
        }
        session = Session.getInstance();

        Button buttonSend = view.findViewById(R.id.btnShownOnMap);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = new MapFragment();
                TrackDetailFragment trackDetailFragment = new TrackDetailFragment();
                Bundle data = new Bundle();
                //todo dorobit
                // /data.putDoubleArray();
            }
        });

        new getTrackInfo().execute(data);

        return view;

    }

    private class getTrackInfo extends AsyncTask<Bundle, Void, Track> {

        @Override
        protected Track doInBackground(Bundle... bundles) {
            Track track = new Track();
            try {
                GeoJSONHandler geoJSONHandler = new GeoJSONHandler(getActivity().getApplicationContext());

                track.name = bundles[0].getString("name", null);
                File file = new File(session.getUserFileDir(), track.name);

                trackData = new JSONObject(new String(geoJSONHandler.getFileContent(file)));
                features = trackData.getJSONArray("features");

                PointHolder pointHolder;
                for (int i = 0; i < features.length(); i++) {
                    pointHolder = new PointHolder();
                    JSONObject feature = features.getJSONObject(i);
                    JSONObject geometry = feature.getJSONObject("geometry");
                    JSONObject properties = feature.getJSONObject("properties");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                    pointHolder.date = dateFormat.parse((String) properties.get("time"));
                    pointHolder.provider = String.valueOf(properties.get("provider"));
                    pointHolder.accurracy = Double.parseDouble(properties.get("accuracy").toString());

                    JSONArray coords = geometry.getJSONArray("coordinates");
                    pointHolder.latLng = new LatLng(coords.getDouble(1), coords.getDouble(0));
                    if (properties.has("altitude")) {
                        pointHolder.latLng.setAltitude(Double.parseDouble(properties.get("altitude").toString()));
                    }
                    track.points.add(pointHolder);
                }
            } catch (JSONException | ParseException | IOException e) {
                e.printStackTrace();
            }


            return track;
        }

        @Override
        protected void onPostExecute(Track track) {
            super.onPostExecute(track);


            TextView txtwName = view.findViewById(R.id.txtwName);
            TextView txtwDistance = view.findViewById(R.id.txtwDistance);
            TextView txtwAvgSpeed = view.findViewById(R.id.txtwAvgSpeed);
            TextView txtwStart = view.findViewById(R.id.txtwStart);
            TextView txtwEnd = view.findViewById(R.id.txtwEnd);
            Button showOnMap = view.findViewById(R.id.btnShownOnMap);

            txtwName.setText(track.name);
            new getTrackDetails().execute(track);

        }
    }

    private class getTrackDetails extends AsyncTask<Track, Void, Bundle> {


        @Override
        protected Bundle doInBackground(Track... tracks) {
            points = tracks[0].points;
            if (points.size() <= 0)
                return null;
            Double distance = 0.0;
            Date start = points.get(0).date;
            Date end = points.get(points.size() - 1).date;


            for (int i = 0; i < points.size() - 1; i++) {

                distance += points.get(i).latLng.distanceTo(points.get(i + 1).latLng);
            }

            Bundle ret = new Bundle();
            ret.putDouble("distance", distance);
            ret.putLong("start", start.getTime());
            ret.putLong("end", end.getTime());

            return ret;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);

            TextView txtwDistance = view.findViewById(R.id.txtwDistance);
            TextView txtwAvgSpeed = view.findViewById(R.id.txtwAvgSpeed);
            TextView txtwStart = view.findViewById(R.id.txtwStart);
            TextView txtwEnd = view.findViewById(R.id.txtwEnd);


            txtwDistance.setText(String.format("%.2f", bundle.getDouble("distance")));


            long mills = bundle.getLong("end") - bundle.getLong("start");
            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;
            long secs = (int) (mills / 1000) % 60;
            String diff = hours + ":" + mins + ":" + secs;

            double avgspeed = bundle.getDouble("distance") / (mills / 1000.0);

            txtwAvgSpeed.setText(String.format("%.2f", avgspeed));

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            txtwStart.setText(formatter.format(new Date(bundle.getLong("start"))));

            txtwEnd.setText(formatter.format(new Date(bundle.getLong("end"))));
        }
    }
}
