package com.suzhou.concept.lil.data.remote.service;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadFileService {

    //下载文件
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);
}
