package com.suzhou.concept.lil.ui.study.eval.bean;

import java.io.Serializable;

/**
 * 发布评测的回调数据
 */
public class PublishEvalBean implements Serializable {

    /**
     * ResultCode : 501
     * AddScore : 5
     * ShuoshuoId : 19347098
     * Message : OK
     */

    private String ResultCode;
    private int AddScore;
    private int ShuoshuoId;
    private String Message;

    private String reward;
    private String rewardMessage;

    public String getResultCode() {
        return ResultCode;
    }

    public void setResultCode(String ResultCode) {
        this.ResultCode = ResultCode;
    }

    public int getAddScore() {
        return AddScore;
    }

    public void setAddScore(int AddScore) {
        this.AddScore = AddScore;
    }

    public int getShuoshuoId() {
        return ShuoshuoId;
    }

    public void setShuoshuoId(int ShuoshuoId) {
        this.ShuoshuoId = ShuoshuoId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getRewardMessage() {
        return rewardMessage;
    }

    public void setRewardMessage(String rewardMessage) {
        this.rewardMessage = rewardMessage;
    }
}
