package com.suzhou.concept.lil.ui.study.eval.bean;

import java.io.Serializable;

/**
 * 句子数据转化层(将青少版和英音美音合并数据展示)
 */
public class SentenceTransBean implements Serializable {

    private String voaId;
    private String idIndex;
    private String paraId;

    private String sentence;
    private String sentenceCn;
    private double timing;//开始时间(这里是long型的，直接转换使用即可，数据为毫秒)
    private double endTiming;//结束时间(这里是long型的，直接转换使用即可，数据为毫秒)

    public SentenceTransBean(String voaId, String idIndex, String paraId, String sentence, String sentenceCn, double timing, double endTiming) {
        this.voaId = voaId;
        this.idIndex = idIndex;
        this.paraId = paraId;
        this.sentence = sentence;
        this.sentenceCn = sentenceCn;
        this.timing = timing;
        this.endTiming = endTiming;
    }


    public String getVoaId() {
        return voaId;
    }

    public String getIdIndex() {
        return idIndex;
    }

    public String getParaId() {
        return paraId;
    }

    public String getSentence() {
        return sentence;
    }

    public String getSentenceCn() {
        return sentenceCn;
    }

    public double getTiming() {
        return timing;
    }

    public double getEndTiming() {
        return endTiming;
    }
}
