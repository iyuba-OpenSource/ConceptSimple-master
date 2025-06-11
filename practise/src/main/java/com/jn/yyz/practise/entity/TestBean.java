package com.jn.yyz.practise.entity;

/**
 * 试题提交
 */
public class TestBean {

    private int testId;

    private int testIndex;

    private int score;

    private String userAnswer;

    /**
     * 做题时间
     */
    private String testTime;

    private String lessonId;


    public TestBean(int testId, int testIndex, int score, String userAnswer, String testTime, String lessonId) {
        this.testId = testId;
        this.testIndex = testIndex;
        this.score = score;
        this.userAnswer = userAnswer;
        this.testTime = testTime;
        this.lessonId = lessonId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }



    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getTestIndex() {
        return testIndex;
    }

    public void setTestIndex(int testIndex) {
        this.testIndex = testIndex;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }
}
