package com.suzhou.concept.lil.util.download;

public class DownloadNewFileEvent {
    //类型
    public static final String type_audio = "audio";//音频
    public static final String type_video = "video";//视频

    //下载状态
    public static final String state_downloading = "downloading";//下载中
    public static final String state_finish = "finish";//下载完成
    public static final String state_error = "error";//下载异常

    private String downloadStatus;//下载状态
    private String showMsg;//显示信息

    public DownloadNewFileEvent(String downloadStatus, String showMsg) {
        this.downloadStatus = downloadStatus;
        this.showMsg = showMsg;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public String getShowMsg() {
        return showMsg;
    }
}
