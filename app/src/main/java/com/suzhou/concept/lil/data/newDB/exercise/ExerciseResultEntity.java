package com.suzhou.concept.lil.data.newDB.exercise;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * 练习题结果记录表
 */
@Entity(primaryKeys = {"voaId","userId","lessonType","exerciseType"})
public class ExerciseResultEntity {

    @NonNull
    public String voaId;//课程
    @NonNull
    public int userId;//用户id
    @NonNull
    public String lessonType;//课程的类别
    @NonNull
    public String exerciseType;//练习题的类别

    public int rightCount;//正确数量
    public int totalCount;//全部数量

    public ExerciseResultEntity() {
    }

    @Ignore
    public ExerciseResultEntity(@NonNull String voaId, int userId, @NonNull String lessonType, @NonNull String exerciseType, int rightCount, int totalCount) {
        this.voaId = voaId;
        this.userId = userId;
        this.lessonType = lessonType;
        this.exerciseType = exerciseType;
        this.rightCount = rightCount;
        this.totalCount = totalCount;
    }
}
