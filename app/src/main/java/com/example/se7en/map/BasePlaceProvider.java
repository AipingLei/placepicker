package com.example.se7en.map;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

/**
 * description: describe the class
 * create by: leiap
 * create date: 2017/8/23
 * update date: 2017/8/23
 * version: 1.0
*/

public abstract  class BasePlaceProvider implements IPlaceProvider{

    private static final String TAG = "BasePlaceProvider";

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    public void BasePlaceProvider(){
    }

    @Override
    public void initialize() {

    }

    @Override
    public void destroyed() {
    }

}
