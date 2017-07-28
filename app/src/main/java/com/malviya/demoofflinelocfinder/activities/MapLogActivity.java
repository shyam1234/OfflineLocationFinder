package com.malviya.demoofflinelocfinder.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.adapters.MapLogAdapter;
import com.malviya.demoofflinelocfinder.database.TableMapLog;
import com.malviya.demoofflinelocfinder.model.MapLogDataModel;
import com.malviya.demoofflinelocfinder.utils.InternetManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 23508 on 6/19/2017.
 */

public class MapLogActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 22;
    private static final int PLACE_PICKER_REQUEST = 1;
    RecyclerView mRecyclerViewMapLog;
    private MapLogAdapter mMapLogAdapter;
    private ArrayList<MapLogDataModel> mListMapLog;
    // private FloatingActionButton mFloatingBtnNewMap;
    private Animation show_fab_1;
    private Animation hide_fab_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_logs);
        mListMapLog = new ArrayList<MapLogDataModel>();
        initView();
    }

    private void initView() {
        mRecyclerViewMapLog = (RecyclerView) findViewById(R.id.recycler_map_logs);
        mRecyclerViewMapLog.setLayoutManager(new LinearLayoutManager(MapLogActivity.this));
        mMapLogAdapter = new MapLogAdapter(MapLogActivity.this, mListMapLog);
        mRecyclerViewMapLog.setAdapter(mMapLogAdapter);
        //-------------------------
        //mFloatingBtnNewMap = (FloatingActionButton) findViewById(R.id.floatingbtn_map_logs);
        //mFloatingBtnNewMap.setOnClickListener(this);
        // initAnim();
        initFloatingMenuBtn();
    }

    private void initFloatingMenuBtn() {
        // Create an icon
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.mipmap.ic_launcher);
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageResource(R.mipmap.ic_launcher);

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.mipmap.ic_launcher);

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageResource(R.mipmap.ic_launcher);

        SubActionButton button1 = itemBuilder.setContentView(itemIcon1).build();
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Malviya", "online++++++++");
                Toast.makeText(MapLogActivity.this, "(Help desk)", Toast.LENGTH_SHORT).show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetManager.isNetworkAvailable(MapLogActivity.this)) {
                    Log.d("Malviya", "Online Plot Location Automatically ++++++++");
                } else {
                    Log.d("Malviya", "Offline Plot Location Automatically ++++++++");
                    Intent intent = new Intent(MapLogActivity.this, OfflineAutoPlotLocationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetManager.isNetworkAvailable(MapLogActivity.this)) {
                    Log.d("Malviya", "Online Plot Location Manual Activity++++++++");
                    Intent intent = new Intent(MapLogActivity.this, OnlineManualPlotOnMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                 /*  PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(MapLogActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }*/

                    /*try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(MapLogActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }*/
                } else {
                    Log.d("Malviya", "Offline Plot Location Manual Activity++++++++");
                    Intent intent = new Intent(MapLogActivity.this, OfflineManualPlotLocationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });

        //attach the sub buttons to the main button
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .attachTo(actionButton)
                .build();
    }

    private void initAnim() {
        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
    }


    @Override
    protected void onResume() {
        super.onResume();
        TableMapLog tableMapLog = new TableMapLog(this);
        mListMapLog = tableMapLog.getMapLogArray();
        mMapLogAdapter = new MapLogAdapter(MapLogActivity.this, mListMapLog);
        mRecyclerViewMapLog.setAdapter(mMapLogAdapter);
        mMapLogAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingbtn_map_logs:
                Log.d("Malviya", "Add new map");
                if (InternetManager.isNetworkAvailable(MapLogActivity.this)) {
                    //for online
                    Intent intent = new Intent(MapLogActivity.this, OnlineMapLoadActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    //for offline
                    Intent intent = new Intent(MapLogActivity.this, OfflineManualPlotLocationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.rel_map_log_row:
                // for edit/delete map
                int posi = (int) v.getTag();
                mListMapLog.get(posi);
                Toast.makeText(this, "Coming up shortly", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imgview_map_log_row:
                showZoomMap((int) v.getTag());
                break;
        }
    }

    private void showZoomMap(int position) {
        Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(R.layout.dialog);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.imgview_dialog);
        setImageFromFile(imageView, mListMapLog.get(position).getFilePath());
        dialog.show();
    }

    public void setImageFromFile(ImageView imageView, String filepath) {
        File imgFile = new File(filepath);
        if (imgFile.exists()) {
            imageView.setImageURI(Uri.fromFile(imgFile));
        }
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                navigateToOtherActivity(toastMsg);
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("Malviya", "Place: " + place.getName());
                navigateToOtherActivity(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Malviya", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void navigateToOtherActivity(String toastMsg) {
        //------------------------------------------------------------------------------------------
        Intent intent = new Intent(MapLogActivity.this, OnlineManualPlotOnMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("location", toastMsg);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
