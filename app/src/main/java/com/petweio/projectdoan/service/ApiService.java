package com.petweio.projectdoan.service;

import com.petweio.projectdoan.Model.ApiResponse;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.Model.LastProperty;
import com.petweio.projectdoan.Model.Property;
import com.petweio.projectdoan.Model.Token;
import com.petweio.projectdoan.Model.TokenResponse;
import com.petweio.projectdoan.Model.User;
import com.petweio.projectdoan.Model.UserSearch;
import com.petweio.projectdoan.Model.Warning;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    Call<ApiResponse> login(@Body Token loginRequest);
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
    @GET("api/get_50_latest_property/{code}")
    Call<List<Property>> get50PropertyByCode(@Path("code") String code);
    @GET("api/get_50_latest_properties")
    Call<List<Property>> get50Property();
    @GET("api/properties")
    Call<List<Property>> getProperties();
    @GET("api/propertiesbytime")
    Call<List<Property>> getPropertiesByTime();
    @GET("api/login-with-token/{token}")
    Call<TokenResponse> loginWithToken(@Path("token") String token);
    @DELETE("api/delete-token-by-name/{username}")
    Call<ApiResponse> deleteTokenByName(@Path("username")String userName);
    @GET("api/user/search/{username}")
    Call<List<UserSearch>> searchUser(@Path("username") String userName);

    @PUT("api/device/{device_id}/warning")
    Call<ApiResponse> updateDeviceWarning(@Path("device_id") int deviceId, @Body Warning isWarning);
    @PUT("api/device/{device_id}/info")
    Call<ApiResponse> updateDeviceInfo(@Path("device_id") int deviceId, @Body Device deviceInfo);
    @PUT("api/device/{device_id}/warning_distance")
    Call<ApiResponse> updateDeviceWarningDistance(@Path("device_id") int deviceId, @Body Warning isWarning);
    @DELETE("api/remove-device-from-user/{username}/{deviceCode}")
    Call<ApiResponse> removeDeviceFromUser(@Path("username") String username,@Path("deviceCode") String deviceCode);
}
