package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.remote.bean.Pay_alipay;
import com.suzhou.concept.lil.data.remote.bean.Pay_wx;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 支付的服务
 */
public interface PayService {

    //获取支付宝的支付链接
    @POST()
    Observable<Pay_alipay> getAliPayOrderLink(@Url String url,
                                              @Query("amount") String amount,
                                              @Query("app_id") int appId,
                                              @Query("userId") int userId,
                                              @Query("code") String sign,
                                              @Query("product_id") String productId,
                                              @Query("WIDsubject") String subject,
                                              @Query("WIDbody") String body,
                                              @Query("WIDtotal_fee") String price,
                                              @Query("deduction") long deduction);

    //获取微信的支付链接
    @POST()
    Observable<Pay_wx> getWxPayOrderLink(@Url String url,
                                         @Query("uid") int userId,
                                         @Query("amount") int amount,
                                         @Query("money") String price,
                                         @Query("productid") int productId,
                                         @Query("appid") int appId,
                                         @Query("sign") String sign,
                                         @Query("format") String json,
                                         @Query("weixinApp") String wxKey);
}
