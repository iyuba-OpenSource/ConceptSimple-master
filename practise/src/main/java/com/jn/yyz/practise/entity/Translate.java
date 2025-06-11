package com.jn.yyz.practise.entity;

public class Translate {

    private String data;

    private boolean isCheck = false;


    public Translate(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
