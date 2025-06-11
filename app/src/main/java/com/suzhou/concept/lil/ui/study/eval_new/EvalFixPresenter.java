package com.suzhou.concept.lil.ui.study.eval_new;

import android.text.TextUtils;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.bean.PublishEvalBean;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceMargeBean;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.LibRxUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EvalFixPresenter extends BasePresenter<EvalFixView> {


    //提交评测数据
    private Disposable submitEvalDis;

    //提交排行榜发布
    private Disposable submitEvalRankDis;

    //合成音频
    private Disposable margeAudioDis;

    //提交合成的音频
    private Disposable submitMargeRankDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(submitEvalDis);
        LibRxUtil.unDisposable(submitEvalRankDis);
        LibRxUtil.unDisposable(margeAudioDis);
        LibRxUtil.unDisposable(submitMargeRankDis);
    }

    //提交评测数据
    public void submitEval(String voaId,String paraId,String indexId,String sentence,String filePath){
        checkViewAttach();
        LibRxUtil.unDisposable(submitEvalDis);
        RetrofitUtil.getInstance().updateEval(true,filePath,voaId,paraId,indexId,sentence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean<EvalShowBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        submitEvalDis = disposable;
                    }

                    @Override
                    public void onNext(BaseBean<EvalShowBean> bean) {
                        if (bean!=null && bean.getResult().equals("1")){
                            getMvpView().showEvalResult(bean.getData(),null);
                        }else {
                            getMvpView().showEvalResult(null,"获取评测数据失败～");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showEvalResult(null,"获取评测数据异常～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //提交排行榜发布
    public void submitEvalRank(String voaId,String idIndex,String paraId,int score,String evalUrl){
        checkViewAttach();
        LibRxUtil.unDisposable(submitEvalRankDis);
        RetrofitUtil.getInstance().publishEval(voaId, paraId, idIndex, score, evalUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PublishEvalBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        submitEvalRankDis = disposable;
                    }

                    @Override
                    public void onNext(PublishEvalBean bean) {
                        if (bean!=null&&bean.getMessage().toLowerCase().equals("ok")){
                            getMvpView().showEvalRankResult(true,null);
                        }else {
                            getMvpView().showEvalRankResult(false,"提交排行榜失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showEvalRankResult(false,"提交排行榜异常，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //合成音频
    public void margeAudio(List<EvalResultBean> evalList){
        checkViewAttach();
        LibRxUtil.unDisposable(margeAudioDis);
        RetrofitUtil.getInstance().updateEvalMarge(evalList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SentenceMargeBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        margeAudioDis = disposable;
                    }

                    @Override
                    public void onNext(SentenceMargeBean bean) {
                        if (bean!=null && bean.getResult().equals("1")){
                            //合并分数显示
                            int margeScore = 0;
                            for (int i = 0; i < evalList.size(); i++) {
                                margeScore+=(int) (evalList.get(i).total_score*20);
                            }
                            margeScore = (int) (margeScore/evalList.size());
                            //回调
                            getMvpView().showMargeResult(bean.getUrl(),margeScore,null);
                        }else {
                            getMvpView().showMargeResult(null,0,"合成音频失败～");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showMargeResult(null,0,"合成音频异常～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //提交合成的音频
    public void submitMargeAudio(List<EvalResultBean> evalList,String margeAudioUrl){
        checkViewAttach();
        LibRxUtil.unDisposable(submitMargeRankDis);
        RetrofitUtil.getInstance().publishEvalMarge(AppClient.Companion.getConceptItem().getVoa_id(),evalList,margeAudioUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PublishEvalBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        submitMargeRankDis = disposable;
                    }

                    @Override
                    public void onNext(PublishEvalBean bean) {
                        if (bean!=null && bean.getMessage().toLowerCase().equals("ok")){
                            //判断奖励进行显示
                            String reward = bean.getReward();
                            float showMoney = TextUtils.isEmpty(reward)?0f: Float.parseFloat(reward);
                            if (showMoney>0){
                                String showMsg = String.format("本次学习获得%1$s元,已自动存入您的钱包账户", BigDecimalUtil.trans2Double(showMoney*0.01f));
                                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.SHOW_DIALOG,showMsg));
                            }
                            getMvpView().showMargePublishResult(true,null);
                        }else {
                            getMvpView().showMargePublishResult(false,"发布合成评测数据到排行榜失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showMargePublishResult(false,"发布合成评测数据到排行榜异常，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
