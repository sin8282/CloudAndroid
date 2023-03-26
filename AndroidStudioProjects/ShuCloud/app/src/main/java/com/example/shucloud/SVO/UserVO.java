package com.example.shucloud.SVO;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("userId")
    private String userId;
    @SerializedName("userEmail")
    private String userEmail;
    @SerializedName("name")
    private String name;
    @SerializedName("userProfile")
    private String userProfile;
    @SerializedName("msg")
    private boolean msg;

    public UserVO(String userId, String userEmail, String name, String userProfile) {

        this.userId = userId;
        this.userEmail = userEmail;
        this.name = name;
        this.userProfile = userProfile;

    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUserProfile() {
        return userProfile;
    }
    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }
    public boolean isMsg() {
        return msg;
    }
    public void setMsg(boolean msg) {
        this.msg = msg;
    }

    @NonNull
    @Override
    public String toString() {
        return "이메일 :: " + getUserEmail()
                + ", 아이디 :: " + getUserId();
    }

}
