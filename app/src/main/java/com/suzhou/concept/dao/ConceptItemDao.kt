package com.suzhou.concept.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.suzhou.concept.bean.ConceptItem

/**
苏州爱语吧科技有限公司
 */
@Dao
interface ConceptItemDao {
    @Insert
    fun insertConceptItem(list:List<ConceptItem>)

    @Query("select * from ConceptItem where bookId=:bookId and language=:language")
    fun selectConceptItemList(bookId:Int,language:String):List<ConceptItem>

    @Query("select * from ConceptItem where bookId=:bookId and language=:language and `index`=:index")
    fun selectSimpleConceptItem(bookId:Int,language:String,index:Int):List<ConceptItem>

    //查询单个数据
    @Query("select * from ConceptItem where bookId=:bookId and language=:language and `index`=:index")
    fun selectListenConceptItem(bookId:Int,language:String,index:Int):List<ConceptItem>?

    @Query("update ConceptItem set listenProgress=:progress where bookId=:bookId and language=:language and `index`=:index")
    fun updateListenConceptItem(bookId:Int,language:String,index:Int,progress:Int)

    @Query("update ConceptItem set evalSuccess=:evalSuccess where bookId=:bookId and language=:language and `index`=:index")
    fun updateEvalConceptItem(bookId:Int,language:String,index:Int,evalSuccess:Int)

    //默认只更新us类型的单词数据
    @Query("update ConceptItem set wordRight=:wordSuccess where bookId=:bookId and `index`=:index")
    fun updateWordConceptItem(bookId:Int,index:Int,wordSuccess:Int)

    //单独更新uk类型的单词数据（上边的us类型的数据更新时，这边同时更新，因为都是一样的）
    @Query("update ConceptItem set wordRight=:wordSuccess where bookId=:bookId and `index`=:index and language=:language")
    fun updateWordConceptItemWithLanguage(bookId:Int,index:Int,wordSuccess:Int,language: String)

    @Query("update ConceptItem set exerciseRight=:exerciseSuccess where bookId=:bookId and language=:language and `index`=:index")
    fun updateExerciseConceptItem(bookId:Int,language:String,index:Int,exerciseSuccess:Int)
}