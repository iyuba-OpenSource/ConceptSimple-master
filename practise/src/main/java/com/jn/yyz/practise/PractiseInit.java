package com.jn.yyz.practise;

import com.jn.yyz.practise.model.NetWorkManager;

public class PractiseInit {


    /**
     * 初始化
     */
    public static void init(int appid) {
        NetWorkManager.getInstance().init();
//        PractiseConstant.UID = String.valueOf(uid);
        PractiseConstant.APPID = String.valueOf(appid);
    }


    public static void setUid(int uid) {
        PractiseConstant.UID = String.valueOf(uid);
    }

    /*public static void setAppid(int appid) {
        PractiseConstant.APPID = String.valueOf(appid);
    }*/
}
