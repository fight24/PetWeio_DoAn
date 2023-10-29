package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("id")
    private long idUser;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String userName;
    @SerializedName("password")
    private String password;
    @SerializedName("devices")
    private List<Device> devices;

    public User() {
    }
    public User(long idUser, String email, String userName, String password, List<Device> devices) {
        this.idUser = idUser;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.devices = devices;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(String email, String userName, String password) {
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
