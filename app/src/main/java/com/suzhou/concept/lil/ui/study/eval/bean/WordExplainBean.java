package com.suzhou.concept.lil.ui.study.eval.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 单词释义
 */
public class WordExplainBean implements Serializable {


    /**
     * result : 1
     * user_pron :
     * ori_pron :
     * match_idx :
     * insert_id :
     * delete_id :
     * substitute_id :
     * key : miss
     * audio : http://res.iciba.com/resource/amp3/oxford/0/8c/10/8c10932ab2313ddb72014181fba9fc08.mp3
     * pron : mɪs
     * proncode : m%C9%AAs
     * def : （用于姓名或姓之前，对未婚女子的称呼）小姐；女士；失误；
     * sent : [{"number":1,"orig":"\nMoreover, the investment is a very personal act, MISS here is not to say the.\n","trans":"\n而且, 投资是件极其个人的行为, MISS在这里就不多说了.\n"},{"number":2,"orig":"He missed the point of my joke.","trans":"他没有听懂我讲的笑话。"},{"number":3,"orig":"If I don't go to the party, I shall feel I'm missing out.","trans":"我要是不去参加聚会一定觉得损失很大。"}]
     */

    private int result;
    private String user_pron;
    private String ori_pron;
    private String match_idx;
    private String insert_id;
    private String delete_id;
    private String substitute_id;
    private String key;
    private String audio;
    private String pron;
    private String proncode;
    private String def;
    private List<SentBean> sent;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getUser_pron() {
        return user_pron;
    }

    public void setUser_pron(String user_pron) {
        this.user_pron = user_pron;
    }

    public String getOri_pron() {
        return ori_pron;
    }

    public void setOri_pron(String ori_pron) {
        this.ori_pron = ori_pron;
    }

    public String getMatch_idx() {
        return match_idx;
    }

    public void setMatch_idx(String match_idx) {
        this.match_idx = match_idx;
    }

    public String getInsert_id() {
        return insert_id;
    }

    public void setInsert_id(String insert_id) {
        this.insert_id = insert_id;
    }

    public String getDelete_id() {
        return delete_id;
    }

    public void setDelete_id(String delete_id) {
        this.delete_id = delete_id;
    }

    public String getSubstitute_id() {
        return substitute_id;
    }

    public void setSubstitute_id(String substitute_id) {
        this.substitute_id = substitute_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getProncode() {
        return proncode;
    }

    public void setProncode(String proncode) {
        this.proncode = proncode;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public List<SentBean> getSent() {
        return sent;
    }

    public void setSent(List<SentBean> sent) {
        this.sent = sent;
    }

    public static class SentBean {
        /**
         * number : 1
         * orig :
         Moreover, the investment is a very personal act, MISS here is not to say the.
         * trans :
         而且, 投资是件极其个人的行为, MISS在这里就不多说了.

         */

        private int number;
        private String orig;
        private String trans;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getOrig() {
            return orig;
        }

        public void setOrig(String orig) {
            this.orig = orig;
        }

        public String getTrans() {
            return trans;
        }

        public void setTrans(String trans) {
            this.trans = trans;
        }
    }
}
