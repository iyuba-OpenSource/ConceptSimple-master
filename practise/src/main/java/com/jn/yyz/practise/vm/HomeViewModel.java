package com.jn.yyz.practise.vm;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.entity.GetSubmit;
import com.jn.yyz.practise.entity.SubmitTest;
import com.jn.yyz.practise.entity.TestBean;
import com.jn.yyz.practise.model.HomeModel;
import com.jn.yyz.practise.model.PractiseModel;
import com.jn.yyz.practise.model.bean.ExamBean;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;
import com.jn.yyz.practise.util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HomeViewModel extends ViewModel {


    private MutableLiveData<HomeTestTitleBean> homeTestTitleBeanMutableLiveData;

    /**
     * 积分和经验值
     */
    private MutableLiveData<ExpBean> expIntBeanMutableLiveData;

    private HomeModel homeModel;

    private MutableLiveData<Integer> integerMutableLiveData;

    private MutableLiveData<ExamBean> examBeanMutableLiveData;

    /**
     * 上传练习
     */
    private MutableLiveData<UploadTestBean> uploadTestBeanMutableLiveData;

    public HomeViewModel() {
        this.homeTestTitleBeanMutableLiveData = new MutableLiveData<>();
        this.homeModel = new HomeModel();
        this.integerMutableLiveData = new MutableLiveData<>();
        this.expIntBeanMutableLiveData = new MutableLiveData<>();
        this.uploadTestBeanMutableLiveData = new MutableLiveData<>();
        this.examBeanMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ExamBean> getExamBeanMutableLiveData() {
        return examBeanMutableLiveData;
    }

    public MutableLiveData<HomeTestTitleBean> getHomeTestTitleBeanMutableLiveData() {
        return homeTestTitleBeanMutableLiveData;
    }

    public MutableLiveData<UploadTestBean> getUploadTestBeanMutableLiveData() {
        return uploadTestBeanMutableLiveData;
    }

    public MutableLiveData<Integer> getIntegerMutableLiveData() {
        return integerMutableLiveData;
    }

    public MutableLiveData<ExpBean> getExpIntBeanMutableLiveData() {
        return expIntBeanMutableLiveData;
    }

    public void requestExamTitleList(int bookId, String type, String sign, String uid) {

        homeModel.getExamTitleList(bookId, type, sign, uid, new HomeModel.Callback() {
            @Override
            public void success(HomeTestTitleBean homeTestTitleBean) {

                homeTestTitleBeanMutableLiveData.postValue(homeTestTitleBean);
            }

            @Override
            public void error(Exception e) {

                integerMutableLiveData.postValue(30000);
            }
        });
    }


    /**
     * 获取积分和经验值
     * @param srid
     * @param lessonId
     */
    public void requestUpdateEXP(int srid, String lessonId, int score) {

        String signStr = MD5Util.MD5(PractiseConstant.UID + "iyuba" + srid + score);
        GetSubmit getSubmit = new GetSubmit(PractiseConstant.UID, srid, PractiseConstant.APPID, lessonId, score, signStr);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(getSubmit));
        homeModel.updateEXP(requestBody, new PractiseModel.ExpCallback() {
            @Override
            public void success(ExpBean expBean) {

                if (expBean.getResult() == 200) {

                    expBean.setId(Integer.parseInt(lessonId));
                    expBean.setSrid(srid);
                    expBean.setScore(score);
                    expIntBeanMutableLiveData.postValue(expBean);
                }
            }

            @Override
            public void error(Exception e) {

            }
        });
    }


    /**
     * 上传试题数据
     * @param mode
     * @param lessonId
     * @param lessonType
     * @param category
     * @param sign
     * @param beginTime
     * @param endTime
     */
    public void requestUpdateEnglishTestRecord(int mode, String lessonId, String lessonType, String category,
                                               String sign, String beginTime, String endTime, List<TestBean> testBeanList) {

        SubmitTest submitTest = new SubmitTest();
        submitTest.setUid(PractiseConstant.UID);
        submitTest.setAppId(PractiseConstant.APPID);
        submitTest.setMode(mode);
        submitTest.setLessonId(lessonId);
        submitTest.setLessonType(lessonType);
        submitTest.setCategory(category);
        submitTest.setSign(sign);
        submitTest.setTestList(testBeanList);
        submitTest.setBeginTime(beginTime);
        submitTest.setEndTime(endTime);


        String gsonStr2 = new Gson().toJson(submitTest);
        Log.d("upload", gsonStr2);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gsonStr2);

        homeModel.updateEnglishTestRecord(requestBody, new PractiseModel.UploadCallback() {
            @Override
            public void success(UploadTestBean uploadTestBean) {

                uploadTestBean.setId(lessonId);
                uploadTestBeanMutableLiveData.postValue(uploadTestBean);
                Log.d("upload", "updateEnglishTestRecord" + uploadTestBean.toString());
            }

            @Override
            public void error(Exception e) {

                Log.d("upload", "updateEnglishTestRecord" + e.toString());
            }
        });
    }


    public void requestGetWrongExamByUid(String uid, String type, int pageNumber,
                                         int pageSize, int version) {

        //"iyuba"+uid+type+东八区当前日期如（2024-01-01

        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        String dayStr = simpleDateFormat.format(new Date());
        String signStr = MD5Util.MD5("iyuba" + uid + type + dayStr);

        homeModel.requestGetWrongExamByUid(uid, type, signStr, pageNumber, pageSize, version, new HomeModel.ExamCallback() {
            @Override
            public void success(ExamBean examBean) {

                examBean.setPageNumber(pageNumber);
                examBeanMutableLiveData.postValue(examBean);
            }

            @Override
            public void error(Exception e) {

                integerMutableLiveData.postValue(40000);
            }
        });
    }
}
