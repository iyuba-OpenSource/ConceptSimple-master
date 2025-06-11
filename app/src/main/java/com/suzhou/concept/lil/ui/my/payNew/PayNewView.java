package com.suzhou.concept.lil.ui.my.payNew;


import com.suzhou.concept.lil.mvp.frame.BaseView;

public interface PayNewView extends BaseView {

    //显示支付链接状态
    void showPayLinkStatus(boolean isError,String showMsg);

    //显示支付功能状态
    void showPayFinishStatus(boolean isFinish,String payStatus);
}
