package com.petweio.projectdoan.Model;

public class HistoryInfo extends Device{
    private String latitude;
    private String longitude;
    private String dayTime;
    private Property property;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public HistoryInfo(String nameDevice, String typeDevice, String imageName, String latitude, String longitude, String dayTime) {
        super(nameDevice, typeDevice, imageName);
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayTime = dayTime;
    }

    public HistoryInfo(Property property) {
        this.property = property;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }
}
