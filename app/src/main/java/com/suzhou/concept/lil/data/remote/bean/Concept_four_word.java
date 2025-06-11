package com.suzhou.concept.lil.data.remote.bean;

import java.io.Serializable;

/**
 * @title: 新概念-全四册-单词数据
 * @date: 2023/5/11 16:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Concept_four_word implements Serializable {
    private static final long serialVersionUID = -8755978687228910039L;


    /**
     * voa_id : 3001
     * word : evidence
     * def : n. 证据
     * pron : ˈevɪdəns
     * examples : 6
     * audio : http://res.iciba.com/resource/amp3/0/0/14/e1/14e10d570047667f904261e6d08f520f.mp3
     * position : 1
     * sentence : However, as the evidence began to accumulate, experts from the Zoo felt obliged to investigate,
     * sentence_cn : 可是，随着证据越来越多，动物园的专家们感到有必要进行一番调查，
     * timing : 32.8
     * end_timing : 40.3
     * sentence_audio : http://static2.iyuba.cn/newconcept/british/3/3_1.mp3
     * sentence_single_audio : http://static2.iyuba.cn/newconcept/sentence/30010/30010_1_6.mp3
     */

    private int voa_id;
    private String word;
    private String def;
    private String pron;
    private String examples;
    private String audio;
    private String position;
    private String sentence;
    private String sentence_cn;
    private String timing;
    private String end_timing;
    private String sentence_audio;
    private String sentence_single_audio;


    public int getVoa_id() {
        return voa_id;
    }

    public String getWord() {
        return word;
    }

    public String getDef() {
        return def;
    }

    public String getPron() {
        return pron;
    }

    public String getExamples() {
        return examples;
    }

    public String getAudio() {
        return audio;
    }

    public String getPosition() {
        return position;
    }

    public String getSentence() {
        return sentence;
    }

    public String getSentence_cn() {
        return sentence_cn;
    }

    public String getTiming() {
        return timing;
    }

    public String getEnd_timing() {
        return end_timing;
    }

    public String getSentence_audio() {
        return sentence_audio;
    }

    public String getSentence_single_audio() {
        return sentence_single_audio;
    }
}
