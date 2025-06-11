package com.jn.yyz.practise.model;

import com.jn.yyz.practise.model.bean.EvalBean;
import com.jn.yyz.practise.model.bean.ExamBean;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;
import com.jn.yyz.practise.model.bean.PronBean;
import com.jn.yyz.practise.model.bean.TestRankingBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiServer {


    /**
     * 获取练习题
     * @param url
     * @param pageNumber
     * @param pageSize
     * @param lessonId
     * @return
     */
    @GET
    Observable<ExamBean> getExam(@Url String url,
                                 @Query("type") String type,
                                 @Query("pageNumber") int pageNumber,
                                 @Query("pageSize") int pageSize,
                                 @Query("lessonId") String lessonId,
                                 @Query("maxId") int maxId,
                                 @Query("uid") int uid,
                                 @Query("version") int version,
                                 @Query("sign") String sign);


    /**
     * 评测
     *
     * @param requestBody
     * @return
     */
    @POST
    Observable<EvalBean> eval(@Url String url, @Body RequestBody requestBody);

    /**
     * 获取音标
     * @return
     */
    @GET
    Observable<PronBean> getPronNew(@Url String url);


    /**
     * 上传练习题记录
     * @param url
     * @param requestBody
     * @return
     */
    @POST
    Observable<UploadTestBean> updateEnglishTestRecord(@Url String url, @Body RequestBody requestBody);


    /**
     * 获得积分
     * https://apps.iyuba.cn/credits/updateEXP.jsp
     * @param url
     * @param requestBody
     * @return
     */
    @POST
    Observable<ExpBean> updateEXP(@Url String url, @Body RequestBody requestBody);

    /**
     * https://ai.iyuba.cn/japanapi/getEnglishTestRanking.jsp
     * 获取练习题排行榜
     *
     * 上面的已经废弃，使用下面的：
     * http://api.iyuba.cn/credits/getExpRanking.jsp
     * 回调数据类型和请求参数一致，因此不需要修改，仅修改url即可
     */
    @GET
    Observable<TestRankingBean> getEnglishTestRanking(@Url String url, @Query("uid") String uid, @Query("flg") String flg,
                                                      @Query("pageNumber") int pageNumber, @Query("pageSize") int pageSize,
                                                      @Query("sign") String sign);


    /**
     *  获取首页试题数据
     * http://class.iyuba.cn/getExamTitleList.jsp
     */
    @GET
    Observable<HomeTestTitleBean> getExamTitleList(@Url String url, @Query("bookId") int bookId, @Query("type") String type,
                                                   @Query("sign") String sign, @Query("uid") String uid);


    /**
     * 获取错题本错题
     * https://apps.iyuba.cn/getWrongExamByUid.jsp
     */
    @GET
    Observable<ExamBean> getWrongExamByUid(@Url String url, @Query("uid") String uid, @Query("type") String type, @Query("sign") String sign,
                                         @Query("pageNumber") int pageNumber, @Query("pageSize") int pageSize, @Query("version") int version);
}
