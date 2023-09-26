package com.petweio.projectdoan.service;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.io.Serializable;

public class MqttClientManager implements Serializable {
    private static MqttAndroidClient mqttClient;

    public static void setMqttClient(MqttAndroidClient client) {
        mqttClient = client;
    }

    public static MqttAndroidClient getMqttClient() {
        return mqttClient;
    }
}
