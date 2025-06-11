package com.suzhou.concept.lil.ui.wordPass.list.passShow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.suzhou.concept.R;
import com.suzhou.concept.bean.LinkDataBean;
import com.suzhou.concept.bean.LinkLineBean;
import com.suzhou.concept.databinding.ActivityWordPassBinding;
import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_pass;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_result;
import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_submit;
import com.suzhou.concept.lil.event.LocalEvalDataRefreshEvent;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingActivity;
import com.suzhou.concept.lil.ui.wordPass.data.WordConfigData;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.DateUtil;
import com.suzhou.concept.lil.util.LibRxUtil;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog;
import com.suzhou.concept.utils.GlobalMemory;
import com.suzhou.concept.utils.view.LinkLineView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 单词的闯关界面
 */
public class WordPassShowActivity extends BaseViewBindingActivity<ActivityWordPassBinding> implements WordPassShowView{

    //参数
    private String type;
    private int bookId;
    private int id;

    //数据
    private WordPassShowPresenter presenter;

    //当前单元的单词数据
    private List<WordShowBean> allWordList;
    //暂存的结果数据
    private List<Report_wordBreak_submit.TestListBean> wordResultList = new ArrayList<>();

    //当前的分组
    private int curWordGroupIndex = 1;

    //固定的每一组单词数据
    private static final int WordGroupCount = 6;
    //开始时间
    private long startTime = 0;
    //提交数据中显示的id(自增)
    private int showId = 0;

    public static void start(Context context,String type,int bookId,int id){
        Intent intent = new Intent();
        intent.setClass(context, WordPassShowActivity.class);
        intent.putExtra(StrLibrary.type,type);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.id,id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getStringExtra(StrLibrary.type);
        bookId = getIntent().getIntExtra(StrLibrary.bookId,0);
        id = getIntent().getIntExtra(StrLibrary.id,0);

        presenter = new WordPassShowPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LibRxUtil.unDisposable(submitDataDis);
        presenter.detachView();
    }

    /******************************初始化*****************************/
    private void initToolBar(){
        binding.toolbar.standardTitle.setText("单词闯关");
        binding.toolbar.standardLeft.setImageResource(R.drawable.left);
        binding.toolbar.standardLeft.setOnClickListener(v->{
            finish();
        });
    }

    private void initData(){
        presenter.getWordDataByUnit(type,bookId,id);
    }

    private void initView(){
        binding.linkView.setOnChoiceResultListener(new LinkLineView.OnChoiceResultListener() {
            @Override
            public void onResultSelected(boolean correct, List<LinkLineBean> result) {
                //显示下一个
                binding.nextView.setVisibility(View.VISIBLE);
                //将数据暂存
                saveResultData(result);
            }
        });
        binding.nextView.setOnClickListener(v->{
            String showText = binding.nextView.getText().toString();

            if (showText.equals("下一组")){
                curWordGroupIndex++;
                fixWordGroup();
            }else if (showText.equals("完成闯关")){
                //判断当前正确率并显示
                showProgressDialog();
            }
        });
    }

    /********************************数据操作**************************/
    //根据分组顺序显示数据
    private void fixWordGroup(){
        //开始时间
        startTime = System.currentTimeMillis();
        //隐藏下一个
        binding.nextView.setVisibility(View.INVISIBLE);

        //获取当前的分组数据(要是为最后一组，并且后面的<3，则合并到当前这个来)

        //获取总的分组
        int wordGroup = 0;
        int lastWordCount = allWordList.size()%WordGroupCount;
        if (lastWordCount<3){
            wordGroup = allWordList.size()/WordGroupCount;
        }else {
            wordGroup = allWordList.size()/WordGroupCount+1;
        }
        // TODO: 2024/8/9 针对李涛私聊中的，单独用户使用的墨水屏pad进行修改一个版本
//        wordGroup = allWordList.size()/WordGroupCount+1;

        //获取当前分组数据
        List<WordShowBean> groupList = new ArrayList<>();
        groupRecycle:for (int i = 0; i < allWordList.size(); i++) {
            //当前位置
            int wordStartIndex = (curWordGroupIndex-1)*WordGroupCount+i;
            //结束位置
            int wordEndIndex = 0;
            if (curWordGroupIndex<wordGroup){
                wordEndIndex = curWordGroupIndex*WordGroupCount;
            }else {
                wordEndIndex = allWordList.size();
            }

            if (wordStartIndex<wordEndIndex){
                groupList.add(allWordList.get(wordStartIndex));
            }else {
                break groupRecycle;
            }
        }

        //将数据处理
        List<LinkDataBean> linkDataBeanList = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            WordShowBean showBean = groupList.get(i);

            //单词数据
            linkDataBeanList.add(new LinkDataBean(showBean.getWord(), i, "0", 0, i, showBean.getWord()));

            //释义数据
            linkDataBeanList.add(new LinkDataBean(showBean.getDef(),i,"0",1,groupList.size()-1-i,showBean.getWord()));
        }

        //设置数据
        binding.linkView.setData(linkDataBeanList);

        //显示下一个
        if (curWordGroupIndex<wordGroup){
            binding.nextView.setText("下一组");
        }else {
            binding.nextView.setText("完成闯关");
        }
    }

    /*********************************回调***************************/
    @Override
    public void showUnitWordData(List<WordShowBean> list) {
        //单词数据
        if (list==null||list.size()==0){
            ToastUtil.showToast(this,"暂未查找到当前单元的单词");
            return;
        }

        //将数据转为全局数据
        allWordList = list;
        //将数据随机混合
        Collections.shuffle(allWordList);

        //刷新第一个数据
        fixWordGroup();
    }

    /********************************其他操作**************************/
    //将结果数据合并成需要上传的数据
    private void saveResultData(List<LinkLineBean> lineList){
        String category = "单词闯关";
        String testMode = "W";
        String startDate = DateUtil.toDateStr(startTime,DateUtil.YMD);
        String endDate = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD);

        String unitId = "0";
        if (type.equals(TypeLibrary.BookType.conceptJunior)){
            unitId = String.valueOf(id);
        }

        //将当前分组数据分拆
        List<LinkDataBean> wordList = binding.linkView.getLeftData();
        List<LinkDataBean> defList = binding.linkView.getRightData();

        //将分组数据和连线数据合并处理
        for (int i = 0; i < lineList.size(); i++) {
            LinkLineBean lineBean = lineList.get(i);

            //单词数据
            LinkDataBean wordData = wordList.get(lineBean.getLeftIndex());
            //释义数据
            LinkDataBean defData = defList.get(lineBean.getRightIndex());

            //合并到提交数据中

            //正确结果
            String rightAnswer = wordData.getWord();
            //用户结果
            String userAnswer = defData.getWord();
            //结果标志
            int answerResult = rightAnswer.equals(userAnswer)?1:0;

            wordResultList.add(new Report_wordBreak_submit.TestListBean(
                    answerResult,
                    startDate,
                    category,
                    unitId,
                    rightAnswer,
                    showId,
                    testMode,
                    endDate,
                    userAnswer
            ));

            //自增数据
            showId++;
        }
    }

    //显示当前进度和之前进度的比对
    private void showProgressDialog(){
        //全部数据
        int allCount = allWordList.size();
        //计算正确数据和全部数据
        int rightCount = 0;
        for (int i = 0; i < wordResultList.size(); i++) {
            Report_wordBreak_submit.TestListBean testListBean = wordResultList.get(i);
            if (testListBean.getAnswerResut() == 1){
                rightCount++;
            }
        }

        //之前的正确率
        WordEntity_pass prePassData = RoomDBManager.getInstance().getWordPassData(type,WordConfigData.getInstance().getShowBookId(),id,GlobalMemory.INSTANCE.getUserInfo().getUid());

        //显示信息
        if (prePassData!=null){
            double rightRate = BigDecimalUtil.trans2Double(rightCount*1.0f/allCount);
            String showMsg = "当前单词闯关结果:\n正确数："+rightCount+"\n总数量："+allCount+"\n正确率："+rightRate*100+"%\n是否提交闯关数据？";

            new AlertDialog.Builder(this)
                    .setTitle("闯关结果")
                    .setMessage(showMsg)
                    .setPositiveButton("提交闯关进度", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            submitLinkData();
                        }
                    }).setNegativeButton("取消并退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
        }else {
            submitLinkData();
        }
    }

    //提交连线数据
    private Disposable submitDataDis;
    private void submitLinkData(){
        startLoading("正在提交单词闯关数据～");

        int bookId = WordConfigData.getInstance().getShowBookId();

        RetrofitUtil.getInstance().submitWordBreakReport(bookId,wordResultList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Report_wordBreak_result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        submitDataDis = d;
                    }

                    @Override
                    public void onNext(Report_wordBreak_result bean) {
                        stopLoading();
                        if (bean!=null&&bean.getResult().equals("1")){
                            //将数据保存在数据库中
                            saveLocalData();
                            //刷新数据库的数据
                            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.WORD_PASS_REFRESH,null));
                            //同步刷新首页的单词进度数据
                            EventBus.getDefault().post(new LocalEvalDataRefreshEvent());
                            //回退界面
                            finish();
                        }else {
                            ToastUtil.showToast(WordPassShowActivity.this,"提交闯关数据失败～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopLoading();
                        ToastUtil.showToast(WordPassShowActivity.this,"提交闯关数据异常～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //将数据保存在数据库中
    private void saveLocalData(){
        //全部数据
        int allCount = allWordList.size();
        //计算正确数据和全部数据
        int rightCount = 0;
        for (int i = 0; i < wordResultList.size(); i++) {
            Report_wordBreak_submit.TestListBean testListBean = wordResultList.get(i);
            if (testListBean.getAnswerResut() == 1){
                rightCount++;
            }
        }

        //获取之前的通过状态
        int passState = 0;
        float rightRate = rightCount*1.0f/allCount;
        if (rightRate>=0.8f){
            passState = 1;
        }

        if (passState == 0){
            WordEntity_pass passData = RoomDBManager.getInstance().getWordPassData(type,WordConfigData.getInstance().getShowBookId(),id,GlobalMemory.INSTANCE.getUserInfo().getUid());
            if (passData!=null){
                passState = passData.isPass;
            }
        }

        //保存到数据库
        WordEntity_pass pass = new WordEntity_pass(
                type,
                String.valueOf(WordConfigData.getInstance().getShowBookId()),
                String.valueOf(id),
                GlobalMemory.INSTANCE.getUserInfo().getUid(),
                rightCount,
                allCount,
                passState
        );
        RoomDBManager.getInstance().saveWordPassData(pass);
    }

    //加载弹窗
    private LoadingMsgDialog loadingDialog;

    private void startLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingMsgDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null&&loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
