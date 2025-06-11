package com.suzhou.concept.net

import com.suzhou.concept.bean.BookItem
import com.suzhou.concept.bean.EvaluationSentenceResponse
import com.suzhou.concept.bean.LikeYoungResponse
import com.suzhou.concept.bean.MergeDubResponse
import com.suzhou.concept.bean.MineReleaseResponse
import com.suzhou.concept.bean.YoungItem
import com.suzhou.concept.bean.YoungList
import com.suzhou.concept.bean.YoungRankResponse
import com.suzhou.concept.bean.YoungSentenceList
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
苏州爱语吧科技有限公司
@Date:  2022/10/15
@Author:  han rong cheng
 */
interface YoungService {
    /**
     * 获取青少版课本列表
     * */
    @GET
    suspend fun getBookList(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): YoungList<YoungItem>

    /**
     * 获取单本课本的内容
     * */
    @GET
    suspend fun getSingleBook(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): YoungList<BookItem>

    /**
     * 获取单本课本的句子
     * */
    @GET
    suspend fun getYoungSentence(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): YoungSentenceList

    /**
     * 获取单本课本的句子-新的(处理数据错误的情况)
     * */
    @GET
    suspend fun getYoungSentenceNew(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): String

    /**
     * 请求青少版口语秀排行榜
     * */
    @GET
    suspend fun getYoungSpeakingRank(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): YoungRankResponse


    /**
     * 下载文件
     * */
    @GET
    @Streaming
    suspend fun downloadFile(@Url url: String): ResponseBody

    /**
     * 口语秀句子
     * */
    @POST
    suspend fun evalSentence(
        @Url url: String,
        @Body body: RequestBody
    ):EvaluationSentenceResponse

    /**
     * 点赞别人的口语秀
     * */
    @GET
    suspend fun likeOtherSpeaking(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):LikeYoungResponse

    /**
     * 已发布
     * */
    @GET
    suspend fun getMintReleased(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):MineReleaseResponse

    /**
     * 合成&发布配音
     * */
    @POST
    suspend fun mergeReleaseSpeaking(
        @Url url: String,
        @QueryMap map: Map<String, String>,
        @Body body: RequestBody
    ):MergeDubResponse
}