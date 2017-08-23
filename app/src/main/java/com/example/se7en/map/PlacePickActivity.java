package com.example.se7en.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.se7en.map.amap.AMapContainer;
import com.example.se7en.map.google.GoogleMapContainer;
import com.example.se7en.map.google.GooglePlaceProvider;
import com.example.se7en.map.model.Place;
import com.example.se7en.map.view.MapContainer;
import com.example.se7en.map.view.RecyclerAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlacePickActivity extends AppCompatActivity implements IPlaceListener, IPlacesListener, IMapReadyCallback {

    private static final String TAG = "PlacePickActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
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
    @Bind(R.id.map_layout)
    FrameLayout mapLayout;

    public static final int GOOGLE_MAP = 0;

    public static final int GD_MAP = 1;

    public static final String START_TYPE = "start_type";
    //private GoogleMap mGoogleMap;
    private List<Place> mDatas;

    private MapContainer mMapContainer;

    private IPlaceProvider mPlaceProvider;

    RecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        initView();
        initPlaceProvider(savedInstanceState);
    }

    private void initView() {
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTitle.setVisibility(View.GONE);
                pickDone.setVisibility(View.GONE);
                searchCollapsing.setMinimumHeight(0);
                searchCollapsing.setVisibility(View.GONE);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter(mRecyclerView.getContext()));

    }

    private void initPlaceProvider(Bundle savedInstanceState) {
        Intent intent = getIntent();
        int type = intent.getIntExtra(START_TYPE, GOOGLE_MAP);
        if (type == GOOGLE_MAP) {
            mPlaceProvider = new GooglePlaceProvider(this);
            mMapContainer = new GoogleMapContainer(this);
            mMapContainer.onCreate(savedInstanceState);
        } else if (type == GD_MAP) {
            //TODO GD map
            mPlaceProvider = new GooglePlaceProvider(this);
            mMapContainer = new AMapContainer(this);
            mMapContainer.onCreate(savedInstanceState);
        } else {
            Log.e(TAG, "none map service founded, you should define a map provider (GD_MAP or GOOGLE_MAP)");
        }

        if (mPlaceProvider == null) {
            return;
        }

        //mPlaceProvider.fetchCurrentPlace(this);
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
        mPlaceProvider.destroyed();
        mMapContainer = null;
        mPlaceProvider = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapContainer.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            mPlaceProvider.fetchCurrentPlace(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapContainer.onResume();
    }

    public static void start(Context context, int type) {
        Intent intent = new Intent();
        intent.putExtra(START_TYPE, type);
        intent.setClass(context, PlacePickActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onPlaceFetched(Place place) {
        if (place != null) {
            mPlaceProvider.searchPlaces(place.latitude, place.longitude, this);
        }
    }

    @Override
    public void onPlaceFetchError(Exception errorMessage) {
        Log.e(TAG, "onPlaceFetchError" + errorMessage);
    }

    @Override
    public void onPlacesFetched(List<Place> place) {
        if (mRecyclerView == null) return;
        mDatas = place;
        mMapContainer.moveToLocation(mDatas.get(0).latitude, mDatas.get(0).longitude);
        mAdapter.setDatas(mDatas);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlacesFetchError(String errorMsg) {

    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(PlacePickActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(PlacePickActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                mPlaceProvider.fetchCurrentPlace(this);
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @Override
    public void onMapReady(MapContainer container) {
        View view = container.getMapView();
        mapLayout.addView(view,view.getLayoutParams());
    }
}
