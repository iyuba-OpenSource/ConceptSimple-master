package com.suzhou.concept.lil.event;

/**
 * @title: 回调显示的事件
 * @date: 2023/9/25 13:56
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RefreshEvent {

    /**************************类型************************/
    //toast显示
    public static final String SHOW_TOAST = "SHOW_TOAST";
    //dialog显示
    public static final String SHOW_DIALOG = "SHOW_DIALOG";
    //阅读-语言切换
    public static final String READ_LANGUAGE = "READ_LANGUAGE";
    //用户-登出
    public static final String USER_LOGOUT = "USER_LOGOUT";
    //用户-会员状态
    public static final String USER_VIP = "USER_VIP";
    //背景音频-停止
    public static final String AUDIO_STOP = "AUDIO_STOP";
    //背景音频-视图显示
    public static final String AUDIO_VIEW_SHOW = "AUDIO_VIEW_SHOW";
    //背景音频-视图隐藏
    public static final String AUDIO_VIEW_HIDE = "AUDIO_VIEW_HIDE";
    //背景音频-初始化
    public static final String AUDIO_INIT = "AUDIO_INIT";
    //我的配音-发布界面选中数据回调
    public static final String KOUYU_SELECT = "KOUYU_SELECT";
    //我的配音-无数据隐藏编辑
    public static final String KOUYU_NODATA = "KOUYU_NODATA";
    //我的配音-点赞后的回调
    public static final String KOUYU_AGREE = "KOUYU_AGREE";
    //学习界面-数据加载完成
    public static final String STUDY_FINISH = "STUDY_FINISH";
    //学习界面-排行界面刷新
    public static final String STUDY_RANK_REFRESH = "STUDY_RANK_REFRESH";
    //学习界面-音频切换
    public static final String STUDY_AUDIO_SWITCH = "STUDY_AUDIO_SWITCH";
    //生词本-数据刷新
    public static final String WORD_NOTE_REFRESH = "WORD_NOTE_REFRESH";
    //单词-刷新界面数据
    public static final String WORD_REFRESH = "WORD_REFRESH";
    //单词-刷新闯关进度
    public static final String WORD_PASS_REFRESH = "WORD_PASS_REFRESH";

    /*******************状态操作*******************/
    //可以播放
    public static final String AUDIO_CAN_PLAY = "0x01";
    //禁止播放
    public static final String AUDIO_WARN_PLAY = "0x02";
    //播放
    public static final String AUDIO_PLAY = "0x03";
    //暂停
    public static final String AUDIO_PAUSE = "0x04";

    private String type;
    private String msg;//预置状态操作：0x01--可以播放，0x02--禁止播放

    public RefreshEvent(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
