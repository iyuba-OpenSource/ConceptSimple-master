package com.jn.yyz.practise.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExamBean {


    @SerializedName("result")
    private int result;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private List<DataDTO> data;
    @SerializedName("pron")
    private List<?> pron;
    @SerializedName("lessonId")
    private int lessonId;


    //记录加载的页数
    private int pageNumber;


    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public List<?> getPron() {
        return pron;
    }

    public void setPron(List<?> pron) {
        this.pron = pron;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public static class DataDTO {
        @SerializedName("explain")
        private String explain;
        @SerializedName("flg")
        private String flg;
        @SerializedName("question")
        private String question;
        @SerializedName("testCategory")
        private String testCategory;
        @SerializedName("answer3")
        private String answer3;
        @SerializedName("lessonId")
        private int lessonId;
        @SerializedName("testType")
        private int testType;
        @SerializedName("answer2")
        private String answer2;
        @SerializedName("answer5")
        private String answer5;
        @SerializedName("pic")
        private String pic;
        @SerializedName("answer4")
        private String answer4;
        @SerializedName("answer1")
        private String answer1;
        @SerializedName("testIndex")
        private int testIndex;
        @SerializedName("tags")
        private String tags;
        @SerializedName("sounds")
        private String sounds;
        @SerializedName("answer")
        private String answer;
        @SerializedName("testMode")
        private int testMode;
        @SerializedName("testId")
        private int testId;
        @SerializedName("id")
        private int id;
        @SerializedName("attach")
        private String attach;
        @SerializedName("category")
        private String category;
        @SerializedName("lessonType")
        private String lessonType;


        /**
         *正确的标记   1:正确  0：错误
         */
        private int correctFlag = -1;
        /**
         * 用户答案
         */
        private String userAnswer;

        private String testTime;


        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public int getCorrectFlag() {
            return correctFlag;
        }

        public void setCorrectFlag(int correctFlag) {
            this.correctFlag = correctFlag;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public String getFlg() {
            return flg;
        }

        public void setFlg(String flg) {
            this.flg = flg;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getTestCategory() {
            return testCategory;
        }

        public void setTestCategory(String testCategory) {
            this.testCategory = testCategory;
        }

        public String getAnswer3() {
            return answer3;
        }

        public void setAnswer3(String answer3) {
            this.answer3 = answer3;
        }

        public int getLessonId() {
            return lessonId;
        }

        public void setLessonId(int lessonId) {
            this.lessonId = lessonId;
        }

        public int getTestType() {
            return testType;
        }

        public void setTestType(int testType) {
            this.testType = testType;
        }

        public String getAnswer2() {
            return answer2;
        }

        public void setAnswer2(String answer2) {
            this.answer2 = answer2;
        }

        public String getAnswer5() {
            return answer5;
        }

        public void setAnswer5(String answer5) {
            this.answer5 = answer5;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getAnswer4() {
            return answer4;
        }

        public void setAnswer4(String answer4) {
            this.answer4 = answer4;
        }

        public String getAnswer1() {
            return answer1;
        }

        public void setAnswer1(String answer1) {
            this.answer1 = answer1;
        }

        public int getTestIndex() {
            return testIndex;
        }

        public void setTestIndex(int testIndex) {
            this.testIndex = testIndex;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getSounds() {
            return sounds;
        }

        public void setSounds(String sounds) {
            this.sounds = sounds;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public int getTestMode() {
            return testMode;
        }

        public void setTestMode(int testMode) {
            this.testMode = testMode;
        }

        public int getTestId() {
            return testId;
        }

        public void setTestId(int testId) {
            this.testId = testId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAttach() {
            return attach;
        }

        public void setAttach(String attach) {
            this.attach = attach;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getLessonType() {
            return lessonType;
        }

        public void setLessonType(String lessonType) {
            this.lessonType = lessonType;
        }
    }
}
