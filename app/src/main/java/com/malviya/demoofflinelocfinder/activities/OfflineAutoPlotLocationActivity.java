package com.malviya.demoofflinelocfinder.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.adapters.PlotLocAdapter;
import com.malviya.demoofflinelocfinder.model.MainDataModel;

import java.util.ArrayList;

/**
 * Created by 23508 on 6/15/2017.
 */

public class OfflineAutoPlotLocationActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "ITC";
    private static final int REQUEST_CHECK_SETTINGS = 2;
    public static final String[] runtimePermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int LOCATION_PERMISSION_IDENTIFIER = 1;
    private static final int MAX_INTERVAL = 10 * 1000;//in millisec
    private static final int INIT_INTERVAL = 2 * 1000;//in millisec
    public static final float ACCURACY = 10;//25;//10;
    private RecyclerView mRecyclerLocList;
    private PlotLocAdapter mMainAdapter;
    private ArrayList<MainDataModel> mListOfLatiLongi;
    private static LocationManager locationManager;
    private Button mBtnPlot;
    private static ProgressBar mProgressBar;
    private Button mBtnLocStart;
    private Button mBtnLocStop;
    private SeekBar mSeekbarInterval;
    private TextView mTextViewInterval;
    private boolean mIsStopBtnPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_auto_plot_loc);
        mListOfLatiLongi = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initView();
    }


    private void initRecycler() {
        mRecyclerLocList = (RecyclerView) findViewById(R.id.recycler_main_loc);
        mRecyclerLocList.setLayoutManager(new LinearLayoutManager(this));
        mMainAdapter = new PlotLocAdapter(OfflineAutoPlotLocationActivity.this, mListOfLatiLongi);
        mRecyclerLocList.setAdapter(mMainAdapter);
    }

    private void initView() {
        mBtnLocStart = (Button) findViewById(R.id.btn_plot_loc_start);
        mBtnLocStop = (Button) findViewById(R.id.btn_plot_loc_stop);
        mBtnPlot = (Button) findViewById(R.id.btn_main_plot);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_main);
        mProgressBar.setVisibility(View.GONE);
        mBtnPlot.setOnClickListener(this);
        mBtnLocStart.setOnClickListener(this);
        mBtnLocStop.setOnClickListener(this);

        mTextViewInterval = (TextView) findViewById(R.id.textview_interval);
        mSeekbarInterval = (SeekBar) findViewById(R.id.seekbar_interval);
        mSeekbarInterval.setMax(MAX_INTERVAL);
        mSeekbarInterval.setProgress(INIT_INTERVAL);
        mTextViewInterval.setText("" + (INIT_INTERVAL / 1000));
        mSeekbarInterval.setOnSeekBarChangeListener(this);
        diablePlotBtn();
        initRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopLocationListner();
    }

    private void stopLocationListner() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            disableStopBtn();
            if (mProgressBar.isShown()) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_plot_loc_start:
                mProgressBar.setVisibility(View.VISIBLE);
                getLoc(Integer.parseInt(mTextViewInterval.getText().toString()));
                mIsStopBtnPressed = false;
                break;
            case R.id.btn_plot_loc_stop:
                mIsStopBtnPressed = true;
                stopLocationListner();
                break;
            case R.id.btn_main_plot:
                Toast.makeText(OfflineAutoPlotLocationActivity.this, "Internet needed to show proper map", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OfflineAutoPlotLocationActivity.this, PlotOnMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", mListOfLatiLongi);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
        }
    }

    private void getLoc(long pMilliSec) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(runtimePermissions, LOCATION_PERMISSION_IDENTIFIER);
            }
            return;
        } else {
            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, pMilliSec, 0, this);
            }
        }
    }


    private boolean isExist(double latitude, double longitude) {
        for (MainDataModel obj : mListOfLatiLongi) {
            if (obj.getLatitude() == getValue(latitude) && obj.getLongitude() == getValue(longitude)) {
                Log.d("ITC", "Already exist location " + latitude + "-" + longitude);
                Toast.makeText(this, "Already exists :lat:" + latitude + " lon:" + longitude, Toast.LENGTH_SHORT).show();
               /* if (mProgressBar.isShown()) {
                    mProgressBar.setVisibility(View.GONE);
                }*/
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy() < ACCURACY) {
            if (!isExist(location.getLatitude(), location.getLongitude())) {
                mListOfLatiLongi.add(new MainDataModel(location.getLatitude(), location.getLongitude(), location.getAccuracy(), location.getAltitude()));
                Toast.makeText(this, "lat:" + location.getLatitude() + " longi:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
            mMainAdapter.notifyDataSetChanged();
       /* if (mProgressBar.isShown()) {
            mProgressBar.setVisibility(View.GONE);
        }*/
            // enablePlotBtn();
            if (!mIsStopBtnPressed) {
                enableStopBtn();
                Log.d("ITC", "location+++ " + location);
                locationManager.removeUpdates(this);
                getLoc(Integer.parseInt(mTextViewInterval.getText().toString()));
            }
        }else{
            Toast.makeText(this, "ACCURACY is more "+location.getAccuracy(), Toast.LENGTH_SHORT).show();
        }
    }

    private void enablePlotBtn() {
        if (mListOfLatiLongi.size() > 0) {
            mBtnPlot.setAlpha(1.0f);
            mBtnPlot.setEnabled(true);
        }
    }

    private void enableStopBtn() {
        mBtnLocStop.setAlpha(1.0f);
        mBtnLocStop.setEnabled(true);
    }

    private void disableStopBtn() {
        mBtnLocStop.setAlpha(0.4f);
        mBtnLocStop.setEnabled(false);
        enablePlotBtn();
    }

    private void diablePlotBtn() {
        mBtnPlot.setAlpha(0.4f);
        mBtnPlot.setEnabled(false);
        disableStopBtn();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("ITC", "location 111");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("ITC", "location 222");
        getLoc(Integer.parseInt(mTextViewInterval.getText().toString()));
    }

    @Override
    public void onProviderDisabled(String provider) {
        displayLocationSettingsRequest(this);
        Log.d("ITC", "location 33");
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(OfflineAutoPlotLocationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                            if (mProgressBar.isShown()) {
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    private double getValue(double num) {
        return num;//Double.parseDouble(new DecimalFormat("##.###").format(Math.abs(num)));
    }

    //for seekbar---------------
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mTextViewInterval.setText("" + (progress / 1000));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    //end of seekbar---------

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_IDENTIFIER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(runtimePermissions, LOCATION_PERMISSION_IDENTIFIER);
                        }
                    }
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}

