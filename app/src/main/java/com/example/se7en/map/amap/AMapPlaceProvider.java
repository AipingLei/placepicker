package com.example.se7en.map.amap;


import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
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
import java.util.Locale;

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
        if (geocoderSearch == null){
            geocoderSearch = new GeocodeSearch(mActivity);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
        LatLonPoint latLonPoint = new LatLonPoint(latitude,longitude);

        // the first param is Latlng，second is radius to search, the last is Latlng type;
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 50000,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

    }

    @Override
    public void textSearch(String keyWords) {
        if (mPlacesListener == null) throw  new  NullPointerException("should set a PlaceListener first");
        String city = mCurrentPlace == null ? "" : mCurrentPlace.city;
        query = new PoiSearch.Query(keyWords, "", city);
        query.setPageSize(20);//
        query.setPageNum(currentPage);//
        PoiSearch poiSearch = new PoiSearch(mActivity, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void placeDetail(Place place) {
        //There is no need to get AMap Detail info/
        mPlacesListener.onPlacesDetailFetched(place);
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
                String  sProvince = result.getRegeocodeAddress().getProvince();
                List<PoiItem> poiItems = result.getRegeocodeAddress().getPois();
                if (poiItems != null && poiItems.size() > 0){
                    List<Place> places = new ArrayList<>();
                    for (PoiItem poi :poiItems){
                        Place place = PlaceAdapter.build(poi);
                        place.city = sCity;
                        place.province =sProvince;
                        place.country = Locale.getDefault().getDisplayCountry();
                        place.countryCode = Locale.getDefault().getCountry();
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
