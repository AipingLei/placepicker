package com.example.se7en.map.amap;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.se7en.map.IPlaceListener;
import com.example.se7en.map.IPlaceProvider;
import com.example.se7en.map.IPlacesListener;
import com.example.se7en.map.PlacePickActivity;
import com.example.se7en.map.model.Place;

public class AMapPlaceProvider implements IPlaceProvider, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public AMapLocationClient mLocationClient = null;

    private PlacePickActivity mAcitivity;

    private IPlaceListener mCurrentPlaceListener;

    private GeocodeSearch geocoderSearch;

    private int currentPage;


    public AMapPlaceProvider(PlacePickActivity activity){
        mAcitivity = activity;
        mLocationClient = new AMapLocationClient(activity);
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setMockEnable(false);
        mLocationOption.setHttpTimeOut(20000);
        mLocationClient.setLocationOption(mLocationOption);
    }


    @Override
    public void fetchCurrentPlace(IPlaceListener listener) {
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.startLocation();
        mCurrentPlaceListener = listener;
    }

    @Override
    public void searchPlaces(double latitude, double longitude, IPlacesListener listener) {
        if (geocoderSearch == null){
            geocoderSearch = new GeocodeSearch(mAcitivity);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
//        LatLonPoint point = new LatLonPoint(latitude,longitude);
//        PoiSearch.Query query = new PoiSearch.Query(point, 5000,
//                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//
//        currentPage = 0;
//        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);// 设置查第一页
//
//        poiSearch = new PoiSearch(this, query);
//        poiSearch.setOnPoiSearchListener(this);
//        poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
//        // 设置搜索区域为以lp点为圆心，其周围5000米范围
//        poiSearch.searchPOIAsyn();// 异步搜索
//        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求

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
                if (mCurrentPlaceListener != null){
                    Place place = new Place();
                    place.longitude = aMapLocation.getLongitude();
                    place.latitude = aMapLocation.getLatitude();
                    place.address = aMapLocation.getAddress();
                    place.name = aMapLocation.getPoiName();
                    mCurrentPlaceListener.onPlaceFetched(place);
                }
                //可在其中解析amapLocation获取相应内容。
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
//        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                Place place = new Place();
//                addressName = result.getRegeocodeAddress().getFormatAddress()
//                        + "附近";
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(latLonPoint), 15));
//                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
//                ToastUtil.show(ReGeocoderActivity.this, addressName);
//            } else {
//                ToastUtil.show(ReGeocoderActivity.this, R.string.no_result);
//            }
//        } else {
//            ToastUtil.showerror(this, rCode);
//        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
