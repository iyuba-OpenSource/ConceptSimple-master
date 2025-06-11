package com.suzhou.concept.lil.ui.study.word.train.show;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.sdk.other.NetworkUtil;
import com.suzhou.concept.R;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.ui.study.word.train.WordBean;
import com.suzhou.concept.lil.ui.study.word.train.WordTrainAdapter;
import com.suzhou.concept.lil.ui.study.word.train.WordTrainPresenter;
import com.suzhou.concept.lil.view.dialog.MultiButtonDialog;
import com.suzhou.concept.lil.view.dialog.SingleButtonDialog;

import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * @title: 汉英训练或者英汉训练
 * @date: 2023/8/15 16:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 根据标识符显示
 */
public class WordTrain_enCnFragment extends Fragment {

    private String showType;
    private String wordType;
    private int bookId;
    private int voaId;

    //音频是否加载完成
    private boolean isCanPlay = false;

    //当前单词的位置
    private int selectIndex = 0;
    //当前数据
    private Pair<WordBean,List<WordBean>> curBean;
    //当前需要练习的数据
    private List<Pair<WordBean,List<WordBean>>> pairList;

    private WordTrainAdapter trainAdapter;
    private WordTrainPresenter presenter;

    //播放器
    private ExoPlayer exoPlayer;
    //震动
    private Vibrator vibrator;
    //结果弹窗
    private SingleButtonDialog resultDialog;
    //进度弹窗
    private MultiButtonDialog progressDialog;

    //保存结果，用于显示（暂时保存，当前未用到）
    private List<Pair<WordBean, WordBean>> resultList = new ArrayList<>();
    //正确的单词数量
    private int rightCount = 0;
    //已经完成进度的单词数量
    private int progressCount = 0;

    //控件
    private View toolbarView;
    private RecyclerView recyclerView;
    private Button nextView;
    private ProgressBar progressView;
    private TextView progressTextView;
    private TextView wordView;

    /**
     *
     * @param showType 展示的类型-英汉、汉英
     * @param wordType 数据类型-全四册、青少版
     * @param bookId 书籍id
     * @param voaId 课程id
     * @return
     */
    public static WordTrain_enCnFragment getInstance(String showType, String wordType, int bookId,int voaId){
        WordTrain_enCnFragment fragment = new WordTrain_enCnFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.showType,showType);
        bundle.putString(StrLibrary.type,wordType);
        bundle.putInt(StrLibrary.bookId,bookId);
        bundle.putInt(StrLibrary.voaId,voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getArguments().getString(StrLibrary.showType);
        wordType = getArguments().getString(StrLibrary.type);
        bookId = getArguments().getInt(StrLibrary.bookId);
        voaId = getArguments().getInt(StrLibrary.voaId,0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_word_train_encn,null);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();
        initClick();

        updateData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        pausePlay();
        closeResultDialog();
        closeProgressDialog();
    }

    /***********************初始化*********************/
    private void initView(View rootView){
        toolbarView = rootView.findViewById(R.id.toolbar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        nextView = rootView.findViewById(R.id.next);
        progressView = rootView.findViewById(R.id.progress);
        progressTextView = rootView.findViewById(R.id.progressText);
        wordView = rootView.findViewById(R.id.word);
    }

    private void initList(){
        toolbarView.setVisibility(View.GONE);
        pairList = WordTrainPresenter.getInstance().getRandomWordShowDataNew(wordType,bookId,voaId);

        trainAdapter = new WordTrainAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(trainAdapter);
        trainAdapter.setListener(new WordTrainAdapter.OnSimpleClickListener<Pair<Integer, WordBean>>() {
            @Override
            public void onClick(Pair<Integer, WordBean> pair) {
                //设置显示
                trainAdapter.refreshAnswer(pair.first,curBean.first.getWord());
                //判断当前是否正确
                if (pair.second.getWord().equals(curBean.first.getWord())){
                    //一致
                    startPlay();
                    //增加正确数量
                    rightCount++;
                }else {
                    //不一致
                    showVibrate();
                    //这里要求加上播放音频，感觉怪怪的，不知道用户能否分辨出正确读音还是错误读音
                    startPlay();
                }
                //写入进度
                progressCount++;
                //保存结果
                resultList.add(new Pair<>(curBean.first, pair.second));
                //显示下一个
                nextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        isCanPlay = true;
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                isCanPlay = false;
                ToastFactory.showShort(getActivity(),"当前单词音频加载错误，请点击单词再次播放");
            }
        });
    }

    private void initClick(){
        nextView.setOnClickListener(v->{
            pausePlay();
            stopVibrate();

            String showText = nextView.getText().toString();
            if (showText.equals("查看结果")){
                showResultDialog();
            }else {
                selectIndex++;
                updateData();

                if (selectIndex>=pairList.size()-1){
                    nextView.setText("查看结果");
                }
            }
        });
    }

    /*************************刷新显示***************************/
    private void updateData(){
        curBean = pairList.get(selectIndex);

        if (showType.equals(TypeLibrary.WordTrainType.Train_enToCn)){
            wordView.setText(curBean.first.getWord());
            trainAdapter.refreshData(curBean.second, TypeLibrary.TextShowType.CN);
        }else if (showType.equals(TypeLibrary.WordTrainType.Train_cnToEn)){
            wordView.setText(curBean.first.getDef());
            trainAdapter.refreshData(curBean.second, TypeLibrary.TextShowType.EN);
        }
        nextView.setVisibility(View.INVISIBLE);

        progressView.setMax(pairList.size());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            progressView.setProgress(selectIndex+1,true);
        }else {
            progressView.setProgress(selectIndex+1);
        }
        progressTextView.setText((selectIndex+1)+"/"+pairList.size());

        isCanPlay = false;
        Log.d("单词训练000", curBean.first.getWord()+"--"+curBean.first.getWordAudioUrl());
        preparePlay(curBean.first.getWordAudioUrl());
    }

    /*********************辅助功能**********************/
    //加载音频
    private void preparePlay(String audioUrl){
        Log.d("单词训练", audioUrl==null?"无链接":audioUrl.toString());
        if (!NetworkUtil.isConnected(getActivity())){
            ToastFactory.showShort(getActivity(),"请链接网络后重试");
            return;
        }

        if (exoPlayer!=null&&!TextUtils.isEmpty(audioUrl)){
            exoPlayer.setPlayWhenReady(false);
            MediaItem mediaItem = MediaItem.fromUri(audioUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }
    }

    //播放音频
    private void startPlay(){
        if (exoPlayer!=null&&isCanPlay){
            exoPlayer.play();
        }
    }

    //暂停播放
    private void pausePlay(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }

    //显示震动
    private void showVibrate(){
        stopVibrate();
        if (vibrator==null){
            vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        }
        vibrator.vibrate(300L);
    }

    //停止震动
    private void stopVibrate(){
        if (vibrator!=null){
            vibrator.cancel();
        }
    }

    //显示结果弹窗
    private void showResultDialog(){
        String msg = "正确数量："+rightCount+"\n正确率："+(rightCount*100/pairList.size())+"%"+"\n总数量："+pairList.size();

        if (resultDialog==null){
            resultDialog = new SingleButtonDialog(getActivity());
            resultDialog.create();
        }
        resultDialog.setTitle("训练结果");
        resultDialog.setMsg(msg);
        resultDialog.setButton("确定", new SingleButtonDialog.OnSingleClickListener() {
            @Override
            public void onClick() {
                getActivity().finish();
            }
        });
        resultDialog.show();
    }

    //关闭结果弹窗
    private void closeResultDialog(){
        if (resultDialog!=null){
            resultDialog.dismiss();
        }
    }

    //显示进度弹窗
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog = new MultiButtonDialog(getActivity());
            progressDialog.create();
        }
        String msg = "当前已完成"+progressCount+"个单词，还有"+(pairList.size()-progressCount)+"个单词需要训练，是否退出当前训练？";
        progressDialog.setTitle("训练进度");
        progressDialog.setMsg(msg);
        progressDialog.setButton("继续训练", "立即退出", new MultiButtonDialog.OnMultiClickListener() {
            @Override
            public void onAgree() {
                getActivity().finish();
            }

            @Override
            public void onDisagree() {

            }
        });
        progressDialog.show();
    }

    //关闭进度弹窗
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    //退出提示
    public boolean showExistDialog(){
        if (progressCount!=0&&progressCount<pairList.size()){
            showProgressDialog();
            return true;
        }

        closeResultDialog();
        return false;
    }
}
