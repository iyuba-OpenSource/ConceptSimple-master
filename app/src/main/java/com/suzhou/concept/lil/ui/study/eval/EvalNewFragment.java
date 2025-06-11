//package com.suzhou.concept.lil.ui.study.eval;
//
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
//import com.suzhou.concept.AppClient;
//import com.suzhou.concept.R;
//import com.suzhou.concept.bean.EvaluationSentenceItem;
//import com.suzhou.concept.bean.YoungSentenceItem;
//import com.suzhou.concept.dao.AppDatabase;
//import com.suzhou.concept.databinding.FragmentEvalNewBinding;
//import com.suzhou.concept.lil.data.newDB.RoomDBManager;
//import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
//import com.suzhou.concept.lil.data.remote.RetrofitUtil;
//import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;
//import com.suzhou.concept.lil.event.RefreshEvent;
//import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
//import com.suzhou.concept.lil.ui.study.eval.bean.PublishEvalBean;
//import com.suzhou.concept.lil.ui.study.eval.bean.SentenceMargeBean;
//import com.suzhou.concept.lil.ui.study.eval.bean.SentenceTransBean;
//import com.suzhou.concept.lil.ui.study.eval.checkEval.EvalWordFixPage;
//import com.suzhou.concept.lil.ui.study.eval.util.HelpDateUtil;
//import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
//import com.suzhou.concept.lil.ui.study.eval.util.RecordManager;
//import com.suzhou.concept.lil.ui.study.eval.util.RxTimer;
//import com.suzhou.concept.lil.util.BigDecimalUtil;
//import com.suzhou.concept.utils.GlobalMemory;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;
//import personal.iyuba.personalhomelibrary.utils.ToastFactory;
//
///**
// *  新的评测界面
// */
//public class EvalNewFragment extends Fragment {
//
//    private EvalNewAdapter evalNewAdapter;
//
//    private MediaPlayer sentencePlayer;//句子播放
//    private RecordManager recordManager;//录音
//    private MediaPlayer evalPlayer;//评测播放
//
//    private String margeAudioUrl;//合成音频的链接
//    private MediaPlayer margePlayer;
//    private boolean isMargePrepare = false;
//    private boolean isOpenMarge = true;
//
//    private EvalWordFixPage fixPage;
//    //是否可以播放音频（当前界面可以，切换到其他界面不行）
//    private boolean isCanPlay = false;
//
//    //布局样式
//    private FragmentEvalNewBinding binding;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        binding = FragmentEvalNewBinding.inflate(inflater,container,false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        initView();
//        initData();
//        checkData();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        isCanPlay = true;
//    }
//
//    private void checkData(){
//        updateStyle(true,null);
//
//        int userId = GlobalMemory.INSTANCE.getUserInfo().getUid();
//        String voaId = AppClient.Companion.getConceptItem().getVoa_id();
//
//        //检查数据
//        List<SentenceTransBean> list = null;
//        if (GlobalMemory.INSTANCE.getCurrentYoung()){
//            List<YoungSentenceItem> youngList = AppDatabase.Companion.getDatabase(getActivity()).youngSentenceDao().selectClassSentence(userId,Integer.parseInt(voaId));
//            list = HelpUtil.transYoungToSentence(youngList);
//        }else {
//            List<EvaluationSentenceItem> usukList = AppDatabase.Companion.getDatabase(getActivity()).localSentenceDao().selectSentenceList(userId,Integer.parseInt(voaId));
//            list = HelpUtil.transUSUKToSentence(usukList);
//        }
//
//        if (list!=null && list.size()>0) {
//            updateStyle(false, null);
//            evalNewAdapter.refreshData(list);
//        }else {
//            updateStyle(false,"数据获取失败，请重试～");
//        }
//    }
//
//    private void initView(){
//        binding.evalButton.setOnClickListener(v->{
//            checkData();
//        });
//
//        binding.buttonMarge.setOnClickListener(v->{
//            String voaId = AppClient.Companion.getConceptItem().getVoa_id();
//            int uId = GlobalMemory.INSTANCE.getUserInfo().getUid();
//            List<EvalResultBean> evalList = RoomDBManager.getInstance().getEvalResultData(uId,voaId);
//            if (evalList!=null&&evalList.size()>=2){
//                showLoading("正在合并音频中...");
//
//                RetrofitUtil.getInstance().updateEvalMarge(evalList)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<SentenceMargeBean>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(SentenceMargeBean bean) {
//                                hideLoading();
//
//                                if (bean.getResult().equals("1")){
//                                    binding.bottomPlay.setVisibility(View.VISIBLE);
//
//                                    //数据显示
//                                    isMargePrepare = false;
//                                    margeAudioUrl = bean.getUrl();
//
//                                    //合并分数显示
//                                    int margeScore = 0;
//                                    for (int i = 0; i < evalList.size(); i++) {
//                                        margeScore+=(int) (evalList.get(i).total_score*20);
//                                    }
//                                    margeScore = (int) (margeScore/evalList.size());
//                                    binding.scoreView.setText(String.valueOf(margeScore));
//                                }else {
//                                    ToastFactory.showShort(getActivity(),"合成失败，请重试~");
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                hideLoading();
//
//                                ToastFactory.showShort(getActivity(),"合成失败，请重试~");
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
//            }else {
//                ToastFactory.showShort(getActivity(),"请至少评测两句后再合成~");
//            }
//        });
//        binding.playView.setOnClickListener(v->{
//            if (!isOpenMarge){
//                ToastFactory.showShort(getActivity(),"正在进行其他操作，请等待完成后");
//                return;
//            }
//
//            if (margePlayer!=null&&margePlayer.isPlaying()){
//                pauseMargePlayer();
//            }else {
//                setMargePlayer();
//            }
//        });
//        binding.publishView.setOnClickListener(v->{
//            if (handler.hasMessages(LISTEN)){
//                handler.removeMessages(LISTEN);
//            }
//            binding.playProgress.setProgress(0);
//            binding.playTime.setText(HelpDateUtil.transPlayFormat(HelpDateUtil.MINUTE,0));
//            binding.playView.setImageResource(R.drawable.play_evaluation_old);
//
//            if (margeAudioUrl!=null){
//                showLoading("正在发布到排行榜...");
//
//                List<EvalResultBean> evalList = RoomDBManager.getInstance().getEvalResultData(GlobalMemory.INSTANCE.getUserInfo().getUid(),AppClient.Companion.getConceptItem().getVoa_id());
//                if (evalList!=null&&evalList.size()>0){
//                    RetrofitUtil.getInstance().publishEvalMarge(AppClient.Companion.getConceptItem().getVoa_id(),evalList,margeAudioUrl)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Observer<PublishEvalBean>() {
//                                @Override
//                                public void onSubscribe(Disposable d) {
//
//                                }
//
//                                @Override
//                                public void onNext(PublishEvalBean bean) {
//                                    hideLoading();
//
//                                    if (bean.getMessage().toLowerCase().equals("ok")){
//                                        ToastFactory.showShort(getActivity(),"发布合成配音成功，请至排行界面查看");
//                                        //刷洗排行榜数据
//                                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.STUDY_RANK_REFRESH,null));
//
//                                        //判断奖励进行显示
//                                        String reward = bean.getReward();
//                                        float showMoney = TextUtils.isEmpty(reward)?0f: Float.parseFloat(reward);
//                                        if (showMoney>0){
//                                            String showMsg = String.format("本次学习获得%1$s元,已自动存入您的钱包账户", BigDecimalUtil.trans2Double(showMoney*0.01f));
//                                            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.SHOW_DIALOG,showMsg));
//                                        }
//                                    }else {
//                                        ToastFactory.showShort(getActivity(),"发布失败，请重试");
//                                    }
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    hideLoading();
//
//                                    ToastFactory.showShort(getActivity(),"发布配音失败，请重试～");
//                                }
//
//                                @Override
//                                public void onComplete() {
//
//                                }
//                            });
//                }else {
//                    ToastFactory.showShort(getActivity(),"请合成配音后进行发布");
//                }
//            }else {
//                ToastFactory.showShort(getActivity(),"请合成配音后进行发布");
//            }
//        });
//    }
//
//    private void initData(){
//        //展示数据
//        evalNewAdapter = new EvalNewAdapter(getActivity(),new ArrayList<>());
//        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
//        binding.recyclerView.setLayoutManager(manager);
//        binding.recyclerView.setAdapter(evalNewAdapter);
//        evalNewAdapter.setOnCallBackListener(new EvalNewAdapter.OnCallBackListener() {
//            @Override
//            public void onWarn() {
//                if (margePlayer!=null&&margePlayer.isPlaying()){
//                    margePlayer.pause();
//                    margePlayer.seekTo(0);
//                }
//
//                handler.removeMessages(LISTEN);
//                binding.playTime.setText("00:00");
//                binding.playProgress.setProgress(0);
//                binding.playView.setImageResource(R.drawable.play_evaluation_old);
//                isMargePrepare = false;
//            }
//
//            @Override
//            public void onOpen(boolean isOpen) {
//                isOpenMarge = isOpen;
//            }
//
//            @Override
//            public void onEval(boolean isSentence, String voaId, String paraId, String indexId, String sentence, String filePath) {
//                showLoading("正在评测中...");
//
//                RetrofitUtil.getInstance().updateEval(isSentence,filePath,voaId,paraId,indexId,sentence)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<BaseBean<EvalShowBean>>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(BaseBean<EvalShowBean> bean) {
//                                hideLoading();
//
//                                if (bean.getResult().equals("1")){
//                                    evalNewAdapter.showEvalResult(bean.getData());
//                                }else {
//                                    onError(null);
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                hideLoading();
//
//                                ToastFactory.showShort(getActivity(),"评测失败，请重试");
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
//            }
//
//            @Override
//            public void publish(String voaId, String idIndex, String paraId, int score, String url) {
//                showLoading("正在发布到排行榜...");
//
//                RetrofitUtil.getInstance().publishEval(voaId, paraId, idIndex, score, url)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<PublishEvalBean>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(PublishEvalBean bean) {
//                                hideLoading();
//
//                                if (bean.getMessage().toLowerCase().equals("ok")){
//                                    ToastFactory.showShort(getActivity(),"发布评测成功，请至排行界面查看");
//                                    //刷新排行
//                                    EventBus.getDefault().post(new RefreshEvent(RefreshEvent.STUDY_RANK_REFRESH,null));
//                                }else {
//                                    ToastFactory.showShort(getActivity(),"发布失败，请重试");
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                hideLoading();
//
//                                ToastFactory.showShort(getActivity(),"发布失败，请重试");
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
//            }
//
//            @Override
//            public void checkEval(EvalShowBean showBean, SentenceTransBean transBean) {
//                //跳转纠音
//                fixPage = EvalWordFixPage.getInstance(showBean, transBean);
//                fixPage.show(getActivity().getSupportFragmentManager(),"");
//            }
//        });
//
//        //下面的数据初始化
//        binding.bottomPlay.setVisibility(View.GONE);
//        binding.playProgress.setProgress(0);
//
//        RxTimer.timerInIO(500L, new RxTimer.RxAction() {
//            @Override
//            public void action(long number) {
//                try {
//                    //原文播放
//                    sentencePlayer = new MediaPlayer();
//                    sentencePlayer.setDataSource(GlobalMemory.INSTANCE.getVideoUrl(HelpUtil.getVoaId()));
//                    sentencePlayer.prepare();
//                    sentencePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            evalNewAdapter.initReadPlayer(true);
//                        }
//                    });
//                    sentencePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            evalNewAdapter.stopReadHandler();
//                        }
//                    });
//
//                    //评测播放
//                    evalPlayer = new MediaPlayer();
//
//                    //合成播放
//                    margePlayer = new MediaPlayer();
//                    margePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            isMargePrepare = true;
//                            binding.playProgress.setMax(margePlayer.getDuration());
//                            binding.playTime.setText(HelpDateUtil.transPlayFormat(HelpDateUtil.MINUTE,binding.playProgress.getProgress()));
//                            binding.totalTime.setText(HelpDateUtil.transPlayFormat(HelpDateUtil.MINUTE,margePlayer.getDuration()));
//
//                            if (!isCanPlay){
//                                return;
//                            }
//
//                            binding.playView.setImageResource(R.drawable.pause_evaluation_old);
//                            margePlayer.start();
//                            handler.sendEmptyMessage(LISTEN);
//                        }
//                    });
//                    margePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            margePlayer.pause();
//                            handler.removeMessages(LISTEN);
//                            binding.playProgress.setProgress(0);
//                            binding.playTime.setText(HelpDateUtil.transPlayFormat(HelpDateUtil.MINUTE,0));
//                            binding.playView.setImageResource(R.drawable.play_evaluation_old);
//                        }
//                    });
//
//                    evalNewAdapter.setPlayer(sentencePlayer,evalPlayer);
//                    RxTimer.cancel();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private static final int MARGE = 1;//合成
//    private static final int LISTEN = 2;//试听
//    private static final int STOP = 3;//停止
//
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what){
//                case MARGE:
//                    break;
//                case LISTEN:
//                    binding.playView.setImageResource(R.drawable.pause_evaluation_old);
//                    binding.playProgress.setProgress(margePlayer.getCurrentPosition());
//                    binding.playTime.setText(HelpDateUtil.transPlayFormat(HelpDateUtil.MINUTE,binding.playProgress.getProgress()));
//
//                    handler.sendEmptyMessageDelayed(LISTEN,500L);
//                    break;
//                case STOP:
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//
//    private LoadingDialog loadingDialog;
//    //显示加载
//    private void showLoading(String showMsg){
//        if (loadingDialog==null){
//            loadingDialog = new LoadingDialog(getActivity());
//        }
//        if (!TextUtils.isEmpty(showMsg)){
//            loadingDialog.setMessage(showMsg);
//        }
//        loadingDialog.show();
//    }
//
//    //关闭加载
//    private void hideLoading(){
//        if (loadingDialog!=null&&loadingDialog.isShowing()){
//            loadingDialog.dismiss();
//        }
//    }
//
//    private void setMargePlayer(){
//        if (margePlayer==null){
//            margePlayer = new MediaPlayer();
//        }
//
//        try {
//            if (isMargePrepare){
//                margePlayer.start();
//                binding.playView.setImageResource(R.drawable.pause_evaluation_old);
//
//                handler.sendEmptyMessage(LISTEN);
//            }else {
//                margePlayer.reset();
//                margePlayer.setDataSource(getActivity(), Uri.parse(HelpUtil.getEvalPlayUrl(margeAudioUrl)));
//                margePlayer.prepare();
//            }
//        }catch (Exception e){
//            ToastFactory.showShort(getActivity(),"加载合成音频异常");
//        }
//    }
//
//    private void pauseMargePlayer(){
//        if (margePlayer!=null&&margePlayer.isPlaying()){
//            margePlayer.pause();
//            margePlayer.seekTo(0);
//        }
//
//        handler.removeMessages(LISTEN);
//        binding.playTime.setText("00:00");
//        binding.playProgress.setProgress(0);
//        binding.playView.setImageResource(R.drawable.play_evaluation_old);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        hideLoading();
//
//        if (fixPage!=null){
//            fixPage.dismiss();
//        }
//
//        isCanPlay = false;
//
//        //这里注意停止音频播放和录音操作
//        if (evalNewAdapter!=null){
//            evalNewAdapter.stopRecordAndPlay();
//        }
//    }
//
//    //切换状态显示
//    private void updateStyle(boolean loading,String showMsg){
//        if (loading){
//            binding.evalLoading.setVisibility(View.VISIBLE);
//            binding.evalButton.setVisibility(View.INVISIBLE);
//            binding.evalMsg.setText("正在加载评测数据");
//        }else {
//            if (TextUtils.isEmpty(showMsg)){
//                binding.evalLoading.setVisibility(View.GONE);
//            }else {
//                binding.evalLoading.setVisibility(View.VISIBLE);
//                binding.evalButton.setVisibility(View.VISIBLE);
//                binding.evalMsg.setText(showMsg);
//            }
//        }
//    }
//}
