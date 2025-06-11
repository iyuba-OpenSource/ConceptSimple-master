package com.suzhou.concept.lil.ui.study.word;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.sdk.other.NetworkUtil;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.dollar.MemberCentreActivity;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.FragmentKnowledgeNewBinding;
import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.ui.study.word.train.WordTrainActivity;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的单词界面
 */
public class KnowledgeNewFragment extends BaseViewBindingFragment<FragmentKnowledgeNewBinding> implements KnowledgeNewView{

    //数据
    private KnowledgeNewPresenter presenter;
    //列表适配器
    private KnowledgeListAdapter adapter;
    //播放器
    private ExoPlayer exoPlayer;

    //单词类型
    private String wordType;
    //书籍id
    private int bookId;
    //voaId
    private int voaId;

    public static KnowledgeNewFragment getInstance(String wordType,int bookId,int voaId,int position){
        KnowledgeNewFragment fragment = new KnowledgeNewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.type,wordType);
        bundle.putInt(StrLibrary.bookId,bookId);
        bundle.putInt(StrLibrary.voaId,voaId);
        bundle.putInt(StrLibrary.position,position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new KnowledgeNewPresenter();
        presenter.attachView(this);

        wordType = getArguments().getString(StrLibrary.type);
        bookId = getArguments().getInt(StrLibrary.bookId);
        voaId = getArguments().getInt(StrLibrary.voaId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initPlayer();

        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();

        //暂停播放
        pauseAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /********************************初始化**********************************************/
    private void initList(){
        //单词列表显示
        adapter = new KnowledgeListAdapter(getActivity(),new ArrayList<>());
        binding.wordView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.wordView.setAdapter(adapter);
        adapter.setOnSimpleClickListener(new KnowledgeListAdapter.OnItemClickListener() {
            @Override
            public void onPlay(String playUrl) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后使用");
                    return;
                }

                //暂停播放
                pauseAudio();
                //开始播放
                playAudio(playUrl);
            }
        });


        //训练功能显示
        List<Pair<String,Pair<Integer,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(WordTrainActivity.Train_enToCn, new Pair<>(R.drawable.vector_en2cn,"英汉训练")));
        pairList.add(new Pair<>(WordTrainActivity.Train_cnToEn, new Pair<>(R.drawable.vector_cn2en,"汉英训练")));
        pairList.add(new Pair<>(WordTrainActivity.Word_spell, new Pair<>(R.drawable.vector_spelling,"单词拼写")));
        pairList.add(new Pair<>(WordTrainActivity.Train_listen, new Pair<>(R.drawable.vector_listen,"听力训练")));

        KnowledgeBottomAdapter bottomAdapter = new KnowledgeBottomAdapter(getActivity(),pairList);
        binding.bottomView.setLayoutManager(new GridLayoutManager(getActivity(),pairList.size()));
        binding.bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setOnSimpleClickListener(new OnSimpleClickListener<String>() {
            @Override
            public void onClick(String showType) {
                //判断登陆信息
                if (!GlobalMemory.INSTANCE.isLogin()){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }

                //判断当前是否小于3或者购买会员
                int position = getArguments().getInt(StrLibrary.position,0);
                if (position > 2 && !GlobalMemory.INSTANCE.getUserInfo().isVip()){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("会员购买")
                            .setMessage("单词训练仅限前三课，是否开通会员继续使用？")
                            .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity(), MemberCentreActivity.class));
                                }
                            }).setNegativeButton("取消购买",null)
                            .setCancelable(false)
                            .create().show();
                    return;
                }


                //进入练习界面
                WordTrainActivity.start(getActivity(),showType,wordType,bookId,voaId);
            }
        });
    }

    /*********************************音频**********************************************/
    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成
                        playAudio(null);
                        break;
                    case Player.STATE_ENDED:
                        //播放完成
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(getActivity(),"当前音频播放异常，请重试～");
            }
        });
    }

    private void playAudio(String playUrl){
        if (TextUtils.isEmpty(playUrl)){
            exoPlayer.play();
        }else {
            MediaItem mediaItem = MediaItem.fromUri(playUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }
    }

    private void pauseAudio(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }

    /********************************刷新数据**********************************************/
    private void refreshData(){
        presenter.getWordData(wordType,voaId);
    }

    /*******************************回调数据*******************************/
    @Override
    public void showWordData(List<WordShowBean> list) {
        if (list==null || list.size()==0){
            updateUI("当前课程暂无单词");
            return;
        }

        updateUI(null);

        //刷新数据显示
        adapter.refreshData(list);
    }

    /********************************其他功能**********************************************/
    //切换界面显示
    private void updateUI(String showMsg){
        if (TextUtils.isEmpty(showMsg)){
            binding.emptyLayout.setVisibility(View.GONE);
        }else {
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.showMsg.setText(showMsg);
        }
    }
}