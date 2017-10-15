package com.example.brano.hikingtracker;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    MapView map;
    IMapController mapController;
    TextView txtwZoom;
    TextView txtwLatLon;
    TextView txtwNorth;
    TextView txtwSouth;
    TextView txtwEast;
    TextView txtwWest;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.fragment_map, container, false);

        Context ctx = inflater.getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


        map = view.findViewById(R.id.map);
        mapController = map.getController();

        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(3);

        mapController.setZoom(16);
        GeoPoint startPoint = new GeoPoint(49.40, 18.625);
        mapController.setCenter(startPoint);

        txtwZoom = view.findViewById(R.id.txtwZoom);
        String zoom = String.valueOf(map.getZoomLevel());
        txtwZoom.setText(zoom);


       /* Double lat = center.getLatitude();
        Double lon = center.getLongitude();*/

        //txtwLatLon.setText(String.format(Locale.US,"%.2f,%.2f", lat, lon));

        map.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                BoundingBox bb = event.getSource().getBoundingBox();
                txtwNorth = view.findViewById(R.id.txtwNorth);
                txtwSouth = view.findViewById(R.id.txtwSouth);
                txtwWest = view.findViewById(R.id.txtwWest);
                txtwEast = view.findViewById(R.id.txtwEast);

                txtwNorth.setText(String.format(Locale.US, "%.2f", bb.getLatNorth()));
                txtwSouth.setText(String.format(Locale.US, "%.2f", bb.getLatSouth()));
                txtwEast.setText(String.format(Locale.US, "%.2f", bb.getLonEast()));
                txtwWest.setText(String.format(Locale.US, "%.2f", bb.getLonWest()));
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                String zoom = String.valueOf(event.getZoomLevel());
                txtwZoom = view.findViewById(R.id.txtwZoom);
                txtwZoom.setText(zoom);
                return true;
            }
        });

        return view;

    }

}
