package com.suzhou.concept.lil.ui.study.eval.checkEval;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.iyuba.module.headlinetalk.ui.widget.LoadingDialog;
import com.suzhou.concept.R;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.ui.checkEval.CheckEvalBean;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceTransBean;
import com.suzhou.concept.lil.ui.study.eval.bean.WordExplainBean;
import com.suzhou.concept.lil.ui.study.eval.util.FileManager;
import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
import com.suzhou.concept.lil.ui.study.eval.util.RecordManager;
import com.suzhou.concept.utils.OnWordClickListener;
import com.suzhou.concept.utils.view.SelectableTextView;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * 纠音界面
 */
public class EvalWordFixPage extends DialogFragment {

    private View rootView;

    private TextView topView;
    private ImageView closeView;
    private SelectableTextView showView;

    private TextView rightView;
    private TextView userView;
    private TextView wordExplain;

    private TextView readView;
    private TextView recordView;
    private TextView evalView;

    private LinearLayout loadingLayout;

    private MediaPlayer readPlayer;//原文和评测的播放器
    private RecordManager recordManager;
    //传递的数据
     private EvalShowBean oldEvalBean;
     private SentenceTransBean sentenceBean;
     //获取的数据
    private WordExplainBean explainBean;
//    private EvalShowBean evalBean;
    private CheckEvalBean.DataBean evalBean;
    //选中的单词
    private String selectWord = "";
    //是否录音
    private boolean isRecording = false;
    //是否评测
    private boolean isEval = false;

    private LoadingDialog loadingDialog;

    public static EvalWordFixPage getInstance(EvalShowBean showBean, SentenceTransBean transBean){
        EvalWordFixPage evalWordFixPage = new EvalWordFixPage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("eval", showBean);
        bundle.putSerializable("sentence",transBean);
        evalWordFixPage.setArguments(bundle);
        return evalWordFixPage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.correct_sound_layout_new,null);

        topView = rootView.findViewById(R.id.correct_sound);
        closeView = rootView.findViewById(R.id.close_correct_sound);
        closeView.setOnClickListener(v->{
            dismiss();
        });
        showView = rootView.findViewById(R.id.content_evaluation);

        rightView = rootView.findViewById(R.id.correct_pronunciation);
        userView = rootView.findViewById(R.id.your_pronunciation);
        wordExplain = rootView.findViewById(R.id.word_definition);

        readView = rootView.findViewById(R.id.listen_original);
        readView.setOnClickListener(v->{
            if (isRecording){
                ToastFactory.showShort(getActivity(),"正在录音中");
                return;
            }

            if (isEval){
                ToastFactory.showShort(getActivity(),"正在评测中");
                return;
            }

            try {
                if (readPlayer.isPlaying()){
                    readPlayer.pause();
                }else {
                    readPlayer.reset();
                    readPlayer.setDataSource(explainBean.getAudio());
                    readPlayer.prepare();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        recordView = rootView.findViewById(R.id.click_start);
        recordView.setOnClickListener(v->{
            if (readPlayer.isPlaying()){
                readPlayer.pause();
            }

            if (isEval){
                ToastFactory.showShort(getActivity(),"正在评测中");
                return;
            }

            //开启或者关闭
            if (recordManager!=null){
                if (isRecording){
                    recordManager.stopRecord();
                    recordView.setText(getResources().getString(R.string.click_start));
                    isRecording = false;

                    //去评测
                    updateEval();
                }else {
                    recordManager = new RecordManager(new File(createPath()));
                    recordManager.startRecord();
                    isRecording = true;

                    recordView.setText(getResources().getString(R.string.click_stop));
                }
            }else {
                recordManager = new RecordManager(new File(createPath()));
                recordManager.startRecord();
                isRecording = true;

                recordView.setText(getResources().getString(R.string.click_stop));
            }
        });
        evalView = rootView.findViewById(R.id.word_score);
        evalView.setOnClickListener(v->{
            if (isRecording){
                ToastFactory.showShort(getActivity(),"正在录音中");
                return;
            }

            if (isEval){
                ToastFactory.showShort(getActivity(),"正在评测中");
                return;
            }

            try {
                if (readPlayer.isPlaying()){
                    readPlayer.pause();
                }else {
                    readPlayer.reset();
                    readPlayer.setDataSource(HelpUtil.getEvalPlayWordUrl(evalBean.getUrl()));
                    readPlayer.prepare();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        loadingLayout = rootView.findViewById(R.id.load_layout);

        builder.setView(rootView);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        
        if (readPlayer==null){
            readPlayer = new MediaPlayer();
        }
        readPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                readPlayer.start();
            }
        });


        //显示数据样式
        oldEvalBean = (EvalShowBean) getArguments().getSerializable("eval");
        sentenceBean = (SentenceTransBean) getArguments().getSerializable("sentence");

        showView.setText(HelpUtil.getSentenceSpan(oldEvalBean));
        showView.setOnWordClickListener(new OnWordClickListener() {
            @Override
            protected void onNoDoubleClick(@NonNull String str) {
                Log.d("查词显示", "开始的数据--"+str);
                if (!TextUtils.isEmpty(str)&&!str.equals(selectWord)){
                    Log.d("查词显示", str+"---"+selectWord);

                    selectWord = str;
                    evalView.setVisibility(View.INVISIBLE);

                    topView.setText(str);
                    searchWord(str);

                    //直接停止音频播放和录音
                    stopPlay();
                    stopRecord();
                }
            }
        });


        //检查展示第一个可以展示的单词(第一个正确或者错误的)
        error:for (int i = 0; i < oldEvalBean.getWords().size(); i++) {
            EvalShowBean.WordsBean wordsBean = oldEvalBean.getWords().get(i);
            double score = Double.parseDouble(wordsBean.getScore());

            if (!wordsBean.getContent().equals("---")
                    &&(score<=2.5f||score>=4.0f)){

                selectWord = wordsBean.getContent();
                //这里去除标点符号
                selectWord = selectWord.trim();
                selectWord = selectWord.replace(",","");
                selectWord = selectWord.replace("!","");
                selectWord = selectWord.replace(".","");

                topView.setText(selectWord);
                break error;
            }
        }

        readView.setVisibility(View.VISIBLE);
        recordView.setVisibility(View.VISIBLE);
        evalView.setVisibility(View.INVISIBLE);
        searchWord(selectWord);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getDialog() != null) {
            getDialog().dismiss();
        }

        //停止播放
        stopPlay();

        //停止录音
        stopRecord();
    }


    //查询单词
    private void searchWord(String word){
        showLoading("正在查词中...");

        //查询单词和释义
        RetrofitUtil.getInstance().searchWordNew(word)
                .subscribe(new Observer<WordExplainBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WordExplainBean bean) {
                        if (bean.getResult() == 1){
                            explainBean = bean;

                            rightView.setText("["+bean.getPron()+"]");
                            userView.setText("");
                            wordExplain.setText("单词释义："+bean.getDef());
                        }else {
                            ToastFactory.showShort(getActivity(),"查询单词失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastFactory.showShort(getActivity(),"查询单词失败，请重试～");
                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });
    }

    //提交评测
    private void updateEval(){
        showLoading("正在评测中...");
        isEval = true;

        /*String path = FileManager.getInstance().getWordEvalAudioPath(selectWord);
        RetrofitUtil.getInstance().updateEval(false,path, sentenceBean.getVoaId(), sentenceBean.getParaId(),sentenceBean.getIdIndex(), selectWord)
                .subscribe(new Observer<BaseBean<EvalShowBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseBean<EvalShowBean> bean) {
                        hideLoading();
                        isEval = false;

                        if (bean.getResult().equals("1")){
                            evalBean = bean.getData();

                            evalView.setVisibility(View.VISIBLE);
                            int score = (int) (bean.getData().getTotal_score()*20);
                            evalView.setText(String.valueOf(score));
                        }else {
                            ToastFactory.showShort(getActivity(),"评测失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        isEval = false;

                        ToastFactory.showShort(getActivity(),"评测失败，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });*/

        //评测另一个接口
        String path = FileManager.getInstance().getWordEvalAudioPath(selectWord);
        RetrofitUtil.getInstance().checkEval(selectWord,sentenceBean.getParaId(),sentenceBean.getIdIndex(),sentenceBean.getVoaId(),path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckEvalBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CheckEvalBean bean) {
                        hideLoading();
                        isEval = false;

                        if (bean.getResult().equals("1")){
                            evalBean = bean.getData();

                            evalView.setVisibility(View.VISIBLE);
                            int score = (int) (bean.getData().getTotal_score()*20);
                            evalView.setText(String.valueOf(score));
                            //你的发音的音标
                            if (evalBean.getWords()!=null&&evalBean.getWords().size()>0){
                                CheckEvalBean.DataBean.WordsBean wordsBean = evalBean.getWords().get(0);
                                userView.setText("["+wordsBean.getUser_pron2()+"]");
                            }else {
                                userView.setText("");
                            }
                        }else {
                            ToastFactory.showShort(getActivity(),"评测失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        isEval = false;

                        ToastFactory.showShort(getActivity(),"评测失败，请重试～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //创建存放路径
    private String createPath() {
        String path = FileManager.getInstance().getWordEvalAudioPath(selectWord);
        try {
            File file = new File(path);
            if (file.exists()){
                file.delete();
            }else {
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
            }
            file.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }

    //显示加载
    private void showLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
        }
        loadingDialog.setMessage(showMsg);
        loadingDialog.show();
    }

    //关闭加载
    private void hideLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //停止播放
    private void stopPlay(){
        if (readPlayer != null) {
            if (readPlayer.isPlaying()) {
                readPlayer.pause();
            }
        }
    }

    //停止录音
    private void stopRecord(){
        if (isRecording&&recordManager!=null){
            recordManager.stopRecord();
            recordView.setText(getResources().getString(R.string.click_start));
            isRecording = false;
        }
    }
}
