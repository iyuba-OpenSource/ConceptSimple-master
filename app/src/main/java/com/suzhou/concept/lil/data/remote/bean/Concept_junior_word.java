package com.suzhou.concept.lil.data.remote.bean;

import java.io.Serializable;

/**
 * @title: 新概念-青少版-单词数据
 * @date: 2023/5/11 18:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Concept_junior_word implements Serializable {
    private static final long serialVersionUID = 5395210287164229295L;


    /**
     * def : n.逃学的孩子
     * updateTime : 2020-03-18 14:30:30.0
     * book_id : 289
     * version : 0
     * examples : 1
     * videoUrl : http://static2.iyuba.cn/video/321/321209/321209_2_1.mp4
     * pron : ˈtru:ənt
     * voa_id : 321209
     * idindex : 2
     * audio : http://res.iciba.com/resource/amp3/oxford/0/ed/da/eddaa40bc75dd6cc6cc8b12161fdeb9f.mp3
     * position : 1
     * Sentence_cn : 逃学的孩子们都缺乏想像力。
     * pic_url : 289/25/1.jpg
     * unit_id : 25
     * word : truant
     * Sentence : Children who play truant from school are unimaginative.
     * Sentence_audio : http://staticvip.iyuba.cn/sounds/voa/sentence/202005/321209/321209_2_1.wav
     */

    private String def;
    private String updateTime;
    private String book_id;
    private String version;
    private String examples;
    private String videoUrl;
    private String pron;
    private String voa_id;
    private String idindex;
    private String audio;
    private String position;
    private String Sentence_cn;
    private String pic_url;
    private String unit_id;
    private String word;
    private String Sentence;
    private String Sentence_audio;

    public String getDef() {
        return def;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getVersion() {
        return version;
    }

    public String getExamples() {
        return examples;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getPron() {
        return pron;
    }

    public String getVoa_id() {
        return voa_id;
    }

    public String getIdindex() {
        return idindex;
    }

    public String getAudio() {
        return audio;
    }

    public String getPosition() {
        return position;
    }

    public String getSentence_cn() {
        return Sentence_cn;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public String getWord() {
        return word;
    }

    public String getSentence() {
        return Sentence;
    }

    public String getSentence_audio() {
        return Sentence_audio;
    }
}
