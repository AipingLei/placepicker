package com.example.se7en.map;



/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/
public interface IPlaceProvider {

    void fetchCurrentPlace(IPlacesListener listener);

    void searchPlaces(double latitude, double longitude, IPlacesListener listener);

    void searchPlaces(String keyWords,  IPlacesListener listener);

    void destroyed();

    void initialize();
}
