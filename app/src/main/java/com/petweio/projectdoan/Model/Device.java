package com.petweio.projectdoan.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Device extends DeViceMenuV2 implements Serializable {
    @SerializedName("id")
    private int idDevice;
    @SerializedName("code")
    private String codeDevice;
    @SerializedName("name")
    private String nameDevice;
    @SerializedName("type")
    private String typeDevice;
    @SerializedName("is_warning")
    private boolean is_warning;
    @SerializedName("image_name")
    private String imageName;
    @SerializedName("is_status")
    private boolean is_status;
    @SerializedName("distance")
    private float distance;
    private String bitmapToString;

    public Device(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getBitmapToString() {
        return bitmapToString;
    }

    public void setBitmapToString(String bitmapToString) {
        this.bitmapToString = bitmapToString;
    }

    public Device(int statusImg, int batteryImg, int imgDevice, String textName, String type, String code) {
        super(statusImg, batteryImg, imgDevice, textName, type);
        this.codeDevice = code;
    }

    public Device(int imgDevice, String textName, String type, int idDevice, String codeDevice, String nameDevice, String typeDevice, boolean is_warning, String imageName,boolean is_status) {
        super(imgDevice, textName, type);
        this.idDevice = idDevice;
        this.codeDevice = codeDevice;
        this.nameDevice = nameDevice;
        this.typeDevice = typeDevice;
        this.is_warning = is_warning;
        this.imageName = imageName;
        this.is_status = is_status;
    }

    public Device(String nameDevice, String typeDevice, String imageName) {
        this.nameDevice = nameDevice;
        this.typeDevice = typeDevice;
        this.imageName = imageName;
    }

    public Device(String codeDevice) {
        this.codeDevice = codeDevice;
    }

    public Device(int idDevice, String codeDevice, String nameDevice, String typeDevice, boolean is_warning, String imageName) {
        this.idDevice = idDevice;
        this.codeDevice = codeDevice;
        this.nameDevice = nameDevice;
        this.typeDevice = typeDevice;
        this.is_warning = is_warning;
        this.imageName = imageName;
    }


    public Device(int statusImg, int batteryImg, int imgDevice, String textName, String type, int idDevice, String codeDevice, String nameDevice, String typeDevice, boolean is_warning, String imageName,boolean is_status) {
        super(statusImg, batteryImg, imgDevice, textName, type);
        this.idDevice = idDevice;
        this.codeDevice = codeDevice;
        this.nameDevice = nameDevice;
        this.typeDevice = typeDevice;
        this.is_warning = is_warning;
        this.imageName = imageName;
        this.is_status = is_status;
    }

    public Device(int idDevice, String codeDevice, String nameDevice, String typeDevice, boolean is_warning, String imageName, boolean is_status) {
        this.idDevice = idDevice;
        this.codeDevice = codeDevice;
        this.nameDevice = nameDevice;
        this.typeDevice = typeDevice;
        this.is_warning = is_warning;
        this.imageName = imageName;
        this.is_status = is_status;
    }

    public Device(int batteryImg) {
        super(batteryImg);
    }

    public Device(int idDevice, String codeDevice, String nameDevice) {
        this.idDevice = idDevice;
        this.codeDevice = codeDevice;
        this.nameDevice = nameDevice;
    }

    public Device(String nameDevice, String typeDevice) {
        this.nameDevice = nameDevice;
        this.typeDevice = typeDevice;
    }

    public String getTypeDevice() {
        return typeDevice;
    }

    public void setTypeDevice(String typeDevice) {
        this.typeDevice = typeDevice;
    }

    public boolean isIs_warning() {
        return is_warning;
    }

    public void setIs_warning(boolean is_warning) {
        this.is_warning = is_warning;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(int idDevice) {
        this.idDevice = idDevice;
    }

    public String getCodeDevice() {
        return codeDevice;
    }

    public void setCodeDevice(String codeDevice) {
        this.codeDevice = codeDevice;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public boolean isIs_status() {
        return is_status;
    }

    public void setIs_status(boolean is_status) {
        this.is_status = is_status;
    }

    @NonNull

    @Override
    public String toString() {
        return "Device{" +
                "idDevice=" + idDevice +
                ", codeDevice='" + codeDevice + '\'' +
                ", nameDevice='" + nameDevice + '\'' +
                ", typeDevice='" + typeDevice + '\'' +
                ", is_warning=" + is_warning +
                ", imageName='" + imageName + '\'' +
                ", is_status=" + is_status +
                '}';
    }
}
