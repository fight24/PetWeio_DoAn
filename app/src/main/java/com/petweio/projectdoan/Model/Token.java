package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

public class Token extends User{
    @SerializedName("token")
    private String tokenValue;

    public Token(String tokenValue) {
        this.tokenValue = tokenValue;
    }
    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Token(String userName, String password, String tokenValue) {
        super(userName, password);
        this.tokenValue = tokenValue;
    }

}
