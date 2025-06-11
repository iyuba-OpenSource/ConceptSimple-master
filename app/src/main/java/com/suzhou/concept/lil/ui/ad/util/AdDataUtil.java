package com.suzhou.concept.lil.ui.ad.util;

import com.suzhou.concept.R;
import com.suzhou.concept.utils.OtherUtils;

/**
 * 本地广告数据
 */
public interface AdDataUtil {

    //链接数据
    interface AdUrl{
        /***********开屏广告**********/
        //本地图片
        int localSpreadAdPic = R.drawable.default_splash_ad;

        //本地链接图片
        static String localSpreadAdPicUrl(){
            return "http://app."+ OtherUtils.INSTANCE.getIyuba_cn() +"/dev/upload/1679379374314.jpg";
        }

        //本地跳转链接
        static String localSpreadAdJumpUrl(){
            return "http://app."+OtherUtils.INSTANCE.getIyuba_cn()+"/";
        }

        /**********banner广告**********/
        //本地图片
        int localBannerAdPic = R.drawable.default_banner_ad;

        //本地链接图片
        static String localBannerAdPicUrl(){
            return "http://app."+OtherUtils.INSTANCE.getIyuba_cn()+"/dev/upload/1679381438179.jpg";
        }

        //本地跳转链接
        static String localBannerAdJumpUrl(){
            return "http://app."+OtherUtils.INSTANCE.getIyuba_cn()+"/";
        }

        /***********接口数据***********/
        //接口拼接图片
        static String fixPicUrl(String picUrl){
            return "http://app."+OtherUtils.INSTANCE.getIyuba_cn()+"/dev/"+picUrl;
        }

        //接口拼接链接
        static String fixJumpUrl(String linkUrl){
            return linkUrl;
        }
    }
}
