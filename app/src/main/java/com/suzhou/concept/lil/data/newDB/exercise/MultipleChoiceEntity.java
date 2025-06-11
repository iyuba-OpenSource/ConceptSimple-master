package com.suzhou.concept.lil.data.newDB.exercise;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * 选择题的数据表
 */
@Entity(primaryKeys = {"voaId","indexId","exerciseType"})
public class MultipleChoiceEntity {


    /**
     * question : ____?
     Yes, she is my daughter.
     * answer : 3
     * voa_id : 1003
     * choice_B : Is your daughter
     * choice_C : Is this your daughter
     * choice_D : Is it this your daughter
     * index_id : 1
     * choice_A : Is it your daughter
     */

    @NonNull
    public String voaId;//课程id
    @NonNull
    public int indexId;//位置id
    @NonNull
    public String exerciseType;//类型(区分英音、美音、青少版，避免将来青少版增加数据)

    public String question;
    public String answer;

    public String choiceB;
    public String choiceC;
    public String choiceD;
    public String choiceA;

    public MultipleChoiceEntity() {
    }

    @Ignore
    public MultipleChoiceEntity(@NonNull String voaId, int indexId, @NonNull String exerciseType, String question, String answer, String choiceB, String choiceC, String choiceD, String choiceA) {
        this.voaId = voaId;
        this.indexId = indexId;
        this.exerciseType = exerciseType;
        this.question = question;
        this.answer = answer;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.choiceA = choiceA;
    }
}
