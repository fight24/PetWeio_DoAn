package com.petweio.projectdoan.Model;

public class DeviceMenu {
    private int resourceId;
    private String title;


    String type ;
    public DeviceMenu(int idResource, String title) {
        this.resourceId = idResource;
        this.title = title;
    }
    public DeviceMenu(int idResource, String title,String type) {
        this.resourceId = idResource;
        this.title = title;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int idResource) {
        this.resourceId = idResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
