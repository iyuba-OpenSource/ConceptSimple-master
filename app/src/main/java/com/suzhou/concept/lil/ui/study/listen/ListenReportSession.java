package com.suzhou.concept.lil.ui.study.listen;

import android.text.TextUtils;

import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.LibRxTimer;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 听力学习报告
 * @date: 2023/10/23 16:10
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenReportSession {
    private static ListenReportSession instance;

    public static ListenReportSession getInstance(){
        if (instance==null){
            synchronized (ListenReportSession.class){
                if (instance==null){
                    instance = new ListenReportSession();
                }
            }
        }
        return instance;
    }

    //开始时间
    private long startTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    //结束时间
    private long endTime;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    //单词数量
    private int wordCount = 0;

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(List<EvaluationSentenceItem> list) {
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                String sentence = list.get(i).getSentence();
                //筛选
                sentence = sentence.replace(","," ");
                sentence = sentence.replace("."," ");
                sentence = sentence.replace("!"," ");
                sentence = sentence.replace("?"," ");
                //剪切并合并数据
                String[] wordArray = sentence.split(" ");
                wordCount+=wordArray.length;
            }
        }else {
            wordCount = 0;
        }
    }

    //提交听力学习报告
    private Disposable submitReportDis;
    public void submitReport(String voaId,boolean isEnd){
        LibRxTimer.getInstance().unDisposable(submitReportDis);
        RetrofitUtil.getInstance().submitListenReport(startTime,endTime,wordCount,voaId,isEnd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Listen_report>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitReportDis = d;
                    }

                    @Override
                    public void onNext(Listen_report bean) {
                        if (bean!=null&&bean.getResult().equals("1")){
                            float price = TextUtils.isEmpty(bean.getReward())?0:Float.parseFloat(bean.getReward());
                            if (price>0){
                                price = (float) BigDecimalUtil.trans2Double(price*0.01f);
                                String formatStr = "本次学习获得%1$s元红包奖励,已自动存入您的钱包账户";
                                String showMsg = String.format(formatStr,String.valueOf(price));
                                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.SHOW_TOAST,showMsg));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        int a = 0;
                    }

                    @Override
                    public void onComplete() {
                        LibRxTimer.getInstance().unDisposable(submitReportDis);
                    }
                });
    }
}
