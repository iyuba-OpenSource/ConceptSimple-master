package com.suzhou.concept.lil.event;

import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_submit;

import java.util.List;

/**
 * 单词闯关事件
 */
public class WordBreakEvent {

    private boolean breakStatus;
    private List<Report_wordBreak_submit.TestListBean> list;

    public WordBreakEvent(boolean breakStatus, List<Report_wordBreak_submit.TestListBean> list) {
        this.breakStatus = breakStatus;
        this.list = list;
    }

    public boolean isBreakStatus() {
        return breakStatus;
    }

    public List<Report_wordBreak_submit.TestListBean> getList() {
        return list;
    }
}
