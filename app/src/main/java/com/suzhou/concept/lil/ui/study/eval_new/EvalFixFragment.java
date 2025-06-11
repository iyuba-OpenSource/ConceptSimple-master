package com.suzhou.concept.lil.ui.study.eval_new;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.dollar.MemberCentreActivity;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.bean.YoungSentenceItem;
import com.suzhou.concept.dao.AppDatabase;
import com.suzhou.concept.databinding.FragmentEvalNewBinding;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceTransBean;
import com.suzhou.concept.lil.ui.study.eval.checkEval.EvalWordFixPage;
import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
import com.suzhou.concept.lil.ui.study.eval.util.RecordManager;
import com.suzhou.concept.lil.util.PermissionDialogUtil;
import com.suzhou.concept.lil.util.LibRxTimer;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog;
import com.suzhou.concept.utils.GlobalMemory;
import com.suzhou.concept.utils.OtherUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EvalFixFragment extends BaseViewBindingFragment<FragmentEvalNewBinding> implements EvalFixView{

    //数据
    private EvalFixPresenter presenter;
    //适配器
    private EvalFixAdapter adapter;

    //原文播放器
    private ExoPlayer audioPlayer;
    //录音器
    private RecordManager recordManager;
    //评测播放器
    private ExoPlayer evalPLayer;
    //合成播放器
    private ExoPlayer margePlayer;

    //是否正在录音评测
    private boolean isRecordAndEval = false;
    //是否初始化完成原文音频
    private boolean isInitArticleAudio = false;
    //是否初始化完成合成音频
    private boolean isInitMargeAudio = false;

    //评测保存的音频文件路径
    private String evalAudioPath = null;
    //合成音频的文件地址
    private String margeAudioUrl = null;

    //纠音
    private EvalWordFixPage fixPage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new EvalFixPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //样式初始化
        initList();
        initBottom();
        //播放器初始化
        initArticlePlayer();
        initEvalPLayer();
        initMargeAudioPlayer();

        //刷新数据
        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();

        //关闭各种播放器
        pauseArticleAudio();
        pauseMargeAudio();
        pauseMargeAudio();
        //关闭录音操作
        stopRecord();
        //关闭纠音
        if (fixPage!=null){
            fixPage.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    /************************************初始化*******************************************/
    private void initList(){
        adapter = new EvalFixAdapter(getActivity(),new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnEvalClickListener(new EvalFixAdapter.OnEvalClickListener() {
            @Override
            public void onItemClick(int position) {
                //暂停原文、评测和合成音频播放
                pauseArticleAudio();
                pauseEvalAudio();
                pauseMargeAudio();

                //判断录音
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }
                //刷新显示
                adapter.refreshShowItem(position);
            }

            @Override
            public void onAudioPlay(long startTime,long endTime) {
                //暂停评测和合成播放
                pauseEvalAudio();
                pauseMargeAudio();
                //判断录音
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }

                //防止时间错误
                if (endTime >= audioPlayer.getDuration()){
                    endTime = audioPlayer.getDuration();
                }

                //判断播放状态
                if (audioPlayer!=null){
                    if (audioPlayer.isPlaying()){
                        pauseArticleAudio();
                    }else {
                        playArticleAudio(startTime, endTime);
                    }
                }else {
                    ToastUtil.showToast(getActivity(),"未初始化播放器");
                }
            }

            @Override
            public void onRecord(long recordTime,String voaId,String paraId,String indexId,String sentence) {
                //暂停原文、评测和合成播放
                pauseArticleAudio();
                pauseEvalAudio();
                pauseMargeAudio();

                //判断登录
                if (!GlobalMemory.INSTANCE.isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }

                //判断限制
                //数据库中评测的数量
                int evalSize = RoomDBManager.getInstance().getEvalResultData(GlobalMemory.INSTANCE.getUserInfo().getUid(), String.valueOf(HelpUtil.getVoaId())).size();
                //当前是否已经评测
                EvalResultBean resultBean = RoomDBManager.getInstance().getSingleEval(GlobalMemory.INSTANCE.getUserInfo().getUid(), voaId,indexId,paraId);
                if (!GlobalMemory.INSTANCE.getUserInfo().isVip() && evalSize>=3 && resultBean==null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("评测限制")
                            .setMessage("普通用户至多评测三句，会员无限制，是否开通会员？")
                            .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity(), MemberCentreActivity.class));
                                }
                            }).setNegativeButton("取消", null)
                            .setCancelable(false)
                            .show();
                    return;
                }

                //增加权限弹窗显示
                List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
                pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));

                PermissionDialogUtil.getInstance().showMsgDialog(getActivity(), pairList, new PermissionDialogUtil.OnPermissionResultListener() {
                    @Override
                    public void onGranted(boolean isSuccess) {
                        if (isSuccess){

                            //判断录音
                            if (isRecordAndEval){
                                //停止录音
                                stopRecord();
                                //提交评测
                                submitEval(voaId, paraId, indexId, sentence);
                            }else {
                                //开始录音
                                startRecord(recordTime, voaId, paraId, indexId,sentence);
                            }
                        }
                    }
                });
            }

            @Override
            public void onEvalPlay(String playUrl,String voaId,String paraId,String indexId) {
                //暂停原文、合成播放
                pauseArticleAudio();
                pauseMargeAudio();
                //判断录音
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }
                //播放评测音频
                playUrl = "http://iuserspeech."+ OtherUtils.INSTANCE.getIyuba_cn()+":9001/voa/"+playUrl;
                //判断本地是否存在数据
                String localPath = getEvalAudioPath(voaId,paraId,indexId,GlobalMemory.INSTANCE.getUserInfo().getUid());
                File localFile = new File(localPath);
                if (localFile.exists()){
                    playUrl = localPath;
                }

                //判断评测播放
                if (evalPLayer!=null){
                    if (evalPLayer.isPlaying()){
                        pauseEvalAudio();
                    }else {
                        playEvalAudio(playUrl);
                    }
                }else {
                    ToastUtil.showToast(getActivity(),"初始化评测播放器失败~");
                }
            }

            @Override
            public void onPublish(String voaId,String paraId,String indexId,int totalScore,String evalUrl) {
                //停止原文、评测和合成播放
                pauseArticleAudio();
                pauseEvalAudio();
                pauseMargeAudio();
                //判断录音
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }
                //提交发布
                submitRankPublish(voaId, paraId, indexId, totalScore, evalUrl);
            }

            @Override
            public void onCheckEval(EvalShowBean showBean, SentenceTransBean transBean) {
                //纠音

                //关闭播放
                pauseArticleAudio();
                pauseEvalAudio();
                pauseMargeAudio();
                //判断录音
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }
                //跳转纠音
                fixPage = EvalWordFixPage.getInstance(showBean, transBean);
                fixPage.show(getActivity().getSupportFragmentManager(),"");
            }
        });

    }

    private void initBottom(){
        binding.bottomPlay.setVisibility(View.INVISIBLE);
        binding.buttonMarge.setOnClickListener(v->{
            //合成
            //登录判断
            if (!GlobalMemory.INSTANCE.isLogin()){
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            //停止原文和评测播放
            pauseArticleAudio();
            pauseEvalAudio();
            //判断录音
            if (isRecordAndEval){
                ToastUtil.showToast(getActivity(),"正在录音评测中～");
                return;
            }

            //提交数据
            String voaId = AppClient.Companion.getConceptItem().getVoa_id();
            int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
            List<EvalResultBean> evalList = RoomDBManager.getInstance().getEvalResultData(userId,voaId);
            if (evalList!=null&&evalList.size()>=2){
                margeAudio(evalList);
            }else {
                ToastUtil.showToast(getActivity(),"请评测至少两句后合成");
            }
        });
        binding.playView.setOnClickListener(v->{
            //播放
            //停止原文和评测播放
            pauseArticleAudio();
            pauseEvalAudio();
            //判断录音
            if (isRecordAndEval){
                ToastUtil.showToast(getActivity(),"正在录音评测中～");
                return;
            }

            //判断播放合成
            if (margePlayer!=null){
                if (margePlayer.isPlaying()){
                    pauseMargeAudio();
                }else {
                    playMargeAudio(null,0);
                }
            }else {
                ToastUtil.showToast(getActivity(),"");
            }
        });
        binding.publishView.setOnClickListener(v->{
            //发布

            //停止原文和评测播放
            pauseArticleAudio();
            pauseEvalAudio();
            pauseMargeAudio();
            //判断录音
            if (isRecordAndEval){
                ToastUtil.showToast(getActivity(),"正在录音评测中～");
                return;
            }

            //提交发布
            submitMargeRankPublish();
        });
        binding.playProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //停止原文和评测播放
                pauseArticleAudio();
                pauseEvalAudio();
                pauseMargeAudio();
                //判断录音
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }

                //暂停播放
                if (margePlayer!=null&&margePlayer.isPlaying()){
                    margePlayer.pause();
                }
                //关闭定时器
                LibRxTimer.getInstance().cancelTimer(timer_margePlayerTimer);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止原文和评测播放
                pauseArticleAudio();
                pauseEvalAudio();
                //判断录音
                if (isRecordAndEval){
                    ToastUtil.showToast(getActivity(),"正在录音评测中～");
                    return;
                }

                //开始播放
                playMargeAudio(null,binding.playProgress.getProgress());
            }
        });
    }

    /*************************************刷新数据****************************************/
    //刷新数据
    private void refreshData(){
        updateUI(true,null);

        //获取本地数据处理
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
            updateUI(false, null);
            adapter.refreshData(list);
        }else {
            updateUI(false,"数据获取失败，请重试～");
        }
    }

    /**************************************音频和录音*******************************************/
    //原文播放器计时器
    private static final String timer_articleAudioTimer = "articleAudioTimer";
    //初始化原文播放器
    private void initArticlePlayer(){
        audioPlayer = new ExoPlayer.Builder(getActivity()).build();
        audioPlayer.setPlayWhenReady(false);
        audioPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        isInitArticleAudio = true;
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseArticleAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(),"加载原文音频异常～");
            }
        });

        //加载音频数据
        String audioUrl = GlobalMemory.INSTANCE.getVideoUrl(HelpUtil.getVoaId());
        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        audioPlayer.setMediaItem(mediaItem);
        audioPlayer.prepare();
    }

    //开始原文播放
    private void playArticleAudio(long startTime,long endTime){
        //开启播放
        if (!isInitArticleAudio){
            ToastUtil.showToast(getActivity(),"当前音频未初始化~");
            adapter.refreshAudioPlay(false,0,0);
            return;
        }

        //播放音频
        audioPlayer.seekTo(startTime);
        audioPlayer.play();
        //开启计时器
        LibRxTimer.getInstance().multiTimerInMain(timer_articleAudioTimer, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                long playTime = audioPlayer.getCurrentPosition();
                long progressTime = playTime - startTime;
                long totalTime = endTime - startTime;

                Log.d("播放时间", "播放时间--"+playTime+"--进度时间--"+progressTime+"--总时间--"+totalTime+"--结束时间--"+endTime);

                //刷新进度
                adapter.refreshAudioPlay(true,progressTime,totalTime);
                //计算进度
                if (progressTime>=totalTime){
                    pauseArticleAudio();
                }
            }
        });
    }

    //暂停原文播放
    private void pauseArticleAudio(){
        //暂停播放器
        if (audioPlayer!=null&&audioPlayer.isPlaying()){
            audioPlayer.pause();
        }
        //关闭计时器
        LibRxTimer.getInstance().cancelTimer(timer_articleAudioTimer);
        //刷新列表样式
        adapter.refreshAudioPlay(false,0,0);
    }

    //录音计时器
    private static final String timer_recordTimer = "recordTimer";
    //开启录音
    private void startRecord(long recordTime,String voaId,String paraId,String indexId,String sentence){
        //进行录音操作
        evalAudioPath = getEvalAudioPath(voaId,paraId,indexId,GlobalMemory.INSTANCE.getUserInfo().getUid());
        try {
            File file = new File(evalAudioPath);
            if (file.exists()){
                file.delete();
            }

            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }catch (Exception e){
            evalAudioPath = null;
        }

        if (TextUtils.isEmpty(evalAudioPath)){
            ToastUtil.showToast(getActivity(),"无法创建文件，请授予存储权限后使用~");
            return;
        }

        //设置状态
        isRecordAndEval = true;
        //开始录音
        recordManager = new RecordManager(new File(evalAudioPath));
        recordManager.startRecord();
        //显示样式
        adapter.refreshRecord(true);
        //开启计时器
        LibRxTimer.getInstance().multiTimerInMain(timer_recordTimer, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                //当前录音的时间
                long curRecordTime = number*200L;

                if (curRecordTime >= recordTime){
                    //停止录音
                    stopRecord();
                    //开启评测
                    submitEval(voaId,paraId,indexId,sentence);
                }
            }
        });
    }

    //停止录音
    private void stopRecord(){
        if (recordManager!=null&&isRecordAndEval){
            recordManager.stopRecord();
        }
        //关闭计时器
        LibRxTimer.getInstance().cancelTimer(timer_recordTimer);
        //还原样式
        adapter.refreshRecord(false);
    }

    //初始化评测播放器
    private void initEvalPLayer(){
        evalPLayer = new ExoPlayer.Builder(getActivity()).build();
        evalPLayer.setPlayWhenReady(false);
        evalPLayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        playEvalAudio(null);
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseEvalAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(),"加载评测音频异常～");
            }
        });
    }

    //评测播放的计时器
    private static final String timer_evalPlayTimer = "evalPlayTimer";
    //开启评测播放
    private void playEvalAudio(String playUrlOrPath){
        if (!TextUtils.isEmpty(playUrlOrPath)){

            MediaItem mediaItem = null;
            if (playUrlOrPath.startsWith("http://")||playUrlOrPath.startsWith("https://")){
                mediaItem = MediaItem.fromUri(playUrlOrPath);
            }else {

                Uri playUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    playUri = FileProvider.getUriForFile(getActivity(),getResources().getString(R.string.authorities),new File(playUrlOrPath));
                }else {
                    playUri = Uri.fromFile(new File(playUrlOrPath));
                }

                mediaItem = MediaItem.fromUri(playUri);
            }

            evalPLayer.setMediaItem(mediaItem);
            evalPLayer.prepare();
        }else {
            evalPLayer.play();
            //开启计时器
            LibRxTimer.getInstance().multiTimerInMain(timer_evalPlayTimer, 0, 200L, new LibRxTimer.RxActionListener() {
                @Override
                public void onAction(long number) {
                    long progressTime = evalPLayer.getCurrentPosition();
                    long totalTime = evalPLayer.getDuration();

                    adapter.refreshEvalPlay(true,progressTime,totalTime);
                }
            });
        }
    }

    //暂停评测播放
    private void pauseEvalAudio(){
        if (evalPLayer!=null&&evalPLayer.isPlaying()){
            evalPLayer.pause();
        }
        //切换样式
        adapter.refreshEvalPlay(false,0,0);
        //停止计时器
        LibRxTimer.getInstance().cancelTimer(timer_evalPlayTimer);
    }

    //初始化合成音频
    private void initMargeAudioPlayer(){
        margePlayer = new ExoPlayer.Builder(getActivity()).build();
        margePlayer.setPlayWhenReady(false);
        margePlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        isInitMargeAudio = true;
                        //显示进度
                        binding.playProgress.setMax((int) margePlayer.getDuration());
                        binding.totalTime.setText(transTimeToMinute(margePlayer.getDuration()));
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        pauseMargeAudio();
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(),"合成音频播放器初始化失败～");
            }
        });
    }

    //合成播放器的计时器
    private static final String timer_margePlayerTimer = "margePlayerTimer";
    //播放合成音频
    private void playMargeAudio(String margeAudioUrl,long showProgress){
        if (!TextUtils.isEmpty(margeAudioUrl)){
            MediaItem item = MediaItem.fromUri(margeAudioUrl);
            margePlayer.setMediaItem(item);
            margePlayer.prepare();
            return;
        }

        //判断初始化
        if (!isInitMargeAudio){
            ToastUtil.showToast(getActivity(),"初始化合成音频失败");
            return;
        }

        Log.d("显示时间", "时间--"+showProgress);

        margePlayer.seekTo(showProgress);
        margePlayer.play();
        //设置进度
        binding.playProgress.setProgress((int) showProgress);
        binding.playProgress.setMax((int) margePlayer.getDuration());
        binding.playTime.setText(transTimeToMinute(showProgress));
        binding.totalTime.setText(transTimeToMinute(margePlayer.getDuration()));
        //刷新显示
        binding.playView.setImageResource(R.drawable.pause_evaluation_old);
        //定时器处理
        LibRxTimer.getInstance().multiTimerInMain(timer_margePlayerTimer, 0, 200L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                binding.playTime.setText(transTimeToMinute(margePlayer.getCurrentPosition()));
                binding.playProgress.setProgress((int) margePlayer.getCurrentPosition());
            }
        });
    }

    //停止播放合成音频
    private void pauseMargeAudio(){
        if (margePlayer!=null&&margePlayer.isPlaying()){
            margePlayer.pause();
        }
        //关闭定时器
        LibRxTimer.getInstance().cancelTimer(timer_margePlayerTimer);
        //刷新显示
        binding.playView.setImageResource(R.drawable.play_evaluation_old);
        binding.playTime.setText("00:00");
        binding.playProgress.setProgress(0);
    }

    /***************************************提交数据***************************************/
    //提交评测
    private void submitEval(String voaId,String paraId,String indexId,String sentence){
        //开启弹窗
        startLoading("正在提交句子的评测数据，请稍后～");
        //启动提交接口
        presenter.submitEval(voaId,paraId,indexId,sentence,evalAudioPath);
    }

    //提交排行榜发布
    private void submitRankPublish(String voaId,String paraId,String indexId,int totalScore,String evalUrl){
        startLoading("正在提交评测数据到排行榜～");

        presenter.submitEvalRank(voaId, paraId, indexId, totalScore, evalUrl);
    }

    //合成音频
    private void margeAudio(List<EvalResultBean> evalList){
        startLoading("正在合成音频～");

        presenter.margeAudio(evalList);
    }

    //提交合成音频到排行榜
    private void submitMargeRankPublish(){
        startLoading("正在提交合成数据到排行榜~");

        List<EvalResultBean> evalList = RoomDBManager.getInstance().getEvalResultData(GlobalMemory.INSTANCE.getUserInfo().getUid(),AppClient.Companion.getConceptItem().getVoa_id());
        if (evalList!=null&&evalList.size()>0){
            presenter.submitMargeAudio(evalList,margeAudioUrl);
        }else {
            ToastUtil.showToast(getActivity(),"请合成配音后发布");
        }
    }

    /***************************************接口回调***************************************/
    @Override
    public void showEvalResult(EvalShowBean bean, String showMsg) {
        stopLoading();
        //设置评测停止
        isRecordAndEval = false;

        if (bean!=null){
            //当前的数据
            SentenceTransBean transBean = adapter.getShowData();
            //保存在本地
            RoomDBManager.getInstance().saveSingleEval(transBean.getVoaId(), transBean.getParaId(), transBean.getIdIndex(), bean, evalAudioPath);
            //刷新显示
            adapter.refreshDataShow();
            //设置合成状态
            binding.bottomPlay.setVisibility(View.INVISIBLE);
        }else {
            //显示信息
            ToastUtil.showToast(getActivity(),showMsg);
        }
    }

    @Override
    public void showEvalRankResult(boolean isSuccess,String showMsg) {
        stopLoading();

        if (isSuccess){
            ToastUtil.showToast(getActivity(),"发布到排行榜成功，请点击查看");
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.STUDY_RANK_REFRESH,null));
            return;
        }

        ToastUtil.showToast(getActivity(),showMsg);
    }

    @Override
    public void showMargeResult(String margeUrl,int averageScore,String showMsg) {
        stopLoading();

        if (TextUtils.isEmpty(showMsg)){
            margeAudioUrl = margeUrl;
            binding.scoreView.setText(String.valueOf(averageScore));
            //加载音频
            String playMargeUrl = HelpUtil.getEvalPlayUrl(margeAudioUrl);
            playMargeAudio(playMargeUrl,0);
            //显示操作
            binding.bottomPlay.setVisibility(View.VISIBLE);
            return;
        }

        ToastUtil.showToast(getActivity(),showMsg);
    }

    @Override
    public void showMargePublishResult(boolean isSuccess,String showMsg) {
        stopLoading();

        if (isSuccess){
            //刷新数据
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.STUDY_RANK_REFRESH,null));
            return;
        }

        //显示信息
        ToastUtil.showToast(getActivity(),showMsg);
    }

    /**************************************其他*******************************************/
    private LoadingMsgDialog loadingMsgDialog;

    //显示加载弹窗
    private void startLoading(String showMsg){
        if (loadingMsgDialog==null){
            loadingMsgDialog = new LoadingMsgDialog(getActivity());
            loadingMsgDialog.create();
        }

        loadingMsgDialog.setMsg(showMsg);
        loadingMsgDialog.show();
    }

    //关闭加载弹窗
    private void stopLoading(){
        if (loadingMsgDialog!=null&&loadingMsgDialog.isShowing()){
            loadingMsgDialog.dismiss();
        }
    }

    //更新ui显示
    private void updateUI(boolean loading,String showMsg){
        if (loading){
            binding.evalLoading.setVisibility(View.VISIBLE);
            binding.evalButton.setVisibility(View.INVISIBLE);
            binding.evalMsg.setText("正在加载评测数据");
        }else {
            if (TextUtils.isEmpty(showMsg)){
                binding.evalLoading.setVisibility(View.GONE);
            }else {
                binding.evalLoading.setVisibility(View.VISIBLE);
                binding.evalButton.setVisibility(View.VISIBLE);
                binding.evalMsg.setText(showMsg);
                binding.evalButton.setOnClickListener(v->{
                    refreshData();
                });
            }
        }
    }

    //获取评测音频保存路径
    private String getEvalAudioPath(String voaId,String paraId,String idIndex,int userId){
        String fileName = voaId+"_"+paraId+"_"+idIndex+"_"+userId+".mp3";

        String prefixPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            prefixPath = getActivity().getExternalFilesDir(null).getPath();
        }else {
            prefixPath = Environment.getExternalStorageDirectory().getPath();
        }

        String savePath = prefixPath+"/eval/"+voaId+"/"+fileName;
        return savePath;
    }

    //转换时间为分钟
    private String transTimeToMinute(long showTime){
        long second = showTime/1000L;

        int minute = (int) (second/60);
        int lastSecond = (int) (second%60);

        String showMinute = "";
        if (minute>=10){
            showMinute = String.valueOf(minute);
        }else {
            showMinute = "0"+minute;
        }

        String showSecond = "";
        if (lastSecond>=10){
            showSecond = String.valueOf(lastSecond);
        }else {
            showSecond = "0"+lastSecond;
        }

        return showMinute+":"+showSecond;
    }
}
