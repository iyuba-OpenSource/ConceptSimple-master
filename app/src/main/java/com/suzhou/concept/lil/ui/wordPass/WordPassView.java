package com.suzhou.concept.lil.ui.wordPass;

import com.suzhou.concept.lil.data.newDB.word.pass.bean.WordPassBean;
import com.suzhou.concept.lil.mvp.frame.BaseView;

import java.util.List;

public interface WordPassView extends BaseView {

    //获取单词数据
    void showWordData(List<WordPassBean> list, String showMsg);

    //获取远程单词数据
    void loadRemoteData();

    //获取单词的进度数据
    void showWordPassResult(boolean isSuccess,String showMsg,List<WordPassBean> passList);
}
