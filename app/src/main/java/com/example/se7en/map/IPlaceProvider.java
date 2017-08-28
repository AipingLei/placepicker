package com.example.se7en.map;


import com.example.se7en.map.model.Place;
import com.example.se7en.map.observer.IPlacesListener;

/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/
public interface IPlaceProvider {

    IPlaceProvider setListener(IPlacesListener listener);

    void currentNearby();

    void nearbySearch(double latitude, double longitude);

    void textSearch(String keyWords);

    void placeDetail(Place place);

    void destroyed();

    void initialize();
}
