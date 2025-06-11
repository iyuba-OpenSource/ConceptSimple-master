package com.suzhou.concept.dao.young

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suzhou.concept.AppClient
import com.suzhou.concept.bean.YoungSentenceItem
import com.suzhou.concept.utils.GlobalMemory

/**
苏州爱语吧科技有限公司
@Date:  2022/11/8
@Author:  han rong cheng
 */
@Dao
interface YoungSentenceDao {
    @Query("select * from YoungSentenceItem where voaId=:voaId and userId=:userId")
    fun selectClassSentence(userId:Int,voaId:Int):List<YoungSentenceItem>

    @Query("select * from YoungSentenceItem where voaId=:voaId and ParaId=:paraId")
    fun selectEvalInfoSentence(paraId:Int,voaId:Int=AppClient.conceptItem.voa_id.toInt()):List<YoungSentenceItem>

    //增加idIndex的数据查询
    @Query("select * from YoungSentenceItem where voaId=:voaId and ParaId=:paraId and IdIndex=:idIndex")
    fun selectEvalInfoSentenceNew(paraId: Int,idIndex:Int,voaId: Int=AppClient.conceptItem.voa_id.toInt()):YoungSentenceItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSentenceList(list: List<YoungSentenceItem>)

    @Query("update YoungSentenceItem set onlyKay=:onlyKey,success=:success,fraction=:fraction,selfVideoUrl=:url where userId=:userId and voaId=:voaId and ParaId=:paraId")
    fun updateYoungSentenceItemStatus(
        paraId: Int,
        onlyKey: String,
        success: Boolean,
        fraction: String,
        url: String,
        userId: Int = GlobalMemory.userInfo.uid,
        voaId: Int = AppClient.conceptItem.voa_id.toInt()
    )
}