package com.example.se7en.map;


import com.example.se7en.map.model.Place;


public interface IPlaceListener {

    void onPlaceFetched(Place place);

    void onPlaceFetchError(Exception errorMessage);

}
