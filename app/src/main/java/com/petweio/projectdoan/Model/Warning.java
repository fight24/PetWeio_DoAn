package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

public class Warning{
    @SerializedName("is_warning")
    private boolean isWarning;
    @SerializedName("distance")
    private float distance;
    public Warning(boolean isWarning) {
        this.isWarning = isWarning;
    }

    public Warning(boolean isWarning, float distance) {
        this.isWarning = isWarning;
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public void setWarning(boolean warning) {
        isWarning = warning;
    }
}
