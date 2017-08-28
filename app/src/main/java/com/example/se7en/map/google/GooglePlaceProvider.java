package com.example.se7en.map.google;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.se7en.map.IPlaceProvider;
import com.example.se7en.map.R;
import com.example.se7en.map.google.service.GooglePlacesSearch;
import com.example.se7en.map.observer.IPlacesListener;
import com.example.se7en.map.model.Place;

import java.util.Locale;


/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/
public class GooglePlaceProvider implements IPlaceProvider,LocationListener {

    private Activity mActivity;

    public static final  int LATITUDE = 0;

    public static final  int LONGITUDE = 1;

    private IPlacesListener mPlaceListener;

    private GooglePlacesSearch mSearchEngine;

    private  Place mCurrentPlace;

    private LocationManager locationManager;

    public GooglePlaceProvider(Activity activity){
        mActivity = activity;
        mSearchEngine = new GooglePlacesSearch(activity.getResources().getString(R.string.google_maps_key),
                Locale.getDefault().getLanguage());
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public IPlaceProvider setListener(IPlacesListener listener) {
        mPlaceListener = listener;
        return this;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void currentNearby() {
        if (mPlaceListener == null) throw  new  NullPointerException("you should set a PlaceListener");
        if (mCurrentPlace == null){
            locationManager.requestLocationUpdates("fused", 0, 0, this);
        }

    }

    @Override
    public void nearbySearch(double latitude, double longitude) {
//        if (mLastLatLng != null){
//            double distance = SphericalUtil.computeDistanceBetween(mLastLatLng,new LatLng(latitude,longitude));
//            if (distance < 5) return;
//        }
        nearbySearch(latitude,longitude,0);
    }

    public void nearbySearch(double latitude, double longitude, int radius) {
        if (mPlaceListener == null) throw  new  NullPointerException("you should set a PlaceListener");
        mSearchEngine.observer(mPlaceListener)
                .location(latitude,longitude)
                .radius(radius)
                .keyword(null)
                .nearbySearch();
    }

    @Override
    public void textSearch(String keyWords) {
        if (mPlaceListener == null) throw  new  NullPointerException("you should set a PlaceListener");
        if (mCurrentPlace != null){
            mSearchEngine.observer(mPlaceListener)
                    .location(mCurrentPlace.latitude,mCurrentPlace.longitude)
                    .radius(50000)
                    .keyword(keyWords)
                    .nearbySearch();
        }else {
            mSearchEngine.observer(mPlaceListener)
                    .radius(50000)
                    .keyword(keyWords)
                    .textSearch();
        }
    }

    @Override
    public void placeDetail(Place place) {
        mSearchEngine.placeDetail(place.placeID);
    }

    @Override
    public void destroyed() {
        mActivity = null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("test","test");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("test","test");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("test","test");
    }

    @Override
    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();
        if (mCurrentPlace == null && accuracy < 200) {
            mCurrentPlace = new Place();
            mCurrentPlace.latitude = location.getLatitude();
            mCurrentPlace.longitude = location.getLongitude();
            //mPlaceListener.onCurrentPlaceFetched(mCurrentPlace);
            nearbySearch(mCurrentPlace.latitude,mCurrentPlace.longitude);
            locationManager.removeUpdates(this);
        }
    }

}
