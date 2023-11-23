package com.petweio.projectdoan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.api.ApiManager;
import com.petweio.projectdoan.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAppCompatActivity extends AppCompatActivity {
    public ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Ẩn mũi tên "Back" trên ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        apiService = ApiManager.getInstance().getMyApiService();
        Log.d(getString(R.string.DEBUG_TAG),"OK ");



    }

    private void showDevice(String name){
        Call<List<Device>> call = apiService.showDevicesFromUser(name);

        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(@NonNull Call<List<Device>> call, @NonNull Response<List<Device>> response) {
                if(response.isSuccessful()){
                    List<Device> deviceList = response.body();
                    assert response.body() != null;
                    if(deviceList != null) {
                        for(Device device : deviceList){

                        }
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Device>> call, @NonNull Throwable t) {

            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
