package com.jn.yyz.practise.event;

/**
 * 关闭界面操作
 */
public class FinishPageEventBus {
    //界面类型
    public static final String Page_exercise = "exercise";//练习题界面

    //参数
    private String showPage;

    public FinishPageEventBus(String showPage) {
        this.showPage = showPage;
    }

    public String getShowPage() {
        return showPage;
    }
}
