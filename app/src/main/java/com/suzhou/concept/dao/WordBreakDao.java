package com.suzhou.concept.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.suzhou.concept.bean.WordBreakBean;

import java.util.List;

@Dao
public interface WordBreakDao {

    //插入单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSingleData(WordBreakBean bean);

    //插入多个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMultipleData(List<WordBreakBean> list);

    //获取当前单元的数据
    @Query("select * from WordBreakBean where type =:type and bookId=:bookId and unitId=:unitId and userId=:userId")
    WordBreakBean getMultipleData(String type,int bookId,int unitId,int userId);

    //获取当前用户的数据
    @Query("select * from WordBreakBean where userId=:userId")
    WordBreakBean getMultipleData(int userId);

    //获取当前单元的正确单词数量
    @Query("select rightCount from WordBreakBean where type =:type and bookId=:bookId and unitId=:unitId and userId=:userId")
    int getRightWordCount(String type,int bookId,int unitId,int userId);
}
