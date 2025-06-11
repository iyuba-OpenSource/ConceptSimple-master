package com.jn.yyz.practise.model.bean;

import com.google.gson.annotations.SerializedName;

public class UploadTestBean {


    @SerializedName("result")
    private int result;
    @SerializedName("message")
    private String message;

    private String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UploadTestBean{" +
                "result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
