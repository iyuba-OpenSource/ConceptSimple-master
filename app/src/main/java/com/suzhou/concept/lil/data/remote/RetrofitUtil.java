package com.suzhou.concept.lil.data.remote;

import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.module.toolbox.MD5;
import com.suzhou.concept.AppClient;
import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.bean.YoungRankItem;
import com.suzhou.concept.bean.YoungSentenceItem;
import com.suzhou.concept.lil.data.remote.bean.Ad_result;
import com.suzhou.concept.lil.data.remote.bean.Ad_stream_result;
import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;
import com.suzhou.concept.lil.data.remote.bean.Ad_click_result;
import com.suzhou.concept.lil.data.remote.bean.Ad_clock_submit;
import com.suzhou.concept.lil.data.remote.bean.App_check;
import com.suzhou.concept.lil.data.remote.bean.Concept_four_word;
import com.suzhou.concept.lil.data.remote.bean.Concept_junior_word;
import com.suzhou.concept.lil.data.remote.bean.Exercise_concept;
import com.suzhou.concept.lil.data.remote.bean.Exercise_concept_submit;
import com.suzhou.concept.lil.data.remote.bean.Pay_alipay;
import com.suzhou.concept.lil.data.remote.bean.Rank_eval;
import com.suzhou.concept.lil.data.remote.bean.Rank_exercise;
import com.suzhou.concept.lil.data.remote.bean.Rank_listen;
import com.suzhou.concept.lil.data.remote.bean.Rank_read;
import com.suzhou.concept.lil.data.remote.bean.Rank_speech;
import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_result;
import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_submit;
import com.suzhou.concept.lil.data.remote.bean.User_info;
import com.suzhou.concept.lil.data.remote.bean.Word_pass;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBeanFix;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBeanNew;
import com.suzhou.concept.lil.data.remote.service.AdService;
import com.suzhou.concept.lil.data.remote.service.DownloadFileService;
import com.suzhou.concept.lil.data.remote.service.EvalService;
import com.suzhou.concept.lil.data.remote.service.ExerciseService;
import com.suzhou.concept.lil.data.remote.service.KouyuService;
import com.suzhou.concept.lil.data.remote.service.PayService;
import com.suzhou.concept.lil.data.remote.service.RankService;
import com.suzhou.concept.lil.data.remote.service.RewardService;
import com.suzhou.concept.lil.data.remote.service.StudyReportService;
import com.suzhou.concept.lil.data.remote.service.UserInfoService;
import com.suzhou.concept.lil.data.remote.service.VerifyService;
import com.suzhou.concept.lil.data.remote.service.WordCollectService;
import com.suzhou.concept.lil.data.remote.service.WordService;
import com.suzhou.concept.lil.ui.checkEval.CheckEvalBean;
import com.suzhou.concept.lil.ui.my.kouyu.KouyuDeleteBean;
import com.suzhou.concept.lil.ui.my.walletList.Reward_history;
import com.suzhou.concept.lil.ui.my.wordNote.WordDeleteBean;
import com.suzhou.concept.lil.ui.my.wordNote.WordNoteBean;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.bean.PublishEvalBean;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceMargeBean;
import com.suzhou.concept.lil.ui.study.eval.bean.WordExplainBean;
import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
import com.suzhou.concept.lil.ui.study.exercise.bean.ExerciseSubmitBean;
import com.suzhou.concept.lil.ui.study.listen.Listen_report;
import com.suzhou.concept.lil.ui.study.read.Read_mark;
import com.suzhou.concept.lil.util.DateUtil;
import com.suzhou.concept.lil.util.EncodeUtil;
import com.suzhou.concept.utils.GlobalMemory;
import com.suzhou.concept.utils.OtherUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitUtil {

    private static RetrofitUtil instance;

    public static RetrofitUtil getInstance() {
        if (instance == null) {
            synchronized (RetrofitUtil.class) {
                if (instance == null) {
                    instance = new RetrofitUtil();
                }
            }
        }
        return instance;
    }

    /******************************审核信息*************************/
    //获取当前审核状态
    public Observable<App_check> verify(int verifyId) {
        //http://api.qomolama.cn/getRegisterAll.jsp
        String url = "http://api.qomolama.cn/getRegisterAll.jsp";

        String version = HelpUtil.getAppVersion(AppClient.Companion.getContext(), AppClient.Companion.getContext().getPackageName());
        if (HelpUtil.isBelongToOppoPhone()) {
            version = "oppo_" + version;
        }

        VerifyService verifyService = createJson(VerifyService.class);
        return verifyService.verify(url, verifyId, version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /*****************************用户信息****************************/
    //获取用户信息20001
    public Observable<User_info> getUserInfo() {
        //http://api.iyuba.com.cn/v2/api.iyuba
        String url = "http://api." + OtherUtils.INSTANCE.getIyuba_com() + "/v2/api.iyuba";

        int protocol = 20001;
        int appId = AppClient.appId;
        String format = "json";
        String platform = "android";
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();

        String sign = EncodeUtil.md5(protocol + "" + userId + "iyubaV2");

        UserInfoService infoService = createJson(UserInfoService.class);
        return infoService.getUserInfo(url, protocol, appId, userId, userId, format, sign, platform)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /******************广告***************/
    //加载广告数据
    public Observable<List<Ad_result>> getAd(int flag) {
        String url = "http://dev." + OtherUtils.INSTANCE.getIyuba_cn() + "/getAdEntryAll.jsp";
        int appId = AppClient.appId;
        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();

        AdService adService = createJson(AdService.class);
        return adService.getAd(url, appId, uid, flag);
    }

    //加载广告数据
    public Observable<List<Ad_result>> getAd(int userId,int adFlag,int appId) {
        String url = "http://dev." + OtherUtils.INSTANCE.getIyuba_cn() + "/getAdEntryAll.jsp";

        AdService adService = createJson(AdService.class);
        return adService.getAd(url, appId, userId, adFlag);
    }

    //接口-获取信息流广告数据
    public Observable<List<Ad_stream_result>> getTemplateAd(int userId, int flag, int appId){
        String url = "http://dev." + OtherUtils.INSTANCE.getIyuba_cn() + "/getAdEntryAll.jsp";

        AdService adService = createJson(AdService.class);
        return adService.getStreamAd(url,appId,userId,flag);
    }

    //设置广告点击后的奖励
    public Observable<Ad_click_result> getAdClickReward(int uid, int platform, int adSpace) {
        String url = "http://api." + OtherUtils.INSTANCE.getIyuba_cn() + "/credits/adClickReward.jsp";
        int appId = AppClient.appId;
        long timestamp = System.currentTimeMillis() / 1000L;

        String sign = String.valueOf(uid) + String.valueOf(appId) + "iyubaV2" + String.valueOf(timestamp);
        sign = EncodeUtil.md5(sign);

        AdService commonService = createJson(AdService.class);
        return commonService.getAdClick(url, uid, appId, platform, adSpace, timestamp, sign);
    }

    //定时提交广告数据
    public Observable<Ad_clock_submit> submitAdData(int userId, String device, String deviceId, String packageName, String ads) {
        String url = "http://iuserspeech." + OtherUtils.INSTANCE.getIyuba_cn() + ":9001/japanapi/addAdInfo.jsp";
        int appId = AppClient.appId;
        long timestamp = System.currentTimeMillis() / 1000L;
        int os = 2;

        AdService commonService = createJson(AdService.class);
        return commonService.submitAdData(url, String.valueOf(timestamp), appId, device, deviceId, userId, packageName, os, ads);
    }

    /**********************************支付***************************/
    //获取支付宝的支付链接
    public Observable<Pay_alipay> getAliPayLink(String amount, String price, String productId, String subject, String body, long deduction) {
        String url = "http://vip." + OtherUtils.INSTANCE.getIyuba_cn() + "/alipay.jsp";

        int appId = AppClient.appId;
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String sign = EncodeUtil.md5(userId + "iyuba" + DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD));
        String subjectFix = EncodeUtil.encode(subject);
        String bodyFix = EncodeUtil.encode(body);

        PayService payService = createJson(PayService.class);
        return payService.getAliPayOrderLink(url, amount, appId, userId, sign, productId, subjectFix, bodyFix, price, deduction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /*****************评测***************/
    //评测句子
    public Observable<BaseBean<EvalShowBean>> updateEval(boolean isSentence, String filePath, String voaId, String paraId, String idIndex, String sentence) {
        String flg = isSentence ? "2" : "0";
        int appId = AppClient.appId;
        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String wordId = "0";

        String url = "http://iuserspeech." + OtherUtils.INSTANCE.getIyuba_cn() + ":9001/test/concept/";

        File file = new File(filePath);
        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", "concept")
                .addFormDataPart("flg", flg)

                .addFormDataPart("wordId", wordId)
                .addFormDataPart("userId", String.valueOf(uid))
                .addFormDataPart("appId", String.valueOf(appId))

                .addFormDataPart("newsId", voaId)
                .addFormDataPart("paraId", paraId)
                .addFormDataPart("IdIndex", idIndex)

                .addFormDataPart("sentence", sentence)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();


        EvalService evalService = createJson(EvalService.class);
        return evalService.uploadEvalData(url, multipartBody);
    }

    //发布单个句子
    public Observable<PublishEvalBean> publishEval(String voaId, String paraId, String idIndex, int score, String content) {
        String url = "http://voa." + OtherUtils.INSTANCE.getIyuba_cn() + "/voa/UnicomApi";

        String topic = AppClient.appName;
        String platform = "android";
        String format = "json";
        int protocol = 60002;
        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String userName = GlobalMemory.INSTANCE.getUserInfo().getUsername();
        int shuoshuotype = 2;

        EvalService evalService = createJson(EvalService.class);
        return evalService.publishEvalData(url, platform, format, protocol, topic, uid, userName, voaId, idIndex, paraId, score, shuoshuotype, content);
    }

    //合成句子
    public Observable<SentenceMargeBean> updateEvalMarge(List<EvalResultBean> evalList) {
        String url = "http://iuserspeech." + OtherUtils.INSTANCE.getIyuba_cn() + ":9001/test/merge/";
        String type = "concept";

        StringBuilder audioBuilder = new StringBuilder();
        for (int i = 0; i < evalList.size(); i++) {
            audioBuilder.append(evalList.get(i).url);

            if (i != evalList.size() - 1) {
                audioBuilder.append(",");
            }
        }

        String audios = audioBuilder.toString();

        EvalService evalService = createJson(EvalService.class);
        return evalService.uploadEvalMargeData(url, audios, type);
    }

    //发布合成句子
    public Observable<PublishEvalBean> publishEvalMarge(String voaId, List<EvalResultBean> evalList, String margeLink) {
        String url = "http://voa." + OtherUtils.INSTANCE.getIyuba_cn() + "/voa/UnicomApi";
        String topic = "concept";
        String platform = "android";
        String format = "json";
        int protocol = 60003;
        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String userName = GlobalMemory.INSTANCE.getUserInfo().getUsername();
        int shuoshuotype = 4;

        //合并数据，获取平均分
        int totalScore = 0;

        //增加奖励信息
        int rewardVersion = 1;
        int appId = AppClient.appId;

        if (evalList != null && evalList.size() > 0) {
            for (int i = 0; i < evalList.size(); i++) {
                int score = (int) (evalList.get(i).total_score * 20);
                totalScore += score;
            }
        }

        int avgScore = totalScore / evalList.size();

        EvalService evalService = createJson(EvalService.class);
        return evalService.publishEvalMargeData(url, topic, platform, format, protocol, uid, userName, voaId, avgScore, shuoshuotype, margeLink, appId, rewardVersion);
    }

    //查询单词释义
    //http://word.iyuba.cn/words/apiWordAi.jsp?q=morning&user_pron=&ori_pron=&appid=283&uid=14084808
    public Observable<WordExplainBean> searchWord(String word) {
        String url = "http://word." + OtherUtils.INSTANCE.getIyuba_cn() + "/words/apiWordAi.jsp";

        int appId = AppClient.appId;
        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();

        EvalService evalService = createJson(EvalService.class);
        return evalService.searchWord(url, word, "", "", appId, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //查询单词释义-新
    //https://apps.iyuba.cn/words/apiWordJson.jsp?q=IVF&format=json
    public Observable<WordExplainBean> searchWordNew(String word) {
        String url = "https://apps." + OtherUtils.INSTANCE.getIyuba_cn() + "/words/apiWordJson.jsp";

        String format = "json";

        EvalService evalService = createJson(EvalService.class);
        return evalService.searchWordNew(url, word, format)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //纠音中提交单词评测
    public Observable<CheckEvalBean> checkEval(String word, String paraId, String idIndex, String voaId, String filePath) {
        String url = "http://iuserspeech." + OtherUtils.INSTANCE.getIyuba_cn() + ":9001/test/ai10/";
        //sentence	text/plain; charset=utf-8		have
        //flg	text/plain; charset=utf-8		2
        //paraId	text/plain; charset=utf-8		3
        //newsId	text/plain; charset=utf-8		313002
        //IdIndex	text/plain; charset=utf-8		1
        //appId	text/plain; charset=utf-8		260
        //wordId	text/plain; charset=utf-8		1
        //type	text/plain; charset=utf-8		primaryenglish
        //userId	text/plain; charset=utf-8		12071118
        //file	multipart/form-data	have.amr	1.69 KB (1,734 bytes)

        int appId = AppClient.appId;
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String type = "concept";
        int wordId = 1;
        int flg = 2;

        File file = new File(filePath);
        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sentence", word)
                .addFormDataPart("flg", String.valueOf(flg))
                .addFormDataPart("paraId", paraId)
                .addFormDataPart("newsId", voaId)
                .addFormDataPart("IdIndex", idIndex)
                .addFormDataPart("appId", String.valueOf(appId))
                .addFormDataPart("wordId", String.valueOf(wordId))
                .addFormDataPart("type", type)
                .addFormDataPart("userId", String.valueOf(userId))
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        EvalService evalService = createJson(EvalService.class);
        return evalService.checkEvalData(url, multipartBody);
    }

    /**************内容数据****************/
    //获取全四册的内容
    public Observable<BaseBean<List<EvaluationSentenceItem>>> getUSUKLessonText(String voaId) {
        String url = "http://apps." + OtherUtils.INSTANCE.getIyuba_cn() + "/concept/getConceptSentence.jsp";

        EvalService evalService = createJson(EvalService.class);
        return evalService.getUSUKCourseDetailData(url, voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //获取青少版的内容
    public Observable<BaseBeanFix<List<YoungSentenceItem>>> getYoungLessonText(String voaId) {
        String url = "http://apps." + OtherUtils.INSTANCE.getIyuba_cn() + "/iyuba/textExamApi.jsp";

        EvalService evalService = createJson(EvalService.class);
        return evalService.getChildCourseDetailData(url, voaId);
    }

    /**************************奖励(钱包)数据********************************/
    //获取奖励记录数据
    public Observable<BaseBean<List<Reward_history>>> getRewardHistoryData(int pageIndex, int pageNum) {
        String url = "http://api." + OtherUtils.INSTANCE.getIyuba_cn() + "/credits/getuseractionrecord.jsp";

        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String sign = MD5.getMD5ofStr(uid + "iyuba" + DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD));

        RewardService rewardService = createJson(RewardService.class);
        return rewardService.getRewardHistory(url, uid, pageIndex, pageNum, sign);
    }

    /****************************学习报告*******************************/
    //提交阅读的学习报告
    public Observable<Read_mark> submitReadReport(long startTime, long endTime, int wordCount, String voaId) {
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/updateNewsStudyRecord.jsp";

        String format = "xml";
        String platform = "android";
        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();
        int endFlag = 1;
        String categoryId = "0";

        //去掉获取mac地址的功能
//        GetDeviceInfo deviceInfo = new GetDeviceInfo(AppClient.context);
//        String device = deviceInfo.getLocalDeviceType();
//        String deviceId = deviceInfo.getLocalMACAddress();
        String device = "";
        String deviceId = "";


        int appId = AppClient.appId;

        String startDate = DateUtil.toDateStr(startTime, DateUtil.YMDHMS);
        startDate = EncodeUtil.encode(startDate);
        String endDate = DateUtil.toDateStr(endTime, DateUtil.YMDHMS);
        endDate = EncodeUtil.encode(endDate);

        String type = "concept";

        //增加奖励机制
        int rewardVersion = 1;

        RewardService rewardService = createXml(RewardService.class);
        return rewardService.submitReadReport(url, format, uid, startDate, endDate, type, type, voaId, appId, device, deviceId, endFlag, wordCount, categoryId, platform, rewardVersion);
    }

    //提交听力学习报告
    public Observable<Listen_report> submitListenReport(long startTime, long endTime, int wordCount, String voaId, boolean isEnd) {
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/updateStudyRecordNew.jsp";

        String format = "json";
        String platform = "android";
        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();
        int endFlag = 0;
        if (isEnd) {
            endFlag = 1;
        }

        String device = "";
        String deviceId = "";

        int appId = AppClient.appId;
        String appName = AppClient.appName;
        int testMode = 1;
        int testNum = 1;

        String startDate = DateUtil.toDateStr(startTime, DateUtil.YMDHMS);
        String endDate = DateUtil.toDateStr(endTime, DateUtil.YMDHMS);

        String signData = uid + startTime + DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String sign = EncodeUtil.md5(signData);

        //增加奖励机制
        int rewardVersion = 1;

        RewardService rewardService = createJson(RewardService.class);
        return rewardService.submitListenReport(url, format, appId, appName, appName, voaId, uid, device, deviceId, startDate, endDate, endFlag, String.valueOf(wordCount), String.valueOf(testMode), platform, String.valueOf(testNum), sign, rewardVersion);
    }

    //提交单词闯关的学习报告
    public Observable<Report_wordBreak_result> submitWordBreakReport(int bookId, List<Report_wordBreak_submit.TestListBean> list) {
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/updateExamRecordNew.jsp";

        //设置需要上传的数据(这里去掉获取mac地址的功能)
//        String deviceId = "";
//        try {
//            GetDeviceInfo deviceInfo = new GetDeviceInfo(AppClient.context);
//            deviceId = deviceInfo.getLocalMACAddress();
//        } catch (Exception e) {
//            deviceId = "";
//        }
        String deviceId = "";

        int appId = AppClient.appId;
        String format = "json";
        String lesson = String.valueOf(bookId);
        int mode = 2;
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String sign = String.valueOf(userId) + String.valueOf(appId) + String.valueOf(bookId) + "iyubaExam" + DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        sign = EncodeUtil.md5(sign);

        Report_wordBreak_submit submit = new Report_wordBreak_submit();
        submit.setAppId(appId);
        submit.setFormat(format);
        submit.setUid(String.valueOf(userId));
        submit.setMode(mode);
        submit.setLesson(lesson);
        submit.setDeviceId(deviceId);
        submit.setScoreList(new ArrayList<>());
        submit.setSign(sign);
        submit.setTestList(list);

        //转换为json
        String json = GsonUtils.toJson(submit);

        //将数据转换为body
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        //提交数据
        StudyReportService reportService = createJson(StudyReportService.class);
        return reportService.submitWordBreakReport(url, body);
    }
    /*****************************************单词收藏*********************************/
    //获取已经收藏的单词
    public Observable<WordNoteBean> getCollectWordData(int pageIndex, int pageCount) {
        String url = "http://word." + OtherUtils.INSTANCE.getIyuba_cn() + "/words/wordListService.jsp";

        int uid = GlobalMemory.INSTANCE.getUserInfo().getUid();

        WordCollectService collectService = createXml(WordCollectService.class);
        return collectService.getCollectWordData(url, uid, pageIndex, pageCount);
    }

    //收藏/取消收藏单词
    public Observable<WordDeleteBean> collectWord(List<WordCollectBean> list, String mode) {
        String url = "http://word." + OtherUtils.INSTANCE.getIyuba_cn() + "/words/updateWord.jsp";

        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String groupName = "Iyuba";

        //合并单词显示
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i).word);

            if (i != list.size() - 1) {
                buffer.append(",");
            }
        }

        WordCollectService collectService = createXml(WordCollectService.class);
        return collectService.collectWord(url, userId, mode, groupName, buffer.toString());
    }

    /*****************************************单词收藏*********************************/
    //数据-获取新概念全四册的单词数据
    public Observable<BaseBean<List<Concept_four_word>>> getConceptFourWordData(int bookId) {
        String url = "http://apps." + OtherUtils.INSTANCE.getIyuba_cn() + "/concept/getConceptWord.jsp";

        WordService conceptService = createJson(WordService.class);
        return conceptService.getConceptFourWordData(url, bookId);
    }

    //数据-获取新概念青少版的单词数据
    public Observable<BaseBean<List<Concept_junior_word>>> getConceptJuniorWordData(int bookId) {
        String url = "http://apps." + OtherUtils.INSTANCE.getIyuba_cn() + "/iyuba/getWordByUnit.jsp";

        WordService conceptService = createJson(WordService.class);
        return conceptService.getConceptJuniorWordData(url, bookId);
    }

    /******************************************口语**********************************/
    //删除口语配音
    public Observable<KouyuDeleteBean> deleteKouyuData(int id) {
        String url = "http://voa." + OtherUtils.INSTANCE.getIyuba_cn() + "/voa/UnicomApi";

        int protocol = 61003;

        KouyuService kouyuService = createJson(KouyuService.class);
        return kouyuService.deleteKouyuData(url, protocol, id);
    }

    //获取口语的排行数据
    public Observable<BaseBeanNew<List<YoungRankItem>>> getKouyuRankData(int voaId,int startNum,int showCount){
        //http://voa.iyuba.cn/voa/UnicomApi
        String url = "http://voa."+OtherUtils.INSTANCE.getIyuba_cn()+"/voa/UnicomApi";

        int sort = 2;
        int protocol = 60001;
        String format = "json";
        String platform = "android";
        String topic = "concept";
        int selectType = 3;

        KouyuService kouyuService = createJson(KouyuService.class);
        return kouyuService.getKouyuRankData(url,platform,format,protocol,voaId,startNum,showCount,sort,topic,selectType);
    }

    /***********************************************习题*****************************/
    //获取新概念的习题数据
    public Observable<Exercise_concept> getConceptExerciseData(String voaId) {
        String url = "http://apps." + OtherUtils.INSTANCE.getIyuba_cn() + "/concept/getConceptExercise.jsp";

        ExerciseService exerciseService = createJson(ExerciseService.class);
        return exerciseService.getConceptExerciseData(url, voaId);
    }

    //提交新概念的习题数据
    public Observable<Exercise_concept_submit> submitConceptExerciseData(ExerciseSubmitBean submitBean) {
        //http://daxue.iyuba.cn/ecollege/updateTestRecordNew.jsp
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/updateTestRecordNew.jsp";

        String format = "json";
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        int appId = AppClient.appId;
        String appName = AppClient.appName;
        String deviceId = "";
        String sign = userId + "iyubaTest" + DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        sign = EncodeUtil.md5(sign);
        String dataList = GsonUtils.toJson(submitBean);

        ExerciseService exerciseService = createJson(ExerciseService.class);
        return exerciseService.submitConceptExercise(url, format, userId, appId, deviceId, appName, sign, dataList);
    }

    /**********************************排行******************************************/
    //获取学习界面的评测的排行数据
    public Observable<Rank_eval> getEvalRankData(int voaId, int userId, int start, int count, String type) {
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/getTopicRanking.jsp";

        String topic = "concept";
        String sign = userId + topic + String.valueOf(voaId) + start + count + DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        sign = EncodeUtil.md5(sign);

        RankService rankService = createJson(RankService.class);
        return rankService.getEvalRankData(url, topic, voaId, userId, start, count, sign, type);
    }

    //获取总排行的口语排行数据
    public Observable<Rank_speech> getSpeechRankData(int userId, String type, int start, int total) {
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/getTopicRanking.jsp";

        String topic = "concept";
        int topicId = 0;
        String shuoshuoType = "0";

        String date = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String sign = EncodeUtil.md5(String.valueOf(userId) + topic + String.valueOf(topicId) + String.valueOf(start) + String.valueOf(total) + date);

        RankService rankService = createJson(RankService.class);
        return rankService.getSpeechRankData(url, userId, type, start, total, sign, topic, topicId, shuoshuoType);
    }

    //获取总排行的阅读排行数据
    public Observable<Rank_read> getReadRankData(int userId, String type, int start, int total) {
        String url = "http://cms." + OtherUtils.INSTANCE.getIyuba_cn() + "/newsApi/getNewsRanking.jsp";

        String date = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String sign = EncodeUtil.md5(String.valueOf(userId) + type + String.valueOf(start) + String.valueOf(total) + date);

        RankService rankService = createJson(RankService.class);
        return rankService.getReadRankData(url, userId, type, start, total, sign);
    }

    //获取总排行的练习排行数据
    public Observable<Rank_exercise> getExerciseRankData(int userId, String type, int start, int total) {
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/getTestRanking.jsp";

        String date = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String sign = EncodeUtil.md5(String.valueOf(userId) + type + String.valueOf(start) + String.valueOf(total) + date);

        RankService rankService = createJson(RankService.class);
        return rankService.getExerciseRankData(url, userId, type, start, total, sign);
    }

    //获取总排行的听力排行数据
    public Observable<Rank_listen> getListenRankData(int userId, String type, int start, int total) {
        String url = "http://daxue." + OtherUtils.INSTANCE.getIyuba_cn() + "/ecollege/getStudyRanking.jsp";

        String mode = "listening";

        String date = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String sign = EncodeUtil.md5(String.valueOf(userId) + type + String.valueOf(start) + String.valueOf(total) + date);

        RankService rankService = createJson(RankService.class);
        return rankService.getListenRankData(url, userId, type, start, total, sign, mode);
    }

    /*****************************************下载文件**********************************/
    //下载文件
    public Observable<ResponseBody> downloadFile(String url) {
        //okhttp
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        String baseUrl = "";
        String baseFix1 = "http://staticvip." + OtherUtils.INSTANCE.getIyuba_cn();
        String baseFix2 = "http://static0." + OtherUtils.INSTANCE.getIyuba_cn();
        if (url.startsWith(baseFix1)) {
            baseUrl = baseFix1;
            url = url.replace(baseFix1, "");
        } else if (url.startsWith(baseFix2)) {
            baseUrl = baseFix2;
            url = url.replace(baseFix2, "");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        DownloadFileService dubbingService = retrofit.create(DownloadFileService.class);
        return dubbingService.downloadFile(url);
    }

    /*******************************************单词闯关********************************/
    //获取已经闯关的单词数据
    public Observable<Word_pass> getConceptWordPassData(int bookId,int userId){
        String url = "http://daxue."+ OtherUtils.INSTANCE.getIyuba_cn() +"/ecollege/getExamDetailNew.jsp";

        int appId = AppClient.appId;
        String testMode = "W";
        String mode = "2";
        String format = "json";
        String sign = String.valueOf(userId) + String.valueOf(bookId) + "2W" + String.valueOf(AppClient.appId)+ DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);
        sign = EncodeUtil.md5(sign);

        WordService wordService = createJson(WordService.class);
        return wordService.getConceptPassWordData(url,appId,bookId,testMode,mode,format,userId,sign);
    }

    private <T> T createJson(Class<T> clz) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl("http://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(clz);
    }

    private <T> T createXml(Class<T> clz) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl("http://www.baidu.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(clz);
    }

    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        return client;
    }
}
