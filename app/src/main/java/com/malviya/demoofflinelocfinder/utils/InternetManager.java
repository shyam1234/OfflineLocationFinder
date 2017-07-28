package com.malviya.demoofflinelocfinder.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 23508 on 6/19/2017.
 */

public class InternetManager {

    public  static  boolean isNetworkAvailable(Activity pActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) pActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
