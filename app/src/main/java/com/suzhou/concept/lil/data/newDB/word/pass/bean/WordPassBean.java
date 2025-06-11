package com.suzhou.concept.lil.data.newDB.word.pass.bean;

public class WordPassBean {

    private String type;
    private int bookId;
    private int id;


    //显示数据
    private int rightCount;//正确数
    private int totalCount;//总数
    private int passState;//通过状态

    public WordPassBean(String type, int bookId, int id, int rightCount, int totalCount, int passState) {
        this.type = type;
        this.bookId = bookId;
        this.id = id;
        this.rightCount = rightCount;
        this.totalCount = totalCount;
        this.passState = passState;
    }

    public String getType() {
        return type;
    }

    public int getBookId() {
        return bookId;
    }

    public int getId() {
        return id;
    }

    public int getRightCount() {
        return rightCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPassState() {
        return passState;
    }
}
