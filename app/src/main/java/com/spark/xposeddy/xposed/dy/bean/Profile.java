package com.spark.xposeddy.xposed.dy.bean;

/**
 * 达人信息
 */
public class Profile {
    private String uid;
    private String secUid;

    public Profile() {
    }

    public Profile(String uid, String secUid) {
        this.uid = uid;
        this.secUid = secUid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setSecUid(String secUid) {
        this.secUid = secUid;
    }

    public String getUid() {
        return uid;
    }

    public String getSecUid() {
        return secUid;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "uid='" + uid + '\'' +
                ", secUid='" + secUid + '\'' +
                '}';
    }
}
