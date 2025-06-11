package com.jn.yyz.practise.model.bean;

import com.google.gson.annotations.SerializedName;

public class ExpBean {


    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private int result;

    private int score;

    private int srid;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "ExpBean{" +
                "msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}
