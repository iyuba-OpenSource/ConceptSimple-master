package com.suzhou.concept.dao.young

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suzhou.concept.bean.YoungItem
import kotlinx.coroutines.flow.Flow

/**
苏州爱语吧科技有限公司
@Date:  2022/11/10
@Author:  han rong cheng
 */
@Dao
interface YoungItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertYoungItem(list:List<YoungItem>)


    @Query("select * from YoungItem")
    fun selectAllYoungItem():Flow<List<YoungItem>>
}