package com.example.se7en.map.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;

import com.example.se7en.map.Constants;
import com.example.se7en.map.FetchAddressIntentService;
import com.example.se7en.map.IPlaceProvider;
import com.example.se7en.map.IPlacesListener;
import com.example.se7en.map.model.Place;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/
public class GooglePlaceProvider implements IPlaceProvider,OnCompleteListener<PlaceLikelihoodBufferResponse> {

    private Activity mActivity;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
   // private FusedLocationProviderClient mFusedLocationClient;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;

    private PlaceDetectionClient mPlaceDetectionClient;

    public static final  int LATITUDE = 0;

    public static final  int LONGITUDE = 1;

    private IPlacesListener mPlaceListener;

    private AutocompleteFilter mPlaceFilter;

    private LatLng mLastLatLng ;

    public GooglePlaceProvider(Activity activity){
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mActivity = activity;
        mResultReceiver = new AddressResultReceiver(new Handler());
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(activity, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(activity, null);

        mPlaceFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void fetchCurrentPlace(final IPlacesListener listener) {
        mPlaceListener = listener;
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(this);
        //mPlaceDetectionClient.getCurrentPlace();
       //one: FusedLocationClient method
//        mFusedLocationClient.getLastLocation()
//                .addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        Location location = task.getResult();
//                        if (task.isSuccessful() && location != null) {
//                            Place place = new Place();
//                            place.latitude = location.getLatitude();
//                            place.longitude = location.getLongitude();
//                            listener.onPlaceFetched(place);
//                        } else {
//                            listener.onPlaceFetchError(task.getException());
//                        }
//                    }
//                });
    }

    @Override
    public void searchPlaces(double latitude, double longitude, IPlacesListener listener) {
        mPlaceListener = listener;
        double[] location = new double[2];
        //33.960089, -118.130957
        location[LATITUDE] = latitude;
        location[LONGITUDE] = longitude;
//        location[LATITUDE] = 33.960089;
//        location[LONGITUDE] = -118.130957;
        mLastLatLng = new LatLng(latitude,longitude);
        startIntentService(mActivity,location);

    }

    @Override
    public void searchPlaces(String keyWords, IPlacesListener listener) {
        Task<AutocompletePredictionBufferResponse> results =
                mGeoDataClient.getAutocompletePredictions(keyWords, toBounds(mLastLatLng,5000),
                        mPlaceFilter);

        results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                AutocompletePredictionBufferResponse response = task.getResult();
                List<Place> places = new ArrayList<>();
                List<AutocompletePrediction> predictions =  DataBufferUtils.freezeAndClose(response);
                for (AutocompletePrediction prediction : predictions) {
                    Place place = new Place().build(prediction);
                    places.add(place);
                }
                if (places.size() > 0){
                    mLastLatLng = new LatLng(places.get(0).latitude,places.get(0).longitude);
                }
                mPlaceListener.onPlacesFetched(places);
                response.release();
            }
        });
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    @Override
    public void destroyed() {
        mActivity = null;
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

    @Override
    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
        List<Place> places = new ArrayList<>();
        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
            Place place = new Place().build(placeLikelihood);
            places.add(place);
        }
        if (places.size() > 0){
            mLastLatLng = new LatLng(places.get(0).latitude,places.get(0).longitude);
        }

        mPlaceListener.onPlacesFetched(places);
        likelyPlaces.release();
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
                        mPlaceListener.onPlacesFetched(places);
                    }else {
                        mPlaceListener.onPlacesFetchError("");
                    }
                }
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
        }
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
