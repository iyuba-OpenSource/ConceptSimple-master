package com.suzhou.concept.net

import com.suzhou.concept.bean.*
import com.suzhou.concept.utils.net.ConverterFormat
import com.suzhou.concept.utils.net.ResponseConverter
import okhttp3.RequestBody
import retrofit2.http.*

/**
苏州爱语吧科技有限公司

 An annotation argument must be a compile-time constant:注释参数必须是编译时常量
 所以想用@Headers()来转换域名的想法作废，还是老实用@Url吧
 */
interface ConceptService {
    //获取课程列表
    @GET("concept/getConceptTitle.jsp?flg=1")
    suspend fun getConceptList(
        @Query("language") language: String = "UK",
        @Query("book") book: Int
    ): ConceptList

    //获取QQ群
    @GET
    suspend fun requestQQGroup(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):QqResponse

    //获取pdf
    @GET
    suspend fun getPdfFile(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): PdfResponse

    //取词
    @ResponseConverter(ConverterFormat.XML)
    @GET
    suspend fun pickWord(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): PickWord

    //获取生词列表
    @ResponseConverter(ConverterFormat.XML)
    @GET
    suspend fun requestStrangenessWord(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): StrangenessWord

    //改变单词收藏状态
    @ResponseConverter(ConverterFormat.XML)
    @GET
    suspend fun changeCollectStatus(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): WordStatus

    //请求客服QQ
    @GET
    suspend fun requestCustomerService(
        @Url url: String,
        @Query("appid") appid: Int,
    ):CustomerResponse


    //评测
    @POST
    suspend fun evaluationSentence(
        @Url url:String,
        @Body body: RequestBody
    ): EvaluationSentenceResponse

    //获取排行
    @GET
    suspend fun getRankData(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):RankResponse

    //合成语音
    @GET
    suspend fun mergeVideos(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):MergeResponse

    //发布合成后的
    @GET
    suspend fun releaseMerge(
        @Url url:String,
        @QueryMap map: Map<String, String>
    ):ReleaseResponse

    //排行具体信息
    @GET
    suspend fun getWorksByUserId(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ):RankInfoResponse

    //分享单个评测
    @GET
    suspend fun releaseSimple(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ):ReleaseResponse

    //点赞
    @GET
    suspend fun likeEvaluation(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ):ReleaseResponse

    //纠音&&单词评测
    @GET
    suspend fun correctSound(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ):CorrectSoundResponse

    //分享课文
    @GET
    suspend fun shareContent(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ):ShareContentResponse

    /**
     * 学习记录提交
     */
    @GET
    suspend fun submitStudyRecord(
        @Url url: String,
        @QueryMap map:Map<String,String>
    ):StudyRecordResponse

    /**
     * 学习打卡
     * */
    @GET
    suspend fun signEveryDay(
        @Url url: String,
        @QueryMap map:Map<String,String>
    ):SignResponse



}