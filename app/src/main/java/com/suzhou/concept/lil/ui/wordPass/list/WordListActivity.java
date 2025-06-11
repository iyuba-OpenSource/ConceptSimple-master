package com.suzhou.concept.lil.ui.wordPass.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.ActivityWordListBinding;
import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingActivity;
import com.suzhou.concept.lil.ui.wordPass.list.passShow.WordPassShowActivity;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.dialog.LoadingDialog;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词列表界面
 */
public class WordListActivity extends BaseViewBindingActivity<ActivityWordListBinding> implements WordListView{

    //数据
    private WordListPresenter presenter;
    //适配器
    private WordListAdapter adapter;
    //播放器
    private ExoPlayer exoPlayer;
    //收藏单词的选中位置
    private int selectCollectPosition = 0;

    public static void start(Context context,String type,int bookId,int id){
        Intent intent = new Intent();
        intent.setClass(context, WordListActivity.class);
        intent.putExtra(StrLibrary.type,type);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.id,id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new WordListPresenter();
        presenter.attachView(this);

        initData();
        initList();
        initClick();
        initExoPlayer();

        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        //停止音频
        pauseAudio();
        //取消操作
        presenter.detachView();
    }

    /*****************************初始化*********************************/
    private void initData(){
        String type = getIntent().getStringExtra(StrLibrary.type);
        int id = getIntent().getIntExtra(StrLibrary.id,0);

        String showTitle = "";
        switch (type){
            case TypeLibrary.BookType.conceptFour:
                //全四册
                showTitle = "Lesson "+(id%1000);
                break;
            case TypeLibrary.BookType.conceptJunior:
                //青少版
                showTitle = "Unit "+id;
                break;
            default:
                showTitle = "未知课程";
                break;
        }
        binding.toolbar.standardTitle.setText(showTitle);
        binding.toolbar.standardLeft.setImageResource(R.drawable.left);
        binding.toolbar.standardLeft.setOnClickListener(v->{
            finish();
        });
    }

    private void initList(){
        adapter = new WordListAdapter(this,new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter.setOnWordListClickListener(new WordListAdapter.OnWordListClickListener() {
            @Override
            public void onCollect(boolean hasCollected, int collectPosition,WordShowBean showBean) {
                selectCollectPosition = collectPosition;

                //显示加载弹窗
                startLoading();
                //合并数据并操作(有收藏数据的话，可以删除)
                presenter.collectWord(showBean,hasCollected);
            }

            @Override
            public void onPlay(String playUrl) {
                //播放音频(判断是否当前播放)
                if (exoPlayer!=null){
                    playAudio(playUrl);
                }else {
                    initExoPlayer();
                    playAudio(playUrl);
                }
            }
        });
    }

    private void initClick(){
        binding.passView.setOnClickListener(v->{
            //登录判断
            if (!GlobalMemory.INSTANCE.isLogin()){
                context.startActivity(new Intent(context, LoginActivity.class));
                return;
            }

            String type = getIntent().getStringExtra(StrLibrary.type);
            int bookId = getIntent().getIntExtra(StrLibrary.bookId,0);
            int id = getIntent().getIntExtra(StrLibrary.id,0);
            WordPassShowActivity.start(this,type,bookId,id);
        });
    }

    /*****************************刷新数据*********************************/
    private void refreshData(){
        String type = getIntent().getStringExtra(StrLibrary.type);
        int bookId = getIntent().getIntExtra(StrLibrary.bookId,0);
        int id = getIntent().getIntExtra(StrLibrary.id,0);

        presenter.getWordDataByUnit(type,bookId,id);
    }

    /*****************************刷新数据*********************************/
    //初始化音频
    private void initExoPlayer(){
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //加载完成，播放音频
                        playAudio(null);
                        break;
                    case Player.STATE_ENDED:
                        //结束播放
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastUtil.showToast(WordListActivity.this,"播放音频失败("+error.getMessage()+")");
            }
        });
    }

    //播放音频
    private void playAudio(String pathOrUrl){
        if (!TextUtils.isEmpty(pathOrUrl)){
            //暂停音频
            pauseAudio();
            //合并播放链接
            String playUrl = pathOrUrl;
            //准备操作
            MediaItem mediaItem = MediaItem.fromUri(playUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }else {
            exoPlayer.play();
        }
    }

    //暂停音频
    private void pauseAudio(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }

    /*****************************弹窗*********************************/
    private LoadingDialog loadingDialog;

    private void startLoading(){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        if (loadingDialog!=null&&!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    /*******************************回调************************/
    @Override
    public void showUnitWordData(List<WordShowBean> list) {
        if (list==null){
            ToastUtil.showToast(this,"暂无当前的单词数据");
            binding.passView.setVisibility(View.GONE);
            return;
        }

        binding.passView.setVisibility(View.VISIBLE);
        adapter.refreshData(list);
    }

    @Override
    public void showCollectWordData(boolean isSuccess, String showMsg) {
        stopLoading();
        ToastUtil.showToast(this,showMsg);

        if (!isSuccess){
            return;
        }

        adapter.notifyItemChanged(selectCollectPosition);
    }

    /*******************************回调***************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event){
        if (event.getType().endsWith(RefreshEvent.WORD_PASS_REFRESH)){
            //闯关完成
            finish();
        }
    }
}
