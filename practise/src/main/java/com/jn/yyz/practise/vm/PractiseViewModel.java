package com.jn.yyz.practise.vm;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.entity.GetSubmit;
import com.jn.yyz.practise.entity.SubmitTest;
import com.jn.yyz.practise.entity.TestBean;
import com.jn.yyz.practise.model.PractiseModel;
import com.jn.yyz.practise.model.bean.EvalBean;
import com.jn.yyz.practise.model.bean.ExamBean;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.TestRankingBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;
import com.jn.yyz.practise.util.MD5Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PractiseViewModel extends ViewModel {

    private final MutableLiveData<ExamBean> examBeanMutableLiveData;

    private PractiseModel practiseModel;

    private MutableLiveData<EvalBean> evalBeanMutableLiveData;

    /**
     * 网络请求异常
     */
    private MutableLiveData<Integer> integerMutableLiveData;

    private MutableLiveData<UploadTestBean> uploadTestBeanMutableLiveData;

    private MutableLiveData<ExpBean> expIntBeanMutableLiveData;

    private MutableLiveData<TestRankingBean> testRankingBeanMutableLiveData;

    public PractiseViewModel() {

        this.examBeanMutableLiveData = new MutableLiveData<>();
        this.evalBeanMutableLiveData = new MutableLiveData<>();
        this.practiseModel = new PractiseModel();
        this.integerMutableLiveData = new MutableLiveData<>();
        this.uploadTestBeanMutableLiveData = new MutableLiveData<>();
        this.expIntBeanMutableLiveData = new MutableLiveData<>();
        this.testRankingBeanMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<UploadTestBean> getUploadTestBeanMutableLiveData() {
        return uploadTestBeanMutableLiveData;
    }


    public MutableLiveData<Integer> getIntegerMutableLiveData() {
        return integerMutableLiveData;
    }

    public MutableLiveData<ExamBean> getExamBeanMutableLiveData() {
        return examBeanMutableLiveData;
    }


    public MutableLiveData<EvalBean> getEvalBeanMutableLiveData() {
        return evalBeanMutableLiveData;
    }

    public MutableLiveData<ExpBean> getExpBeanMutableLiveData() {
        return expIntBeanMutableLiveData;
    }

    public MutableLiveData<TestRankingBean> getTestRankingBeanMutableLiveData() {
        return testRankingBeanMutableLiveData;
    }

    public void requestExam(String type, int maxId, int uid, int pageNumber, int pageSize, String lessonId, int version) {

        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");

        String signStr = "iyuba" + 0 + "smallvideo" + 0 + simpleDateFormat.format(new Date());
        String sign = MD5Util.MD5(signStr);
        practiseModel.getExam(PractiseConstant.URL_GET_EXAM, type, pageNumber, pageSize,
                lessonId, maxId, uid, version, null, new PractiseModel.ExamCallback() {
                    @Override
                    public void success(ExamBean examBean) {

                        examBeanMutableLiveData.postValue(examBean);
                    }

                    @Override
                    public void error(Exception e) {

                        integerMutableLiveData.postValue(30001);
                    }
                });
    }


    /**
     * 评测
     * @param filePath
     * @param appid
     * @param type
     * @param uid
     * @param newsId
     * @param paraId
     * @param IdIndex
     * @param sentence
     */
    public void requestEval(String filePath, String appid, String type, String uid, String newsId,
                            String paraId, String IdIndex, String sentence) {

        MediaType mediaType = MediaType.parse("application/octet-stream");
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(mediaType, file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", type)
                .addFormDataPart("userId", uid)
                .addFormDataPart("newsId", newsId)
                .addFormDataPart("paraId", paraId)
                .addFormDataPart("IdIndex", IdIndex)
                .addFormDataPart("sentence", sentence)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("wordId", "0")
                .addFormDataPart("flg", "2")
                .addFormDataPart("appId", appid)
                .build();
        practiseModel.test(requestBody, new PractiseModel.EvalCallback() {
            @Override
            public void success(EvalBean evalBean) {

                evalBeanMutableLiveData.postValue(evalBean);
            }

            @Override
            public void error(Exception e) {

                integerMutableLiveData.postValue(30002);
            }
        });
    }

    /**
     * 上传试题数据
     * @param mode
     * @param lessonId 错题本传0
     * @param lessonType
     * @param category
     * @param sign
     * @param beginTime
     * @param endTime
     */
    public void requestUpdateEnglishTestRecord(int mode, String lessonId, String lessonType, String category,
                                               String sign, String beginTime, String endTime, List<ExamBean.DataDTO> dataDTOList) {


        List<TestBean> testBeanList = new ArrayList<>();
        for (int i = 0; i < dataDTOList.size(); i++) {

            ExamBean.DataDTO dataDTO = dataDTOList.get(i);
            if (dataDTO.getTestType() == 205 || dataDTO.getTestType() == 206) {

                dataDTO.setCorrectFlag(1);
                dataDTO.setUserAnswer(dataDTO.getAnswer());
            }
            TestBean testBean = new TestBean(dataDTO.getTestId(), dataDTO.getTestIndex(), dataDTO.getCorrectFlag()
                    , dataDTO.getUserAnswer(), dataDTO.getTestTime(), dataDTO.getLessonId() + "");
            testBeanList.add(testBean);
        }


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

        practiseModel.updateEnglishTestRecord(requestBody, new PractiseModel.UploadCallback() {
            @Override
            public void success(UploadTestBean uploadTestBean) {

                uploadTestBeanMutableLiveData.postValue(uploadTestBean);
                Log.d("upload", "updateEnglishTestRecord" + uploadTestBean.toString());
            }

            @Override
            public void error(Exception e) {

                Log.d("upload", "updateEnglishTestRecord" + e.toString());
            }
        });
    }


    public void requestUpdateEXP(int srid, String lessonId, List<ExamBean.DataDTO> dataDTOList) {


        //正确的个数
        int count = 0;
        for (int i = 0; i < dataDTOList.size(); i++) {

            ExamBean.DataDTO dataDTO = dataDTOList.get(i);
            if (dataDTO.getCorrectFlag() == 1) {
                count++;
            }
        }

        String signStr = MD5Util.MD5(PractiseConstant.UID + "iyuba" + srid + count);
        GetSubmit getSubmit = new GetSubmit(PractiseConstant.UID, srid, PractiseConstant.APPID, lessonId, count, signStr);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(getSubmit));
        int finalCount = count;
        practiseModel.updateEXP(requestBody, new PractiseModel.ExpCallback() {
            @Override
            public void success(ExpBean expBean) {

                if (expBean.getResult() == 200) {

                    expBean.setSrid(srid);
                    expBean.setScore(finalCount);
                    expIntBeanMutableLiveData.postValue(expBean);
                }
            }

            @Override
            public void error(Exception e) {

            }
        });
    }

    public void requestEnglishTestRanking(String flg, int pageNumber, int pageSize) {


        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        String dateStr = simpleDateFormat.format(new Date());
        String signMd5 = MD5Util.MD5("iyubaExam" + PractiseConstant.UID + flg + pageNumber + "" + pageSize + dateStr);

        practiseModel.getEnglishTestRanking(PractiseConstant.UID, flg, pageNumber, pageSize, signMd5, new PractiseModel.TestRankingCallback() {
            @Override
            public void success(TestRankingBean testRankingBean) {

                testRankingBean.setPage(pageNumber);
                testRankingBeanMutableLiveData.postValue(testRankingBean);
            }

            @Override
            public void error(Exception e) {

            }
        });
    }

    //新的练习题接口
    public void requestExpRankData(String flg, int pageNumber, int pageSize){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        String dateStr = simpleDateFormat.format(new Date());
        String signMd5 = MD5Util.MD5("iyubaExam" + PractiseConstant.UID + flg + pageNumber + "" + pageSize + dateStr);

        practiseModel.getExpRankData(PractiseConstant.UID, flg, pageNumber, pageSize, signMd5, new PractiseModel.TestRankingCallback() {
            @Override
            public void success(TestRankingBean testRankingBean) {
                testRankingBean.setPage(pageNumber);
                testRankingBeanMutableLiveData.postValue(testRankingBean);
            }

            @Override
            public void error(Exception e) {

            }
        });
    }
}
