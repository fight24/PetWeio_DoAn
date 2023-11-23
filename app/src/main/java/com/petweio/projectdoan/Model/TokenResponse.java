package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("id")
    private long id;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("user_name")
    private String userName;

    public TokenResponse(long id, long userId, String userName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
