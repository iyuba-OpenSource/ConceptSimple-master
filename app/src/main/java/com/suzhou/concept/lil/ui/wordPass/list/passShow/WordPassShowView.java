package com.suzhou.concept.lil.ui.wordPass.list.passShow;

import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.mvp.frame.BaseView;

import java.util.List;

public interface WordPassShowView extends BaseView {

    //获取当前单元/课程的单词数据
    void showUnitWordData(List<WordShowBean> list);
}
