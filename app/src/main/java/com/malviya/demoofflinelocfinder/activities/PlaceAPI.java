package com.malviya.demoofflinelocfinder.activities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.malviya.demoofflinelocfinder.model.SearchPlaceDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by 23508 on 6/27/2017.
 */

public class PlaceAPI {
    private static final String TAG = PlaceAPI.class.getSimpleName();
    //----------------------------------------------------------------------------------------------------------
    /*30'000 credits daily limit per application (identified by the parameter 'username'), the hourly limit is 2000 credits.
     A credit is a web service request hit for most services. An exception is thrown when the limit is exceeded.*/
    private static final String PLACES_API_BASED_ON_PINCODE = "http://api.geonames.org/postalCodeLookupJSON?";
    private static final String POSTALCODE = "postalcode";
    private static final String COUNTRY = "country";
    private static final String COUNTRY_VALUE = "IN";
    private static final String USERNAME = "username";
    private static final String USERNAME_VALUE = "prafulla";
    //------------------------------------------------------------------------------------------------------------
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    public static final String GOOGLE_API = "AIzaSyAWcFkZJYMVeTR8ZpphSKJuBVs0G9X-UMg";
    //-----------------------------------------------------------------------------------------------------------
    private final Context mContext;

    private ArrayList<SearchPlaceDataModel> mList = new ArrayList<>();

    public PlaceAPI(Context pContext) {
        mContext = pContext;
    }

    public ArrayList<SearchPlaceDataModel> autocomplete(String input) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = null;
            if (validateNumber(input)) {
                sb = new StringBuilder(PLACES_API_BASED_ON_PINCODE);
                sb.append("&" + POSTALCODE+"="+ input.trim());
                sb.append("&" + COUNTRY+"=" + COUNTRY_VALUE);
                sb.append("&" + USERNAME+"=" + USERNAME_VALUE);
            } else {
                sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + GOOGLE_API);
                // sb.append("&types=(cities)");
                sb.append("&types=geocode");
                sb.append("&input=" + URLEncoder.encode(input.trim(), "utf8"));
            }


            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return mList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return mList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        if (validateNumber(input)) {
            parsePinCode(jsonResults);
        } else {
            parsePlace(jsonResults);
        }

        return mList;
    }

    private void parsePinCode(StringBuilder jsonResults) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResults.toString());
           // String status = jsonObject.getString("status");
            mList.clear();
            JSONArray predictions = jsonObject.getJSONArray("postalcodes");
            for (int i = 0; i < predictions.length(); i++) {
                SearchPlaceDataModel obj = new SearchPlaceDataModel();
                JSONObject jsonObject1 = new JSONObject(predictions.get(i).toString());
                obj.setDescription(jsonObject1.optString("placeName")+" "+jsonObject1.optString("adminName2"));
                obj.setPlace_id(null);
                obj.setPinCode(jsonObject1.getString("postalcode"));
                obj.setPlace_lati(jsonObject1.getDouble("lat"));
                obj.setPlace_longi(jsonObject1.getDouble("lng"));
                mList.add(obj);
            }
            Log.d(TAG, "mList process JSON results : " + mList.toString());

        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }
    }

    private void parsePlace(StringBuilder jsonResults) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResults.toString());
            String status = jsonObject.getString("status");
            mList.clear();
            if (status.equalsIgnoreCase("OK")) {
                JSONArray predictions = jsonObject.getJSONArray("predictions");
                for (int i = 0; i < predictions.length(); i++) {
                    SearchPlaceDataModel obj = new SearchPlaceDataModel();
                    JSONObject jsonObject1 = new JSONObject(predictions.get(i).toString());
                    obj.setDescription(jsonObject1.optString("description"));
                    obj.setPlace_id(jsonObject1.optString("place_id"));
                    mList.add(obj);
                }
                Log.d(TAG, "mList process JSON results : " + mList.toString());
            } else {
                Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }
    }

    private boolean validateNumber(String input) {
        String regexStr = "^[0-9]*$";
        if (input.trim().matches(regexStr)) {
            return true;
        }
        return false;
    }
}
