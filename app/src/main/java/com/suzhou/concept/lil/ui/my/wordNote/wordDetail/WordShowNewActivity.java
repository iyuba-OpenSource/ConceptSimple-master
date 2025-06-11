package com.suzhou.concept.lil.ui.my.wordNote.wordDetail;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.ui.my.wordNote.WordDeleteBean;
import com.suzhou.concept.lil.ui.study.eval.bean.WordExplainBean;
import com.suzhou.concept.lil.util.LibRxUtil;
import com.suzhou.concept.lil.view.dialog.LoadingDialog;
import com.suzhou.concept.utils.ExtraKeysFactory;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * 单词详情
 */
public class WordShowNewActivity extends AppCompatActivity {

    private boolean isWordAudioPrepare = false;
    private MediaPlayer wordPlayer;
    private String wordAudioUrl = null;

    //适配器
    private WordShowNewAdapter newAdapter;
    //加载弹窗
    private LoadingDialog loadingDialog;
    //查询出的数据
    private WordCollectBean collectBean;

    private ImageView playView;
    private TextView wordView;
    private TextView pornView;
    private TextView descView;
    private ImageView collectView;

    private TextView tipsView;
    private RecyclerView recyclerView;

    //是否已经删除了数据
    private  boolean isDeleteWord = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_word_new);

        String word = getIntent().getStringExtra(ExtraKeysFactory.definitionWord);
        initToolbar(word);
        initView();
        initClick();

        loadWord(word);
    }

    @Override
    protected void onStop() {
        super.onStop();

        pausePlayer();

//        View view = null;
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return false;
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LibRxUtil.unDisposable(collectWordDis);
        closeLoading();

        //刷新前边的生词列表数据
        if (isDeleteWord){
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.WORD_NOTE_REFRESH,null));
        }
    }

    private void initToolbar(String title){
        TextView titleView = findViewById(R.id.standard_title);
        titleView.setText(title);
        ImageView leftView =findViewById(R.id.standard_left);
        leftView.setBackgroundResource(R.drawable.left);
        leftView.setOnClickListener(v->{
            finish();
        });
    }

    private void initView(){
        playView = findViewById(R.id.word_play);
        wordView = findViewById(R.id.key_item);
        pornView = findViewById(R.id.porn);
        descView = findViewById(R.id.desc);
        collectView = findViewById(R.id.collect);

        tipsView = findViewById(R.id.word_sentence_tips);
        recyclerView = findViewById(R.id.word_sentence_list);
    }

    private void initClick(){
        playView.setOnClickListener(v->{
            if (isDestroyed()){
                return;
            }

            if (wordPlayer!=null){
                if (wordPlayer.isPlaying()){
                    pausePlayer();
                }else {
                    startPlayer();
                }
            }else {
                startPlayer();
            }
        });
        collectView.setOnClickListener(v->{
            if (!GlobalMemory.INSTANCE.isLogin()){
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            String word = getIntent().getStringExtra(ExtraKeysFactory.definitionWord);
            //检查数据库中的数据来判断是否需要收藏/取消收藏
            WordCollectBean collectBean = RoomDBManager.getInstance().getSingleWordCollectData(GlobalMemory.INSTANCE.getUserInfo().getUid(),word);
            if (collectBean!=null){
                collectWord("delete");
            }else {
                collectWord("insert");
            }
        });
    }

    //加载数据
    private void loadWord(String word){
        openLoading();

        RetrofitUtil.getInstance().searchWordNew(word)
                .subscribe(new Observer<WordExplainBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WordExplainBean bean) {
                        closeLoading();

                        if (bean.getResult() == 1){
                            //刷新数据
                            refreshData(bean);
                        }else {
                            ToastFactory.showShort(WordShowNewActivity.this,"查询单词失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeLoading();

                        ToastFactory.showShort(WordShowNewActivity.this,"查询单词失败，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //刷新数据
    private void refreshData(WordExplainBean bean){
        this.wordAudioUrl = bean.getAudio();

        wordView.setText(bean.getKey());
        if (TextUtils.isEmpty(bean.getPron())){
            pornView.setVisibility(View.GONE);
        }else {
            pornView.setText("["+bean.getPron()+"]");
        }
        descView.setText(bean.getDef());

        if (bean.getSent()!=null&&bean.getSent().size()>0){
            tipsView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            newAdapter = new WordShowNewAdapter(this,bean.getSent());
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(newAdapter);
        }else {
            tipsView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        //转换成收藏的数据库数据
        collectBean = new WordCollectBean();
        collectBean.word = bean.getKey();
        collectBean.audio = bean.getAudio();
        collectBean.pron = bean.getPron();
        collectBean.def = bean.getDef();
        collectBean.userId = GlobalMemory.INSTANCE.getUserInfo().getUid();

        //同步刷新收藏状态
        WordCollectBean dbData = RoomDBManager.getInstance().getSingleWordCollectData(GlobalMemory.INSTANCE.getUserInfo().getUid(),bean.getKey());
        if (dbData!=null){
            collectView.setImageResource(R.drawable.ic_collected);
        }else {
            collectView.setImageResource(R.drawable.ic_collect_no);
        }
    }

    //收藏/取消收藏
    private Disposable collectWordDis;
    private void collectWord(String mode){
        openLoading();
        List<WordCollectBean> list = new ArrayList<>();
        list.add(collectBean);

        RetrofitUtil.getInstance().collectWord(list,mode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WordDeleteBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectWordDis = d;
                    }

                    @Override
                    public void onNext(WordDeleteBean bean) {
                        String showStr = "收藏";
                        if (mode.equals("delete")){
                            showStr = "取消收藏";
                        }

                        if (bean!=null&&bean.result==1){
                            //根据状态进行收藏/取消收藏操作
                            if (mode.equals("insert")){
                                RoomDBManager.getInstance().saveMultiWordCollectData(list);
                                //图标变换
                                collectView.setImageResource(R.drawable.ic_collected);
                                //设置标志
                                isDeleteWord = false;
                            }else if (mode.equals("delete")){
                                RoomDBManager.getInstance().deleteMultiWordCollectData(list);
                                //图标变换
                                collectView.setImageResource(R.drawable.ic_collect_no);
                                //设置标志
                                isDeleteWord = true;
                            }
                            //显示信息
                            ToastFactory.showShort(WordShowNewActivity.this, showStr +"单词成功");
                        }else {
                            ToastFactory.showShort(WordShowNewActivity.this, showStr +"单词失败，请重试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String showStr = "收藏";
                        if (mode.equals("delete")){
                            showStr = "取消收藏";
                        }
                        ToastFactory.showShort(WordShowNewActivity.this, showStr +"单词异常，请重试");
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(collectWordDis);
                        closeLoading();
                    }
                });
    }

    //开始音频播放
    private void startPlayer(){
        if (TextUtils.isEmpty(wordAudioUrl)){
            ToastFactory.showShort(this,"未找到音频链接");
            return;
        }

        try {
            if (wordPlayer==null){
                wordPlayer = new MediaPlayer();
                wordPlayer.setDataSource(wordAudioUrl);
                wordPlayer.prepare();

                wordPlayer.setOnPreparedListener(mp->{
                    isWordAudioPrepare = true;
                    wordPlayer.start();
                });
                wordPlayer.setOnCompletionListener(mp->{
                    pausePlayer();
                });
            }else {
                if (isWordAudioPrepare){
                    wordPlayer.start();
                }else {
                    ToastFactory.showShort(this,"单词音频正在加载中~");
                }
            }
        }catch (Exception e){
            ToastFactory.showShort(this,"播放音频失败");
        }
    }

    //暂停音频播放
    private void pausePlayer(){
        if (wordPlayer!=null&&wordPlayer.isPlaying()){
            wordPlayer.pause();
        }
    }

    //开启加载
    private void openLoading(){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.show();
    }

    //关闭加载
    private void closeLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
