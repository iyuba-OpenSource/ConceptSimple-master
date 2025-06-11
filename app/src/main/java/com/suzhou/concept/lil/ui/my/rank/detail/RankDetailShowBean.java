package com.suzhou.concept.lil.ui.my.rank.detail;

public class RankDetailShowBean {

    public interface ShowType{
        String listen = "listen";//听力
        String speech = "speech";//口语
        String read = "read";//阅读
        String exercise = "exercise";//练习
    }

    private int rankIndex;//排行
    private String imageUrl;//图片链接
    private String showName;//显示名称
    private String showType;//显示类型


    /***********听力**************/
    private long listenArticleCount;//文章数
    private long listenWordsCount;//单词数
    private long listenTime;//时间(分钟)

    /***********口语**************/
    private long speechSentenceCount;//句子数
    private long speechTotalScore;//总分
    private double speechAverageScore;//平均分

    /***********阅读**************/
    private long readArticleCount;//文章数
    private long readWordsCount;//单词数
    private long readWpm;//阅读的wpm

    /***********练习**************/
    private long exerciseTotalCount;//总题数
    private long exerciseRightCount;//正确数
    private double exerciseRightRate;//正确率


    //设置需要的数据

    public int getRankIndex() {
        return rankIndex;
    }

    public void setRankIndex(int rankIndex) {
        this.rankIndex = rankIndex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public long getListenArticleCount() {
        return listenArticleCount;
    }

    public void setListenArticleCount(long listenArticleCount) {
        this.listenArticleCount = listenArticleCount;
    }

    public long getListenWordsCount() {
        return listenWordsCount;
    }

    public void setListenWordsCount(long listenWordsCount) {
        this.listenWordsCount = listenWordsCount;
    }

    public long getListenTime() {
        return listenTime;
    }

    public void setListenTime(long listenTime) {
        this.listenTime = listenTime;
    }

    public long getSpeechSentenceCount() {
        return speechSentenceCount;
    }

    public void setSpeechSentenceCount(long speechSentenceCount) {
        this.speechSentenceCount = speechSentenceCount;
    }

    public long getSpeechTotalScore() {
        return speechTotalScore;
    }

    public void setSpeechTotalScore(long speechTotalScore) {
        this.speechTotalScore = speechTotalScore;
    }

    public double getSpeechAverageScore() {
        return speechAverageScore;
    }

    public void setSpeechAverageScore(double speechAverageScore) {
        this.speechAverageScore = speechAverageScore;
    }

    public long getReadArticleCount() {
        return readArticleCount;
    }

    public void setReadArticleCount(long readArticleCount) {
        this.readArticleCount = readArticleCount;
    }

    public long getReadWordsCount() {
        return readWordsCount;
    }

    public void setReadWordsCount(long readWordsCount) {
        this.readWordsCount = readWordsCount;
    }

    public long getReadWpm() {
        return readWpm;
    }

    public void setReadWpm(long readWpm) {
        this.readWpm = readWpm;
    }

    public long getExerciseTotalCount() {
        return exerciseTotalCount;
    }

    public void setExerciseTotalCount(long exerciseTotalCount) {
        this.exerciseTotalCount = exerciseTotalCount;
    }

    public long getExerciseRightCount() {
        return exerciseRightCount;
    }

    public void setExerciseRightCount(long exerciseRightCount) {
        this.exerciseRightCount = exerciseRightCount;
    }

    public double getExerciseRightRate() {
        return exerciseRightRate;
    }

    public void setExerciseRightRate(double exerciseRightRate) {
        this.exerciseRightRate = exerciseRightRate;
    }
}
