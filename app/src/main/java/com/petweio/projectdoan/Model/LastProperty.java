package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

public class LastProperty {
    @SerializedName("device_code")
   private String device_code;
    @SerializedName("latest_property")
   private Property latest_property;

    public LastProperty(String device_code, Property latest_property) {
        this.device_code = device_code;
        this.latest_property = latest_property;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public Property getLatest_property() {
        return latest_property;
    }

    public void setLatest_property(Property latest_property) {
        this.latest_property = latest_property;
    }

    @Override
    public String toString() {
        return "LastProperty{" +
                "device_code='" + device_code + '\'' +
                ", latest_property=" + latest_property +
                '}';
    }
}
