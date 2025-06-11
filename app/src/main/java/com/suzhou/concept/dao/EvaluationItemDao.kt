package com.suzhou.concept.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.suzhou.concept.bean.EvaluationSentenceDataItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationItemDao {
    @Insert
    fun insertEvaluation(resultList: List<EvaluationSentenceDataItem>)

    @Query("select * from EvaluationSentenceDataItem where userId=:userId and voaId=:voaId")
    fun selectEvaluationList(userId:Int,voaId:Int):Flow<List<EvaluationSentenceDataItem>>

    @Query("select * from EvaluationSentenceDataItem where onlyKay=:onlyKey")
    fun selectEvaluationByKey(onlyKey:String):List<EvaluationSentenceDataItem>

    @Query("delete from EvaluationSentenceDataItem where onlyKay=:onlyKey")
    fun deleteSentenceDataItemByKey(onlyKey:String)

    @Query("update EvaluationSentenceDataItem set score=:score where onlyKay=:onlyKey and `index`=:index")
    fun updateEvaluationChildStatus(score:Float,onlyKey: String,index:Int)
}