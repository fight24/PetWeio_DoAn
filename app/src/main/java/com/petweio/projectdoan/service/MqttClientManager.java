package com.petweio.projectdoan.service;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;

public class MqttClientManager {
    private static final String TAG ="[MqttClientManager]" ;
    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";// "tcp://namcu.ddns.net:1883"
    private static final String CLIENT_ID = "your_client_id";

    MqttConnectOptions mqttConnectOptions;
    MqttAndroidClient mqttAndroidClient;
    Context context;

    public MqttClientManager(Context context) {
        this.mqttAndroidClient = new MqttAndroidClient(context,BROKER_URL,CLIENT_ID);
        this.mqttConnectOptions = new MqttConnectOptions();
        this.mqttConnectOptions.setCleanSession(true);
        this.mqttConnectOptions.setAutomaticReconnect(true);
        this.context = context;

    }

    public MqttConnectOptions getMqttConnectOptions() {
        return mqttConnectOptions;
    }

    public void setMqttConnectOptions(MqttConnectOptions mqttConnectOptions) {
        this.mqttConnectOptions = mqttConnectOptions;
    }

    public void connectMqtt(){
        try {
            IMqttToken token = this.mqttAndroidClient.connect(this.mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected");
//                    mqttSub(mqttAndroidClient,new String[]{"device01,device02,device03"},new int[]{1,1,1});
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to connect");

                }
            });

        }catch (MqttException e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

    }
    public void subscribeMqtt(String topic,int qos){
        if(mqttAndroidClient.isConnected()){
            try {
                mqttAndroidClient.subscribe(topic,qos);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void unsubscribe(String topic){
        if(mqttAndroidClient.isConnected()){
            try {
                mqttAndroidClient.unsubscribe(topic);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void subscribeAll(String[] topic,int[] qos){
        if(mqttAndroidClient.isConnected()){
            try {
                mqttAndroidClient.subscribe(topic,qos);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void publishMqtt(String topic, String message){
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        if(mqttAndroidClient.isConnected()){
            try {
                mqttAndroidClient.publish(topic, mqttMessage);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MqttAndroidClient getMqttAndroidClient() {
        return mqttAndroidClient;
    }

    public void setMqttAndroidClient(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }

    public void disconnectMqtt(){
        setContext(null);
        if(mqttAndroidClient.isConnected()){
            try {
                mqttAndroidClient.disconnect();

            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
