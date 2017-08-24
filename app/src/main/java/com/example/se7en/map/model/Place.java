package com.example.se7en.map.model;



import android.location.Address;
import android.os.Parcelable;

import com.amap.api.services.core.PoiItem;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.ArrayList;
import java.util.List;

public class Place {

    public String name;

    public String address;

    public double latitude;

    private String country;

    public double longitude;


    public Place(){
    }

    public Place(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Place  build(PoiItem poiItem){
        if (poiItem == null) return this;
        this.name = poiItem.toString();
        this.address = poiItem.getSnippet();
        this.latitude = poiItem.getLatLonPoint().getLatitude();
        this.longitude = poiItem.getLatLonPoint().getLongitude();
        return this;
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

    public Place build(PlaceLikelihood placeLikelihood) {
        try {
            this.latitude = placeLikelihood.getPlace().getLatLng().latitude;
            this.longitude = placeLikelihood.getPlace().getLatLng().longitude;
            this.name = placeLikelihood.toString();
            this.address = placeLikelihood.getPlace().getAddress().toString();
            this.country = placeLikelihood.getPlace().getLocale().getCountry();
        }catch (Exception e){

        }
        return this;
    }

}
