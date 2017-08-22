package com.example.se7en.map;

public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
        "com.example.se7en.googlemap";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
        ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
        ".LOCATION_DATA_EXTRA";

//    No location data provided – The intent extras do not include the Location object that is required for reverse geocoding.
//    Invalid latitude or longitude used – The latitude and/or longitude values that are provided in the Location object are invalid.
//    No geocoder available – The background geocoding service is not available due to a network error or IO exception.Sorry,
//    no address found – The geocoder can't find an address for the given latitude/longitude.
}