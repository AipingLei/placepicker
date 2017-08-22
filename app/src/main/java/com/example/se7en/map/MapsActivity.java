package com.example.se7en.map;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.se7en.map.view.RecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mGoogleMap;
    private ArrayList<String> mDatas;
    private RecyclerView mRecyclerView;
    RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        initData();
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter(mRecyclerView.getContext(), mDatas));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    protected void initData() {
        mDatas = new ArrayList<>();
        for (int i = 'A'; i < 'Z'; i++) {
            mDatas.add("Google" + (char) i);
        }
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
//                            Toast.makeText(MapsActivity.this,
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
}
