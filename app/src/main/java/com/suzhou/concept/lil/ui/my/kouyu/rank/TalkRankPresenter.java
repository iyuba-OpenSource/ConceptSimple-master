package com.suzhou.concept.lil.ui.my.kouyu.rank;

import com.suzhou.concept.bean.YoungRankItem;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBeanNew;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.util.LibRxUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TalkRankPresenter extends BasePresenter<TalkRankView> {

    //获取排行数据
    private Disposable getRankDataDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(getRankDataDis);
    }


    //获取排行的数据
    public void getRankData(int voaId,int startNum,int showCount){
        checkViewAttach();
        LibRxUtil.unDisposable(getRankDataDis);
        RetrofitUtil.getInstance().getKouyuRankData(voaId, startNum, showCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBeanNew<List<YoungRankItem>>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getRankDataDis = disposable;
                    }

                    @Override
                    public void onNext(BaseBeanNew<List<YoungRankItem>> bean) {
                        if (bean!=null&&bean.getData()!=null&&bean.getData().size()>0){
                            getMvpView().showRankData(bean.getData(),null);
                        }else {
                            getMvpView().showRankData(null,null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showRankData(null,"获取排行数据异常，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
