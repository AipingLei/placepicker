package com.example.se7en.map;


import com.example.se7en.map.model.Place;

import java.util.List;


public interface IPlacesListener {

    void onPlacesFetched(List<Place> place);

    void onPlacesFetchError(String errorMsg);

}
