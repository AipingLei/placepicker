package com.example.se7en.map.amap;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.se7en.map.PlacePickActivity;
import com.example.se7en.map.R;
import com.example.se7en.map.view.MapContainer;



public class AMapContainer implements MapContainer<MapView> {

    private PlacePickActivity mActivity;

    private MapView mMapView;
    private AMap aMap;

    public AMapContainer(PlacePickActivity aActivity){
        mActivity = aActivity;

    }

    @Override
    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMapView = new MapView(mActivity);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(params);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        mActivity.onMapReady(this);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
    }

    @Override
    public void moveToLocation(double latitude, double longitude) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude,longitude));
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(mActivity.getResources(), R.drawable.aky)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        //markerOption.setFlat(true);//设置marker平贴地图效果
    }
}
