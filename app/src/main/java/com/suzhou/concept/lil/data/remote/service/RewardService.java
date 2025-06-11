package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;
import com.suzhou.concept.lil.ui.study.listen.Listen_report;
import com.suzhou.concept.lil.ui.study.read.Read_mark;
import com.suzhou.concept.lil.ui.my.walletList.Reward_history;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title: 奖励的接口
 * @date: 2023/9/22 19:14
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface RewardService {


    /******************************************现金奖励*********************************/
    //获取现金奖励的历史记录
    //http://api.iyuba.cn/credits/getuseractionrecord.jsp?uid=6307010&pages=1&pageCount=20&sign=0fd32b5d167482f0cc3561b2abc70738
    @GET()
    Observable<BaseBean<List<Reward_history>>> getRewardHistory(@Url String url,
                                                                @Query("uid") int uid,
                                                                @Query("pages") int pages,
                                                                @Query("pageCount") int pageCount,
                                                                @Query("sign") String sign);


    /**********************************************学习报告*****************************/
    //提交阅读的学习报告
    //http://daxue.iyuba.cn/ecollege/updateNewsStudyRecord.jsp?format=xml&uid=14524771&BeginTime=2023-08-04+16%3A56%3A18&EndTime=2023-08-04+16%3A57%3A23&appName=headline&Lesson=%25E8%258B%25B1%25E8%25AF%25AD%25E5%25A4%25B4%25E6%259D%25A1&LessonId=25218&appId=240&Device=HONORPCT-AL10HWPCT&DeviceId=02:00:00:00:00:00&EndFlg=1&wordcount=200&categoryid=127&platform=android
    @FormUrlEncoded
    @POST()
    Observable<Read_mark> submitReadReport(@Url String url,
                                           @Field("format") String format,
                                           @Field("uid") int uid,
                                           @Field("BeginTime") String beginTime,
                                           @Field("EndTime") String endTime,
                                           @Field("appName") String appName,
                                           @Field("Lesson") String lesson,
                                           @Field("LessonId") String lessonId,
                                           @Field("appId") int appid,
                                           @Field("Device") String device,
                                           @Field("DeviceId") String deviceId,
                                           @Field("EndFlg") int endFlag,
                                           @Field("wordcount") long wordCount,
                                           @Field("categoryid") String categoryid,
                                           @Field("platform") String platform,
                                           @Field("rewardVersion") int rewardVersion);

    //听力学习报告
    //http://daxue.iyuba.cn/ecollege/updateStudyRecordNew.jsp
    //format	json
    //appId	260
    //appName	primaryEnglish
    //Lesson	primaryEnglish
    //LessonId	313081
    //uid	15351268
    //Device
    //DeviceId
    //BeginTime	2024-04-08 14:04:33
    //EndTime	2024-04-08 14:04:39
    //EndFlg	0
    //TestWords	4
    //TestMode	1
    //platform	android
    //TestNumber	1
    //sign	9d8369b6e2b6bf127de145a58358f051
    //rewardVersion	1
    @FormUrlEncoded
    @POST
    Observable<Listen_report> submitListenReport(@Url String url,
                                                 @Field("format") String format,
                                                 @Field("appId") int appId,
                                                 @Field("appName") String appName,
                                                 @Field("Lesson") String appNameEncode,
                                                 @Field("LessonId") String lessonId,
                                                 @Field("uid") int uid,
                                                 @Field("Device") String device,
                                                 @Field("DeviceId") String deviceId,
                                                 @Field("BeginTime") String BeginTimeEncode,
                                                 @Field("EndTime") String EndTimeEncode,
                                                 @Field("EndFlg") int EndFlg,
                                                 @Field("TestWords") String TestWords,
                                                 @Field("TestMode") String TestMode,
                                                 @Field("platform") String platform,
                                                 @Field("TestNumber") String TestNumber,
                                                 @Field("sign") String sign,
                                                 @Field("rewardVersion") int rewardVersion);

}
