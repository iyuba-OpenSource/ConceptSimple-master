package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.remote.bean.App_check;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 审核接口
 */
public interface VerifyService {

    //审核接口处理(微课、视频、人教版等)
    //http://api.qomolama.cn/getRegisterAll.jsp
    @GET()
    Observable<App_check> verify(@Url String url,
                                 @Query(StrLibrary.appId) int appId,
                                 @Query(StrLibrary.appVersion) String version);
}
