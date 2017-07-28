package com.malviya.demoofflinelocfinder.model;

public class SearchPlaceDataModel {
    private String description;
    private String place_id;

    private double place_lati=0.0;
    private double place_longi=0.0;
    private String pinCode;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }


    public double getPlace_lati() {
        return place_lati;
    }

    public void setPlace_lati(double place_lati) {
        this.place_lati = place_lati;
    }

    public double getPlace_longi() {
        return place_longi;
    }

    public void setPlace_longi(double place_longi) {
        this.place_longi = place_longi;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}