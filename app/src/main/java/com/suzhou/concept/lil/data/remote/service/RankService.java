package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.remote.bean.Rank_eval;
import com.suzhou.concept.lil.data.remote.bean.Rank_exercise;
import com.suzhou.concept.lil.data.remote.bean.Rank_listen;
import com.suzhou.concept.lil.data.remote.bean.Rank_read;
import com.suzhou.concept.lil.data.remote.bean.Rank_speech;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 排行的服务
 */
public interface RankService {

    /**********************************评测的排行******************************/
    //获取学习界面的排行的数据
    //http://daxue.iyuba.cn/ecollege/getTopicRanking.jsp?topic=concept&topicid=1002&uid=15399731&start=0&total=20&sign=18feb75d19d05c3f4581c2703f6c4a61&type=D
    @GET
    Observable<Rank_eval> getEvalRankData(@Url String url,
                                          @Query(StrLibrary.topic) String topic,
                                          @Query(StrLibrary.topicid) int voaId,
                                          @Query(StrLibrary.uid) int userId,
                                          @Query(StrLibrary.start) int startIndex,
                                          @Query(StrLibrary.total) int totalCount,
                                          @Query(StrLibrary.sign) String sign,
                                          @Query(StrLibrary.type) String type);


    /************************************总的排行********************************/
    //获取听力的排行数据
    //http://daxue.iyuba.cn/ecollege/getStudyRanking.jsp?uid=15399731&type=D&start=0&total=30&sign=d840fdfa88bdc9a6beb5a6fa437a13fd&mode=listening
    @GET
    Observable<Rank_listen> getListenRankData(@Url String url,
                                                     @Query(StrLibrary.uid) int userId,
                                                     @Query(StrLibrary.type) String type,
                                                     @Query(StrLibrary.start) int start,
                                                     @Query(StrLibrary.total) int total,
                                                     @Query(StrLibrary.sign) String sign,
                                                     @Query(StrLibrary.mode) String mode);

    //获取测试的排行数据
    //http://daxue.iyuba.cn/ecollege/getTestRanking.jsp?uid=15399731&type=D&start=0&total=30&sign=d840fdfa88bdc9a6beb5a6fa437a13fd
    @GET
    Observable<Rank_exercise> getExerciseRankData(@Url String url,
                                                  @Query(StrLibrary.uid) int userId,
                                                  @Query(StrLibrary.type) String type,
                                                  @Query(StrLibrary.start) int start,
                                                  @Query(StrLibrary.total) int total,
                                                  @Query(StrLibrary.sign) String sign);

    //获取口语的排行数据
    //http://daxue.iyuba.cn/ecollege/getTopicRanking.jsp?uid=15399731&type=D&start=0&total=30&sign=108d2ff2b1acf18d8da5725d6048045c&topic=concept&topicid=0&shuoshuotype=0
    @GET
    Observable<Rank_speech> getSpeechRankData(@Url String url,
                                              @Query(StrLibrary.uid) int userId,
                                              @Query(StrLibrary.type) String type,
                                              @Query(StrLibrary.start) int start,
                                              @Query(StrLibrary.total) int total,
                                              @Query(StrLibrary.sign) String sign,
                                              @Query(StrLibrary.topic) String topic,
                                              @Query(StrLibrary.topicid) int topicId,
                                              @Query(StrLibrary.shuoshuotype) String shuoshuoType);

    //获取阅读的排行数据
    //http://cms.iyuba.cn/newsApi/getNewsRanking.jsp?uid=15399731&type=D&start=0&total=30&sign=d840fdfa88bdc9a6beb5a6fa437a13fd
    @GET
    Observable<Rank_read> getReadRankData(@Url String url,
                                          @Query(StrLibrary.uid) int userId,
                                          @Query(StrLibrary.type) String type,
                                          @Query(StrLibrary.start) int start,
                                          @Query(StrLibrary.total) int total,
                                          @Query(StrLibrary.sign) String sign);
}
