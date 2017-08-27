package com.example.se7en.map.amap;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.example.se7en.map.observer.ICameraChangeListener;
import com.example.se7en.map.PlacePickActivity;
import com.example.se7en.map.R;
import com.example.se7en.map.view.MapContainer;



public class AMapContainer implements MapContainer<FrameLayout> {

    private PlacePickActivity mActivity;

    private MapView mMapView;

    ImageView mCenterMarker;

    ImageView mCurrentControl;

    //Marker screenMarker = null;

    private FrameLayout mapLayout;

    private AMap aMap;

    private ICameraChangeListener mCameraChangeListener;

    private double[] mCurrentPosition;

    private boolean currentLoad = false;


    public AMapContainer(PlacePickActivity aActivity){
        mActivity = aActivity;

    }

    @Override
    public FrameLayout getMapView() {
        return mapLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState,ViewGroup parent) {
        //mMapView = new MapView(mActivity);
        mapLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.amap_layout, parent, false);
        mCurrentControl = (ImageView)mapLayout.findViewById(R.id.current_position);
        mCenterMarker = (ImageView)mapLayout.findViewById(R.id.center_marker);
        mCurrentControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPosition != null){
                    moveToLocation(mCurrentPosition[0],mCurrentPosition[1]);
                }
            }
        });
        mMapView = (MapView)mapLayout.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        mActivity.onMapReady(AMapContainer.this);

        UiSettings mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);

//        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
//            @Override
//            public void onMapLoaded() {
//                addMarkerInScreenCenter();
//            }
//        });

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
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),17f)) ;
        //startJumpAnimation();
    }

    @Override
    public void setCurrentLocation(double latitude, double longitude) {
        if (mCurrentPosition == null){
            mCurrentPosition = new double[2];
            mCurrentPosition[0] = latitude;
            mCurrentPosition[1] = longitude;
            aMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)))
                    .setPosition(new LatLng(latitude,longitude))
            ;
            float y = mCenterMarker.getY();
            int height = mCenterMarker.getHeight();
            mCenterMarker.setY(y-height/2);
            // 设置可视范围变化时的回调的接口方法
            aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition position) {

                }

                @Override
                public void onCameraChangeFinish(CameraPosition position) {
                    if (currentLoad){
                        mCameraChangeListener.onCameraChangeFinish(position.target.latitude,position.target.longitude);
                    }else {
                        currentLoad = true;
                    }
                }
            });
            moveToLocation(latitude,longitude);
        }
    }

    @Override
    public void setFocusChangeListener(ICameraChangeListener listener) {
        mCameraChangeListener = listener;
    }

//    /**
//     * 在屏幕中心添加一个Marker
//     */
//    private void addMarkerInScreenCenter() {
//        LatLng latLng = aMap.getCameraPosition().target;
//        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
//        screenMarker = aMap.addMarker(new MarkerOptions()
//                .anchor(0.5f,0.5f)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.aky)));
//        //设置Marker在屏幕上,不跟随地图移动
//        screenMarker.setPositionByPixels(screenPosition.x,screenPosition.y);
//
//    }

//    /**
//     * 屏幕中心marker 跳动
//     */
//    public void startJumpAnimation() {
//        if (screenMarker != null ) {
//            //根据屏幕距离计算需要移动的目标点
//            final LatLng latLng = screenMarker.getPosition();
//            Point point =  aMap.getProjection().toScreenLocation(latLng);
//            point.y -= dip2px(mActivity,125);
//            LatLng target = aMap.getProjection()
//                    .fromScreenLocation(point);
//            //使用TranslateAnimation,填写一个需要移动的目标点
//            Animation animation = new TranslateAnimation(latLng);
//            animation.setInterpolator(new Interpolator() {
//                @Override
//                public float getInterpolation(float input) {
//                    // 模拟重加速度的interpolator
//                    if(input <= 0.5) {
//                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
//                    } else {
//                        return (float) (0.5f - Math.sqrt((input - 0.5f)*(1.5f - input)));
//                    }
//                }
//            });
//            //整个移动所需要的时间
//            animation.setDuration(600);
//            //设置动画
//            screenMarker.setAnimation(animation);
//            //开始动画
//            screenMarker.startAnimation();
//
//        } else {
//            Log.e("amap","screenMarker is null");
//        }
//    }

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
