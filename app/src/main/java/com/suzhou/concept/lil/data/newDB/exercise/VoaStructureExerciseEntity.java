package com.suzhou.concept.lil.data.newDB.exercise;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * 填空题的数据表
 */
@Entity(primaryKeys = {"voaId","indexId","exerciseType"})
public class VoaStructureExerciseEntity {

    /**
     * number : 1
     * note : This is my shirt.(变为一般疑问句)
     * ques_num : 0
     * answer : Is this your shirt?
     * desc_CH : 按要求改写下列句子
     * column : 1
     * id : 1003
     * desc_EN : Rewrite these sentences
     * type : 0
     */

    @NonNull
    public String voaId;//课程id
    @NonNull
    public int indexId;//序号
    @NonNull
    public String exerciseType;//类型-英音、美音、青少版

    public String note;
    public String ques_num;
    public String answer;
    public String desc_CH;
    public String column;
    public String desc_EN;
    public String type;

    public VoaStructureExerciseEntity() {
    }

    @Ignore
    public VoaStructureExerciseEntity(@NonNull String voaId, int indexId, @NonNull String exerciseType, String note, String ques_num, String answer, String desc_CH, String column, String desc_EN, String type) {
        this.voaId = voaId;
        this.indexId = indexId;
        this.exerciseType = exerciseType;
        this.note = note;
        this.ques_num = ques_num;
        this.answer = answer;
        this.desc_CH = desc_CH;
        this.column = column;
        this.desc_EN = desc_EN;
        this.type = type;
    }
}
