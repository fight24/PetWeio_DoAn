package com.petweio.projectdoan.Model;

import java.util.List;

public class User {
    private int idUser;
    private String email;
    private String userName;
    private String password;
    private List<Device> devices;

    public User() {
    }

    public User(int idUser, String email, String userName, String password, List<Device> devices) {
        this.idUser = idUser;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.devices = devices;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
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
