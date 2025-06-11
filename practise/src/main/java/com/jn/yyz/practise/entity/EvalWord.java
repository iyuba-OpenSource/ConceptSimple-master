package com.jn.yyz.practise.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/**
 * 评测单词
 */
public class EvalWord {
    @NonNull
    private String voaid;
    @NonNull
    private String paraid;
    @NonNull
    private String senIndex;
    @NonNull
    private int level;
    @NonNull
    private int bookid;
    @NonNull
    private String type;
    @NonNull
    private int chapter_order;

    @NonNull
    @SerializedName("index")
    private String index;
    @SerializedName("content")
    private String content;
    @SerializedName("pron")
    private String pron;
    @SerializedName("pron2")
    private String pron2;
    @SerializedName("user_pron")
    private String userPron;
    @SerializedName("user_pron2")
    private String userPron2;
    @SerializedName("score")
    private String score;
    @SerializedName("insert")
    private String insert;
    @SerializedName("delete")
    private String delete;
    @SerializedName("substitute_orgi")
    private String substituteOrgi;
    @SerializedName("substitute_user")
    private String substituteUser;


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChapter_order() {
        return chapter_order;
    }

    public void setChapter_order(int chapter_order) {
        this.chapter_order = chapter_order;
    }

    public String getSenIndex() {
        return senIndex;
    }

    public void setSenIndex(String senIndex) {
        this.senIndex = senIndex;
    }

    public String getVoaid() {
        return voaid;
    }

    public void setVoaid(String voaid) {
        this.voaid = voaid;
    }

    public String getParaid() {
        return paraid;
    }

    public void setParaid(String paraid) {
        this.paraid = paraid;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getPron2() {
        return pron2;
    }

    public void setPron2(String pron2) {
        this.pron2 = pron2;
    }

    public String getUserPron() {
        return userPron;
    }

    public void setUserPron(String userPron) {
        this.userPron = userPron;
    }

    public String getUserPron2() {
        return userPron2;
    }

    public void setUserPron2(String userPron2) {
        this.userPron2 = userPron2;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getInsert() {
        return insert;
    }

    public void setInsert(String insert) {
        this.insert = insert;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getSubstituteOrgi() {
        return substituteOrgi;
    }

    public void setSubstituteOrgi(String substituteOrgi) {
        this.substituteOrgi = substituteOrgi;
    }

    public String getSubstituteUser() {
        return substituteUser;
    }

    public void setSubstituteUser(String substituteUser) {
        this.substituteUser = substituteUser;
    }
}
