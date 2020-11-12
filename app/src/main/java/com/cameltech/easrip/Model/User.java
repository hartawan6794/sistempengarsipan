package com.cameltech.easrip.Model;

import java.io.Serializable;

public class User implements Serializable {

    String key;
    String email;
    String username;
    String userImage;
    String userLvl;

    public User() {

    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUserNama(String username) {
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setImageUser(String imageUser) {
        this.userImage = imageUser;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserLvl() {
        return userLvl;
    }

    public void setUserLvl(String userLvl) {
        this.userLvl = userLvl;
    }

    public User(String key, String email, String userLvl, String userNama, String image) {
        this.key = key;
        this.email = email;
        this.userLvl = userLvl;
        this.username = userNama;
        this.userImage = image;
    }
}
