package com.example.se7en.map;


import android.app.Activity;
import android.content.Context;

/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/
public interface IPlaceProvider {

    void fetchCurrentPlace(IPlaceListener listener);

    void searchPlaces(double latitude, double longitude, IPlacesListener listener);

    void destroyed();

    void initialize();
}
