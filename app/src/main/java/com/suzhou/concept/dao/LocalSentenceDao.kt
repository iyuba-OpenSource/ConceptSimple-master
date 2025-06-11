package com.suzhou.concept.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suzhou.concept.bean.EvaluationSentenceItem

@Dao
interface LocalSentenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSentence(sentenceResult:List<EvaluationSentenceItem>)

    @Query("select * from EvaluationSentenceItem where voaid=:voaId and userId=:userId")
    fun selectSentenceList(userId:Int,voaId:Int):List<EvaluationSentenceItem>

    @Query("update EvaluationSentenceItem set onlyKay=:onlyKey,success=:success,fraction=:fraction,selfVideoUrl=:url where userId=:userId and voaid=:voaId and IdIndex=:index")
    fun updateEvaluationSentenceItemStatus(userId: Int,voaId: Int,index:Int,onlyKey:String,success:Boolean,fraction:String,url:String)

    @Query("select * from EvaluationSentenceItem where voaid=:voaId and IdIndex=:idIndex and Paraid=:paraId")
    fun selectSimpleEvaluation(voaId:String,idIndex:Int,paraId:String):List<EvaluationSentenceItem>
}