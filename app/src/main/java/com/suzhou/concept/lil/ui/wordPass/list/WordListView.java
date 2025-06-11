package com.suzhou.concept.lil.ui.wordPass.list;

import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.mvp.frame.BaseView;

import java.util.List;

public interface WordListView extends BaseView {

    //获取当前单元/课程的单词数据
    void showUnitWordData(List<WordShowBean> list);

    //收藏/取消收藏单词
    void showCollectWordData(boolean isSuccess,String showMsg);
}
