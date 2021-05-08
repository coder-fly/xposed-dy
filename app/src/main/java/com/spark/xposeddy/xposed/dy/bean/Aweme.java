package com.spark.xposeddy.xposed.dy.bean;

/**
 * 视频信息
 */
public class Aweme {
    private String awemeId;
    private String uid;

    public Aweme() {
    }

    public Aweme(String awemeId, String uid) {
        this.awemeId = awemeId;
        this.uid = uid;
    }

    public void setAwemeId(String awemeId) {
        this.awemeId = awemeId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAwemeId() {
        return awemeId;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "Aweme{" +
                "awemeId='" + awemeId + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
