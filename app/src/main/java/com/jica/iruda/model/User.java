package com.jica.iruda.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;             // uid
    private String name;            // 이름
    private String email;           // 이메일
    private String profileImg;      // 프로필 이미지

    public User() {
    }

    public User(String id, String name, String email, String profileImg) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImg = profileImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
