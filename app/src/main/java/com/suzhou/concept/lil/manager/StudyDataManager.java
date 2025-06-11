package com.suzhou.concept.lil.manager;

import com.yd.saas.config.utils.SPUtil;

/**
 * @title: 学习界面管理
 * @date: 2023/8/28 09:56
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyDataManager {
    private static StudyDataManager instance;

    public static StudyDataManager getInstance(){
        if (instance==null){
            synchronized (StudyDataManager.class){
                if (instance==null){
                    instance = new StudyDataManager();
                }
            }
        }
        return instance;
    }

    //学习界面-阅读语言切换
    private static final String readLanguage  = "READ_LANGUAGE";

    public boolean getReadShowCn(){
        return SPUtil.getInstance().getBoolean(readLanguage,false);
    }

    public void setReadShowCn(boolean isShowCn){
        SPUtil.getInstance().putBoolean(readLanguage,isShowCn);
    }
}
