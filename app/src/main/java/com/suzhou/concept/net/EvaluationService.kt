package com.suzhou.concept.net

import com.suzhou.concept.AppClient
import com.suzhou.concept.bean.*
import okhttp3.RequestBody
import retrofit2.http.*

/**
苏州爱语吧科技有限公司
@Date:  2022/9/9
@Author:  han rong cheng
 */
interface EvaluationService {
    //获取句子列表
    @GET("concept/getConceptSentence.jsp")
    suspend fun getConceptSentenceList(@Query("voaid") voaid: Int): EvaluationSentenceList

}