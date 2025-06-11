package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.ui.my.wordNote.WordDeleteBean;
import com.suzhou.concept.lil.ui.my.wordNote.WordNoteBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title:
 * @date: 2023/10/8 18:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface WordCollectService {

    //获取收藏的单词
    //http://word.iyuba.cn/words/wordListService.jsp?u=12071118&pageNumber=1&pageCounts=10
    @GET
    Observable<WordNoteBean> getCollectWordData(@Url String url,
                                                @Query("u") int userid,
                                                @Query("pageNumber") int pageIndex,
                                                @Query("pageCounts") int pageCount);

    //收藏/取消收藏单词
    //http://word.iyuba.cn/words/updateWord.jsp?userId=12071118&mod=delete&groupName=Iyuba&word=and%2Cboss
    @GET
    Observable<WordDeleteBean> collectWord(@Url String url,
                                           @Query("userId") int userId,
                                           @Query("mod") String mode,
                                           @Query("groupName") String groupName,
                                           @Query("word") String word);
}
