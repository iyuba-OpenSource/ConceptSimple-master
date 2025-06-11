package com.suzhou.concept.lil.ui.study.read;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.ad.adblocker.AdBlocker;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.bean.YoungSentenceItem;
import com.suzhou.concept.dao.AppDatabase;
import com.suzhou.concept.lil.ui.ad.util.show.AdShowUtil;
import com.suzhou.concept.lil.ui.ad.util.show.interstitial.AdInterstitialShowManager;
import com.suzhou.concept.lil.ui.ad.util.show.interstitial.AdInterstitialViewBean;
import com.suzhou.concept.lil.ui.ad.util.upload.AdUploadManager;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.event.UserinfoRefreshEvent;
import com.suzhou.concept.lil.manager.StudyDataManager;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceTransBean;
import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.NoScrollLinearLayoutManager;
import com.suzhou.concept.lil.view.dialog.LoadingDialog;
import com.suzhou.concept.utils.ExpandKt;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * @title: 阅读界面
 * @date: 2023/9/27 13:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadFragment extends Fragment {

    //控件
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private TextView submitBtn;

    private LinearLayoutCompat loadingLayout;
    private ProgressBar loadingProgress;
    private TextView loadingMsg;
    private Button loadingbtn;

    //适配器
    private ReadAdapter readAdapter;
    //单词数量
    private int wordCount = 0;
    //进入时间
    private long startTime = 0;
    //正常阅读速度
    private static final int READ_SPEED = 600;
    //加载弹窗
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_fix_read,null);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        checkDataNew();
    }

    @Override
    public void onResume() {
        super.onResume();

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        updateLoadingStyle(false,null);
    }

    /*************************初始化数据****************************/
    private void initView(View rootView){
        View toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        refreshLayout = rootView.findViewById(R.id.refreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        submitBtn = rootView.findViewById(R.id.submit);
        submitBtn.setVisibility(View.GONE);

        loadingLayout = rootView.findViewById(R.id.read_loading);
        loadingProgress = rootView.findViewById(R.id.read_progress);
        loadingMsg = rootView.findViewById(R.id.read_msg);
        loadingbtn = rootView.findViewById(R.id.read_button);
        loadingbtn.setOnClickListener(v->{
            checkDataNew();
        });
    }

    private void initList(){
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);

        readAdapter = new ReadAdapter(getActivity(),new ArrayList<>());
        NoScrollLinearLayoutManager manager = new NoScrollLinearLayoutManager(getActivity(),false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(readAdapter);

        submitBtn.setOnClickListener(v->{
            //是否登录
            if (!GlobalMemory.INSTANCE.isLogin()){
                ExpandKt.showGoLoginDialog(getActivity());
                return;
            }

            //判断速度
            long endTime = System.currentTimeMillis();
            float timeScale = (endTime - startTime)*1.0f/(1000*60);
            int readSpeed = (int) (wordCount/timeScale);
            if (readSpeed>=READ_SPEED){
                showSpeedDialog();
                return;
            }

            //提交数据
            showSubmitDialog(startTime,endTime,wordCount);
        });
    }

    /******************************加载数据******************************/
    private void checkDataNew(){
        updateLoadingStyle(false,null);

        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        String voaId = AppClient.Companion.getConceptItem().getVoa_id();

        //检查数据
        List<SentenceTransBean> list = null;
        if (GlobalMemory.INSTANCE.getCurrentYoung()){
            List<YoungSentenceItem> youngList = AppDatabase.Companion.getDatabase(getActivity()).youngSentenceDao().selectClassSentence(userId,Integer.parseInt(voaId));
            list = HelpUtil.transYoungToSentence(youngList);
        }else {
            List<EvaluationSentenceItem> usukList = AppDatabase.Companion.getDatabase(getActivity()).localSentenceDao().selectSentenceList(userId,Integer.parseInt(voaId));
            list = HelpUtil.transUSUKToSentence(usukList);
        }

        if (list!=null && list.size()>0) {
            transData(list);
        }else {
            updateLoadingStyle(false,"数据获取失败，请重试～");
        }
    }

    /******************************辅助功能****************************/
    //显示加载
    private void startLoading(){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.show();
    }

    //关闭加载
    private void stopLoading(){
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    //转换数据为标准的段落数据
    private void transData(List<SentenceTransBean> list){
        List<Pair<String,String>> pairList = new ArrayList<>();
        if (list!=null&&list.size()>0){

            Map<String,Pair<String,String>> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                SentenceTransBean bean = list.get(i);

                //这里的新概念和中小学内容使用单独显示，其他类型的使用paraId进行分段展示
                pairList.add(new Pair<>(bean.getSentence(),bean.getSentenceCn()));

                //拆分出来单词数据
                int wordSize = bean.getSentence().split(" ").length;
                wordCount+=wordSize;
            }
        }

        readAdapter.refreshData(pairList);
        //显示按钮
        submitBtn.setVisibility(View.VISIBLE);
    }

    //显示阅读过快弹窗
    private void showSpeedDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle("阅读过快")
                .setMessage("你认真读完这篇文章了吗？请用正常速度阅读")
                .setPositiveButton("确定",null)
                .setCancelable(false)
                .create().show();
    }

    //显示提交弹窗
    private void showSubmitDialog(long startTime,long endTime,int wordCount){
        String readTimeStr = toMinute(endTime-startTime);
        float timeScale = (endTime - startTime)*1.0f/(1000*60);
        int readSpeed = (int) (wordCount/timeScale);
        String msg = "当前阅读统计：\n文章单词数："+wordCount+"\n阅读时长："+readTimeStr+"\n阅读速度："+readSpeed+"单词/分钟\n是否提交阅读记录？";

        new AlertDialog.Builder(getActivity())
                .setTitle("提交阅读记录")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        submitReadMark(startTime,endTime,wordCount,AppClient.Companion.getConceptItem().getVoa_id());
                    }
                }).setNegativeButton("取消",null)
                .create().show();
    }

    /*************************************提交数据***************************/
    //提交阅读记录
    private void submitReadMark(long startTime,long endTime,int wordCount,String voaId){
        startLoading();
        submitBtn.setVisibility(View.INVISIBLE);

        RetrofitUtil.getInstance().submitReadReport(startTime, endTime, wordCount, voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Read_mark>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Read_mark bean) {
                        if (bean!=null&&bean.result.equals("1")){
                            //判断是否有奖励
                            int reward = TextUtils.isEmpty(bean.reward)?0:Integer.parseInt(bean.reward);
                            if (reward>0){
                                double price = BigDecimalUtil.trans2Double(reward*0.01f);
                                String showMsg = String.format("本次学习获得%1$s元红包奖励,已自动存入您的钱包账户",price);
                                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.SHOW_TOAST,showMsg));
                            }else {
                                ToastFactory.showShort(getActivity(),"提交阅读记录完成");
                            }

                            //这里增加广告显示操作
                            loadAd();
                        }else {
                            ToastFactory.showShort(getActivity(),"提交失败，服务器链接超时或数据错误，请稍后重试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastFactory.showShort(getActivity(),"提交失败，服务器链接超时或数据错误，请稍后重试");
                    }

                    @Override
                    public void onComplete() {
                        stopLoading();
                        submitBtn.setVisibility(View.VISIBLE);
                    }
                });
    }

    //转换成分钟时间
    private String toMinute(long time){
        long second = time/1000;

        long minute = second/60;
        long lastSecond = second%60;

        String minuteStr = minute>=10?String.valueOf(minute):"0"+minute;
        String lastSecondStr = lastSecond>=10?String.valueOf(lastSecond):"0"+lastSecond;

        return minuteStr+":"+lastSecondStr;
    }

    /*************************回调显示*****************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event){
        if (event.getType().equals(RefreshEvent.READ_LANGUAGE)){
            //阅读-语言显示
            readAdapter.refreshLanguage(StudyDataManager.getInstance().getReadShowCn());
        }
    }

    //样式切换
    private void  updateLoadingStyle(boolean loading,String showMsg){
        if (loading){
            loadingLayout.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);
            loadingMsg.setVisibility(View.VISIBLE);
            loadingbtn.setVisibility(View.INVISIBLE);

            loadingMsg.setText("正在加载文本数据");
        }else{
            if (TextUtils.isEmpty(showMsg)){
                loadingLayout.setVisibility(View.GONE);
            }else{
                loadingLayout.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.GONE);
                loadingMsg.setVisibility(View.VISIBLE);
                loadingbtn.setVisibility(View.VISIBLE);

                loadingMsg.setText(showMsg);
            }
        }
    }

    /********************************新的插屏广告******************************/
    // TODO: 2024/4/28 根据展姐要求，这里在阅读完成后显示半插屏的广告
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //界面数据
    private AdInterstitialViewBean interstitialViewBean = null;

    //显示插屏广告
    private void showInterstitialAd() {
        //请求广告
        if (interstitialViewBean == null){
            interstitialViewBean = new AdInterstitialViewBean(new AdInterstitialShowManager.OnAdInterstitialShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {

                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    if (isJumpByUserClick){
                        //跳转界面操作
                    }

                    //点击广告操作
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;

                        String fixShowType = AdShowUtil.NetParam.AdShowPosition.show_interstitial;
                        String fixAdType = adType;
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                ToastUtil.showToast(getActivity(),showMsg);

                                if (isSuccess){
                                    EventBus.getDefault().post(new UserinfoRefreshEvent());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onAdClose(String adType) {

                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdInterstitialShowManager.getInstance().setShowData(getActivity(),interstitialViewBean);
        }
        AdInterstitialShowManager.getInstance().showInterstitialAd();
        //重置数据
//        isGetRewardByClickAd = false;
    }
    //加载插屏广告
    private void loadAd(){
        if (getActivity()==null|| getActivity().isFinishing() || getActivity().isDestroyed()){
            return;
        }

        if (AdBlocker.getInstance().shouldBlockAd() || GlobalMemory.INSTANCE.getUserInfo().isVip()) {
            return;
        }

        showInterstitialAd();
    }
}
