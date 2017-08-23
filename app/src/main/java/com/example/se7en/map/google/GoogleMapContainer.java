package com.example.se7en.map.google;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.example.se7en.map.view.MapContainer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/

public class GoogleMapContainer implements MapContainer<MapView>,OnMapReadyCallback {

    private MapView mMapView;

    private Activity mActivity;

    private GoogleMap mGoogleMap;

    private double[] mLocation;

    public GoogleMapContainer(Activity aActivity){
        mActivity = aActivity;
    }

    @Override
    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        mMapView = new MapView(mActivity);
        Resources r = mActivity.getResources();
        int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        mMapView.setLayoutParams(params);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);


        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        mActivity = null;
        mMapView = null;
        mGoogleMap = null;
    }

    @Override
    public void onStart() {
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        mMapView.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker If Google Play services is not installed on the device, the user will
     * prompted to installit inside the SupportMapFragment. This method will only be triggered once
     * the user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        moveToLocation(mLocation);
    }

    private void moveToLocation(double[] location) {
        if (mGoogleMap == null){
            mLocation = location;
            return;
        }
        if (location != null && location.length > 1){
            LatLng sydney = new LatLng(location[0], location[1]);
            mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }
}
