package com.petweio.projectdoan.service;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.io.Serializable;

public class MqttClientManager implements Serializable {
    private MqttAndroidClient mqttClient;

    public MqttClientManager(MqttAndroidClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public MqttClientManager() {

    }


    public  void setMqttClient(MqttAndroidClient client) {
        this.mqttClient = client;
    }

    public  MqttAndroidClient getMqttClient() {
        return mqttClient;
    }
}
