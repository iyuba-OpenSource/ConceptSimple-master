package com.suzhou.concept.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suzhou.concept.bean.WordItem

/**
苏州爱语吧科技有限公司
 */
@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: List<WordItem>)

    //查询所有的单词，限制100个
    @Query("select * from WordItem where bookId=:bookId limit 100")
    fun selectByBookId(bookId:Int):List<WordItem>

    //根据userId和bookId查询
    @Query("select * from WordItem where bookId=:bookId and userId=:userId")
    fun selectByBookId(bookId:Int,userId:Int):List<WordItem>

    //根据unitId查询信息
    @Query("select * from WordItem where bookId=:bookId and unitId=:unitId and userId=:uId")
    fun selectByUnitId(bookId: Int,unitId: Int,uId:Int):List<WordItem>

    //根据voaId查询信息
    @Query("select * from WordItem where bookId=:bookId and voa_id=:voaId and userId=:uId")
    fun selectByVoaId(bookId: Int,voaId: Int,uId:Int):List<WordItem>

    //更新item答题是否正确的状态
    @Query("update WordItem set correct=:flag where bookId=:bookId and unitId =:unitInt and position=:position and userId=:userId")
    fun updateRightStatus(bookId:Int,position:Int,flag:Boolean,userId:Int,unitInt: Int)

    @Query("update WordItem set userId=:loginId where userId=:defaultId")
    fun updateUserId(defaultId:Int,loginId:Int)


    //查询某个课程下单词的答题正确数量
    @Query("select count(1) from WordItem where voa_id=:voaId and userId=:userId and correct=:correct")
    fun selectRightWordNum(voaId:Int,userId:Int,correct:Boolean):Int
    //查询课程下的单词数量
    @Query("select count(distinct word) from WordItem where voa_id=:voaId")
    fun selectWordCount(voaId:Int):Int
}