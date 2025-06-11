package com.suzhou.concept.lil.ui.study.exercise.common;

import com.iyuba.module.toolbox.GsonUtils;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.Exercise_concept_submit;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.ui.study.exercise.bean.ExerciseMarkBean;
import com.suzhou.concept.lil.ui.study.exercise.bean.ExerciseSubmitBean;
import com.suzhou.concept.lil.util.LibRxUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommonExercisePresenter extends BasePresenter<CommonExerciseView> {

    //提交做题的学习报告
    private Disposable submitExerciseDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(submitExerciseDis);
    }

    //提交做题的学习报告
    public void submitExerciseData(Map<Integer, ExerciseMarkBean> dataMap){
        checkViewAttach();
        LibRxUtil.unDisposable(submitExerciseDis);

        //转换数据
        List<ExerciseMarkBean> dataList = new ArrayList<>();
        for (int key:dataMap.keySet()){
            dataList.add(dataMap.get(key));
        }
        ExerciseSubmitBean submitBean = new ExerciseSubmitBean(GsonUtils.toJson(dataList));

        RetrofitUtil.getInstance().submitConceptExerciseData(submitBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Exercise_concept_submit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitExerciseDis = d;
                    }

                    @Override
                    public void onNext(Exercise_concept_submit bean) {
                        if (bean!=null&&bean.getResult().equals("1")){
                            getMvpView().showSubmitResult(true);
                        }else {
                            getMvpView().showSubmitResult(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showSubmitResult(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
