package com.malviya.demoofflinelocfinder.model;

/**
 * Created by 23508 on 6/19/2017.
 */

public class MapLogDataModel {
    private String location="";
    private String filePath="";
    private String acre;
    private String lastModified;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAcre() {
        return acre;
    }

    public void setAcre(String acre) {
        this.acre = acre;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
