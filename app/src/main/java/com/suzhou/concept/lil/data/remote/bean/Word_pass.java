package com.suzhou.concept.lil.data.remote.bean;

import java.util.List;

/**
 * 单词进度数据
 */
public class Word_pass {

    /**
     * result : 1
     * mode : 2
     * totalRight : 20
     * msg : Success
     * uid : 12071118
     * dataWrong : [{"TestId":"15","score":0,"userAnswer":"down there","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"}]
     * testMode : W
     * lesson : 281
     * dataRight : [{"TestId":"11","score":1,"userAnswer":"any","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"12","score":1,"userAnswer":"ship","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"21","score":1,"userAnswer":"cinema","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"20","score":1,"userAnswer":"church","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"6","score":1,"userAnswer":"bridge","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"7","score":1,"userAnswer":"Tower Bridge","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"18","score":1,"userAnswer":"classroom","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"9","score":1,"userAnswer":"pass","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"19","score":1,"userAnswer":"park","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"1","score":1,"userAnswer":"London Eye","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"4","score":1,"userAnswer":"together","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"16","score":1,"userAnswer":"Big Ben","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"13","score":1,"userAnswer":"some","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"5","score":1,"userAnswer":"River Thames","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"17","score":1,"userAnswer":"its","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"3","score":1,"userAnswer":"believe","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"14","score":1,"userAnswer":"Westminster Bridge","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"10","score":1,"userAnswer":"binoculars","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"8","score":1,"userAnswer":"really","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"},{"TestId":"2","score":1,"userAnswer":"fun","testMode":"W","LessonId":"16","testTime":"2024-08-14 00:00:00.0","Lesson":"281"}]
     * totalWrong : 1
     */

    private int result;
    private String mode;
    private int totalRight;
    private String msg;
    private int uid;
    private String testMode;
    private String lesson;
    private int totalWrong;
    private List<DataWrongDTO> dataWrong;
    private List<DataRightDTO> dataRight;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(int totalRight) {
        this.totalRight = totalRight;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTestMode() {
        return testMode;
    }

    public void setTestMode(String testMode) {
        this.testMode = testMode;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public int getTotalWrong() {
        return totalWrong;
    }

    public void setTotalWrong(int totalWrong) {
        this.totalWrong = totalWrong;
    }

    public List<DataWrongDTO> getDataWrong() {
        return dataWrong;
    }

    public void setDataWrong(List<DataWrongDTO> dataWrong) {
        this.dataWrong = dataWrong;
    }

    public List<DataRightDTO> getDataRight() {
        return dataRight;
    }

    public void setDataRight(List<DataRightDTO> dataRight) {
        this.dataRight = dataRight;
    }

    public static class DataWrongDTO {
        /**
         * TestId : 15
         * score : 0
         * userAnswer : down there
         * testMode : W
         * LessonId : 16
         * testTime : 2024-08-14 00:00:00.0
         * Lesson : 281
         */

        private String TestId;
        private int score;
        private String userAnswer;
        private String testMode;
        private String LessonId;
        private String testTime;
        private String Lesson;

        public String getTestId() {
            return TestId;
        }

        public void setTestId(String TestId) {
            this.TestId = TestId;
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

        public String getTestMode() {
            return testMode;
        }

        public void setTestMode(String testMode) {
            this.testMode = testMode;
        }

        public String getLessonId() {
            return LessonId;
        }

        public void setLessonId(String LessonId) {
            this.LessonId = LessonId;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public String getLesson() {
            return Lesson;
        }

        public void setLesson(String Lesson) {
            this.Lesson = Lesson;
        }
    }

    public static class DataRightDTO {
        /**
         * TestId : 11
         * score : 1
         * userAnswer : any
         * testMode : W
         * LessonId : 16
         * testTime : 2024-08-14 00:00:00.0
         * Lesson : 281
         */

        private String TestId;
        private int score;
        private String userAnswer;
        private String testMode;
        private String LessonId;
        private String testTime;
        private String Lesson;

        public String getTestId() {
            return TestId;
        }

        public void setTestId(String TestId) {
            this.TestId = TestId;
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

        public String getTestMode() {
            return testMode;
        }

        public void setTestMode(String testMode) {
            this.testMode = testMode;
        }

        public String getLessonId() {
            return LessonId;
        }

        public void setLessonId(String LessonId) {
            this.LessonId = LessonId;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public String getLesson() {
            return Lesson;
        }

        public void setLesson(String Lesson) {
            this.Lesson = Lesson;
        }
    }
}
