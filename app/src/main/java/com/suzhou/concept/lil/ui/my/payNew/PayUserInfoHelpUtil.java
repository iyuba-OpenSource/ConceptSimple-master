package com.suzhou.concept.lil.ui.my.payNew;

import android.text.TextUtils;

import com.suzhou.concept.bean.LoginResponse;
import com.suzhou.concept.bean.SelfResponse;
import com.suzhou.concept.lil.data.remote.bean.User_info;
import com.suzhou.concept.utils.GlobalMemory;
import com.suzhou.concept.utils.OtherUtils;

/**
 * 支付完成后将用户信息转换成全局的用户信息
 */
public class PayUserInfoHelpUtil {

    public static void saveUserinfo(int userId,User_info userInfo){
        SelfResponse selfResponse = new SelfResponse(
                userInfo.getAlbums(),
                userInfo.getGender(),
                userInfo.getDistance(),
                userInfo.getBlogs(),
                userInfo.getMiddle_url(),
                userInfo.getContribute(),
                userInfo.getShengwang(),
                userInfo.getBio(),
                userInfo.getPosts(),
                String.valueOf(userInfo.getRelation()),
                String.valueOf(userInfo.getResult()),
                userInfo.getIsteacher(),
                userInfo.getCredits(),
                userInfo.getNickname(),
                userInfo.getEmail(),
                userInfo.getViews(),
                String.valueOf(userInfo.getAmount()),
                String.valueOf(userInfo.getFollower()),
                userInfo.getMobile(),
                userInfo.getAllThumbUp(),
                userInfo.getIcoins(),
                userInfo.getMessage(),
                userInfo.getFriends(),
                userInfo.getDoings(),
                String.valueOf(userInfo.getExpireTime()),
                String.valueOf(userInfo.getMoney()),
                String.valueOf(userInfo.getFollowing()),
                userInfo.getSharings(),
                TextUtils.isEmpty(userInfo.getVipStatus())?0:Integer.parseInt(userInfo.getVipStatus()),
                userInfo.getUsername(),
                "");
        LoginResponse response = new LoginResponse(
                String.valueOf(userInfo.getAmount()),
                userInfo.getMobile(),
                userInfo.getMessage(),
                String.valueOf(userInfo.getResult()),
                userId,
                userInfo.getIsteacher(),
                String.valueOf(userInfo.getExpireTime()),
                String.valueOf(userInfo.getMoney()),
                TextUtils.isEmpty(userInfo.getCredits())?0:Integer.parseInt(userInfo.getCredits()),
                TextUtils.isEmpty(userInfo.getCredits())?0:Integer.parseInt(userInfo.getCredits()),
                userInfo.getNickname(),
                TextUtils.isEmpty(userInfo.getVipStatus())?0:Integer.parseInt(userInfo.getVipStatus()),
                getHeadUrl(userId),
                userInfo.getEmail(),
                userInfo.getUsername(),
                selfResponse);
        GlobalMemory.INSTANCE.setUserInfo(response);
    }

    //获取用户的头像
    private static String getHeadUrl(int userId){
        String urlSuffix = "http://api."+ OtherUtils.INSTANCE.getIyuba_com()+"v2/api.iyuba?";
        urlSuffix = urlSuffix+"protocol=10005&uid="+userId+"&size=big&timestamp="+System.currentTimeMillis();
        return urlSuffix;
    }
}
