package com.suzhou.concept.lil.data.remote.bean;

import java.util.List;

/**
 * 学习报告-单词闯关-提交数据
 */
public class Report_wordBreak_submit {

    /**
     * DeviceId : 02:00:00:00:00:00
     * appId : 260
     * format : json
     * lesson : 206
     * mode : 2
     * scoreList : []
     * sign : 2b1049f6a06b6f8193b5a92994e49857
     * testList : [{"AnswerResut":0,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"chair","TestId":1,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"chair"},{"AnswerResut":0,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"where","TestId":7,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"where"},{"AnswerResut":1,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"desk","TestId":2,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"desk"},{"AnswerResut":0,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"on","TestId":4,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"on"},{"AnswerResut":0,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"under","TestId":6,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"under"},{"AnswerResut":0,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"in","TestId":5,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"in"},{"AnswerResut":0,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"the","TestId":8,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"the"},{"AnswerResut":0,"BeginTime":"2024-04-08","Category":"单词闯关","LessonId":"1","RightAnswer":"blackboard","TestId":3,"TestMode":"W","TestTime":"2024-04-08","UserAnswer":"blackboard"}]
     * uid : 15351268
     */

    private String DeviceId;
    private int appId;
    private String format;
    private String lesson;
    private int mode;
    private String sign;
    private String uid;
    private List<?> scoreList;
    private List<TestListBean> testList;

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String DeviceId) {
        this.DeviceId = DeviceId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<?> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<?> scoreList) {
        this.scoreList = scoreList;
    }

    public List<TestListBean> getTestList() {
        return testList;
    }

    public void setTestList(List<TestListBean> testList) {
        this.testList = testList;
    }

    public static class TestListBean {
        /**
         * AnswerResut : 0
         * BeginTime : 2024-04-08
         * Category : 单词闯关
         * LessonId : 1
         * RightAnswer : chair
         * TestId : 1
         * TestMode : W
         * TestTime : 2024-04-08
         * UserAnswer : chair
         */

        private int AnswerResut;
        private String BeginTime;
        private String Category;
        private String LessonId;
        private String RightAnswer;
        private int TestId;
        private String TestMode;
        private String TestTime;
        private String UserAnswer;

        public TestListBean() {
        }

        public TestListBean(int answerResut, String beginTime, String category, String lessonId, String rightAnswer, int testId, String testMode, String testTime, String userAnswer) {
            AnswerResut = answerResut;
            BeginTime = beginTime;
            Category = category;
            LessonId = lessonId;
            RightAnswer = rightAnswer;
            TestId = testId;
            TestMode = testMode;
            TestTime = testTime;
            UserAnswer = userAnswer;
        }

        public int getAnswerResut() {
            return AnswerResut;
        }

        public void setAnswerResut(int AnswerResut) {
            this.AnswerResut = AnswerResut;
        }

        public String getBeginTime() {
            return BeginTime;
        }

        public void setBeginTime(String BeginTime) {
            this.BeginTime = BeginTime;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public String getLessonId() {
            return LessonId;
        }

        public void setLessonId(String LessonId) {
            this.LessonId = LessonId;
        }

        public String getRightAnswer() {
            return RightAnswer;
        }

        public void setRightAnswer(String RightAnswer) {
            this.RightAnswer = RightAnswer;
        }

        public int getTestId() {
            return TestId;
        }

        public void setTestId(int TestId) {
            this.TestId = TestId;
        }

        public String getTestMode() {
            return TestMode;
        }

        public void setTestMode(String TestMode) {
            this.TestMode = TestMode;
        }

        public String getTestTime() {
            return TestTime;
        }

        public void setTestTime(String TestTime) {
            this.TestTime = TestTime;
        }

        public String getUserAnswer() {
            return UserAnswer;
        }

        public void setUserAnswer(String UserAnswer) {
            this.UserAnswer = UserAnswer;
        }

        @Override
        public String toString() {
            return "TestListBean{" +
                    "AnswerResut=" + AnswerResut +
                    ", BeginTime='" + BeginTime + '\'' +
                    ", Category='" + Category + '\'' +
                    ", LessonId='" + LessonId + '\'' +
                    ", RightAnswer='" + RightAnswer + '\'' +
                    ", TestId=" + TestId +
                    ", TestMode='" + TestMode + '\'' +
                    ", TestTime='" + TestTime + '\'' +
                    ", UserAnswer='" + UserAnswer + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Report_wordBreak_submit{" +
                "DeviceId='" + DeviceId + '\'' +
                ", appId=" + appId +
                ", format='" + format + '\'' +
                ", lesson='" + lesson + '\'' +
                ", mode=" + mode +
                ", sign='" + sign + '\'' +
                ", uid='" + uid + '\'' +
                ", scoreList=" + scoreList +
                ", testList=" + testList +
                '}';
    }
}
