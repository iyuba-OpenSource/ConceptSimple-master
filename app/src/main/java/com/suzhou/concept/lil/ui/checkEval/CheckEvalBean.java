package com.suzhou.concept.lil.ui.checkEval;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @title:
 * @date: 2023/10/10 13:59
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class CheckEvalBean {

    /**
     * result : 1
     * message : SCORE OK!
     * data : {"sentence":"have","words":[{"index":"0","content":"have","pron":"HH AE V","pron2":"hæv","user_pron":"T M UW M","user_pron2":"tmuːm","score":"0.0","insert":"T","delete":"","substitute_orgi":"HH AE V","substitute_user":"M UW M"}],"scores":0,"total_score":0,"filepath":"/data/projects/voa/mp34/202310/primaryenglish/20231010/16969175097613774.mp3","URL":"wav10/202310/primaryenglish/20231010/16969175097613774.mp3"}
     */

    private String result;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * sentence : have
         * words : [{"index":"0","content":"have","pron":"HH AE V","pron2":"hæv","user_pron":"T M UW M","user_pron2":"tmuːm","score":"0.0","insert":"T","delete":"","substitute_orgi":"HH AE V","substitute_user":"M UW M"}]
         * scores : 0
         * total_score : 0.0
         * filepath : /data/projects/voa/mp34/202310/primaryenglish/20231010/16969175097613774.mp3
         * URL : wav10/202310/primaryenglish/20231010/16969175097613774.mp3
         */

        private String sentence;
        private int scores;
        private double total_score;
        private String filepath;
        @SerializedName("URL")
        private String url;
        private List<WordsBean> words;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public int getScores() {
            return scores;
        }

        public void setScores(int scores) {
            this.scores = scores;
        }

        public double getTotal_score() {
            return total_score;
        }

        public void setTotal_score(double total_score) {
            this.total_score = total_score;
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

        public List<WordsBean> getWords() {
            return words;
        }

        public void setWords(List<WordsBean> words) {
            this.words = words;
        }

        public static class WordsBean {
            /**
             * index : 0
             * content : have
             * pron : HH AE V
             * pron2 : hæv
             * user_pron : T M UW M
             * user_pron2 : tmuːm
             * score : 0.0
             * insert : T
             * delete :
             * substitute_orgi : HH AE V
             * substitute_user : M UW M
             */

            private String index;
            private String content;
            private String pron;
            private String pron2;
            private String user_pron;
            private String user_pron2;
            private String score;
            private String insert;
            private String delete;
            private String substitute_orgi;
            private String substitute_user;

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

            public String getUser_pron() {
                return user_pron;
            }

            public void setUser_pron(String user_pron) {
                this.user_pron = user_pron;
            }

            public String getUser_pron2() {
                return user_pron2;
            }

            public void setUser_pron2(String user_pron2) {
                this.user_pron2 = user_pron2;
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

            public String getSubstitute_orgi() {
                return substitute_orgi;
            }

            public void setSubstitute_orgi(String substitute_orgi) {
                this.substitute_orgi = substitute_orgi;
            }

            public String getSubstitute_user() {
                return substitute_user;
            }

            public void setSubstitute_user(String substitute_user) {
                this.substitute_user = substitute_user;
            }
        }
    }
}
