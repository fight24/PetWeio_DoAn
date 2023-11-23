package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

public class Warning{
    @SerializedName("is_warning")
    private boolean isWarning;

    public Warning(boolean isWarning) {
        this.isWarning = isWarning;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public void setWarning(boolean warning) {
        isWarning = warning;
    }
}
