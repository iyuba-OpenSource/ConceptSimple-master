package com.suzhou.concept.net

import com.suzhou.concept.utils.net.JsonOrXmlConverterFactory
import com.suzhou.concept.utils.OtherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

object ServiceCreator {
    private val BASE_URL = "http://apps.${OtherUtils.iyuba_cn}/"
    private const val timeOut=30L
    private val client = OkHttpClient.Builder()
        .callTimeout(timeOut, TimeUnit.SECONDS)
        .readTimeout(timeOut, TimeUnit.SECONDS)
        .writeTimeout(timeOut, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            Timber.tag("请求地址:___________________").d(request.url().toString())
            chain.proceed(request)
        }
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(JsonOrXmlConverterFactory.create())
        .client(client)
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified T> create(): T = create(T::class.java)
}