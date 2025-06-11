package com.jn.yyz.practise.model.bean;

import com.google.gson.annotations.SerializedName;
import com.jn.yyz.practise.entity.EvalWord;

import java.util.List;

public class EvalBean {


    @SerializedName("result")
    private String result;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private DataDTO data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("sentence")
        private String sentence;
        @SerializedName("words")
        private List<EvalWord> words;
        @SerializedName("scores")
        private int scores;
        @SerializedName("total_score")
        private double totalScore;
        @SerializedName("filepath")
        private String filepath;
        @SerializedName("URL")
        private String url;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public List<EvalWord> getWords() {
            return words;
        }

        public void setWords(List<EvalWord> words) {
            this.words = words;
        }

        public int getScores() {
            return scores;
        }

        public void setScores(int scores) {
            this.scores = scores;
        }

        public double getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(double totalScore) {
            this.totalScore = totalScore;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}
