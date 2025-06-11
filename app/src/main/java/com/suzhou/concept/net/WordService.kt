package com.suzhou.concept.net


import com.suzhou.concept.bean.ConceptWordResponse
import com.suzhou.concept.bean.YoungWordList
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
苏州爱语吧科技有限公司
 */
interface WordService {
    //获取单词列表
    @GET("concept/getConceptWord.jsp")
    suspend fun getConceptWord(
        @Query("book") bookId: Int
    ): ConceptWordResponse

    /**
     * 获取青少版单词
     * */
    @GET
    suspend fun getYoungWord(
        @Url url: String,
        @Query("bookid") bookId: Int
    ): YoungWordList
}