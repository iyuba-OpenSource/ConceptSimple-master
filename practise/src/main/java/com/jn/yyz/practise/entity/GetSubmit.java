package com.jn.yyz.practise.entity;

public class GetSubmit {


    private String uid;

    private int srid;

    private String appid;

    private String lessonId;

    private int credits;

    private String sign;


    public GetSubmit(String uid, int srid, String appid, String lessonId, int credits, String sign) {
        this.uid = uid;
        this.srid = srid;
        this.appid = appid;
        this.lessonId = lessonId;
        this.credits = credits;
        this.sign = sign;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
