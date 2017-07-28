package com.malviya.demoofflinelocfinder.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.database.TableMapLog;
import com.malviya.demoofflinelocfinder.model.MainDataModel;
import com.malviya.demoofflinelocfinder.model.MapLogDataModel;
import com.malviya.demoofflinelocfinder.utils.SphericalUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by 23508 on 6/16/2017.
 */

public class PlotOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnMapClickListener {
    private static final int WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;
    public static final String FOLDER_NAME_FOR_SELFI = "malviya_map";
    private ArrayList<MainDataModel> mListOfLatiLongi;
    private Button mSaveMap;
    private double mAcre;
    private String filePath = "";
    private MapFragment mMapFragment;
    private LinearLayout mLinearHolder;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommap);
        if (ContextCompat.checkSelfPermission(PlotOnMapActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PlotOnMapActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        }

        mListOfLatiLongi = new ArrayList<>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            ArrayList<MainDataModel> obj = (ArrayList<MainDataModel>) bundle.getSerializable("list");
            if (obj != null) {
                mListOfLatiLongi = obj;
            }
        }
        initView();
    }

    private void initView() {
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_pane);
        mLinearHolder = (LinearLayout) findViewById(R.id.lin_custommap1);
        mMapFragment.getMapAsync(this);
        mSaveMap = (Button) findViewById(R.id.btn_custommap_save_map);
        mSaveMap.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null && mListOfLatiLongi.size() > 0) {
            googleMap = map;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //googleMap.setMyLocationEnabled(true);
            LatLng mapCenter = new LatLng(mListOfLatiLongi.get(0).getLatitude(), mListOfLatiLongi.get(0).getLongitude());
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(mapCenter)
                    .zoom(18)
                    .bearing(0)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 18));
            drawPolygon();
            map.setOnPolylineClickListener(this);
            map.setOnPolygonClickListener(this);
            googleMap.setOnMapClickListener(this);
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        }
    }

    private void drawPolygon() {
        if (googleMap != null) {
            googleMap.clear();
            LatLng mapCenter = new LatLng(mListOfLatiLongi.get(0).getLatitude(), mListOfLatiLongi.get(0).getLongitude());

            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                    .title(mAcre + " acre")
                    .position(mapCenter)
                    .visible(true)
                    .draggable(false)
                    .flat(false))
                    .showInfoWindow();
            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (MainDataModel model : mListOfLatiLongi) {
                latLngs.add(new LatLng(model.getLatitude(), model.getLongitude()));
                googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                        .position(latLngs.get(latLngs.size() - 1))
                        .flat(false)
                        .visible(true)
                        .draggable(false))
                        .showInfoWindow();
            }
            mAcre = SphericalUtil.computeArea(latLngs) * 0.000247105f;
            // float acre = 0.000247105f; // (1 sqrt meter = 0.000247105f acre)


            PolygonOptions polygonOptions = new PolygonOptions().geodesic(true);//.fillColor(R.color.colorPrimaryDark).strokeColor(R.color.colorAccent);
            for (MainDataModel loc : mListOfLatiLongi) {
                // polylineOptions.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
                polygonOptions.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
                // polylineOptions.clickable(true);
                polygonOptions.clickable(true);
            }
            //Polyline polyline = map.addPolyline(polylineOptions);
            Polygon polygon = googleMap.addPolygon(polygonOptions);
            polygon.setTag("alpha");
            stylePolygon(polygon);
            // Animate the change in camera view over 2 seconds
            // googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);
        }
    }

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_custommap_save_map:
                captureScreen();
                break;
        }
    }

    private void captureScreen() {
        if (ContextCompat.checkSelfPermission(PlotOnMapActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PlotOnMapActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
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
                        Toast.makeText(PlotOnMapActivity.this, "Unexpected error, " + e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        //write into table-------------------------------------------------------------------------------
        Dialog dialog = new Dialog(PlotOnMapActivity.this, R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(R.layout.dialog_location_name);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText editText = (EditText) dialog.findViewById(R.id.edittext_dialog_loc_name);
        Button button = (Button) dialog.findViewById(R.id.btn_dialog_loc_name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() > 0) {
                    TableMapLog tableMapLog = new TableMapLog(PlotOnMapActivity.this);
                    MapLogDataModel model = new MapLogDataModel();
                    model.setLocation(editText.getText().toString());
                    model.setFilePath(filePath);
                    model.setAcre("" + mAcre);
                    model.setLastModified("" + ((new Date()).getTime()));
                    tableMapLog.addMapLog(model);
                    startActivity(new Intent(PlotOnMapActivity.this, MapLogActivity.class));
                    finish();
                } else {
                    Toast.makeText(PlotOnMapActivity.this, "Please enter location", Toast.LENGTH_SHORT).show();
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

  /*  public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        //Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(true); // clear drawing cache;
        return bitmap;
    }*/


    public void CaptureMapScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                PlotOnMapActivity.this.saveScreenCaptureToExternalStorage(bitmap);
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
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

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
            mListOfLatiLongi.add(new MainDataModel(latLng.latitude, latLng.longitude,0,0));
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
}
