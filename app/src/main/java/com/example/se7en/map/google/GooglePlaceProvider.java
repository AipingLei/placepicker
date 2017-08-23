package com.example.se7en.map.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;

import com.example.se7en.map.Constants;
import com.example.se7en.map.FetchAddressIntentService;
import com.example.se7en.map.IPlaceListener;
import com.example.se7en.map.IPlaceProvider;
import com.example.se7en.map.IPlacesListener;
import com.example.se7en.map.model.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/
public class GooglePlaceProvider implements IPlaceProvider{
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * The formatted location address.
     */
    private String mAddressOutput;

    private boolean mAddressRequested;

    public static final  int LATITUDE = 0;

    public static final  int LONGITUDE = 1;

    private IPlacesListener mPlaceListener;

    public GooglePlaceProvider(Context context){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void fetchCurrentPlace(Activity activity, final IPlaceListener listener) {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(activity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (task.isSuccessful() && location != null) {
                            Place place = new Place();
                            place.latitude = location.getLatitude();
                            place.longitude = location.getLongitude();
                        } else {
                            listener.onError(task.getException().toString());
                        }
                    }
                });
    }

    @Override
    public void searchPlaces(Context context, double latitude, double longitude, IPlacesListener listener) {
        mPlaceListener = listener;
        double[] location = new double[2];
        location[LATITUDE] = latitude;
        location[LONGITUDE] = longitude;
        startIntentService(context,location);

    }

    @Override
    public void destroyed() {

    }

    @Override
    public void initialize() {

    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(Context context,double[] location) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(context, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        context.startService(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == Constants.FAILURE_RESULT){

            }else {
                if (mPlaceListener != null){
                    List<Place> places = Place.getPlaceList
                            (resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY));
                    if (places != null){
                        mPlaceListener.onPlaceFetched(places);
                    }else {
                        mPlaceListener.onError("");
                    }
                }
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }
}
