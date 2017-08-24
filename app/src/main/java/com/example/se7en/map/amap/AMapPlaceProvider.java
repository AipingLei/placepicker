package com.example.se7en.map.amap;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.se7en.map.IPlaceListener;
import com.example.se7en.map.IPlaceProvider;
import com.example.se7en.map.IPlacesListener;
import com.example.se7en.map.PlacePickActivity;
import com.example.se7en.map.model.Place;

import java.util.ArrayList;
import java.util.List;

public class AMapPlaceProvider implements IPlaceProvider, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public AMapLocationClient mLocationClient = null;

    private PlacePickActivity mActivity;

    private GeocodeSearch geocoderSearch;

    private int currentPage;

    private IPlacesListener mPlacesListener;

    public static  final  int PAGE_SIZE = 20;

    private LatLng mCurrentPoint;


    public AMapPlaceProvider(PlacePickActivity activity){
        mActivity = activity;
        mLocationClient = new AMapLocationClient(activity);
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setMockEnable(false);
        mLocationOption.setHttpTimeOut(20000);
        mLocationClient.setLocationOption(mLocationOption);
    }


    @Override
    public void fetchCurrentPlace(IPlacesListener listener) {
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.startLocation();
    }

    @Override
    public void searchPlaces(double latitude, double longitude, IPlacesListener listener) {
        if (geocoderSearch == null){
            geocoderSearch = new GeocodeSearch(mActivity);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
        mPlacesListener = listener;
        LatLonPoint latLonPoint = new LatLonPoint(latitude,longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 1000,
                GeocodeSearch.GPS);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求

    }

    @Override
    public void searchPlaces(String keyWords, IPlacesListener listener) {

    }

    @Override
    public void destroyed() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                    mCurrentPoint = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    searchPlaces(mCurrentPoint.latitude,mCurrentPoint.longitude,mPlacesListener);
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                List<PoiItem> poiItems = result.getRegeocodeAddress().getPois();
                if (poiItems != null){
                    List<Place> places = new ArrayList<>();
                    for (PoiItem poi :poiItems){
                        Place place = new Place().build(poi);
                        places.add(place);
                    }
                    mPlacesListener.onPlacesFetched(places);
                }
            } else {
                mPlacesListener.onPlacesFetchError("no result");
            }
        } else {
            mPlacesListener.onPlacesFetchError("rCode error :"+ rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
