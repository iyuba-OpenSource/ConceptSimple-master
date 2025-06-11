package com.suzhou.concept.lil.data.newDB.word.collect;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * @title:
 * @date: 2023/10/8 16:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface WordCollectDao {

    //插入单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(WordCollectBean word);

    //插入多个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMultiData(List<WordCollectBean> words);

    //查询所有的数据
    @Query("select * from WordCollect where userId=:userId")
    List<WordCollectBean> getAllData(int userId);

    //查询单个单词数据
    @Query("select * from WordCollect where userId=:userId and word=:word")
    WordCollectBean getSingleData(int userId,String word);

    //删除单个数据
    @Query("delete from WordCollect where word=:word and userId=:userId")
    void deleteSingleData(String word,int userId);

    //删除多个数据
    @Delete
    void deleteMultiData(List<WordCollectBean> words);
}
