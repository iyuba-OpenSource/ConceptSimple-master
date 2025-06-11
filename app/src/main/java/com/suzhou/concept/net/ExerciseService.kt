package com.suzhou.concept.net

import com.suzhou.concept.bean.ExerciseResponse
import com.suzhou.concept.bean.SomeResponse
import com.suzhou.concept.bean.StudyRecordResponse
import com.suzhou.concept.bean.TestRecordItem
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

/**
苏州爱语吧科技有限公司
@Date:  2023/2/7
@Author:  han rong cheng
 */
interface ExerciseService {
    /**
     * 获取练习题
     * */
    @GET
    suspend fun getConceptExercise(
        @Url url:String,
        @Query("bookNum") bookNum:String
    ):ExerciseResponse

    /**
     * 提交做题记录
     * */
    @GET
    suspend fun submitExerciseRecord(
        @Url url: String,
        @QueryMap map:Map<String,String>
    ): StudyRecordResponse

    /**
     * 获取做题记录
     * */
    @GET
    suspend fun requestTestRecord(
        @Url url:String,
        @QueryMap map: Map<String, String>,
    ): SomeResponse<TestRecordItem>
}