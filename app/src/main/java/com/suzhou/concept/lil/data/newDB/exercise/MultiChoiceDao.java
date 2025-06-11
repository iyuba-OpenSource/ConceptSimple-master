package com.suzhou.concept.lil.data.newDB.exercise;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MultiChoiceDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(MultipleChoiceEntity entity);

    //保存多个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMultiData(List<MultipleChoiceEntity> list);

    //获取单个数据
    @Query("select * from MultipleChoiceEntity where voaId=:voaId and indexId=:indexId and exerciseType=:exerciseType")
    MultipleChoiceEntity getSingleData(String voaId,int indexId,String exerciseType);

    //获取多个数据
    @Query("select * from MultipleChoiceEntity where voaId=:voaId and exerciseType=:exerciseType order by indexId asc")
    List<MultipleChoiceEntity> getMultiData(String voaId,String exerciseType);
}
