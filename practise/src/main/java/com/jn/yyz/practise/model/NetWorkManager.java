package com.jn.yyz.practise.model;



import com.jn.yyz.practise.PractiseConstant;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit的框架
 * 网络请求管理者
 */
public class NetWorkManager {

    private static NetWorkManager mInstance;
    private static Retrofit retrofit;

    private static volatile ApiServer apiServer = null;

    public static NetWorkManager getInstance() {
        if (mInstance == null) {
            synchronized (NetWorkManager.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化必要对象和参数
     */
    public void init() {
        // 初始化okhttp
        OkHttpClient client = new OkHttpClient.Builder()
//                .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
                .build();

        // 初始化Retrofit
        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(PractiseConstant.URL_CLASS)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public static ApiServer getRequestForApi() {
        if (apiServer == null) {
            synchronized (ApiServer.class) {
                if (apiServer==null){
                    if (retrofit==null){
                        NetWorkManager.getInstance().init();
                    }

                    apiServer = retrofit.create(ApiServer.class);
                }
            }
        }
        return apiServer;
    }
}
