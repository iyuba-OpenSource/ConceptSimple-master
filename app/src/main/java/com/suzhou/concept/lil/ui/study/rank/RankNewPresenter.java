package com.suzhou.concept.lil.ui.study.rank;

import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.Rank_eval;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.util.LibRxUtil;
import com.suzhou.concept.utils.OtherUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RankNewPresenter extends BasePresenter<RankNewView> {

    //加载排行数据
    private Disposable getRankDataDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(getRankDataDis);
    }

    //加载排行数据
    public void getRankData(int start,int count,int voaId,int userId){
        checkViewAttach();
        LibRxUtil.unDisposable(getRankDataDis);

        String type = "D";

        RetrofitUtil.getInstance().getEvalRankData(voaId,userId,start,count,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rank_eval>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getRankDataDis  = disposable;
                    }

                    @Override
                    public void onNext(Rank_eval bean) {
                        if (bean!=null){
                            //个人数据
                            getMvpView().showUserRankData(bean);
                            //全部数据
                            getMvpView().showAllRankData(bean.getData());
                        }else {
                            getMvpView().showAllRankData(null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        getMvpView().showAllRankData(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取用户的头像数据
    public String getUserPicUrl(int userId){
        return "http://api."+ OtherUtils.INSTANCE.getIyuba_com()+"/v2/api.iyuba?protocol=10005&size=middle&timestamp="+ System.currentTimeMillis()+"&uid="+userId;
    }
}
