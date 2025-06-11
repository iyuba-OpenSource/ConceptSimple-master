package com.jn.yyz.practise.model;

import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.model.bean.ExamBean;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class HomeModel {


    public void getExamTitleList(int bookId, String type, String sign, String uid, Callback callback) {

        NetWorkManager
                .getRequestForApi()
                .getExamTitleList(PractiseConstant.URL_GET_EXAM_TITLE_LIST, bookId, type, sign, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeTestTitleBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomeTestTitleBean homeTestTitleBean) {
                        callback.success(homeTestTitleBean);
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

    /**
     * 奖励经验值/积分
     * @param requestBody
     * @param expCallback
     */
    public void updateEXP(RequestBody requestBody, PractiseModel.ExpCallback expCallback) {

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


    /**
     * 上传量习题记录
     * @param requestBody
     * @param uploadCallback
     */
    public void updateEnglishTestRecord(RequestBody requestBody, PractiseModel.UploadCallback uploadCallback) {

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

    /**
     * 获取错题本错题
     * @param uid
     * @param type
     * @param sign
     * @param pageNumber
     * @param pageSize
     * @param version
     */
    public void requestGetWrongExamByUid(String uid, String type, String sign, int pageNumber,
                                         int pageSize, int version, ExamCallback examCallback) {

        NetWorkManager
                .getRequestForApi()
                .getWrongExamByUid(PractiseConstant.URL_GET_WRONG_EXAM_BY_UID, uid, type, sign, pageNumber, pageSize, version)
                .subscribeOn(Schedulers.io())
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

    public interface ExamCallback {

        void success(ExamBean examBean);

        void error(Exception e);
    }

    public interface Callback {

        void success(HomeTestTitleBean homeTestTitleBean);

        void error(Exception e);
    }
}
