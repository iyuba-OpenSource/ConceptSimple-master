package com.suzhou.concept.lil.ui.study.eval_new;

import com.suzhou.concept.lil.mvp.frame.BaseView;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;

public interface EvalFixView extends BaseView {

    //显示评测结果
    void showEvalResult(EvalShowBean bean,String showMsg);

    //显示评测发布结果
    void showEvalRankResult(boolean isSuccess,String showMsg);

    //显示合成结果
    void showMargeResult(String margeUrl,int averageScore,String showMsg);

    //显示合成发布结果
    void showMargePublishResult(boolean isSuccess,String showMsg);
}
