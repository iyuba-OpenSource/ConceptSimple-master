package com.suzhou.concept.lil.ui.wordPass.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.suzhou.concept.AppClient;

import data.App;

/**
 * 单词切换数据配置
 */
public class WordConfigData {
    private static WordConfigData instance;

    public static WordConfigData getInstance(){
        if (instance==null){
            synchronized (WordConfigData.class){
                if (instance==null){
                    instance = new WordConfigData();
                }
            }
        }
        return instance;
    }

    //缓存数据
    private SharedPreferences preferences;
    private static final String sp_name = "sp_word";//名称
    private static final String sp_param_showName = "sp_param_showName";//显示名称
    private static final String sp_param_showBookId = "sp_param_showBookId";//书籍id
    private static final String sp_param_showType = "sp_param_showType";//显示类型

    private SharedPreferences getPreferences(){
        if (preferences==null){
            preferences = AppClient.Companion.getContext().getSharedPreferences(sp_name, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    //显示名称
    public void setShowName(String showName){
        getPreferences().edit().putString(sp_param_showName,showName).apply();
    }

    public String getShowName(){
        return getPreferences().getString(sp_param_showName, App.WordConfig.showName);
    }

    //书籍id
    public void setShowBookId(int showBookId){
        getPreferences().edit().putInt(sp_param_showBookId,showBookId).apply();
    }

    public int getShowBookId(){
        return getPreferences().getInt(sp_param_showBookId,App.WordConfig.showBookId);
    }

    //显示类型
    public void setShowType(String showType){
        getPreferences().edit().putString(sp_param_showType,showType).apply();
    }

    public String getShowType(){
        return getPreferences().getString(sp_param_showType,App.WordConfig.showType);
    }
}
