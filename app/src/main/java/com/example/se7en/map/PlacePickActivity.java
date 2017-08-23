package com.example.se7en.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.se7en.map.model.Place;
import com.example.se7en.map.view.MapContainer;
import com.example.se7en.map.view.RecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlacePickActivity extends AppCompatActivity{

    @Bind(R.id.search_title)
    TextView searchTitle;
    @Bind(R.id.pick_done)
    TextView pickDone;
    @Bind(R.id.searchView)
    SearchView searchView;
    @Bind(R.id.search_rl)
    RelativeLayout searchRl;
    @Bind(R.id.my_toolbar)
    Toolbar myToolbar;
//    @Bind(R.id.map_view)
//    MapView mapView;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.search_appbar)
    AppBarLayout searchAppbar;
    @Bind(R.id.search_coordinator)
    CoordinatorLayout searchCoordinator;
    @Bind(R.id.search_collapsing)
    CollapsingToolbarLayout searchCollapsing;

    public static  final  int GOOGLE_MAP = 0;

    public static  final  int GD_MAP =1 ;

    public static  final  String START_TYPE = "start_type" ;
    //private GoogleMap mGoogleMap;
    private ArrayList<Place> mDatas;

    private MapContainer mMapContainer;
    RecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

//        mapView = (MapView) findViewById(R.id.map_view);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTitle.setVisibility(View.GONE);
                pickDone.setVisibility(View.GONE);

                searchCollapsing.setMinimumHeight(0);
                searchCollapsing.setVisibility(View.GONE);
                Log.e("test", "test");
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchTitle.setVisibility(View.VISIBLE);
                pickDone.setVisibility(View.VISIBLE);

                searchCollapsing.setMinimumHeight(300);
                searchCollapsing.setVisibility(View.VISIBLE);
                return false;
            }
        });

        initPlaces();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter(mRecyclerView.getContext(), mDatas));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapContainer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapContainer.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapContainer.onResume();
    }

    protected void initPlaces() {

    }

    protected Location mLastLocation;

    private AddressResultReceiver mResultReceiver;


//    protected void startIntentService() {
//        Intent intent = new Intent(this, FetchAddressIntentService.class);
//        intent.putExtra(Constants.RECEIVER, mResultReceiver);
//        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
//        startService(intent);
//    }

//    private void fetchAddressButtonHander(View view) {
//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location<() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        mLastKnownLocation = location;
//
//                        // In some rare cases the location returned can be null
//                        if (mLastKnownLocation == null) {
//                            return;
//                        }
//
//                        if (!Geocoder.isPresent()) {
//                            Toast.makeText(PlacePickActivity.this,
//                                    R.string.no_geocoder_available,
//                                    Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        // Start service and update UI to reflect new location
//                        startIntentService();
//                        updateUI();
//                    }
//                });
//    }
//
//    class AddressResultReceiver extends ResultReceiver {
//        public AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//
//            // Display the address string
//            // or an error message sent from the intent service.
//            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
//            displayAddressOutput();
//
//            // Show a toast message if an address was found.
//            if (resultCode == Constants.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found));
//            }
//
//        }
//    }

    public static void start(Context context, int type){
        Intent intent = new Intent();
        intent.putExtra(START_TYPE,type);
        intent.setClass(context,PlacePickActivity.class);
        context.startActivity(intent);
    }
}
