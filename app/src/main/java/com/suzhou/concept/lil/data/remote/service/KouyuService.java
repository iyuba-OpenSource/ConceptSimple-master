package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.bean.YoungRankItem;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBeanNew;
import com.suzhou.concept.lil.ui.my.kouyu.KouyuDeleteBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title:
 * @date: 2023/10/9 17:40
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface KouyuService {

    //删除配音的数据
    //http://voa.iyuba.cn/voa/UnicomApi?protocol=61003&id=20197781
    @GET
    Observable<KouyuDeleteBean> deleteKouyuData(@Url String url,
                                                @Query("protocol") int protocol,
                                                @Query("id") int id);

    //获取配音的排行数据
    //http://voa.iyuba.cn/voa/UnicomApi?platform=android&format=json&protocol=60001&voaid=321001&pageNumber=1&pageCounts=10&sort=2&topic=concept&selectType=3
    @GET
    Observable<BaseBeanNew<List<YoungRankItem>>> getKouyuRankData(@Url String url,
                                                                  @Query(StrLibrary.platform) String platform,
                                                                  @Query(StrLibrary.format) String format,
                                                                  @Query(StrLibrary.protocol) int protocol,
                                                                  @Query(StrLibrary.voaid) int voaId,
                                                                  @Query(StrLibrary.pageNumber) int pageNum,
                                                                  @Query(StrLibrary.pageCounts) int showCount,
                                                                  @Query(StrLibrary.sort) int sort,
                                                                  @Query(StrLibrary.topic) String topic,
                                                                  @Query(StrLibrary.selectType) int selectType);
}
