package com.petweio.projectdoan.service;

import com.petweio.projectdoan.Model.ApiResponse;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.Model.LastProperty;
import com.petweio.projectdoan.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/user")
    Call<List<User>> getAllUser();
    @GET("api/user/{id}")
    Call<User> getUserById(@Path("id") Long id);
    @PUT("api/user/{id}")
    Call<ApiResponse> updateUserById(@Path("id") Long id, @Body User updatedUser);
    @POST("api/login")
    Call<ApiResponse> login(@Body User loginRequest);
    @POST("api/user")
    Call<ApiResponse> signUp(@Body User newUser);
    @GET("api/users/search/{username}")
    Call<User> searchUsersByUsername(@Path("username") String username);
    @POST("api/add-devices-to-user/{id}")
    Call<ApiResponse> addDeviceToUser(@Path("id") Long id);
    @GET("api/user/{username}/devices")
    Call<List<Device>> showDevicesFromUser(@Path("username") String username);
    @POST("api/add-device-to-user/{username}")
    Call<ApiResponse> addDeviceToUserByUserName(@Path("username") String username,@Body Device device);

    @GET("api/latest_property/{code}")
    Call<LastProperty> getLastPropertyByCode(@Path("code") String code);
}
