package com.example.se7en.map.model;


import android.location.Address;
import android.os.Parcelable;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.PoiItem;
import com.example.se7en.map.google.model.AddressResult;
import com.example.se7en.map.google.model.PlaceDetailResult;
import com.example.se7en.map.google.model.PlacesSearchResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaceAdapter {

    /** 
     * description: build a Place by AMap PoiItem
     * params:
     * @return : 
     * create by: leiap
     * update date: 2017/8/28
     */
    public static Place build(PoiItem poiItem){
        if (poiItem == null);
        Place place = new Place();
        place.name = poiItem.toString();
        place.placeID = poiItem.getPoiId();
        place.address = poiItem.getSnippet();
        place.latitude = poiItem.getLatLonPoint().getLatitude();
        place.longitude = poiItem.getLatLonPoint().getLongitude();
        place.city = poiItem.getCityName();
        place.province = poiItem.getProvinceName();
        return place;
    }


//    public static Place build(PlaceLikelihood placeLikelihood) {
//        Place place = new Place();
//        try {
//            place.latitude = placeLikelihood.getPlace().getLatLng().latitude;
//            place.longitude = placeLikelihood.getPlace().getLatLng().longitude;
//            place.name = placeLikelihood.getPlace().getName().toString();
//            place.address = placeLikelihood.getPlace().getAddress().toString();
//            place.country = placeLikelihood.getPlace().getLocale().getCountry();
//        }catch (Exception e){
//
//        }
//        return place;
//    }

    public static Place build(AMapLocation aMapLocation) {
        Place place = new Place();
        place.name = aMapLocation.getPoiName();
        place.address = aMapLocation.getAddress();
        place.latitude = aMapLocation.getLatitude();
        place.longitude = aMapLocation.getLongitude();
        place.country = aMapLocation.getCountry();
        place.city = aMapLocation.getCity();
        return  place;
    }

    public static Place build(PlaceDetailResult result) {
        Place place = new Place();
        AddressResult[] addresses = result.address_components;
        for (AddressResult address : addresses){
            if (address.getType() == AddressResult.TYPE_COUNTRY){
                place.country = address.long_name;
                place.countryCode = address.short_name;
            }else if ((address.getType() == AddressResult.TYPE_CITY)){
                place.city = address.long_name;
            }else if (address.getType() == AddressResult.TYPE_PROVINCE){
                place.province = address.long_name;
            }
        }
        if (TextUtils.isEmpty(place.city)){
            place.city = place.province;
        }
        place.latitude = result.geometry.location.lat;
        place.longitude = result.geometry.location.lng;
        place.name = result.name;
        place.placeID = result.place_id;
        place.address = TextUtils.isEmpty(result.formatted_address) ? result.vicinity :result.formatted_address;
        return  place;
    }

    public static Place build(AutocompletePrediction prediction) {
        Place place = new Place();
        place.name = prediction.getPrimaryText(null).toString();
        place.address = prediction.getFullText(null).toString();
        return place;
    }

    public static List<Place> buildList(PlacesSearchResult[] results) {
        if (results == null || results.length == 0){
            return  null;
        }
        List<Place> places = new ArrayList<>();
        for (PlacesSearchResult result : results){
            Place place = new Place();
            place.name = result.name;
            place.address = result.vicinity;
            place.latitude = result.geometry.location.lat;
            place.longitude = result.geometry.location.lng;
            place.placeID = result.place_id;
            places.add(place);
        }
        return  places;
    }

    public static List<Place> buildList2(List<Parcelable> addresses){
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
