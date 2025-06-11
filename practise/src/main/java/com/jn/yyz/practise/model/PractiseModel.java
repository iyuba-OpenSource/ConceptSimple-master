package com.jn.yyz.practise.model;

import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.model.bean.EvalBean;
import com.jn.yyz.practise.model.bean.ExamBean;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.TestRankingBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class PractiseModel {

    public void getExam(String url, String type, int pageNumber, int pageSize,
                        String lessonId, int maxId, int uid, int version, String sign, ExamCallback examCallback) {

        NetWorkManager
                .getRequestForApi()
                .getExam(url, type, pageNumber, pageSize, lessonId, maxId, uid, version, sign)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ExamBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ExamBean examBean) {

                        examCallback.success(examBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                        examCallback.error((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void test(RequestBody body, EvalCallback callback) {

        NetWorkManager
                .getRequestForApi()
                .eval(PractiseConstant.EVAL_URL, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvalBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EvalBean evalBean) {

                        callback.success(evalBean);
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


    public void updateEnglishTestRecord(RequestBody requestBody, UploadCallback uploadCallback) {

        NetWorkManager
                .getRequestForApi()
                .updateEnglishTestRecord(PractiseConstant.URL_UPDATE_ENGLISH_TEST_RECORD, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UploadTestBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UploadTestBean uploadTestBean) {

                        uploadCallback.success(uploadTestBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                        uploadCallback.error((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updateEXP(RequestBody requestBody, ExpCallback expCallback) {

        NetWorkManager
                .getRequestForApi()
                .updateEXP(PractiseConstant.URL_UPDATE_EXP, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ExpBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ExpBean expBean) {

                        expCallback.success(expBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                        expCallback.error((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getEnglishTestRanking(String uid, String flg, int pageNumber, int pageSize, String sign, TestRankingCallback testRankingCallback) {

        NetWorkManager
                .getRequestForApi()
                .getEnglishTestRanking(PractiseConstant.URL_GET_ENGLISH_TEST_RANKING, uid, flg, pageNumber, pageSize, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TestRankingBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TestRankingBean testRankingBean) {

                        testRankingCallback.success(testRankingBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                        testRankingCallback.error((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //新的练习题排行接口
    public void getExpRankData(String uid, String flg, int pageNumber, int pageSize, String sign, TestRankingCallback testRankingCallback){
        NetWorkManager
                .getRequestForApi()
                .getEnglishTestRanking("http://api.iyuba.cn/credits/getExpRanking.jsp", uid, flg, pageNumber, pageSize, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TestRankingBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TestRankingBean testRankingBean) {

                        testRankingCallback.success(testRankingBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                        testRankingCallback.error((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface TestRankingCallback {

        void success(TestRankingBean testRankingBean);

        void error(Exception e);
    }

    public interface ExpCallback {

        void success(ExpBean expBean);

        void error(Exception e);
    }

    public interface UploadCallback {

        void success(UploadTestBean uploadTestBean);

        void error(Exception e);
    }

    public interface EvalCallback {

        void success(EvalBean evalBean);

        void error(Exception e);
    }

    public interface ExamCallback {

        void success(ExamBean examBean);

        void error(Exception e);
    }

}
