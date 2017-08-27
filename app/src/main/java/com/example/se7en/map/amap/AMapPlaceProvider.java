package com.example.se7en.map.amap;


import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.se7en.map.IPlaceProvider;
import com.example.se7en.map.observer.IPlacesListener;
import com.example.se7en.map.PlacePickActivity;
import com.example.se7en.map.model.Place;
import com.example.se7en.map.model.PlaceAdapter;

import java.util.ArrayList;
import java.util.List;

public class AMapPlaceProvider implements IPlaceProvider, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener {

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public AMapLocationClient mLocationClient = null;

    private PlacePickActivity mActivity;

    private GeocodeSearch geocoderSearch;

    private int currentPage;

    private IPlacesListener mPlacesListener;

    public static  final  int PAGE_SIZE = 20;

    private Place mCurrentPlace;

    private PoiSearch.Query query;

    public AMapPlaceProvider(PlacePickActivity activity){
        mActivity = activity;
        mLocationClient = new AMapLocationClient(activity);
        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setMockEnable(false);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setHttpTimeOut(20000);

        mLocationClient.setLocationOption(mLocationOption);
    }


    @Override
    public IPlaceProvider setListener(IPlacesListener listener) {
        mPlacesListener = listener;
        return this;
    }

    @Override
    public void currentNearby() {
        if (mPlacesListener == null) throw  new  NullPointerException("should set a PlaceListener first");
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.startLocation();
    }

    @Override
    public void nearbySearch(double latitude, double longitude) {
        if (mPlacesListener == null) throw  new  NullPointerException("should set a PlaceListener first");
        if (mCurrentPlace == null) return;
        if (geocoderSearch == null){
            geocoderSearch = new GeocodeSearch(mActivity);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
        LatLonPoint latLonPoint = new LatLonPoint(latitude,longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 1000,
                GeocodeSearch.AMAP);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求

    }

    @Override
    public void textSearch(String keyWords) {
        if (mPlacesListener == null) throw  new  NullPointerException("should set a PlaceListener first");
        if (mCurrentPlace == null) return;
        query = new PoiSearch.Query(keyWords, "", mCurrentPlace.city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        PoiSearch poiSearch = new PoiSearch(mActivity, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
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
                    mCurrentPlace = PlaceAdapter.build(aMapLocation);
                    nearbySearch(mCurrentPlace.latitude,mCurrentPlace.longitude);
                    mLocationClient.unRegisterLocationListener(this);
                    mLocationClient.stopLocation();
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
                String  sCity = result.getRegeocodeAddress().getCity();
                String  sCityCode = result.getRegeocodeAddress().getCityCode();
                List<PoiItem> poiItems = result.getRegeocodeAddress().getPois();
                if (poiItems != null && poiItems.size() > 0){
                    List<Place> places = new ArrayList<>();
                    for (PoiItem poi :poiItems){
                        Place place = PlaceAdapter.build(poi);
                        place.city = sCity;
                        place.cityCode = sCityCode;
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

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = result
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        mPlacesListener.onPlacesFetched(toPlaceList(poiItems));
                    } else if (suggestionCities != null){
//                            && suggestionCities.size() > 0) {
//                        showSuggestCity(suggestionCities);
                    } else {
                        mPlacesListener.onPlacesFetchError("");
                    }
                }
            }else {
                mPlacesListener.onPlacesFetchError("");
            }
        }else {
            mPlacesListener.onPlacesFetchError("");
        }
    }

    private List<Place> toPlaceList(List<PoiItem> poiItems) {
        if (poiItems == null ||poiItems.size() == 0) return null;
        List<Place> places = new ArrayList<>();
        for (PoiItem poi : poiItems){
            Place place = PlaceAdapter.build(poi);
            places.add(place);
        }
        return  places;
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {
    }

}
