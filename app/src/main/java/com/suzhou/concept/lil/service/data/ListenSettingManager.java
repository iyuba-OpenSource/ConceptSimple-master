package com.suzhou.concept.lil.service.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.utils.GlobalMemory;

/**
 * @title: 原文界面的设置管理器
 * @date: 2023/10/18 11:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenSettingManager {
    private static final String TAG = "ListenSettingManager";

    private static ListenSettingManager instance;

    public static ListenSettingManager getInstance(){
        if (instance==null){
            synchronized (ListenSettingManager.class){
                if (instance==null){
                    instance = new ListenSettingManager();
                }
            }
        }
        return instance;
    }

    private SharedPreferences preferences;

    private SharedPreferences getPreferences(){
        if (preferences==null){
            preferences = AppClient.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    //播放倍速(这里根据账号来，会员可以设置，非会员不可以设置)
    private static final String play_speed = "play_speed";

    public void setPlaySpeed(float speed){
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        if (userId!=0){
            getPreferences().edit().putFloat(String.valueOf(userId),speed).apply();
        }
    }

    public float getPlaySpeed(){
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        if (userId!=0){
            return getPreferences().getFloat(String.valueOf(userId),1.0f);
        }else {
            return 1.0f;
        }
    }
}
