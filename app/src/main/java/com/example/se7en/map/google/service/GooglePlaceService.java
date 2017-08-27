package com.example.se7en.map.google.service;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GooglePlaceService {

    //public String hostName = "https://maps.googleapis.com";
    //"/maps/api/place/nearbysearch/json"
   //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=30.637740,103.995742&radius=50000&keyword=%E5%8F%8C%E6%B5%81%E6%9C%BA%E5%9C%BA&key=AIzaSyBbiX3jDwDKLILP4diosiDcxMwi9CPZHRA
    //https://maps.googleapis.com/maps/api/place/textsearch/json?location=30.637740,103.995742&radius=50000&query=%E5%8F%8C%E6%B5%81%E6%9C%BA%E5%9C%BA&key=AIzaSyBbiX3jDwDKLILP4diosiDcxMwi9CPZHRA
    @GET("/maps/api/place/nearbysearch/json")
    Observable<GooglePlacesSearch.Response> nearbysearch(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("keyword") String keyword,
            @Query("language") String language,
            @Query("key") String key);

    @GET("/maps/api/place/nearbysearch/json")
    Observable<GooglePlacesSearch.Response> nearbysearch(
            @Query("location") String location,
            @Query("rankby") String rank,
            @Query("keyword") String keyword,
            @Query("language") String language,
            @Query("key") String key);

    @GET("/maps/api/place/textsearch/json")
    Observable<GooglePlacesSearch.Response> textsearch(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("query") String keyword,
            @Query("language") String language,
            @Query("key") String key);

}
