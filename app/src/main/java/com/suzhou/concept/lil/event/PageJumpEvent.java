package com.suzhou.concept.lil.event;

/**
 * 界面跳转事件
 */
public class PageJumpEvent {

    //类型
    public static final String page_wordPass = "wordPass";//单词闯关


    //参数
    private String toPage;

    public PageJumpEvent(String toPage) {
        this.toPage = toPage;
    }

    public String getToPage() {
        return toPage;
    }
}
