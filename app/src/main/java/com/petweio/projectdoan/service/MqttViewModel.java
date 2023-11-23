package com.petweio.projectdoan.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class MqttViewModel extends ViewModel {
    private final MutableLiveData<MqttAndroidClient> mqttData = new MutableLiveData<>();

    public void setMqttData(MqttAndroidClient data) {
        mqttData.setValue(data);
    }

    public LiveData<MqttAndroidClient> getMqttData() {
        return mqttData;
    }
//    private MqttAndroidClient mqttClient;

//    public MqttAndroidClient getMqttClient() {
//        return mqttClient;
//    }
//
//    public void setMqttClient(MqttAndroidClient mqttClient) {
//        this.mqttClient = mqttClient;
//    }
}