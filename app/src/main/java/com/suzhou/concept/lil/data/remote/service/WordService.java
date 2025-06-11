package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.remote.bean.Concept_four_word;
import com.suzhou.concept.lil.data.remote.bean.Concept_junior_word;
import com.suzhou.concept.lil.data.remote.bean.Word_pass;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WordService {

    //获取全四册的单词数据
    //http://apps.iyuba.cn/concept/getConceptWord.jsp?book=1
    @GET()
    Observable<BaseBean<List<Concept_four_word>>> getConceptFourWordData(@Url String url,
                                                                         @Query(StrLibrary.book) int bookId);

    //获取青少版的单词数据
    //http://apps.iyuba.cn/iyuba/getWordByUnit.jsp?bookid=289
    @GET()
    Observable<BaseBean<List<Concept_junior_word>>> getConceptJuniorWordData(@Url String url,
                                                                             @Query(StrLibrary.bookid) int bookId);

    //获取单词进度数据
    //http://daxue.iyuba.cn/ecollege/getExamDetailNew.jsp?appId=222&lesson=281&TestMode=W&mode=2&format=json&uid=12071118&sign=ece6c23e8d4d709733e84fc8d878f7ce
    @GET
    Observable<Word_pass> getConceptPassWordData(@Url String url,
                                                 @Query(StrLibrary.appId) int appId,
                                                 @Query(StrLibrary.lesson) int bookId,
                                                 @Query(StrLibrary.TestMode) String testMode,
                                                 @Query(StrLibrary.mode) String mode,
                                                 @Query(StrLibrary.format) String format,
                                                 @Query(StrLibrary.uid) int userId,
                                                 @Query(StrLibrary.sign) String sign);
}
