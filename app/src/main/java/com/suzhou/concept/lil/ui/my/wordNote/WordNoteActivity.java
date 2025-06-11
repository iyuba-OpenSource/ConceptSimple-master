package com.suzhou.concept.lil.ui.my.wordNote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.suzhou.concept.R;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.ui.my.wordNote.wordDetail.WordShowNewActivity;
import com.suzhou.concept.lil.util.LibRxUtil;
import com.suzhou.concept.lil.view.dialog.LoadingDialog;
import com.suzhou.concept.utils.ExtraKeysFactory;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * @title: 生词本
 * @date: 2023/10/8 16:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordNoteActivity extends AppCompatActivity {

    //固定查询数据内容
    //查询页码
    private int pageIndex = 1;
    //每页数量
    private static final int PAGE_COUNT = 10;

    //控件
    private TextView editView;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    //适配器
    private WordNoteAdapter noteAdapter;
    //音频
    private ExoPlayer exoPlayer;
    //加载弹窗
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_word_note);

        initToolbar();
        initView();
        initList();
        initPlayer();

        switchRefreshStatus(true);
        refreshLayout.autoRefresh();
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        //刷新数据
        refreshLayout.autoRefresh();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        LibRxUtil.unDisposable(collectWordDis);
        LibRxUtil.unDisposable(deleteWordDis);
    }

    /**************************初始化数据********************/
    private void initToolbar(){
        ImageView backView = findViewById(R.id.standard_left);
        backView.setBackgroundResource(0);
        backView.setImageResource(R.drawable.left);
        backView.setOnClickListener(v->{
            finish();
        });
        TextView titleView = findViewById(R.id.standard_title);
        titleView.setText("生词本");
        ImageView rightView = findViewById(R.id.standard_right);
        rightView.setVisibility(View.INVISIBLE);

        editView = findViewById(R.id.standard_right_text);
        editView.setVisibility(View.VISIBLE);
        editView.setTextColor(getResources().getColor(R.color.white));
        editView.setText("编辑");
        editView.setOnClickListener(v->{
            String showText = editView.getText().toString().trim();
            if (showText.equals("编辑")){
                noteAdapter.setEditStatus(true);
                editView.setText("取消");
            }else if (showText.equals("删除")){
                //获取数据进行处理
                List<WordCollectBean> saveList = noteAdapter.getSelectData();
                if (saveList!=null&&saveList.size()>0){
                    //远程接口删除，同步删除本地数据
                    deleteCollectWord(saveList);
                }else {
                    ToastFactory.showShort(this,"请选中数据后操作");
                }
            }else if (showText.equals("取消")){
                noteAdapter.setEditStatus(false);
                editView.setText("编辑");
            }
        });
    }

    private void initView(){
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initList(){
        switchRefreshStatus(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WordNoteActivity.this)){
                    ToastFactory.showShort(WordNoteActivity.this,"请链接网络后重试");
                    return;
                }

                checkRemoteData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(WordNoteActivity.this)){
                    ToastFactory.showShort(WordNoteActivity.this,"请链接网络后重试");
                    return;
                }

                pageIndex = 1;
                checkRemoteData();
                //开启加载操作
                refreshLayout.setEnableLoadMore(true);
            }
        });

        noteAdapter = new WordNoteAdapter(this,new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.setOnItemClickListener(new WordNoteAdapter.OnItemClickListener() {
            @Override
            public void onAudioPlay(String audioUrl) {
                startPlay(audioUrl);
            }

            @Override
            public void onItemClick(WordCollectBean bean) {
                //跳转到查词界面
                Intent intent = new Intent();
                intent.setClass(WordNoteActivity.this, WordShowNewActivity.class);
                intent.putExtra(ExtraKeysFactory.definitionWord,bean.word);
                intent.putExtra(ExtraKeysFactory.listWord,false);
                startActivity(intent);
            }

            @Override
            public void onSelectData(Map<String, WordCollectBean> saveMap) {
                if (saveMap!=null&&saveMap.keySet().size()>0){
                    editView.setText("删除");
                }else {
                    editView.setText("取消");
                }
            }
        });
    }

    /****************************刷新数据********************/
    //数据库操作(原来是想从数据库直接获取数据，但是感觉这样有点问题，先直接从远程接口中获取，后面再处理)
    private void checkDbData(){
        //先从数据库中查询
        List<WordCollectBean> dbList = RoomDBManager.getInstance().getWordCollectAllData(GlobalMemory.INSTANCE.getUserInfo().getUid());
        if (dbList!=null&&dbList.size()>0){
            switchRefreshStatus(false);
            noteAdapter.refreshData(dbList);
        }else {
            //没有数据则从接口中查询
            switchRefreshStatus(true);
            refreshLayout.autoRefresh();
        }
    }

    //远程获取收藏数据
    private Disposable collectWordDis;
    private void checkRemoteData(){
        RetrofitUtil.getInstance().getCollectWordData(pageIndex,PAGE_COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WordNoteBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectWordDis = d;
                    }

                    @Override
                    public void onNext(WordNoteBean bean) {
                        if (bean!=null&&bean.tempWords!=null&&bean.tempWords.size()>0){
                            //一次性转换，节省性能
                            List<WordCollectBean> collectList = transRemoteDataToDbData(bean.tempWords);

                            //这里判断刷新还是增加
                            if (pageIndex==1){
                                //刷新数据
                                noteAdapter.refreshData(collectList);
                            }else {
                                noteAdapter.addData(collectList);
                            }

                            //页码+1
                            if (bean.tempWords.size()>=PAGE_COUNT){
                                pageIndex++;
                            }else {
                                //关闭加载操作
                                refreshLayout.setEnableLoadMore(false);
                            }

                            //保存在本地
                            RoomDBManager.getInstance().saveMultiWordCollectData(collectList);
                        }else {
                            if (pageIndex==1){
                                noteAdapter.refreshData(new ArrayList<>());
                            }else {
                                noteAdapter.addData(new ArrayList<>());
                            }
                            ToastFactory.showShort(WordNoteActivity.this,"暂无更多单词数据");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastFactory.showShort(WordNoteActivity.this,"加载单词数据异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        closeRefreshAndMore();
                        LibRxUtil.unDisposable(collectWordDis);
                    }
                });
    }

    //远程删除收藏单词
    private Disposable deleteWordDis;
    private void deleteCollectWord(List<WordCollectBean> list){
        startLoading();
        RetrofitUtil.getInstance().collectWord(list,"delete")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WordDeleteBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        deleteWordDis = d;
                    }

                    @Override
                    public void onNext(WordDeleteBean bean) {
                        if (bean!=null&&bean.result==1){
                            //同步删除数据
                            RoomDBManager.getInstance().deleteMultiWordCollectData(list);
                            //删除预存数据
                            noteAdapter.clearSelectData();
                            //刷新显示
                            refreshLayout.autoRefresh();
                        }else {
                            ToastFactory.showShort(WordNoteActivity.this,"取消收藏失败，请重试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastFactory.showShort(WordNoteActivity.this,"取消收藏异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        stopLoading();
                        LibRxUtil.unDisposable(deleteWordDis);
                    }
                });
    }

    /*************************音频操作***********************/
    //初始化操作
    private void initPlayer(){
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        if (exoPlayer!=null){
                            exoPlayer.play();
                        }
                        break;
                    case Player.STATE_ENDED:
                        break;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                ToastFactory.showShort(WordNoteActivity.this,"当前音频存在问题，无法进行播放，请重试～");
            }
        });
    }

    //播放音频
    private void startPlay(String audioUrl){
        if (TextUtils.isEmpty(audioUrl)){
            ToastFactory.showShort(this,"音频文件不存在，请重试～");
            return;
        }

        if (!NetworkUtil.isConnected(this)){
            ToastFactory.showShort(WordNoteActivity.this,"请链接网络后重试");
            return;
        }

        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            pausePlay();
        }

        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    //停止播放
    private void pausePlay(){
        if (exoPlayer!=null&&exoPlayer.isPlaying()){
            exoPlayer.pause();
        }
    }

    /*****************************辅助功能**************************/
    //设置刷新功能开启或关闭
    private void switchRefreshStatus(boolean canOpen){
        refreshLayout.setEnableRefresh(canOpen);
        refreshLayout.setEnableLoadMore(canOpen);
    }

    //关闭刷新或更多
    private void closeRefreshAndMore(){
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    //切换远程收藏数据为数据库类型数据，并保存数据
    private List<WordCollectBean> transRemoteDataToDbData(List<WordNoteBean.TempWord> list){
        List<WordCollectBean> dbList = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                WordNoteBean.TempWord tempWord = list.get(i);

                WordCollectBean collectBean = new WordCollectBean();
                collectBean.word = tempWord.word;
                collectBean.pron = tempWord.pronunciation;
                collectBean.audio = tempWord.audioUrl;
                collectBean.def = tempWord.definition;
                collectBean.userId = GlobalMemory.INSTANCE.getUserInfo().getUid();

                dbList.add(collectBean);
            }

            //保存在数据库
            RoomDBManager.getInstance().saveMultiWordCollectData(dbList);
        }
        return dbList;
    }

    //开启加载弹窗
    private void startLoading(){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.show();
    }

    //关闭加载弹窗
    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    /*******************************回调刷新***************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event){
        if (event.getType().equals(RefreshEvent.WORD_NOTE_REFRESH)){
            //生词数据刷新
            refreshLayout.autoRefresh();
        }
    }
}
