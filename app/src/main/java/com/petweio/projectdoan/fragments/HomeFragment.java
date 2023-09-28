package com.petweio.projectdoan.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.petweio.projectdoan.R;
import com.petweio.projectdoan.service.MqttClientManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";


    private static final String ARG_PARAM_MQTT = "MQTT";

    private MqttClientManager mqttClient = new MqttClientManager();
    private MqttAndroidClient mqttAndroidClient;
    TextView textView;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(MqttClientManager client) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_MQTT, client);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            mqttClient =(MqttClientManager) this.getArguments().getSerializable(ARG_PARAM_MQTT);
            assert mqttClient != null;
            mqttAndroidClient = mqttClient.getMqttClient();
            Log.d(TAG, "OK");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        init(rootView);
        return rootView;
    }
    private void init(View rootView){
      textView = rootView.findViewById(R.id.tv_param);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String messageToString = new String(message.getPayload());
                    String msg = "Topic: " + topic + " Message: " +messageToString;
                    textView.setText(msg);

                    Log.e(TAG, msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }catch (Exception e) {
            Log.e(TAG,"Exception" + e);
        }

    }
}