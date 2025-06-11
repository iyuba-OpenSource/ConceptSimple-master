package com.suzhou.concept.lil.data.newDB.eval;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EvalResultDao {

    //保存评测数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveEval(EvalResultBean bean);

    //获取单个评测数据
    @Query("select * from EvalResultBean where uid=:userId and voaId=:voaId and idIndex=:idIndex and paraId=:paraId")
    EvalResultBean getSingleEval(int userId,String voaId,String idIndex,String paraId);

    //获取本课程的评测数据
    @Query("select * from EvalResultBean where uid=:userId and voaId=:voaId")
    List<EvalResultBean> getEvalByVoaId(int userId,String voaId);
}
