package com.example.se7en.map.model;



import android.location.Address;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Place {

    public String name;

    public String address;

    public double latitude;

    public double longitude;

    public Place(){
    }

    public Place(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static List<Place> getPlaceList(List<Parcelable> addresses){
        if (addresses == null || addresses.size() == 0){
            return  null;
        }

        Parcelable parcelable = addresses.get(0);

        if (!(parcelable instanceof Address)){
            return null;
        }
        List<Place> places = new ArrayList<>();
        for (Parcelable pAddress: addresses){
            Address address = (Address)pAddress;
            Place place = new Place();
            place.name = address.getFeatureName();
            int index = address.getMaxAddressLineIndex();
            if (place.name != null && index >= 0){
                place.address = address.getAddressLine(address.getMaxAddressLineIndex());
                place.latitude = address.getLatitude();
                place.longitude = address.getLongitude();
                places.add(place);
            }
        }
        return  places;
    }

}
