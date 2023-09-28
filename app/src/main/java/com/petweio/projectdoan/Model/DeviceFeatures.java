package com.petweio.projectdoan.Model;

public class DeviceFeatures {
    private int resourceId;
    private String title;
    private String value;

    public DeviceFeatures(int resourceId, String title, String value) {
        this.resourceId = resourceId;
        this.title = title;
        this.value = value;
    }
    public DeviceFeatures(int resourceId, String title) {
        this.resourceId = resourceId;
        this.title = title;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
