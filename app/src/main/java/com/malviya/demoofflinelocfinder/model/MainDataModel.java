package com.malviya.demoofflinelocfinder.model;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by 23508 on 6/15/2017.
 */

public class MainDataModel implements Serializable {
    private double altitude;
    private double accuracy;
    private double latitude;
    private double longitude;
    private String location_name="";
    private int img;

    public MainDataModel(double latitude, double longitude, float accuracy, double altitude) {
        this.latitude = getValue(latitude);
        this.longitude = getValue(longitude);
        this.accuracy = getValue(accuracy);
        this.altitude = getValue(altitude);
    }

    public MainDataModel() {

    }

    private double getValue(double num) {
        return num;//Double.parseDouble(new DecimalFormat("##.###").format(Math.abs(num)));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
