package com.jica.iruda.model;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;             // uid
    private String name;            // 이름
    private String email;           // 이메일
    private String profileImg;      // 프로필 이미지

    public User() {
    }

    public User(String uid, String name, String email, String profileImg) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profileImg = profileImg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
