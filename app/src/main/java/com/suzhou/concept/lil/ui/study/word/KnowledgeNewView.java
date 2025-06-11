package com.suzhou.concept.lil.ui.study.word;

import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.mvp.frame.BaseView;

import java.util.List;

public interface KnowledgeNewView extends BaseView {

    //展示单词数据
    void showWordData(List<WordShowBean> list);
}
