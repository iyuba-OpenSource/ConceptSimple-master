package com.suzhou.concept.lil.service.temp;

/**
 * @title: 原文界面临时数据文件
 * @date: 2023/10/18 13:32
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenPlayTempBean {

    private String voaId;
    private String bookId;
    private String audioUrl;
    private String title;
    private String titleCn;
    private int showIndex;//数据所在的位置

    public ListenPlayTempBean(String voaId, String bookId, String audioUrl, String title, String titleCn, int showIndex) {
        this.voaId = voaId;
        this.bookId = bookId;
        this.audioUrl = audioUrl;
        this.title = title;
        this.titleCn = titleCn;
        this.showIndex = showIndex;
    }

    public String getVoaId() {
        return voaId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleCn() {
        return titleCn;
    }

    public int getShowIndex() {
        return showIndex;
    }
}
