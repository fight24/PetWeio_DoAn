package com.petweio.projectdoan.Model;

public class Device {
    private int idDevice;
    private String codeDevice;
    private String nameDevice;

    public Device() {
    }

    public Device(int idDevice, String codeDevice, String nameDevice) {
        this.idDevice = idDevice;
        this.codeDevice = codeDevice;
        this.nameDevice = nameDevice;
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
}
