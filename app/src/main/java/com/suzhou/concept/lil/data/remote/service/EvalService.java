package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.bean.YoungSentenceItem;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBeanFix;
import com.suzhou.concept.lil.ui.checkEval.CheckEvalBean;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.bean.PublishEvalBean;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceMargeBean;
import com.suzhou.concept.lil.ui.study.eval.bean.WordExplainBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface EvalService {

    //评测数据
    @POST
    Observable<BaseBean<EvalShowBean>> uploadEvalData(@Url String url, @Body RequestBody body);

    //发布单个句子
    @GET()
    Observable<PublishEvalBean> publishEvalData(@Url String url,
                                                @Query("platform") String platform,
                                                @Query("format") String format,
                                                @Query("protocol") int protocol,
                                                @Query("topic") String topic,
                                                @Query("userid") int uId,
                                                @Query("username") String userName,
                                                @Query("voaid") String voaId,
                                                @Query("idIndex") String idIndex,
                                                @Query("paraid") String paraId,
                                                @Query("score") int score,
                                                @Query("shuoshuotype") int shuoshuotype,
                                                @Query("content") String content);

    //合成课程配音的数据
    //http://iuserspeech.iyuba.cn:9001/test/merge/
    @FormUrlEncoded
    @POST
    Observable<SentenceMargeBean> uploadEvalMargeData(@Url String url,
                                                      @Field("audios") String audios,
                                                      @Field("type") String type);

    //发布合成的评测到排行榜
    //http://voa.iyuba.cn/voa/UnicomApi
    //topic	concept
    //platform	android
    //format	json
    //protocol	60003
    //userid	13883503
    //username
    //voaid	3002
    //score	1
    //shuoshuotype	4
    //content	wav6/202303/concept/20230303/16778226389594980.mp3
    @FormUrlEncoded
    @POST
    Observable<PublishEvalBean> publishEvalMargeData(@Url String url,
                                                     @Field("topic") String topic,
                                                     @Field("platform") String platform,
                                                     @Field("format") String format,
                                                     @Field("protocol") int protocol,
                                                     @Field("userid") int uid,
                                                     @Field("username") String name,
                                                     @Field("voaid") String voaId,
                                                     @Field("score") int score,
                                                     @Field("shuoshuotype") int sType,
                                                     @Field("content") String content,

                                                     @Field("appid") int appId,
                                                     @Field("rewardVersion") int rewardVersion);

    //获取全四册的课程内容数据
    @GET()
    Observable<BaseBean<List<EvaluationSentenceItem>>> getUSUKCourseDetailData(@Url String url,
                                                                               @Query("voaid") String voaId);

    //获取青少版的课程内容数据
    //获取课程详情数据(青少版)
    //http://apps.iyuba.cn/iyuba/textExamApi.jsp?voaid=321001
    @GET()
    Observable<BaseBeanFix<List<YoungSentenceItem>>> getChildCourseDetailData(@Url String url,
                                                                              @Query("voaid") String voaId);

    //查询单词释义
    //http://word.iyuba.cn/words/apiWordAi.jsp?q=morning&user_pron=&ori_pron=&appid=283&uid=14084808
    @GET()
    Observable<WordExplainBean> searchWord(@Url String url,
                                           @Query("q") String word,
                                           @Query("user_pron") String user_pron,
                                           @Query("ori_pron") String ori_pron,
                                           @Query("appid") int appId,
                                           @Query("uid") int uid);

    //查询单词释义-新
    //https://apps.iyuba.cn/words/apiWordJson.jsp?q=IVF&format=json
    @GET()
    Observable<WordExplainBean> searchWordNew(@Url String url,
                                              @Query("q") String word,
                                              @Query("format") String format);

    //纠音中评测接口
    //http://iuserspeech.iyuba.cn:9001/test/ai10/
    //sentence	text/plain; charset=utf-8		have
    //flg	text/plain; charset=utf-8		2
    //paraId	text/plain; charset=utf-8		3
    //newsId	text/plain; charset=utf-8		313002
    //IdIndex	text/plain; charset=utf-8		1
    //appId	text/plain; charset=utf-8		260
    //wordId	text/plain; charset=utf-8		1
    //type	text/plain; charset=utf-8		primaryenglish
    //userId	text/plain; charset=utf-8		12071118
    //file	multipart/form-data	have.amr	1.69 KB (1,734 bytes)
    @POST()
    Observable<CheckEvalBean> checkEvalData(@Url String url,
                                            @Body RequestBody body);
}
