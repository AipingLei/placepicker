package com.example.se7en.map;


import com.example.se7en.map.model.Place;

import java.util.List;


public interface IPlacesListener {

    void onPlaceFetched(List<Place> place);

    void onError(String errorMsg);

}
