package com.example.se7en.map.view;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.se7en.map.observer.ICameraChangeListener;


/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/

public interface MapContainer<T extends View> {

    T getMapView();

    void onCreate(Bundle savedInstanceState, ViewGroup parent);

    void onResume();

    void onPause();

    void onDestroy();

    void onStart();

    void onStop();

    void moveToLocation(double latitude, double longitude);

    void setCurrentLocation(double latitude, double longitude);

    void setFocusChangeListener(ICameraChangeListener listener);

}
