package com.suzhou.concept.net

import com.suzhou.concept.bean.AdResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface AdService {
    @GET
    suspend fun requestAdType(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ): List<AdResponse>
}