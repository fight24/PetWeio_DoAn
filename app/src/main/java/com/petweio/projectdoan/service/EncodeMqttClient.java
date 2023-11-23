package com.petweio.projectdoan.service;

import android.util.Base64;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class EncodeMqttClient {
    public static String encodeMqttClient(MqttAndroidClient mqttClient) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(mqttClient);
            objectOutputStream.close();

            byte[] data = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static MqttAndroidClient convertToMqtt(String s){
        try {
            byte[] data = Base64.decode(s, Base64.DEFAULT);
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
            MqttAndroidClient mqttClient = (MqttAndroidClient) objectInputStream.readObject();
            objectInputStream.close();
            return mqttClient;
            // Bây giờ bạn có đối tượng MqttAndroidClient sẵn sàng sử dụng trong Fragment
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
