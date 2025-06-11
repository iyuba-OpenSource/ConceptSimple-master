package com.suzhou.concept.lil.ui.study.word.train.show;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.ui.study.word.train.WordBean;
import com.suzhou.concept.lil.ui.study.word.train.WordTrainPresenter;
import com.suzhou.concept.lil.view.dialog.MultiButtonDialog;
import com.suzhou.concept.lil.view.dialog.SingleButtonDialog;

import java.util.List;

/**
 * @title: 拼写训练
 * @date: 2023/8/15 17:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordTrain_spellFragment extends Fragment {

    private String wordType;
    private int bookId;
    private int voaId;

    //当前单词的位置
    private int selectIndex = 0;
    //当前数据
    private Pair<WordBean, List<WordBean>> curBean;
    //当前需要练习的数据
    private List<Pair<WordBean,List<WordBean>>> pairList;

    //震动
    private Vibrator vibrator;

    //结果弹窗
    private SingleButtonDialog resultDialog;
    //进度弹窗
    private MultiButtonDialog progressDialog;

    //正确的单词数量
    private int rightCount = 0;
    //完成的单词数量
    private int progressCount = 0;

    //控件
    private View toolbarView;
    private RecyclerView recyclerView;
    private Button nextView;
    private ProgressBar progressView;
    private TextView progressTextView;
    private TextView wordView;
    private EditText inputView;
    private TextView defView;

    /**
     *
     * @param wordType 数据类型-全四册、青少版
     * @param bookId 书籍id
     * @param voaId 课程id
     * @return
     */
    public static WordTrain_spellFragment getInstance(String wordType, int bookId,int voaId){
        WordTrain_spellFragment fragment = new WordTrain_spellFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.type,wordType);
        bundle.putInt(StrLibrary.bookId,bookId);
        bundle.putInt(StrLibrary.voaId,voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wordType = getArguments().getString(StrLibrary.type);
        bookId = getArguments().getInt(StrLibrary.bookId);
        voaId = getArguments().getInt(StrLibrary.voaId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_word_train_spell,null);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initClick();
        updateData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        closeProgressDialog();
        closeResultDialog();
        stopVibrate();
    }

    /**************************初始化**********************/
    private void initView(View rootView){
        toolbarView = rootView.findViewById(R.id.toolbar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        nextView = rootView.findViewById(R.id.next);
        progressView = rootView.findViewById(R.id.progress);
        progressTextView = rootView.findViewById(R.id.progressText);
        wordView = rootView.findViewById(R.id.word);
        inputView = rootView.findViewById(R.id.input);
        defView = rootView.findViewById(R.id.def);
    }

    private void initData(){
        toolbarView.setVisibility(View.GONE);
        pairList = WordTrainPresenter.getInstance().getRandomWordShowDataNew(wordType,bookId,voaId);
    }

    private void initClick(){
        nextView.setOnClickListener(v->{
            String showText = nextView.getText().toString();
            if (showText.equals("检查拼写")){
                checkData();
            }else if (showText.equals("下一个")){
                selectIndex++;
                updateData();
            }else if (showText.equals("查看结果")){
                showResultDialog();
            }
        });
    }

    /****************************刷新数据********************/
    private void updateData(){
        curBean = pairList.get(selectIndex);

        inputView.setEnabled(true);
        inputView.setText("");
        inputView.setTextColor(getResources().getColor(R.color.black));
        wordView.setText("");
        String pron = curBean.first.getPron();
        if (!TextUtils.isEmpty(pron)){
            pron = "["+pron+"]";
        }
        defView.setText(pron+"\t"+curBean.first.getDef());

        progressView.setMax(pairList.size());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            progressView.setProgress(selectIndex+1,true);
        }else {
            progressView.setProgress(selectIndex+1);
        }
        progressTextView.setText((selectIndex+1)+"/"+pairList.size());

        nextView.setText("检查拼写");
    }

    private void checkData(){
        inputView.setEnabled(false);
        wordView.setText(curBean.first.getWord());
        String spellWord = inputView.getText().toString();
        if (spellWord.equals(curBean.first.getWord())){
            inputView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            //保存正确结果
            rightCount++;
        }else {
            inputView.setTextColor(getResources().getColor(R.color.red));
            showVibrate();
        }

        //写入进度
        progressCount++;

        if (selectIndex>=pairList.size()-1){
            nextView.setText("查看结果");
        }else {
            nextView.setText("下一个");
        }
    }

    /****************************辅助功能***********************/
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
