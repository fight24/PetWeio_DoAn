package com.petweio.projectdoan.service;

import androidx.lifecycle.ViewModel;

import com.petweio.projectdoan.Model.Device;

import java.util.List;

public class MyViewModel extends ViewModel {
    private List<Device> devices;

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
