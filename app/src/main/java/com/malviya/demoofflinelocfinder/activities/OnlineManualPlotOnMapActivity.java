package com.malviya.demoofflinelocfinder.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.malviya.demoofflinelocfinder.DelayAutoCompleteTextView;
import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.adapters.BookAutoCompleteAdapter;
import com.malviya.demoofflinelocfinder.database.TableMapLog;
import com.malviya.demoofflinelocfinder.model.MainDataModel;
import com.malviya.demoofflinelocfinder.model.MapLogDataModel;
import com.malviya.demoofflinelocfinder.model.SearchPlaceDataModel;
import com.malviya.demoofflinelocfinder.network_manager.IWebService;
import com.malviya.demoofflinelocfinder.network_manager.WebService;
import com.malviya.demoofflinelocfinder.utils.SphericalUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 23508 on 6/16/2017.
 */

public class OnlineManualPlotOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnMapClickListener, LocationListener {
    public static final String FOLDER_NAME_FOR_SELFI = "malviya_map";
    public static final String[] runtimePermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int LOCATION_PERMISSION_IDENTIFIER = 1;
    private static final String TAG = "OnlineManualPlot";
    private static final int WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;
    private static final int WIKITUDE_PERMISSIONS_REQUEST_ACCESS = 4;
    private static final int THRESHOLD = 5;
    private static final String URL_LOCATION = "https://maps.googleapis.com/maps/api/place/details/json?";

    //style
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA = Arrays.asList(DOT, GAP, DASH, GAP);
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static LocationManager locationManager;
    private static ProgressBar mProgressBar;
    private ArrayList<MainDataModel> mListOfLatiLongi;
    private Button mSaveMap;
    private double mAcre;
    private String filePath = "";
    private MapFragment mMapFragment;
    private LinearLayout mLinearHolder;
    private GoogleMap googleMap;
    private String mLocation = "";

    /*  public Bitmap screenShot(View view) {
          Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
          view.draw(new Canvas(bitmap));
          //Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
          view.setDrawingCacheEnabled(true); // clear drawing cache;
          return bitmap;
      }*/
    private Button mBtnManual;
    private Button mBtnAutoStart;
    private Button mBtnAutoStop;
    private boolean mIsStopBtnPressed;
    private boolean isManualEnable;

    private static String calculatePolygonArea(ArrayList<MainDataModel> coordinates) {
        double area = 0;

        if (coordinates.size() > 2) {
            for (int i = 0; i < coordinates.size() - 1; i++) {
                MainDataModel p1 = coordinates.get(i);
                MainDataModel p2 = coordinates.get(i + 1);
                area += (p2.getLongitude() - p1.getLongitude()) * (2 + Math.sin(ConvertToRadian(p1.getLatitude())) + Math.sin(ConvertToRadian(p2.getLatitude())));
            }

            area = area * 6378137 * 6378137 / 2;
        }

        double i2 = Math.abs(area);
        String num = (new DecimalFormat("##.###").format(i2));
        return num;
    }

    private static double ConvertToRadian(double input) {
        return input * Math.PI / 180;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_manual_plot_loc);
        if (ContextCompat.checkSelfPermission(OnlineManualPlotOnMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OnlineManualPlotOnMapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        }

        mListOfLatiLongi = new ArrayList<>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.getString("location") != null) {
                mLocation = bundle.getString("location");
            }

            if (bundle.getSerializable("list") != null) {
                ArrayList<MainDataModel> obj = (ArrayList<MainDataModel>) bundle.getSerializable("list");
                if (obj != null) {
                    mListOfLatiLongi = obj;
                }
            }
        }
        initView();
        initLocation();
    }

    private void initView() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_main);
        mProgressBar.setVisibility(View.GONE);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_pane);
        mLinearHolder = (LinearLayout) findViewById(R.id.lin_custommap1);
        mMapFragment.getMapAsync(this);
        mSaveMap = (Button) findViewById(R.id.btn_custommap_save_map);
        mSaveMap.setOnClickListener(this);

        mBtnManual = (Button) findViewById(R.id.btn_online_manual_plot_manual);
        mBtnAutoStart = (Button) findViewById(R.id.btn_online_manual_plot_auto_start);
        mBtnAutoStop = (Button) findViewById(R.id.btn_online_manual_plot_auto_stop);

        mBtnManual.setOnClickListener(this);
        mBtnAutoStart.setOnClickListener(this);
        mBtnAutoStop.setOnClickListener(this);

        initBtn();

    }

    private void initBtn() {
        mBtnAutoStart.setAlpha(0.4f);
        mBtnAutoStart.setEnabled(false);
        mBtnManual.setAlpha(0.4f);
        mBtnManual.setEnabled(false);
        mSaveMap.setAlpha(0.4f);
        mSaveMap.setEnabled(false);
        mBtnAutoStop.setVisibility(View.GONE);
    }

    private void resetBtn() {
        mSaveMap.setAlpha(1);
        mSaveMap.setEnabled(true);
        mBtnAutoStop.setVisibility(View.GONE);
       /* mBtnAutoStart.setAlpha(1);
        mBtnAutoStart.setEnabled(true);*/
        mBtnManual.setAlpha(1);
        mBtnManual.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null) {
            googleMap = map;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(OnlineManualPlotOnMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, WIKITUDE_PERMISSIONS_REQUEST_ACCESS);
                return;
            }
            if (mListOfLatiLongi.size() > 0) {
                LatLng mapCenter = new LatLng(mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLatitude(), mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLongitude());
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(mapCenter)
                        .zoom(18)
                        .bearing(0)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 18));
                drawPolygon();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnPolylineClickListener(this);
            googleMap.setOnPolygonClickListener(this);
            googleMap.setOnMapClickListener(this);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mBtnAutoStart.setAlpha(1);
                    mBtnAutoStart.setEnabled(true);
                    mBtnManual.setAlpha(1);
                    mBtnManual.setEnabled(true);
                    return false;
                }
            });
        }
        LatLng mao = new LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mao, 18));

    }

    private void drawPolygon() {
        if (googleMap != null && mListOfLatiLongi.size() > 0) {
            googleMap.clear();
            LatLng mapCenter = new LatLng(mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLatitude(), mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLongitude());

            ArrayList<LatLng> latLngs = new ArrayList<>();

            //for (MainDataModel model : mListOfLatiLongi) {
            for (int index = 0; index < mListOfLatiLongi.size(); index++) {
                MainDataModel model = mListOfLatiLongi.get(index);
                latLngs.add(new LatLng(model.getLatitude(), model.getLongitude()));
                googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                        .position(latLngs.get(latLngs.size() - 1))
                        .flat(false)
                        .visible(true)
                        .draggable(false))
                        .showInfoWindow();

            }
            // float acre = 0.000247105f; // (1 sqrt meter = 0.000247105f acre)
            mAcre = SphericalUtil.computeArea(latLngs) * 0.000247105f;

            PolygonOptions polygonOptions = new PolygonOptions().geodesic(true);//.fillColor(R.color.colorPrimaryDark).strokeColor(R.color.colorAccent);
            for (int index = 0; index < mListOfLatiLongi.size(); index++) {
                MainDataModel loc = mListOfLatiLongi.get(index);
                // polylineOptions.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
                polygonOptions.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
                // polylineOptions.clickable(true);
                polygonOptions.clickable(true);
            }

            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                    .title(mAcre + " acre")
                    .position(mapCenter)
                    .visible(true)
                    .draggable(false)
                    .flat(false))
                    .showInfoWindow();

            //Polyline polyline = map.addPolyline(polylineOptions);
            Polygon polygon = googleMap.addPolygon(polygonOptions);
            polygon.setTag("alpha");
            stylePolygon(polygon);
        }
    }

    @Override
    public void onClick(View v) {
        if (mProgressBar.isShown()) {
            mProgressBar.setVisibility(View.GONE);
        }
        isManualEnable = false;
        switch (v.getId()) {
            case R.id.btn_custommap_save_map:
                captureScreen();
                break;
            case R.id.btn_online_manual_plot_manual:
                isManualEnable = true;
                Toast.makeText(this, "Manual Plotting Enabled", Toast.LENGTH_SHORT).show();
                locationManager.removeUpdates(this);
                stopLocationListner();
                break;
            case R.id.btn_online_manual_plot_auto_start:
                mProgressBar.setVisibility(View.VISIBLE);
                getLoc(5);
                enableStopBtn();
                Toast.makeText(this, "Auto Plotting Enabled", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_online_manual_plot_auto_stop:
                mIsStopBtnPressed = true;
                stopLocationListner();
                break;
        }
    }

    private void captureScreen() {
        if (ContextCompat.checkSelfPermission(OnlineManualPlotOnMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OnlineManualPlotOnMapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        } else {
            CaptureMapScreen();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CaptureMapScreen();
                } else {
                    Toast.makeText(this, "Please allow access to external storage, otherwise the screen capture can not be saved.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case WIKITUDE_PERMISSIONS_REQUEST_ACCESS: {
                //googleMap.setMyLocationEnabled(true);
                Log.d("LOC", "mListOfLatiLongi lati " + mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLatitude() + "::" + mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLongitude());
                LatLng mapCenter = new LatLng(mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLatitude(), mListOfLatiLongi.get(mListOfLatiLongi.size() - 1).getLongitude());
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(mapCenter)
                        .zoom(18)
                        .bearing(0)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 18));
                drawPolygon();
                googleMap.setOnPolylineClickListener(this);
                googleMap.setOnPolygonClickListener(this);
                googleMap.setOnMapClickListener(this);
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            }
            break;
            case LOCATION_PERMISSION_IDENTIFIER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, this);
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
        }
    }

    protected void saveScreenCaptureToExternalStorage(Bitmap screenCapture) {
        if (screenCapture != null) {
            //----save into gallary
            String folder_main = FOLDER_NAME_FOR_SELFI;
            File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File picsDir = new File(dcimDir, folder_main);
            picsDir.mkdirs(); //make if not exist
            File screenCaptureFile = new File(picsDir, System.currentTimeMillis() + ".jpg");

            try {
                final FileOutputStream out = new FileOutputStream(screenCaptureFile);
                screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                galleryAddPic(screenCaptureFile.toString());
                filePath = screenCaptureFile.toString();
                Log.d("Malviya", "saved map log image path " + filePath);

            } catch (final Exception e) {
                // should not occur when all permissions are set
                this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show toast message in case something went wrong
                        Toast.makeText(OnlineManualPlotOnMapActivity.this, "Unexpected error, " + e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }


        //write into table-------------------------------------------------------------------------------
        Dialog dialog = new Dialog(OnlineManualPlotOnMapActivity.this, R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(R.layout.dialog_location_name);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText editText = (EditText) dialog.findViewById(R.id.edittext_dialog_loc_name);
        editText.setText(mLocation);
        Button button = (Button) dialog.findViewById(R.id.btn_dialog_loc_name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() > 0) {
                    TableMapLog tableMapLog = new TableMapLog(OnlineManualPlotOnMapActivity.this);
                    MapLogDataModel model = new MapLogDataModel();
                    model.setLocation(editText.getText().toString());
                    model.setFilePath(filePath);
                    model.setAcre("" + mAcre);
                    model.setLastModified("" + ((new Date()).getTime()));
                    tableMapLog.addMapLog(model);
                    startActivity(new Intent(OnlineManualPlotOnMapActivity.this, MapLogActivity.class));
                    finish();
                } else {
                    Toast.makeText(OnlineManualPlotOnMapActivity.this, "Please enter location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        //-----------------------------------------------------------------------------------------------
    }

    private void galleryAddPic(String fpath) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(fpath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void CaptureMapScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                OnlineManualPlotOnMapActivity.this.saveScreenCaptureToExternalStorage(bitmap);
            }
        };

        googleMap.snapshot(callback);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @Override
    public void onPolygonClick(Polygon polygon) {
       /* try {
            if (googleMap != null) {
                List<LatLng> list = new ArrayList<>();
                list.add(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()));
                polygon.setPoints(list);
            }
        } catch (Exception e) {
            Log.e("Malviya", "Exception from onPolygonClick " + e.getMessage());
        }*/
    }

    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (!isInList(latLng)) {
            if (isManualEnable)
                mListOfLatiLongi.add(new MainDataModel(latLng.latitude, latLng.longitude, 0, 0));
        }
        drawPolygon();
    }

    private boolean isInList(LatLng latLng) {
        Log.d("Malviya", "isInList " + latLng.latitude + " " + latLng.longitude);
        for (MainDataModel holder : mListOfLatiLongi) {
            if (holder.getLatitude() == latLng.latitude && holder.getLongitude() == latLng.longitude) {
                mListOfLatiLongi.remove(holder);
                Log.d("Malviya", "remove " + latLng.latitude + " " + latLng.longitude);
                return true;
            }
        }
        return false;
    }

    private void initLocation() {

        mListOfLatiLongi.clear();
        final DelayAutoCompleteTextView bookTitle = (DelayAutoCompleteTextView) findViewById(R.id.et_book_title);
        bookTitle.setThreshold(THRESHOLD);
        bookTitle.setAdapter(new BookAutoCompleteAdapter(this)); // 'this' is Activity instance
        bookTitle.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));
        bookTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SearchPlaceDataModel book = (SearchPlaceDataModel) adapterView.getItemAtPosition(position);
                if (book.getPlace_lati() != 0.0 && book.getPlace_longi() != 0.0) {
                    bookTitle.setText(book.getPinCode());
                } else {
                    bookTitle.setText(book.getDescription());
                }
                mLocation = book.getDescription().trim();
                findLatiLongiFromWS(book);
                resetBtn();
            }
        });

        // bookTitle.setText(mLocation);
    }

    private void findLatiLongiFromWS(SearchPlaceDataModel place) {
        final WebService ws = new WebService(this);
        HashMap<String, String> header = new HashMap<>();
        header.put("Accept-Language", "en");
        header.put("Content-Type", "application/json");
        mListOfLatiLongi.clear();
        if (place.getPlace_id() == null) {
            MainDataModel holder = new MainDataModel();
            holder.setLatitude(place.getPlace_lati());
            holder.setLongitude(place.getPlace_longi());
            holder.setLocation_name(place.getDescription());
            // mListOfLatiLongi.add(holder);
            plotOnMap(holder);

        } else {
            ws.requestWS(URL_LOCATION + "placeid=" + place.getPlace_id() + "&key=" + PlaceAPI.GOOGLE_API, header, null, true, new IWebService() {
                @Override
                public void onResponse(String response) {
                    ws.dismissProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status");
                        if (status.equalsIgnoreCase("OK")) {
                            mListOfLatiLongi.clear();
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            String formatted_address = jsonObject1.optString("formatted_address");
                            JSONObject geometry = jsonObject1.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            Double lat = location.getDouble("lat");
                            Double lng = location.getDouble("lng");
                            MainDataModel holder = new MainDataModel();
                            holder.setLatitude(lat);
                            holder.setLongitude(lng);
                            holder.setLocation_name(formatted_address);
                            //mListOfLatiLongi.add(holder);
                            plotOnMap(holder);
                        } else {
                            String error_message = jsonObject.optString("error_message");
                            Toast.makeText(OnlineManualPlotOnMapActivity.this, error_message + " " + status, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception ", " findLatiLongiFromWS " + e.getMessage());
                    }
                }

                @Override
                public void onErrorResponse(String error) {
                    ws.dismissProgressDialog();
                }
            });
        }
    }

    private void plotOnMap(MainDataModel holder) {
        if (googleMap != null) {
            LatLng mao = new LatLng(holder.getLatitude(), holder.getLongitude());
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(mao)
                    .zoom(18)
                    .bearing(0)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mao, 18));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            //mMapFragment.getMapAsync(this);
        }
    }

    private void getLoc(long pMilliSec) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(runtimePermissions, LOCATION_PERMISSION_IDENTIFIER);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pMilliSec, 0.0f, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, pMilliSec, 0.0f, this);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopLocationListner();
    }

    private void stopLocationListner() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            resetBtn();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy() < OfflineAutoPlotLocationActivity.ACCURACY) {
            if (!isExist(location.getLatitude(), location.getLongitude())) {
                mListOfLatiLongi.add(new MainDataModel(location.getLatitude(), location.getLongitude(), location.getAccuracy(), location.getAltitude()));
                Toast.makeText(this, "lat:" + location.getLatitude() + " longi:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            if (!mIsStopBtnPressed) {
                enableStopBtn();
                Log.d("ITC", "location+++ " + location);
                locationManager.removeUpdates(this);
                getLoc(5);
            }
            mMapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "ACCURACY is more " + location.getAccuracy(), Toast.LENGTH_SHORT).show();
        }
    }

    private void enableStopBtn() {
        mBtnAutoStop.setVisibility(View.VISIBLE);
        mBtnAutoStop.setAlpha(1);
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

    private double getValue(double num) {
        return num;//Double.parseDouble(new DecimalFormat("##.###").format(Math.abs(num)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLoc(5);
    }

    @Override
    public void onProviderDisabled(String provider) {
        displayLocationSettingsRequest(this);
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
                            status.startResolutionForResult(OnlineManualPlotOnMapActivity.this, REQUEST_CHECK_SETTINGS);
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

}




