package com.suzhou.concept.lil.data.newDB.exercise;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ExerciseResultDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(ExerciseResultEntity entity);

    //获取单个数据
    @Query("select * from ExerciseResultEntity where voaId=:voaId and userId=:userId and lessonType=:lessonType and exerciseType=:exerciseType")
    ExerciseResultEntity getSingleData(String voaId,int userId,String lessonType,String exerciseType);
}
