package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.remote.bean.Ad_result;
import com.suzhou.concept.lil.data.remote.bean.Ad_stream_result;
import com.suzhou.concept.lil.data.remote.bean.Ad_click_result;
import com.suzhou.concept.lil.data.remote.bean.Ad_clock_submit;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title: 广告数据
 * @date: 2023/10/20 11:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface AdService {

    //广告接口
    //http://dev.iyuba.cn/getAdEntryAll.jsp?uid=0&appId=259&flag=4
    @GET()
    Observable<List<Ad_result>> getAd(@Url String url,
                                      @Query("appId") int appid,
                                      @Query("uid") int uid,
                                      @Query("flag") int flag);

    //信息流广告接口
    //http://dev.iyuba.cn/getAdEntryAll.jsp?uid=0&appId=259&flag=4
    @GET()
    Observable<List<Ad_stream_result>> getStreamAd(@Url String url,
                                                   @Query("appId") int appid,
                                                   @Query("uid") int uid,
                                                   @Query("flag") int flag);

    //广告点击奖励接口
    //http://api.iyuba.cn/credits/adClickReward.jsp?uid=6307010&appid=291&platform=1&ad_space=1&timeStr=1709101522&sign=12b04ffaf5390f4bd60e042ca92f85b9
    @POST()
    Observable<Ad_click_result> getAdClick(@Url String url,
                                           @Query("uid") int userId,
                                           @Query("appid") int appId,
                                           @Query("platform") int platform,
                                           @Query("ad_space") int adSpace,
                                           @Query("timeStr") long timestamp,
                                           @Query("sign") String sign);

    //定时提交广告数据
    //http://iuserspeech.iyuba.cn:9001/japanapi/addAdInfo.jsp?date_time=1710829085&appid=280&device=HONOR&uid=15300794&package=com.iyuba.talkshow.pappa&os=2&ads=%5B%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814594%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814594%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814594%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814594%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814595%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%224%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814595%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%221%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814595%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%224%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814596%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%221%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814596%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814597%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814597%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%221%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814597%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814597%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710814597%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814598%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814598%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710814598%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710814598%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814598%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710814598%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814599%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814599%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814599%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814599%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%221%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710814599%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%223%22%2C%22date_time%22%3A%221710814600%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%224%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710829075%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829075%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710829075%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829075%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710829076%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829076%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%223%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829076%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%224%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%221%22%2C%22date_time%22%3A%221710829076%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829076%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%222%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829076%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%221%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829077%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%220%22%2C%22type%22%3A%220%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829078%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%221%22%2C%22type%22%3A%221%22%7D%2C%7B%22ad_space%22%3A%222%22%2C%22date_time%22%3A%221710829078%22%2C%22install_package%22%3A%22%22%2C%22platform%22%3A%220%22%2C%22type%22%3A%221%22%7D%5D
    @POST()
    Observable<Ad_clock_submit> submitAdData(@Url String url,
                                             @Query("date_time") String date_time,
                                             @Query("appid") int appId,
                                             @Query("device") String device,
                                             @Query("deviceid") String deviceId,
                                             @Query("uid") int userId,
                                             @Query("package") String packageName,
                                             @Query("os") int os,
                                             @Query("ads") String ads);
}
