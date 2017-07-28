package com.malviya.demoofflinelocfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.malviya.demoofflinelocfinder.R;
import com.malviya.demoofflinelocfinder.adapters.OnlineMapLoadAdapter;
import com.malviya.demoofflinelocfinder.model.MapLogDataModel;

import java.util.ArrayList;

/**
 * Created by 23508 on 6/19/2017.
 */

public class OnlineMapLoadActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private static final int PLACE_PICKER_REQUEST = 1;
    private SearchView mSerchViewLoc;
    private RecyclerView mRecyclerViewSearchLoc;
    private OnlineMapLoadAdapter mAdapter;
    private ArrayList<MapLogDataModel> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_load);
        initView();
    }

    private void initView() {
        mSerchViewLoc = (SearchView) findViewById(R.id.searchview_map_load);
        mSerchViewLoc.setOnQueryTextListener(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerViewSearchLoc = (RecyclerView) findViewById(R.id.recycler_map_load);
        mRecyclerViewSearchLoc.setLayoutManager(new LinearLayoutManager(OnlineMapLoadActivity.this));
        mList = new ArrayList<>();
        mAdapter = new OnlineMapLoadAdapter(OnlineMapLoadActivity.this, mList);
        mRecyclerViewSearchLoc.setAdapter(mAdapter);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("Malviya", "query: " + query);
        mSerchViewLoc.clearFocus();
      
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(OnlineMapLoadActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("Malviya", "new Text: " + newText);
        return false;
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){

       }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

}
