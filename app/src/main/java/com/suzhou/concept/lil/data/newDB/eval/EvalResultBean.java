package com.suzhou.concept.lil.data.newDB.eval;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 评测的数据
 */
@Entity(primaryKeys = {"voaId","paraId","idIndex","uid"})
public class EvalResultBean implements Serializable {

    @NonNull
    public String uid;//用户id
    @NonNull
    public String voaId;//课程id
    @NonNull
    public String idIndex;
    @NonNull
    public String paraId;

    public String sentence;
    @NonNull
    public double scores;
    @NonNull
    public double total_score;
    public String filepath;
    @SerializedName("URL")
    public String url;
    public String words;
}
