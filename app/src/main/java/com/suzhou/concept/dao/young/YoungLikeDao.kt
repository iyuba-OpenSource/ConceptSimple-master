package com.suzhou.concept.dao.young

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.suzhou.concept.bean.LikeYoung
import kotlinx.coroutines.flow.Flow

/**
苏州爱语吧科技有限公司
@Date:  2022/12/10
@Author:  han rong cheng
 */
@Dao
interface YoungLikeDao {
    @Insert
    fun insertSimple(item: LikeYoung)

    @Query("select * from LikeYoung where userId=:userId and otherId=:otherId")
    fun selectSimple(userId:Int,otherId:Int):List<LikeYoung>
}