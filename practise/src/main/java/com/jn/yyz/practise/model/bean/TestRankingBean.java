package com.jn.yyz.practise.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestRankingBean {


    private int page;

    @SerializedName("totalTest")
    private String totalTest;
    @SerializedName("totalRight")
    private String totalRight;
    @SerializedName("myExp")
    private int myExp;
    @SerializedName("myid")
    private int myid;
    @SerializedName("myusername")
    private String myusername;
    @SerializedName("myImgSrc")
    private String myImgSrc;
    @SerializedName("myranking")
    private int myranking;
    @SerializedName("result")
    private int result;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<DataDTO> data;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getTotalTest() {
        return totalTest;
    }

    public void setTotalTest(String totalTest) {
        this.totalTest = totalTest;
    }

    public String getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(String totalRight) {
        this.totalRight = totalRight;
    }

    public int getMyExp() {
        return myExp;
    }

    public void setMyExp(int myExp) {
        this.myExp = myExp;
    }

    public int getMyid() {
        return myid;
    }

    public void setMyid(int myid) {
        this.myid = myid;
    }

    public String getMyusername() {
        return myusername;
    }

    public void setMyusername(String myusername) {
        this.myusername = myusername;
    }

    public String getMyImgSrc() {
        return myImgSrc;
    }

    public void setMyImgSrc(String myImgSrc) {
        this.myImgSrc = myImgSrc;
    }

    public int getMyranking() {
        return myranking;
    }

    public void setMyranking(int myranking) {
        this.myranking = myranking;
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

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("uid")
        private String uid;
        @SerializedName("totalTest")
        private String totalTest;
        @SerializedName("totalRight")
        private String totalRight;
        @SerializedName("exp")
        private int exp;
        @SerializedName("ranking")
        private int ranking;
        @SerializedName("username")
        private String username;
        @SerializedName("image")
        private String image;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTotalTest() {
            return totalTest;
        }

        public void setTotalTest(String totalTest) {
            this.totalTest = totalTest;
        }

        public String getTotalRight() {
            return totalRight;
        }

        public void setTotalRight(String totalRight) {
            this.totalRight = totalRight;
        }

        public int getExp() {
            return exp;
        }

        public void setExp(int exp) {
            this.exp = exp;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
