package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

public class UserSearch {
    @SerializedName("id")
    private long idUser;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String userName;
    @SerializedName("password")
    private String password;

    public UserSearch(long idUser, String email, String userName, String password) {
        this.idUser = idUser;
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
}
