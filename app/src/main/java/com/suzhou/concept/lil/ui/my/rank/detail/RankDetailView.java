package com.suzhou.concept.lil.ui.my.rank.detail;

import com.suzhou.concept.lil.data.remote.bean.Rank_exercise;
import com.suzhou.concept.lil.data.remote.bean.Rank_listen;
import com.suzhou.concept.lil.data.remote.bean.Rank_read;
import com.suzhou.concept.lil.data.remote.bean.Rank_speech;
import com.suzhou.concept.lil.mvp.frame.BaseView;

//这里比较懒，直接写到一套里边，后面有条件直接分拆开就行了
public interface RankDetailView extends BaseView {

    //显示口语排行数据
    void showSpeechRankData(Rank_speech rankData);

    //显示听力排行数据
    void showListenRankData(Rank_listen rankData);

    //显示阅读排行数据
    void showReadRankData(Rank_read rankData);

    //显示练习排行数据
    void showExerciseRankData(Rank_exercise rankData);
}
