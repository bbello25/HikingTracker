package com.example.brano.hikingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brano.hikingtracker.AlllTracksFragment.OnListFragmentInteractionListener;
import com.mapbox.services.commons.geojson.FeatureCollection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class AllTracksRecyclerViewAdapter extends RecyclerView.Adapter<AllTracksRecyclerViewAdapter.ViewHolder> {

    private final LinkedHashMap<String, FeatureCollection> mValues;
    private final OnListFragmentInteractionListener mListener;

    public AllTracksRecyclerViewAdapter(LinkedHashMap<String, FeatureCollection> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = getElementByIndex(mValues, position);
        holder.mContentView.setText(getKeyOnPosition(mValues, position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                TrackDetailFragment trackDetailFragment = new TrackDetailFragment();
                Bundle data = new Bundle();
                data.putString("name", (String) holder.mContentView.getText());
                data.putString("geojson", holder.mItem.toJson());
                trackDetailFragment.setArguments(data);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainLayout, trackDetailFragment, trackDetailFragment.getTag())
                        .commit();


                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    private FeatureCollection getElementByIndex(LinkedHashMap<String, FeatureCollection> map, int position) {
        return map.get((map.keySet().toArray())[position]);
    }

    private String getKeyOnPosition(LinkedHashMap<String, FeatureCollection> map, int position) {

        List<String> l = new ArrayList<String>(map.keySet());
        return l.get(position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public FeatureCollection mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
