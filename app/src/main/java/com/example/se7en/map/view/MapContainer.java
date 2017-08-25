package com.example.se7en.map.view;

import android.os.Bundle;
import android.view.View;

import com.example.se7en.map.ICameraChangeListener;


/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/

public interface MapContainer<T extends View> {

    T getMapView();

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onDestroy();

    void onStart();

    void onStop();

    void moveToLocation(double latitude, double longitude);

    void setFocusChangeListener(ICameraChangeListener listener);

}
