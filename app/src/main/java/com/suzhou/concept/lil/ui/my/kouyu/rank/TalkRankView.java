package com.suzhou.concept.lil.ui.my.kouyu.rank;

import com.suzhou.concept.bean.YoungRankItem;
import com.suzhou.concept.lil.mvp.frame.BaseView;

import java.util.List;

public interface TalkRankView extends BaseView {

    //显示排行数据
    void showRankData(List<YoungRankItem> list,String showMsg);
}
