package com.suzhou.concept.lil.ui.study.listen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.sdk.other.NetworkUtil;
import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.dollar.MemberCentreActivity;
import com.suzhou.concept.activity.other.UseInstructionsActivity;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.bean.YoungSentenceItem;
import com.suzhou.concept.dao.AppDatabase;
import com.suzhou.concept.databinding.FineListenFragmentBinding;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.event.UserinfoRefreshEvent;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.service.ListenPlayManager;
import com.suzhou.concept.lil.service.data.ListenPlayEvent;
import com.suzhou.concept.lil.service.data.ListenSettingManager;
import com.suzhou.concept.lil.service.temp.ListenPlaySession;
import com.suzhou.concept.lil.ui.ad.util.show.AdShowUtil;
import com.suzhou.concept.lil.ui.ad.util.show.banner.AdBannerShowManager;
import com.suzhou.concept.lil.ui.ad.util.show.banner.AdBannerViewBean;
import com.suzhou.concept.lil.ui.ad.util.upload.AdUploadManager;
import com.suzhou.concept.lil.ui.my.wordNote.wordDetail.WordShowNewActivity;
import com.suzhou.concept.lil.util.LibRxTimer;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.CenterLayoutManager;
import com.suzhou.concept.utils.ExtraKeysFactory;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * @title: 原文界面新的
 * @date: 2023/10/18 18:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenNewFragment extends Fragment {

    //文本数据
    private List<EvaluationSentenceItem> sentenceList;
    //适配器
    private ListenNewAdapter newAdapter;
    //播放器
    private ExoPlayer exoPlayer;
    //计时器标志
    private static final String PLAY_TAG = "play_tag";
    //选中倍速的位置
    private int selectPlaySpeed = 0;
    //是否暂停音频播放
    private boolean isSwitchPage = false;

    //布局样式
    private FineListenFragmentBinding binding;

    public static ListenNewFragment getInstance(int position){
        ListenNewFragment fragment = new ListenNewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ExtraKeysFactory.position,position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        //记录时间
        ListenReportSession.getInstance().setStartTime(System.currentTimeMillis());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FineListenFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initList();
        checkData();
        //加载广告
        refreshAd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //关闭广告和计时器
        stopAdTimer();
        AdBannerShowManager.getInstance().stopBannerAd();
    }

    /*************************************初始化*****************************/
    private void initView(View rootView){
        binding.playTime.setText("00:00");
        binding.totalTime.setText("00:00");
        binding.languageView.setBackgroundResource(0);
        binding.languageView.setImageResource(R.drawable.cn);
        binding.playView.setBackgroundResource(0);
        binding.playView.setImageResource(R.drawable.ic_study_listen_play);
        binding.speedView.setBackgroundResource(0);
        binding.speedView.setImageResource(R.drawable.speed);
        binding.languageView.setOnClickListener(v->{
            boolean isBilingual = newAdapter.isBilingual();
            if (isBilingual){
                binding.languageView.setImageResource(R.drawable.us);
                newAdapter.refreshLanguage(false);
            }else {
                binding.languageView.setImageResource(R.drawable.cn);
                newAdapter.refreshLanguage(true);
            }
        });
        binding.playView.setOnClickListener(v->{
            if (!ListenPlayManager.getInstance().playService.isPrepare()){
                ToastFactory.showShort(getActivity(),"播放器正在初始化中...");
                return;
            }

            binding.playView.setImageResource(exoPlayer.isPlaying()?R.drawable.ic_study_listen_play:R.drawable.ic_study_listen_pause);
            if (exoPlayer.isPlaying()){
                pauseAudio(true,false);
            }else {
                playAudio(true);
            }
        });
        binding.speedView.setOnClickListener(v->{
            //先判断登录
            if (!GlobalMemory.INSTANCE.isLogin()){
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }

            //然后处理会员
            if (!GlobalMemory.INSTANCE.getUserInfo().isVip()){
                new AlertDialog.Builder(getActivity())
                        .setTitle("倍速调整")
                        .setMessage("音频倍速需要购买会员后使用，是否购买会员?")
                        .setPositiveButton("现在购买", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(getActivity(), MemberCentreActivity.class));
                            }
                        }).setNegativeButton("考虑一下",null)
                        .setCancelable(false)
                        .show();
                return;
            }

            float curPlaySpeed = ListenSettingManager.getInstance().getPlaySpeed();
            if (!GlobalMemory.INSTANCE.getUserInfo().isVip()){
                curPlaySpeed = 1.0f;
            }
            String[] playSpeedArray = new String[]{"0.5x","1.0x","1.5x","2.0x"};
            for (int i = 0; i < playSpeedArray.length; i++) {
                String showSpeedStr = playSpeedArray[i];
                if (showSpeedStr.contains(String.valueOf(curPlaySpeed))){
                    selectPlaySpeed = i;
                    break;
                }
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle("当前倍速："+curPlaySpeed)
                    .setSingleChoiceItems(playSpeedArray, selectPlaySpeed, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectPlaySpeed = which;
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            float selectSpeed = Float.parseFloat(playSpeedArray[selectPlaySpeed].replace("x",""));
                            ListenSettingManager.getInstance().setPlaySpeed(selectSpeed);
                            //设置音频
                            ListenPlayManager.getInstance().playService.setSpeed(selectSpeed);
                        }
                    }).setNegativeButton("取消",null)
                    .setCancelable(false)
                    .show();
        });
        binding.progressView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    //设置进度
                    ListenPlayManager.getInstance().playService.setProgress(progress);
                    //刷新文本显示
                    int selectIndex = getScrolledIndex();
                    newAdapter.refreshSelectIndex(selectIndex);
                    binding.recyclerView.smoothScrollToPosition(selectIndex);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //暂停播放
                pauseAudio(false,false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playAudio(true);
            }
        });

        //上一句
        binding.preView.setOnClickListener(v->{
            int curScrollIndex = getScrolledIndex();
            if (curScrollIndex==0){
                ToastUtil.showToast(getActivity(),"当前已经是第一句了");
                return;
            }

            int startTime = (int) (sentenceList.get(curScrollIndex-1).getTiming()*1000L);
            //设置进度
            ListenPlayManager.getInstance().playService.setProgress(startTime);
        });
        //下一句
        binding.nextView.setOnClickListener(v->{
            int curScrollIndex = getScrolledIndex();
            if (curScrollIndex==sentenceList.size()-1){
                ToastUtil.showToast(getActivity(),"当前已经是最后一句了");
                return;
            }

            int startTime = (int) (sentenceList.get(curScrollIndex+1).getTiming()*1000L);
            //设置进度
            ListenPlayManager.getInstance().playService.setProgress(startTime);
        });
    }

    private void initPlayer(){
        exoPlayer = ListenPlayManager.getInstance().playService.getPlayer();

        //设置倍速
        float playSpeed = ListenSettingManager.getInstance().getPlaySpeed();
        ListenPlayManager.getInstance().playService.setSpeed(playSpeed);

        //数据为空
        if (ListenPlaySession.getInstance().getTempBean()==null){
            //把数据放上
            int position = getArguments().getInt(ExtraKeysFactory.position,0);
            ListenPlaySession.getInstance().setSelectData(position);
            //刷新首页显示
            EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_bg_text));
            //暂停当前的音频
            ListenPlayManager.getInstance().playService.pauseAudio();

            //准备播放
            playAudio(false);
            return;
        }

        //数据不一致
        if (!ListenPlaySession.getInstance().getTempBean().getVoaId().equals(AppClient.Companion.getConceptItem().getVoa_id())){
            //把数据放上
            int position = getArguments().getInt(ExtraKeysFactory.position,0);
            ListenPlaySession.getInstance().setSelectData(position);
            //刷新首页显示
            EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_bg_text));
            //暂停当前的音频
            ListenPlayManager.getInstance().playService.pauseAudio();

            //准备播放
            playAudio(false);
            return;
        }

        if (ListenPlayManager.getInstance().playService.getPlayer().isPlaying()){
            //这里就是数据一致的情况
            binding.playView.setImageResource(R.drawable.ic_study_listen_pause);
            //控制外部显示
            EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_start));
            //启动定时器
            startTimer();
        }else {
            //这里就是数据一致的情况
            binding.playView.setImageResource(R.drawable.ic_study_listen_play);
            //控制外部显示
            EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_pause));
            //启动定时器
            stopTimer();
            //这里需要处理下播放进度啥的
            refreshProgressAndTextShow();
        }
    }

    private void initList(){
        newAdapter = new ListenNewAdapter(getActivity(),new ArrayList<>());
        binding.recyclerView.setLayoutManager(new CenterLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(newAdapter);
        newAdapter.setOnWordClickListener(new OnSimpleClickListener<String>() {
            @Override
            public void onClick(String selectText) {
                //查询单词
                pauseAudio(true,false);

                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastFactory.showShort(getActivity(), "请链接网络后重试～");
                    return;
                }

                if (!TextUtils.isEmpty(selectText)) {
                    //先处理下数据
                    selectText = filterWord(selectText);

                    if (selectText.matches("^[a-zA-Z]*")) {
                        //跳转到查词界面
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), WordShowNewActivity.class);
                        intent.putExtra(ExtraKeysFactory.definitionWord,selectText);
                        intent.putExtra(ExtraKeysFactory.listWord,false);
                        startActivity(intent);
                    } else {
                        ToastFactory.showShort(getActivity(), "请取英文单词");
                    }
                } else {
                    ToastFactory.showShort(getActivity(), "请取英文单词");
                }
            }
        });
    }

    //获取数据
    private void checkData(){
        if (sentenceList!=null&&sentenceList.size()>0){
            sentenceList.clear();
        }

        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
        int voaId = TextUtils.isEmpty(AppClient.Companion.getConceptItem().getVoa_id())?0:Integer.parseInt(AppClient.Companion.getConceptItem().getVoa_id());
        //这里区分青少版和全四册内容显示
        if (GlobalMemory.INSTANCE.getCurrentYoung()){
            List<YoungSentenceItem> youngList = AppDatabase.Companion.getDatabase(getActivity()).youngSentenceDao().selectClassSentence(userId,voaId);
            sentenceList = transYoungToShow(youngList);
        }else {
            sentenceList = AppDatabase.Companion.getDatabase(getActivity()).localSentenceDao().selectSentenceList(userId,voaId);
        }
        if (sentenceList!=null&&sentenceList.size()>0){
            newAdapter.refreshData(sentenceList);

            //将数据放入学习报告中
            ListenReportSession.getInstance().setWordCount(sentenceList);

            //开始播放
            initPlayer();
        }else {
            ToastFactory.showShort(getActivity(),"未查询到当前章节数据");
        }
    }

    /**************************************音频操作**************************/
    //播放音频
    private void playAudio(boolean isContinue){
        if (isContinue){
            binding.playView.setImageResource(R.drawable.ic_study_listen_pause);
            //控制外部显示
            EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_start));
            //启动定时器
            startTimer();
        }else {
            ListenPlayManager.getInstance().playService.playAudio();
        }
    }

    //暂停播放音频
    private void pauseAudio(boolean isSubmit,boolean isEnd){
        if (binding!=null){
            binding.playView.setImageResource(R.drawable.ic_study_listen_play);
        }
        //控制外部显示
        EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_pause));
        //停止计时
        stopTimer();

        if (isSubmit){
            ListenReportSession.getInstance().setEndTime(System.currentTimeMillis());
            ListenReportSession.getInstance().submitReport(AppClient.Companion.getConceptItem().getVoa_id(),isEnd);
        }

        //刷新进度
        if (exoPlayer!=null){
            long curProgress = exoPlayer.getCurrentPosition();
            long totalTime = exoPlayer.getDuration();
            int progress = (int) (curProgress*100L/totalTime);
            updateListenProgress(progress);
        }
    }

    //倒计时器
    private void startTimer(){
        //停止计时器
        stopTimer();
        //切换图片
        binding.playView.setImageResource(R.drawable.ic_study_listen_pause);
        //展示总时间
        binding.totalTime.setText(transToMinute(exoPlayer.getDuration()));
        //设置进度条总时间
        binding.progressView.setMax((int) exoPlayer.getDuration());
        LibRxTimer.getInstance().multiTimerInMain(PLAY_TAG, 0, 500L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //刷新进度条
                binding.progressView.setProgress((int) exoPlayer.getCurrentPosition());
                //刷新播放时间
                binding.playTime.setText(transToMinute(exoPlayer.getCurrentPosition()));
                //刷新文章的文字跳转
                int scrollIndex = getScrolledIndex();
                newAdapter.refreshSelectIndex(scrollIndex);
                binding.recyclerView.smoothScrollToPosition(scrollIndex);

                Log.d("计时器", "action: --计时器");
            }
        });
    }

    //停止计时
    private void stopTimer(){
        LibRxTimer.getInstance().cancelTimer(PLAY_TAG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListenPlayEvent event){

        if (event.getShowType().equals(ListenPlayEvent.PLAY_prepare_finish)){
            //加载完成

            //先显示部分数据
            //展示总时间
            binding.totalTime.setText(transToMinute(exoPlayer.getDuration()));
            //设置播放的总时长
            binding.progressView.setMax((int) exoPlayer.getDuration());

            if (isSwitchPage){
                return;
            }

            playAudio(true);
        }

        if (event.getShowType().equals(ListenPlayEvent.PLAY_complete_finish)){
            //播放完成
            pauseAudio(true,true);

            //直接显示全部
            updateListenProgress(100);

            //准备下一个
            EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_switch,""));
        }
    }

    //登录后的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event){
        //登录或者会员操作
        if (event.getType().equals(RefreshEvent.USER_VIP)){
            refreshAd();
        }
    }

    /**************************************其他操作****************************/
    //将时间转换为分钟
    private String transToMinute(long time){
        time = time/1000;
        long minute = time/60;
        long second = time%60;

        StringBuilder buffer = new StringBuilder();
        if (minute>=10){
            buffer.append(minute);
        }else {
            buffer.append("0").append(minute);
        }
        if (second>=10){
            buffer.append(":").append(second);
        }else {
            buffer.append(":0").append(second);
        }
        return buffer.toString();
    }

    //文章滚动
    private int getScrolledIndex(){
        if (sentenceList!=null&&sentenceList.size()>0&&exoPlayer!=null){
            long playTime = exoPlayer.getCurrentPosition();

            for (int i = 0; i < sentenceList.size(); i++) {
                EvaluationSentenceItem item = sentenceList.get(i);

                //进行处理
                long curEndTime = (long) (item.getEndTiming()*1000);
                if (playTime<=curEndTime){
                    return i;
                }

                //最后一个的特殊情况
                if (i == sentenceList.size()-1){
                    return i;
                }
            }
        }
        return 0;
    }

    //处理单词数据
    private String filterWord(String selectText) {
        selectText = selectText.replace(".", "");
        selectText = selectText.replace(",", "");
        selectText = selectText.replace("!", "");
        selectText = selectText.replace("?", "");
        selectText = selectText.replace("'", "");

        return selectText;
    }

    //停止音频播放
    public void switchOtherPage(boolean isStopAudio){
        isSwitchPage = isStopAudio;

        if (isStopAudio){
            pauseAudio(true,false);
        }
    }

    //刷新进度显示和文章显示
    public void refreshProgressAndTextShow(){
        //刷新进度条
        binding.progressView.setMax((int) exoPlayer.getDuration());
        binding.progressView.setProgress((int) exoPlayer.getCurrentPosition());
        //刷新播放时间
        binding.totalTime.setText(transToMinute(exoPlayer.getDuration()));
        binding.playTime.setText(transToMinute(exoPlayer.getCurrentPosition()));
        //刷新文章的文字跳转
        int scrollIndex = getScrolledIndex();
        newAdapter.refreshSelectIndex(scrollIndex);
        binding.recyclerView.smoothScrollToPosition(scrollIndex);
    }

    //转换青少版数据为标准显示数据
    private List<EvaluationSentenceItem> transYoungToShow(List<YoungSentenceItem> youndList){
        List<EvaluationSentenceItem> list = new ArrayList<>();
        if (youndList!=null&&youndList.size()>0){
            for (int i = 0; i < youndList.size(); i++) {
                YoungSentenceItem item = youndList.get(i);

                list.add(new EvaluationSentenceItem(
                        item.getVoaId(),
                        TextUtils.isEmpty(item.getEndTiming())?0:Float.parseFloat(item.getEndTiming()),
                        item.getParaId(),
                        TextUtils.isEmpty(item.getIdIndex())?0:Integer.parseInt(item.getIdIndex()),
                        TextUtils.isEmpty(item.getTiming())?0:Float.parseFloat(item.getTiming()),
                        item.getSentence_cn(),
                        item.getSentence(),
                        item.getShowCn(),
                        item.getCurrentBlue(),
                        item.getShowOperate(),
                        item.getFraction(),
                        item.getSelfVideoUrl(),
                        item.getOnlyKay(),
                        item.getSuccess(),
                        GlobalMemory.INSTANCE.getUserInfo().getUid()
                ));
            }
        }
        return list;
    }

    //更新播放进度
    private void updateListenProgress(int progress){
        int bookId = AppClient.Companion.getConceptItem().getBookId();
        int index = AppClient.Companion.getConceptItem().getIndex();
        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();

        if (GlobalMemory.INSTANCE.getCurrentYoung()){
            AppDatabase.Companion.getDatabase(getActivity()).youngBookDao().updateItemListen(bookId,index,userId,progress);
        }else {
            String language = AppClient.Companion.getConceptItem().getLanguage();
            AppDatabase.Companion.getDatabase(getActivity()).conceptDao().updateListenConceptItem(bookId,language,index,progress);
        }
    }

    /**************************广告计时器**************************/
    //广告定时器
    private static final String timer_ad = "timer_ad";
    //广告间隔时间
    private static final long adScaleTime = 20*1000L;
    //开始计时
    private void startAdTimer() {
        stopAdTimer();
        LibRxTimer.getInstance().multiTimerInMain(timer_ad, 0, adScaleTime, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                showBannerAd();
            }
        });
    }
    //停止计时
    private void stopAdTimer() {
        LibRxTimer.getInstance().cancelTimer(timer_ad);
    }

    /*******************************新的banner广告显示**********************/
    //是否已经获取了奖励
    private boolean isGetRewardByClickAd = false;
    //显示的界面模型
    private AdBannerViewBean bannerViewBean = null;
    //显示banner广告
    private void showBannerAd(){
        //请求广告
        if (bannerViewBean==null){
            bannerViewBean = new AdBannerViewBean(binding.adLayout.iyubaSdkAdLayout, binding.adLayout.webAdLayout, binding.adLayout.webAdImage, binding.adLayout.webAdClose,binding.adLayout.webAdTips, new AdBannerShowManager.OnAdBannerShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {
                    binding.adLayout.getRoot().setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl) {
                    pauseAudio(false,false);

                    if (isJumpByUserClick){
                        if (TextUtils.isEmpty(jumpUrl)){
                            ToastUtil.showToast(getActivity(),"暂无内容");
                            return;
                        }

                        Intent intent = new Intent();
                        intent.setClass(getActivity(), UseInstructionsActivity.class);
                        intent.putExtra(ExtraKeysFactory.webUrlOut, jumpUrl);
                        startActivity(intent);
                    }

                    //点击广告获取奖励
                    if (!isGetRewardByClickAd){
                        isGetRewardByClickAd = true;

                        //获取奖励
                        String fixShowType = AdShowUtil.NetParam.AdShowPosition.show_banner;
                        String fixAdType = adType;
                        AdUploadManager.getInstance().clickAdForReward(fixShowType, fixAdType, new AdUploadManager.OnAdClickCallBackListener() {
                            @Override
                            public void showClickAdResult(boolean isSuccess, String showMsg) {
                                //直接显示信息即可
                                ToastUtil.showToast(getActivity(),showMsg);

                                if (isSuccess){
                                    EventBus.getDefault().post(new UserinfoRefreshEvent());
                                }
                            }
                        });
                        //点击广告提交数据
                        /*List<AdLocalMarkBean> localAdList = new ArrayList<>();
                        localAdList.add(new AdLocalMarkBean(
                                fixAdType,
                                fixShowType,
                                AdShowUtil.NetParam.AdOperation.operation_click,
                                System.currentTimeMillis()/1000L
                        ));
                        AdUploadManager.getInstance().submitAdMsgData(getActivity().getPackageName(), localAdList, new AdUploadManager.OnAdSubmitCallbackListener() {
                            @Override
                            public void showSubmitAdResult(boolean isSuccess, String showMsg) {
                                //不进行处理
                            }
                        });*/
                    }
                }

                @Override
                public void onAdClose(String adType) {
                    //关闭界面
                    binding.adLayout.getRoot().setVisibility(View.GONE);
                    //关闭计时器
                    stopAdTimer();
                    //关闭广告
                    AdBannerShowManager.getInstance().stopBannerAd();
                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdBannerShowManager.getInstance().setShowData(getActivity(),bannerViewBean);
        }
        AdBannerShowManager.getInstance().showBannerAd();
        //重置数据
        isGetRewardByClickAd = false;
    }
    //配置广告显示
    private void refreshAd(){
        if (!GlobalMemory.INSTANCE.getUserInfo().isVip() && !AdBlocker.getInstance().shouldBlockAd()) {
            startAdTimer();
        }else {
            binding.adLayout.getRoot().setVisibility(View.GONE);
            stopAdTimer();
            AdBannerShowManager.getInstance().stopBannerAd();
        }
    }
}
