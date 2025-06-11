package com.suzhou.concept.lil.data.newDB.word.pass;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordPassDao {

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(WordEntity_pass bean);

    //保存多个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMultiData(List<WordEntity_pass> list);

    //查询当前单元的闯关数据
    @Query("select * from WordEntity_pass where type=:type and bookId=:bookId and id=:id and userId=:userId")
    WordEntity_pass searchDataById(String type,int bookId,int id,int userId);
}
