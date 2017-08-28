package com.example.se7en.map.google;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.se7en.map.PlacePickActivity;
import com.example.se7en.map.R;
import com.example.se7en.map.observer.ICameraChangeListener;
import com.example.se7en.map.view.MapContainer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
 */

public class GoogleMapContainer implements MapContainer<FrameLayout>, OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {

    ImageView mCenterMarker;
    MapView mMapView;
    ImageView mCurrentControl;

    private PlacePickActivity mActivity;

    private GoogleMap mGoogleMap;

    private double[] mCurrentPosition;

    private FrameLayout mapLayout;

    private ICameraChangeListener mCameraChangeListener;


    public GoogleMapContainer(PlacePickActivity aActivity) {
        mActivity = aActivity;
    }

    @Override
    public FrameLayout getMapView() {
        return mapLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, ViewGroup parent) {
        mapLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.google_map_layout, parent, false);
        mCenterMarker = (ImageView)mapLayout.findViewById(R.id.center_marker);
        mCurrentControl = (ImageView)mapLayout.findViewById(R.id.current_position);
        mMapView = (MapView)mapLayout.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mCurrentControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPosition != null){
                    moveToLocation(mCurrentPosition[0],mCurrentPosition[1]);
                }
            }
        });
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
        mActivity.onMapReady(this);
        mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.setOnCameraMoveStartedListener(this);
    }

    @Override
    public void moveToLocation(double latitude, double longitude) {
        if (mGoogleMap == null) {
            return;
        }

        LatLng place = new LatLng(latitude, longitude);
        //mGoogleMap.addMarker(new MarkerOptions().position(place));
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void setCurrentLocation(double latitude, double longitude) {
        if (mCurrentPosition == null){
            mCurrentPosition = new double[2];
            mCurrentPosition[0] = latitude;
            mCurrentPosition[1] = longitude;
            mCenterMarker.setVisibility(View.VISIBLE);
            float y = mCenterMarker.getY();
            int height = mCenterMarker.getHeight();
            mCenterMarker.setY(y-height/2);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
            moveToLocation(latitude,longitude);
        }
    }

    @Override
    public void setFocusChangeListener(ICameraChangeListener listener) {
        mCameraChangeListener = listener;
    }

//    @Override
//    public void onCameraIdle() {
//        if ()
//        CameraPosition position = mGoogleMap.getCameraPosition();
//        double latitude = position.target.latitude;
//        double longitude = position.target.longitude;
//        Log.e("Google Map Container",  "latitude = " + latitude+  "longitude = "+ longitude);
//    }

    public boolean hasUnConsumingMoveEvent;

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            hasUnConsumingMoveEvent = true;
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
        }
    }

    @Override
    public void onCameraIdle() {
        if (hasUnConsumingMoveEvent){
            hasUnConsumingMoveEvent = false;
            CameraPosition position = mGoogleMap.getCameraPosition();
            double latitude = position.target.latitude;
            double longitude = position.target.longitude;
            Log.e("Google Map Container",  "latitude = " + latitude+  "longitude = "+ longitude);
            mCameraChangeListener.onCameraChangeFinish(latitude,longitude);
        }
    }

//    /**
//     * 在屏幕中心添加一个Marker
//     */
//    private void addMarkerInScreenCenter() {
//        LatLng latLng = mGoogleMap.getCameraPosition().target;
//        Point screenPosition = mGoogleMap.getProjection().toScreenLocation(latLng);
//        screenMarker = mGoogleMap.addMarker(new MarkerOptions()
//                .anchor(0.5f,0.5f)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.aky)));
//        screenMarker.setPositionByPixels(screenPosition.x,screenPosition.y);
//        screenMarker.set
//    }
}
