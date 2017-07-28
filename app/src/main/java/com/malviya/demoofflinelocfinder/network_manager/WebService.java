package com.malviya.demoofflinelocfinder.network_manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 23508 on 5/31/2017.
 */

public class WebService {
    private static final int CONN_TIMEOUT = 20000;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public WebService(Context pContext) {
        mContext = pContext;
        mProgressDialog = new ProgressDialog(pContext);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * @param pURL
     * @param mHeader
     * @param mParam
     * @param pIsLoaderShow
     * @param pListner
     */
    public void requestWS(String pURL, final HashMap<String, String> mHeader, final HashMap<String, String> mParam, final boolean pIsLoaderShow, final IWebService pListner) {
        int method = Request.Method.POST;
        if (mParam == null) {
            method = Request.Method.GET;
        }
        if(pIsLoaderShow){
            mProgressDialog.show();
        }
        Log.d("Malviya", "++++++++++++++++++++++++++++++++++");
        Log.d("Malviya", "URL: " + pURL);
        Log.d("Malviya", "mHeader: " + mHeader);
        Log.d("Malviya", "mParam: " + mParam);
        Log.d("Malviya", "method: " + method);
        Log.d("Malviya", "pIsLoaderShow: " + pIsLoaderShow);

        StringRequest stringRequest = new StringRequest(method, pURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pListner.onResponse(response);
                Log.d("Malviya", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message = "Something went wrong Server.";
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                pListner.onErrorResponse(message);
                dismissProgressDialog();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                Log.e("Malviya", "onErrorResponse: " + volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return (mParam!=null?mParam:new HashMap<String,String>());
            }

            @Override
            public Map<String, String> getHeaders() {
                return (mHeader!=null?mHeader:new HashMap<String,String>());
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(CONN_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }
}
