package com.suzhou.concept.lil.ui.study.exercise.bean;

/**
 * 习题提交内容
 */
public class ExerciseMarkBean {

    //用户id
    private int uid;
    //课程id
    private String LessonId;
    //题号
    private int TestNumber;
    //开始时间
    private String BeginTime;
    //结束时间
    private String TestTime;
    //正确答案
    private String RightAnswer;
    //用户答案
    private String UserAnswer;
    //是否正确(1-正确、0-错误)
    private String AnswerResut;
    //app的名称
    private String AppName;

    public ExerciseMarkBean(int uid, String lessonId, int testNumber, String beginTime, String testTime, String rightAnswer, String userAnswer, String answerResut, String appName) {
        this.uid = uid;
        LessonId = lessonId;
        TestNumber = testNumber;
        BeginTime = beginTime;
        TestTime = testTime;
        RightAnswer = rightAnswer;
        UserAnswer = userAnswer;
        AnswerResut = answerResut;
        AppName = appName;
    }

    public int getUid() {
        return uid;
    }

    public String getLessonId() {
        return LessonId;
    }

    public int getTestNumber() {
        return TestNumber;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public String getTestTime() {
        return TestTime;
    }

    public String getRightAnswer() {
        return RightAnswer;
    }

    public String getUserAnswer() {
        return UserAnswer;
    }

    public String getAnswerResut() {
        return AnswerResut;
    }

    public String getAppName() {
        return AppName;
    }
}
