package com.suzhou.concept.lil.service.data;

/**
 * @title: 原文界面播放回调
 * @date: 2023/10/18 11:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenPlayEvent {

    /**************************类型**********************/
    //播放加载完成
    public static final String PLAY_prepare_finish = "PLAY_prepare_finish";

    //播放时间完成
    public static final String PLAY_complete_finish = "PLAY_complete_finish";

    //正在播放
    public static final String PLAY_start = "PLAY_start";

    //暂停播放
    public static final String PLAY_pause = "PLAY_pause";

    //隐藏首页播放
    public static final String PLAY_ui_hide = "PLAY_ui_hide";

    //切换播放
    public static final String PLAY_switch = "PLAY_switch";

    //首页后台的文本和数据显示
    public static final String PLAY_bg_text = "PLAY_bg_text";

    /***************************事件*************************/
    //类型
    private String showType;
    //数据
    private String showMsg;

    public ListenPlayEvent(String showType) {
        this.showType = showType;
    }

    public ListenPlayEvent(String showType, String showMsg) {
        this.showType = showType;
        this.showMsg = showMsg;
    }

    public String getShowType() {
        return showType;
    }

    public String getShowMsg() {
        return showMsg;
    }
}
