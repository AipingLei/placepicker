package com.example.se7en.map;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitRequest<S> {

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;

    public S mService;

    public String mHost;

    public Class<S> mServiceClass;

//    public void setHost(String aHost){
//        mHost = aHost;
//    }
//
//    public void setServerClass(Class<S> aServiceClass){
//        mServiceClass = aServiceClass;
//    }

    public void init(){
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(mHost)
                .build();
        mService = retrofit.create(mServiceClass);
    }
}
