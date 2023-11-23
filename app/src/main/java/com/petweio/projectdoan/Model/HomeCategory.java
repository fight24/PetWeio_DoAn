package com.petweio.projectdoan.Model;

public class HomeCategory {
    private int statusImg;
    private int batteryImg;
    private int imgDevice;
    private String textName;
    private String btnNotification;
    private String typeShow;

    public String getType() {
        return typeShow;
    }

    public void setType(String type) {
        this.typeShow = type;
    }

    public HomeCategory(int imgDevice, String textName, String type) {
        this.imgDevice = imgDevice;
        this.textName = textName;
        this.typeShow = type;
    }

    public HomeCategory() {
    }

    public HomeCategory(int statusImg, int batteryImg, int imgDevice, String textName, String btnNotification, String type) {
        this.statusImg = statusImg;
        this.batteryImg = batteryImg;
        this.imgDevice = imgDevice;
        this.textName = textName;
        this.btnNotification = btnNotification;
        this.typeShow = type;
    }

    public HomeCategory(int statusImg, int batteryImg, int imgDevice, String textName, String type) {
        this.statusImg = statusImg;
        this.batteryImg = batteryImg;
        this.imgDevice = imgDevice;
        this.textName = textName;
        this.typeShow = type;
    }

    public HomeCategory(int batteryImg) {
        this.batteryImg = batteryImg;
    }

    public int getStatusImg() {
        return statusImg;
    }

    public void setStatusImg(int statusImg) {
        this.statusImg = statusImg;
    }

    public int getBatteryImg() {
        return batteryImg;
    }

    public void setBatteryImg(int batteryImg) {
        this.batteryImg = batteryImg;
    }

    public int getImgDevice() {
        return imgDevice;
    }

    public void setImgDevice(int imgDevice) {
        this.imgDevice = imgDevice;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }

    public String getNotificationImg() {
        return btnNotification;
    }

    public void setNotificationImg(String btnNotification) {
        this.btnNotification = btnNotification;
    }
}
