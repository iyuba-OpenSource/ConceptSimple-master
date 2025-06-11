package com.suzhou.concept.dao.young

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suzhou.concept.bean.YoungWordItem

/**
苏州爱语吧科技有限公司
@Date:  2022/11/11
@Author:  han rong cheng
 */
@Dao
interface YoungWordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertYoungWordItem(list:List<YoungWordItem>)


    @Query("select * from YoungWordItem where userId=:userId and book_id=:voaId")
    fun selectYoungWordById(userId:Int,voaId:Int): List<YoungWordItem>

    //根据voaId来查询单词数据
    @Query("select * from YoungWordItem where userId=:userId and voa_id=:voaId")
    fun selectYoungWordByVoaId(userId:Int,voaId:Int): List<YoungWordItem>

    //更新item答题是否正确的状态
    @Query("update YoungWordItem set correct=:flag where book_id=:bookId and unit_id =:unitInt and position=:position and userId=:userId")
    fun updateWordRightStatus(bookId:Int,position:Int,flag:Boolean,userId:Int,unitInt: Int)

    //查询正确的单词数量
    @Query("select count(1) from YoungWordItem where voa_id=:voaId and userId=:userId and correct=:correct")
    fun selectYoungRightWordCount(voaId: Int,userId:Int,correct:Boolean):Int

    //查询所有单词的数量
    @Query("select count(distinct word) from YoungWordItem where voa_id=:voaId")
    fun selectYoungWordCount(voaId: Int):Int
}