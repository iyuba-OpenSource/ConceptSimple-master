package com.suzhou.concept.lil.ui.my.rank.detail;

import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.Rank_exercise;
import com.suzhou.concept.lil.data.remote.bean.Rank_listen;
import com.suzhou.concept.lil.data.remote.bean.Rank_read;
import com.suzhou.concept.lil.data.remote.bean.Rank_speech;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.LibRxUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RankDetailPresenter extends BasePresenter<RankDetailView> {

    //获取听力排行数据
    private Disposable getListenRankDis;
    //获取口语排行数据
    private Disposable getSpeechRankDis;
    //获取阅读排行数据
    private Disposable getReadRankDis;
    //获取练习排行数据
    private Disposable getExerciseRankDis;


    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(getListenRankDis);
        LibRxUtil.unDisposable(getSpeechRankDis);
        LibRxUtil.unDisposable(getReadRankDis);
        LibRxUtil.unDisposable(getExerciseRankDis);
    }

    //获取听力的排行数据
    public void getListenRankData(int userId,String type,int start,int total){
        checkViewAttach();
        LibRxUtil.unDisposable(getListenRankDis);
        RetrofitUtil.getInstance().getListenRankData(userId, type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rank_listen>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getListenRankDis = disposable;
                    }

                    @Override
                    public void onNext(Rank_listen bean) {
                        if (bean!=null){
                            getMvpView().showListenRankData(bean);
                        }else {
                            getMvpView().showListenRankData(null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showListenRankData(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取口语的排行数据
    public void getSpeechRankData(int userId,String type,int start,int total){
        checkViewAttach();
        LibRxUtil.unDisposable(getSpeechRankDis);
        RetrofitUtil.getInstance().getSpeechRankData(userId, type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rank_speech>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getSpeechRankDis = disposable;
                    }

                    @Override
                    public void onNext(Rank_speech bean) {
                        if (bean!=null){
                            getMvpView().showSpeechRankData(bean);
                        }else {
                            getMvpView().showSpeechRankData(null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showSpeechRankData(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取阅读的排行数据
    public void getReadRankData(int userId,String type,int start,int total){
        checkViewAttach();
        LibRxUtil.unDisposable(getReadRankDis);
        RetrofitUtil.getInstance().getReadRankData(userId, type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rank_read>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getReadRankDis = disposable;
                    }

                    @Override
                    public void onNext(Rank_read bean) {
                        if (bean!=null){
                            getMvpView().showReadRankData(bean);
                        }else {
                            getMvpView().showReadRankData(null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showReadRankData(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取练习的排行数据
    public void getExerciseRankData(int userId,String type,int start,int total){
        checkViewAttach();
        LibRxUtil.unDisposable(getExerciseRankDis);
        RetrofitUtil.getInstance().getExerciseRankData(userId, type, start, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rank_exercise>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getExerciseRankDis = disposable;
                    }

                    @Override
                    public void onNext(Rank_exercise bean) {
                        if (bean!=null){
                            getMvpView().showExerciseRankData(bean);
                        }else {
                            getMvpView().showExerciseRankData(null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showExerciseRankData(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**********************************转换列表数据*************************************/
    //转换听力的数据
    public List<RankDetailShowBean> transListenRankData(Rank_listen bean){
        List<RankDetailShowBean> list = new ArrayList<>();
        if (bean!=null && bean.getData() !=null && bean.getData().size()>0){
            for (int i = 0; i < bean.getData().size(); i++) {
                Rank_listen.DataDTO listenData = bean.getData().get(i);

                //转换
                RankDetailShowBean showBean = new RankDetailShowBean();
                showBean.setShowType(RankDetailShowBean.ShowType.listen);

                showBean.setRankIndex(listenData.getRanking());
                showBean.setImageUrl(listenData.getImgSrc());
                showBean.setShowName(listenData.getName());

                int showTime = listenData.getTotalTime()/60;
                showBean.setListenTime(showTime);
                showBean.setListenArticleCount(listenData.getTotalEssay());
                showBean.setListenWordsCount(listenData.getTotalWord());

                list.add(showBean);
            }
        }

        return list;
    }

    //转换口语的数据
    public List<RankDetailShowBean> transSpeechRankData(Rank_speech bean){
        List<RankDetailShowBean> list = new ArrayList<>();
        if (bean!=null || bean.getData()!=null || bean.getData().size()>0){
            for (int i = 0; i < bean.getData().size(); i++) {
                Rank_speech.DataDTO speechData = bean.getData().get(i);

                RankDetailShowBean showBean = new RankDetailShowBean();
                showBean.setShowType(RankDetailShowBean.ShowType.speech);

                showBean.setRankIndex(speechData.getRanking());
                showBean.setImageUrl(speechData.getImgSrc());
                showBean.setShowName(speechData.getName());

                showBean.setSpeechTotalScore(speechData.getScores());
                showBean.setSpeechSentenceCount(speechData.getCount());

                double averageScore = 0;
                if (speechData.getCount()>0){
                    averageScore = BigDecimalUtil.trans2Double(speechData.getScores()*1.0f/speechData.getCount());
                }
                showBean.setSpeechAverageScore(averageScore);

                list.add(showBean);
            }
        }
        return list;
    }

    //转换阅读的数据
    public List<RankDetailShowBean> transReadRankData(Rank_read bean){
        List<RankDetailShowBean> list = new ArrayList<>();
        if (bean!=null || bean.getData()!=null || bean.getData().size()>0){
            for (int i = 0; i < bean.getData().size(); i++) {
                Rank_read.DataDTO readBean = bean.getData().get(i);

                RankDetailShowBean showBean = new RankDetailShowBean();
                showBean.setShowType(RankDetailShowBean.ShowType.read);

                showBean.setRankIndex(readBean.getRanking());
                showBean.setImageUrl(readBean.getImgSrc());
                showBean.setShowName(readBean.getName());

                showBean.setReadArticleCount(readBean.getCnt());
                showBean.setReadWordsCount(readBean.getWords());
                showBean.setReadWpm(readBean.getWpm());

                list.add(showBean);
            }
        }
        return list;
    }

    //转换练习的数据
    public List<RankDetailShowBean> transExerciseRankData(Rank_exercise bean){
        List<RankDetailShowBean> list = new ArrayList<>();
        if (bean!=null || bean.getData()!=null || bean.getData().size()>0) {
            for (int i = 0; i < bean.getData().size(); i++) {
                Rank_exercise.DataDTO exerciseData = bean.getData().get(i);

                RankDetailShowBean showBean = new RankDetailShowBean();
                showBean.setShowType(RankDetailShowBean.ShowType.exercise);

                showBean.setRankIndex(exerciseData.getRanking());
                showBean.setImageUrl(exerciseData.getImgSrc());
                showBean.setShowName(exerciseData.getName());

                showBean.setExerciseRightCount(exerciseData.getTotalRight());
                showBean.setExerciseTotalCount(exerciseData.getTotalTest());

                double rightRate = 0;
                if (exerciseData.getTotalTest()>0){
                    rightRate = BigDecimalUtil.trans2Double(exerciseData.getTotalRight()*1.0f/exerciseData.getTotalTest());
                }
                showBean.setExerciseRightRate(rightRate);

                list.add(showBean);
            }
        }
        return list;
    }

    /***********************************转换用户数据*********************************/
    //转换听力的用户数据
    public RankDetailShowBean transListenUserData(Rank_listen bean){
        if (bean.getMyid()==0){
            return null;
        }

        RankDetailShowBean showBean = new RankDetailShowBean();
        showBean.setShowType(RankDetailShowBean.ShowType.listen);

        showBean.setRankIndex(bean.getMyranking());
        showBean.setShowName(bean.getMyname());
        showBean.setImageUrl(bean.getMyimgSrc());

        showBean.setListenTime(bean.getTotalTime());
        showBean.setListenWordsCount(bean.getTotalWord());
        showBean.setListenArticleCount(bean.getTotalEssay());

        return showBean;
    }

    //转换口语的用户数据
    public RankDetailShowBean transSpeechUserData(Rank_speech bean){
        if (bean.getMyid()==0){
            return null;
        }

        RankDetailShowBean showBean = new RankDetailShowBean();
        showBean.setShowType(RankDetailShowBean.ShowType.speech);

        showBean.setRankIndex(bean.getMyranking());
        showBean.setImageUrl(bean.getMyimgSrc());
        showBean.setShowName(bean.getMyname());

        showBean.setSpeechTotalScore(bean.getMyscores());
        showBean.setSpeechSentenceCount(bean.getMycount());

        double averageScore = 0;
        if (bean.getMycount()>0){
            averageScore = BigDecimalUtil.trans2Double(bean.getMyscores()*1.0f/bean.getMycount());
        }
        showBean.setSpeechAverageScore(averageScore);

        return showBean;
    }

    //转换阅读的用户数据
    public RankDetailShowBean transReadUserData(Rank_read bean){
        if (bean.getMyid()==0){
            return null;
        }

        RankDetailShowBean showBean = new RankDetailShowBean();
        showBean.setShowType(RankDetailShowBean.ShowType.read);

        showBean.setRankIndex(bean.getMyranking());
        showBean.setImageUrl(bean.getMyimgSrc());
        showBean.setShowName(bean.getMyname());

        showBean.setReadArticleCount(bean.getMycnt());
        showBean.setReadWordsCount(bean.getMywords());
        showBean.setReadWpm(bean.getMywpm());

        return showBean;
    }

    //转换练习的用户数据
    public RankDetailShowBean transExerciseUserData(Rank_exercise bean){
        if (bean.getMyid()==0){
            return null;
        }

        RankDetailShowBean showBean = new RankDetailShowBean();
        showBean.setShowType(RankDetailShowBean.ShowType.exercise);

        showBean.setRankIndex(bean.getMyranking());
        showBean.setImageUrl(bean.getMyimgSrc());
        showBean.setShowName(bean.getMyname());

        showBean.setExerciseRightCount(bean.getTotalRight());
        showBean.setExerciseTotalCount(bean.getTotalTest());

        double rightRate = 0;
        if (bean.getTotalTest()>0){
            rightRate = BigDecimalUtil.trans2Double(bean.getTotalRight()*1.0f/bean.getTotalTest());
        }
        showBean.setExerciseRightRate(rightRate);

        return showBean;
    }
}
