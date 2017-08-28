
package com.example.se7en.map.google.service;


import com.example.se7en.map.RetrofitRequest;
import com.example.se7en.map.google.internel.StringJoin;
import com.example.se7en.map.google.model.PlaceDetailResult;
import com.example.se7en.map.google.model.PlacesSearchResult;
import com.example.se7en.map.observer.IPlacesListener;
import com.example.se7en.map.google.internel.ApiResponse;
import com.example.se7en.map.google.model.PlacesSearchResponse;
import com.example.se7en.map.model.PlaceAdapter;


import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class GooglePlacesSearch extends RetrofitRequest<GooglePlaceService> {

  private  final static String GOOGLE_HOST = "https://maps.googleapis.com";

  private  final static int NEARBY = 1;

  private  final static int TEXT_SEARCH = 2;


  /**
   * Specifies the latitude/longitude around which to retrieve place information.
   */
  private String mLocation;

  /**
   * Specifies a term to be matched against all content that Google has indexed for this place. This
   * includes but is not limited to name, type, and address, as well as customer reviews and other
   * third-party content.
   */
  private String mKeyword;

  public String mApiKey;

  /**
   * Specifies the distance (in meters) within which to return place results. The maximum allowed
   * radius is 50,000 meters. Note that radius must not be included if {@code rankby=DISTANCE} is
   * specified.
   */
  private int mRadius = 1000;

  /**
   * Returns the next 20 results from a previously run search. Setting {@code pageToken} will
   * execute a search with the same parameters used previously — all parameters other than {@code
   * pageToken} will be ignored.
   * The page token from a previous result.
   */
  private String mPageToken;

  private IPlacesListener mPlaceListener;

  private String language;

  public GooglePlacesSearch(String apiKey,String language){
    mHost = GOOGLE_HOST;
    mServiceClass = GooglePlaceService.class;
    mApiKey = apiKey;
    this.language = language;
    init();
  }

  public GooglePlacesSearch location(double latitude, double longitude) {
    mLocation = StringJoin.join(',',String.valueOf(latitude),String.valueOf(longitude));
    return  this;
  }

  public GooglePlacesSearch radius(int distance) {
    if (distance > 50000) {
      throw new IllegalArgumentException("The maximum allowed radius is 50,000 meters.");
    }
    this.mRadius = distance;
    return this;
  }

  public GooglePlacesSearch keyword(String keyword) {
    mKeyword = keyword;
    return  this;
  }


  public GooglePlacesSearch pageToken(String nextPageToken) {
    mPageToken = nextPageToken;
    return  this;
  }

  public GooglePlacesSearch observer(IPlacesListener aPlaceListener) {
    mPlaceListener = aPlaceListener;
    return  this;
  }

  public void placeDetail(String placeID){
    mService.details(placeID,language,mApiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<GooglePlacesSearch.DetailResponse>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                mPlaceListener.onPlacesFetchError(e.getMessage());
              }

              @Override
              public void onNext(GooglePlacesSearch.DetailResponse response) {
                if (response.successful()){
                  mPlaceListener.onPlacesDetailFetched(PlaceAdapter.build(response.getResult()));
                }else {
                  mPlaceListener.onPlacesFetchError(response.status+response.errorMessage);
                }
              }
            });
  }

  public void nearbySearch(){

    if (!validate(NEARBY)) return;
    Observable<GooglePlacesSearch.Response> observable;
    if (mRadius > 0){
      observable = mService.nearbysearch(mLocation,mRadius,mKeyword,language,mApiKey);
    }else {
      observable = mService.nearbysearch(mLocation,"distance",mKeyword,language,mApiKey);
    }

    observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<GooglePlacesSearch.Response>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                mPlaceListener.onPlacesFetchError(e.getMessage());
              }

              @Override
              public void onNext(Response response) {
                if (response.successful() && response.getResult().results != null && response.getResult().results.length > 10 ){
                  mPlaceListener.onPlacesFetched(PlaceAdapter.buildList(response.getResult().results));
                }else {
                  //try textSearch again
                  textSearch();
                }
              }
            });
  }

  public void textSearch(){

    if (!validate(TEXT_SEARCH)) return;

    mService.textsearch(mLocation,mRadius,mKeyword,language,mApiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<GooglePlacesSearch.Response>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                mPlaceListener.onPlacesFetchError(e.getMessage());
              }

              @Override
              public void onNext(GooglePlacesSearch.Response response) {
                if (response.successful()){
                  mPlaceListener.onPlacesFetched(PlaceAdapter.buildList(response.getResult().results));
                }else {
                  mPlaceListener.onPlacesFetchError(response.status+response.errorMessage);
                }
              }
            });
  }

  private boolean validate(int type) {
    if (mApiKey == null || mPlaceListener == null ) return false;
    if (type == NEARBY){
      return mLocation != null;
    }else if (type == TEXT_SEARCH){
      return mKeyword != null;
    }
    return false;
  }



  public static class Response implements ApiResponse<PlacesSearchResponse> {

    public String status;
    public String htmlAttributions[];
    public PlacesSearchResult results[];
    public String nextPageToken;
    public String errorMessage;

    @Override
    public boolean successful() {
      return "OK".equals(status) || "ZERO_RESULTS".equals(status);
    }

    @Override
    public PlacesSearchResponse getResult() {
      PlacesSearchResponse result = new PlacesSearchResponse();
      result.htmlAttributions = htmlAttributions;
      result.results = results;
      result.nextPageToken = nextPageToken;
      return result;
    }

    @Override
    public Exception getError() {
      if (successful()) {
        return null;
      }
      return new Exception(status+errorMessage);
    }
  }

  public static  class DetailResponse implements ApiResponse<PlaceDetailResult>{

    public String status;

    public String errorMessage;

    public String formatted_address;

    public PlaceDetailResult result;

    @Override
    public boolean successful() {
      return "OK".equals(status) || "ZERO_RESULTS".equals(status);
    }

    @Override
    public PlaceDetailResult getResult() {
      return result;
    }

    @Override
    public Exception getError() {
      if (successful()) {
        return null;
      }
      return new Exception(status+errorMessage);
    }
  }


}
