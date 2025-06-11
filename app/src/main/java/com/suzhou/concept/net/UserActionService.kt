package com.suzhou.concept.net

import com.suzhou.concept.bean.*
import okhttp3.MultipartBody
import retrofit2.http.*

/**
苏州爱语吧科技有限公司
 */
interface UserActionService {
    //登录
    @GET
    suspend fun login(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): LoginResponse

    //更改用户名
    @GET
    suspend fun modifyUserName(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): ModifyUserNameResponse

    //注销
    @GET
    suspend fun logoutUser(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): LogoutUserResponse


    //更改用户头像
    @Multipart
    @POST
    suspend fun uploadPhoto(
        @Url url: String,
        @Part part:MultipartBody.Part
    ): UploadPhotoResponse

    //判断手机号是否已经注册
    @GET
    suspend fun getRegisterStatus(
        @Url url:String,
        @QueryMap map: Map<String, String>
    ):LoginResponse

    //用户の手动注册&&秒验注册
    @GET
    suspend fun register(
        @Url url:String,
        @QueryMap map: Map<String, String>
    ):LoginResponse

    //刷新用户信息
    @GET
    suspend fun refreshSelf(
        @Url url:String,
        @QueryMap map: Map<String, String>
    ): SelfResponse

    //请求支付
    @GET
    suspend fun requestPayVip(
        @Url url:String,
        @QueryMap map: Map<String, String>
    ): RequestPayResponse

    //支付
    @GET
    suspend fun payVip(
        @Url url:String,
        @Query("data") data:String
    ): PayResponse


    //秒验登录
    @GET
    suspend fun secondVerify(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):SecondVerifyResponse


    /**
     * 打卡分享
     * */
    @GET
    suspend fun shareAddScore(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):AddScoreResponse

    /**
     * 口语排行榜
     * */
    @GET
    suspend fun getTopicRanking(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):RankResponse

    /**
     * 学习&&听力排行榜
     * */
    @GET
    suspend fun getStudyListenRanking(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):GroupRankResponse
}