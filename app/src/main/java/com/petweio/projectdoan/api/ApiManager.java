package com.petweio.projectdoan.api;

import com.petweio.projectdoan.service.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static final String BASE_URL = "https://dcbc-2405-4802-1d12-a520-d7d9-40d0-a9-84cc.ngrok-free.app/";
    private final ApiService myApiService;
    private static ApiManager instance;

    public ApiManager() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("X-Api-Key", "api_promax");

            Request modifiedRequest = requestBuilder.build();
            return chain.proceed(modifiedRequest);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        myApiService = retrofit.create(ApiService.class);
    }
    public static synchronized ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }
    public ApiService getMyApiService() {
        return myApiService;
    }
}
