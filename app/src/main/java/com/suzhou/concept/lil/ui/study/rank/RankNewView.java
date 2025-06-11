package com.suzhou.concept.lil.ui.study.rank;

import com.suzhou.concept.lil.data.remote.bean.Rank_eval;
import com.suzhou.concept.lil.mvp.frame.BaseView;

import java.util.List;

public interface RankNewView extends BaseView {

    //展示个人数据
    void showUserRankData(Rank_eval rankData);

    //展示排行数据
    void showAllRankData(List<Rank_eval.DataDTO> list);
}
