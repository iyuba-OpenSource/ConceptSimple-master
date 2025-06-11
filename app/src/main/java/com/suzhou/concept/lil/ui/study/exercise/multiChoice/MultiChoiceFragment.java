package com.suzhou.concept.lil.ui.study.exercise.multiChoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.FragmentExerciseMultiChoiceBinding;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.exercise.ExerciseResultEntity;
import com.suzhou.concept.lil.data.newDB.exercise.MultipleChoiceEntity;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.ui.study.exercise.bean.ExerciseMarkBean;
import com.suzhou.concept.lil.ui.study.exercise.common.CommonExercisePresenter;
import com.suzhou.concept.lil.ui.study.exercise.common.CommonExerciseView;
import com.suzhou.concept.lil.util.DateUtil;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择题界面
 */
public class MultiChoiceFragment extends BaseViewBindingFragment<FragmentExerciseMultiChoiceBinding> implements CommonExerciseView {

    //参数
    private String voaId;
    private String lessonType;

    //当前的选择题数据
    private List<MultipleChoiceEntity> choiceList;
    //当前的答案选择数据
    private Map<Integer, ExerciseMarkBean> userAnswerMap = new HashMap<>();
    //当前的习题位置
    private int curExercisePosition = 0;
    //适配器
    private MultiChoiceAdapter choiceAdapter;
    //数据
    private CommonExercisePresenter presenter;

    //开始时间
    private long exerciseStartTime;
    //是否已经完成做题
    private boolean isFinishChoice = false;

    public static MultiChoiceFragment getInstance(String voaId, String exerciseType) {
        MultiChoiceFragment fragment = new MultiChoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.voaId, voaId);
        bundle.putString(StrLibrary.type, exerciseType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voaId = getArguments().getString(StrLibrary.voaId);
        lessonType = getArguments().getString(StrLibrary.type);

        exerciseStartTime = System.currentTimeMillis();

        presenter = new CommonExercisePresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initClick();
        //获取数据
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLoading();

        presenter.detachView();
    }

    /********************************初始化*********************************/
    private void initList() {
        choiceAdapter = new MultiChoiceAdapter(getActivity(), new ArrayList<>());
        binding.choiceLayout.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.choiceLayout.setAdapter(choiceAdapter);
        choiceAdapter.setOnSimpleClickListener(new OnSimpleClickListener<Integer>() {
            @Override
            public void onClick(Integer selectChoice) {
                //当前题目数据
                MultipleChoiceEntity choiceEntity = choiceList.get(curExercisePosition);
                //当前选中的答案
                String choiceAnswer = String.valueOf(selectChoice+1);
                String choiceStatus = choiceEntity.answer.equals(choiceAnswer)?"1":"0";

                ExerciseMarkBean submitBean = new ExerciseMarkBean(
                        GlobalMemory.INSTANCE.getUserInfo().getUid(),
                        voaId,
                        choiceEntity.indexId,
                        DateUtil.toDateStr(exerciseStartTime,DateUtil.YMDHMS),
                        DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMDHMS),
                        choiceEntity.answer,
                        choiceAnswer,
                        choiceStatus,
                        AppClient.appName
                );
                userAnswerMap.put(curExercisePosition,submitBean);
            }
        });
    }

    private void initClick() {
        binding.preExercise.setOnClickListener(v -> {
            if (curExercisePosition == 0) {
                ToastUtil.showLongToast(getActivity(), "当前已经是第一个");
                return;
            }

            curExercisePosition--;
            showData(curExercisePosition);
        });
        binding.nextExercise.setOnClickListener(v -> {
            String showText = binding.nextExercise.getText().toString();
            if (showText.equals("提交答案")) {
                //判断是否全都完成
                if (userAnswerMap.size() != choiceList.size()) {
                    ToastUtil.showToast(getActivity(), "请完成所有题目后提交");
                    return;
                }

                //提交数据
                submitExercise();
                return;
            }

            if (curExercisePosition == choiceList.size() - 1) {
                ToastUtil.showLongToast(getActivity(), "当前已经是最后一个");
                return;
            }

            curExercisePosition++;
            showData(curExercisePosition);
        });
        binding.startExercise.setOnClickListener(v->{
            if (!GlobalMemory.INSTANCE.isLogin()){
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }

            isFinishChoice = false;
            binding.startLayout.setVisibility(View.GONE);
        });
        binding.retryExercise.setOnClickListener(v->{
            //设置完成标识
            isFinishChoice = false;
            //清除之前的数据
            userAnswerMap.clear();
            //设置为第一个位置
            curExercisePosition = 0;
            //显示数据
            showData(curExercisePosition);
            //关闭重置按钮
            binding.retryExercise.setVisibility(View.INVISIBLE);
        });
    }

    private void showData(int showPosition) {
        //重置时间
        exerciseStartTime = System.currentTimeMillis();

        MultipleChoiceEntity entity = choiceList.get(showPosition);
        binding.question.setText("(单选)" + entity.indexId + "." + entity.question);
        //获取显示数据
        List<String> showList = new ArrayList<>();
        showList.add("A." + entity.choiceA);
        showList.add("B." + entity.choiceB);
        showList.add("C." + entity.choiceC);
        showList.add("D." + entity.choiceD);
        //获取选中数据
        ExerciseMarkBean submitBean = userAnswerMap.get(curExercisePosition);
        if (submitBean == null) {
            choiceAdapter.refreshData(showList, -1,-1);
        } else {
            int choiceIndex = Integer.parseInt(submitBean.getUserAnswer());
            //这里需要-1，因为适配器中显示的是位置
            if (choiceIndex>0){
                choiceIndex--;
            }

            //判断是否已经完成
            if (isFinishChoice){
                int rightIndex = Integer.parseInt(submitBean.getRightAnswer());
                if (rightIndex>0){
                    rightIndex--;
                }
                choiceAdapter.refreshData(showList, choiceIndex,rightIndex);
            }else {
                choiceAdapter.refreshData(showList, choiceIndex,-1);
            }
        }

        if (showPosition == 0) {
            binding.preExercise.setVisibility(View.INVISIBLE);
        } else {
            binding.preExercise.setVisibility(View.VISIBLE);
        }

        if (showPosition == choiceList.size() - 1) {
            if (!isFinishChoice){
                binding.nextExercise.setVisibility(View.VISIBLE);
                binding.nextExercise.setText("提交答案");
            }else {
                binding.nextExercise.setVisibility(View.INVISIBLE);
            }
        } else {
            binding.nextExercise.setVisibility(View.VISIBLE);
            binding.nextExercise.setText("下一个");
        }
    }

    /*********************************回调********************************/
    @Override
    public void showSubmitResult(boolean isSuccess) {
        stopLoading();

        if (!isSuccess){
            ToastUtil.showToast(getActivity(),"提交习题记录失败，请重试");
            return;
        }

        //显示结果
        showExerciseResult();
    }

    /**********************************操作*********************************/
    //获取数据
    public void getData() {
        choiceList = RoomDBManager.getInstance().getConceptMultiChoiceData(voaId, lessonType);
        if (choiceList != null && choiceList.size() > 0) {
            refreshUI(true);

            //初始化位置
            curExercisePosition = 0;
            //清除选中数据
            userAnswerMap.clear();
            //设置未完成数据
            isFinishChoice = false;
            //关闭重置按钮
            binding.retryExercise.setVisibility(View.INVISIBLE);
            //显示数据
            showData(curExercisePosition);
        } else {
            refreshUI(false);
        }
    }

    //刷新ui显示
    private void refreshUI(boolean hasData) {
        if (hasData) {
            binding.emptyLayout.setVisibility(View.GONE);
        } else {
            binding.emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    //提交做题记录
    private void submitExercise(){
        startLoading("正在提交做题记录");

        presenter.submitExerciseData(userAnswerMap);
    }

    //显示做题结果
    private void showExerciseResult() {
        //保存结果数据
        int rightCount = 0;
        int totalCount = userAnswerMap.size();
        for (int key:userAnswerMap.keySet()){
            String resultStr = userAnswerMap.get(key).getAnswerResut();
            if (resultStr.equals("1")){
                rightCount++;
            }
        }
        ExerciseResultEntity entity = new ExerciseResultEntity(
                voaId,
                GlobalMemory.INSTANCE.getUserInfo().getUid(),
                lessonType,
                TypeLibrary.ExerciseType.Exercise_multiChoice,
                rightCount,
                totalCount);
        RoomDBManager.getInstance().saveConceptExerciseResultData(entity);

        //设置完成标识
        isFinishChoice = true;
        binding.retryExercise.setVisibility(View.VISIBLE);

        //显示结果信息
        curExercisePosition = 0;
        showData(curExercisePosition);
    }

    //加载弹窗
    private LoadingMsgDialog loadingMsgDialog;

    private void startLoading(String showMsg){
        if (loadingMsgDialog==null){
            loadingMsgDialog = new LoadingMsgDialog(getActivity());
            loadingMsgDialog.create();
        }
        loadingMsgDialog.setMsg(showMsg);
        loadingMsgDialog.show();
    }

    private void stopLoading(){
        if (loadingMsgDialog!=null&&loadingMsgDialog.isShowing()){
            loadingMsgDialog.dismiss();
        }
    }
}
