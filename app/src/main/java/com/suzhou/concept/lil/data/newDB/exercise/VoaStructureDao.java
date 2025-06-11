package com.suzhou.concept.lil.data.newDB.exercise;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VoaStructureDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(VoaStructureExerciseEntity entity);

    //保存多个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMultiData(List<VoaStructureExerciseEntity> list);

    //获取单个数据
    @Query("select * from VoaStructureExerciseEntity where voaId=:voaId and indexId=:indexId and exerciseType=:exerciseType")
    VoaStructureExerciseEntity getSingleData(String voaId,int indexId,String exerciseType);

    //获取多个数据
    @Query("select * from VoaStructureExerciseEntity where voaId=:voaId and exerciseType=:exerciseType order by indexId asc")
    List<VoaStructureExerciseEntity> getMultiData(String voaId,String exerciseType);
}
