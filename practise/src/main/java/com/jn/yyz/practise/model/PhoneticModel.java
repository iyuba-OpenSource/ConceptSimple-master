package com.jn.yyz.practise.model;

import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.model.bean.PronBean;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PhoneticModel {


    public void getPronNew(Callback callback) {

        NetWorkManager
                .getRequestForApi()
                .getPronNew(PractiseConstant.URL_GET_PRON_NEW)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PronBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PronBean pronBean) {

                        callback.success(pronBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.error((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public interface Callback {

        void success(PronBean pronBean);

        void error(Exception e);
    }


}
