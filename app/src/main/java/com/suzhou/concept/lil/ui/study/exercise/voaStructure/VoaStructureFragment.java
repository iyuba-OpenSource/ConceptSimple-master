package com.suzhou.concept.lil.ui.study.exercise.voaStructure;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.FragmentExerciseVoaStructureBinding;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.exercise.ExerciseResultEntity;
import com.suzhou.concept.lil.data.newDB.exercise.VoaStructureExerciseEntity;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.ui.study.exercise.bean.ExerciseMarkBean;
import com.suzhou.concept.lil.ui.study.exercise.common.CommonExercisePresenter;
import com.suzhou.concept.lil.ui.study.exercise.common.CommonExerciseView;
import com.suzhou.concept.lil.util.DateUtil;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 填空题界面
 */
public class VoaStructureFragment extends BaseViewBindingFragment<FragmentExerciseVoaStructureBinding> implements CommonExerciseView {

    //参数
    private String voaId;
    private String lessonType;

    //习题数据
    private List<VoaStructureExerciseEntity> structureList;
    //答案数据
    private Map<Integer, ExerciseMarkBean> userAnswerMap = new HashMap<>();

    //当前的题目位置
    private int curExercisePosition = 0;

    //数据
    private CommonExercisePresenter presenter;

    //开始时间
    private long exerciseStartTime;
    //是否已经完成做题
    private boolean isFinishChoice = false;

    public static VoaStructureFragment getInstance(String voaId, String exerciseType) {
        VoaStructureFragment fragment = new VoaStructureFragment();
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

        initClick();
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopLoading();

        presenter.detachView();
    }

    /**********************************初始化********************************/
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
                if (userAnswerMap.size() != structureList.size()) {
                    ToastUtil.showToast(getActivity(), "请完成所有题目后提交");
                    return;
                }

                //提交数据
                submitExercise();
                return;
            }

            if (curExercisePosition == structureList.size() - 1) {
                ToastUtil.showLongToast(getActivity(), "当前已经是最后一个");
                return;
            }

            curExercisePosition++;
            showData(curExercisePosition);
        });
        binding.startExercise.setOnClickListener(v -> {
            if (!GlobalMemory.INSTANCE.isLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }

            isFinishChoice = false;
            binding.startLayout.setVisibility(View.GONE);
        });
        binding.retryExercise.setOnClickListener(v -> {
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

        binding.userAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //输入的答案
                String inputText = binding.userAnswer.getText().toString().trim();
                if (TextUtils.isEmpty(inputText)) {
                    return;
                }
                //当前的数据
                VoaStructureExerciseEntity entity = structureList.get(curExercisePosition);
                //正确的答案
                String rightText = entity.answer.trim();
                //正确和错误信息
                String rightStatus = checkAnswerIsRight(inputText,rightText)?"1":"0";

                ExerciseMarkBean markBean = new ExerciseMarkBean(
                        GlobalMemory.INSTANCE.getUserInfo().getUid(),
                        voaId,
                        entity.indexId,
                        DateUtil.toDateStr(exerciseStartTime, DateUtil.YMDHMS),
                        DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMS),
                        entity.answer,
                        inputText,
                        rightStatus,
                        AppClient.appName
                );
                userAnswerMap.put(curExercisePosition, markBean);
            }
        });
    }

    private void showData(int showPosition) {
        //重置时间
        exerciseStartTime = System.currentTimeMillis();

        VoaStructureExerciseEntity entity = structureList.get(curExercisePosition);
        String descEn = entity.desc_EN;
        descEn = descEn.replaceAll("\\+\\+\\+","");
        binding.desc.setText(descEn + "(" + entity.desc_CH + ")");
        //如果问题不存在，则不显示
        if (TextUtils.isEmpty(entity.note)){
            binding.question.setVisibility(View.GONE);
        }else {
            binding.question.setVisibility(View.VISIBLE);
            binding.question.setText(entity.indexId + "." + entity.note);
        }
        //判断填空和答案显示
        ExerciseMarkBean markBean = userAnswerMap.get(curExercisePosition);
        if (markBean == null) {
            binding.rightAnswerTips.setVisibility(View.INVISIBLE);
            binding.rightAnswer.setVisibility(View.INVISIBLE);
            binding.userAnswer.setEnabled(true);
            binding.userAnswer.setTextColor(getResources().getColor(R.color.black));
            binding.userAnswer.setBackgroundResource(R.drawable.shape_corner_gray_border_10dp);
            binding.userAnswer.setText("");
        } else {
            //当前填写的内容
            String userStr = markBean.getUserAnswer();
            binding.userAnswer.setText(userStr);

            if (isFinishChoice) {
                binding.userAnswer.setEnabled(false);
                String rightStr = markBean.getRightAnswer().trim();
                String newUserStr = markBean.getUserAnswer().trim();
                //这里正确答案可能有多个，准备多个显示
                StringBuffer rightBuffer = new StringBuffer();
                boolean isRight = false;

                if (rightStr.contains("###")){
                    //显示为任选其一
                    binding.rightAnswerTips.setText("Right answer:");

                    String[] rightArray = rightStr.split("###");
                    for (int i = 0; i < rightArray.length; i++) {
                        String tempStr = rightArray[i].trim();
                        rightBuffer.append(tempStr);
                        if (i != rightArray.length-1){
                            rightBuffer.append("\n");
                        }
                    }
                }else {
                    rightBuffer.append(rightStr);
                }

                //是否为正确答案
                isRight = checkAnswerIsRight(newUserStr,rightStr);

                if (isRight) {
                    binding.userAnswer.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    binding.userAnswer.setBackgroundResource(R.drawable.shape_corner_right_border_10dp);
                } else {
                    binding.userAnswer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    binding.userAnswer.setBackgroundResource(R.drawable.shape_corner_error_border_10dp);
                }

                binding.rightAnswerTips.setVisibility(View.VISIBLE);
                binding.rightAnswer.setVisibility(View.VISIBLE);
                binding.rightAnswer.setText(rightBuffer.toString());
            } else {
                binding.userAnswer.setEnabled(true);
                binding.userAnswer.setTextColor(getResources().getColor(R.color.black));
                binding.userAnswer.setBackgroundResource(R.drawable.shape_corner_gray_border_10dp);
                binding.rightAnswerTips.setVisibility(View.INVISIBLE);
                binding.rightAnswer.setVisibility(View.INVISIBLE);
            }
        }

        if (showPosition == 0) {
            binding.preExercise.setVisibility(View.INVISIBLE);
        } else {
            binding.preExercise.setVisibility(View.VISIBLE);
        }

        if (showPosition == structureList.size() - 1) {
            if (!isFinishChoice) {
                binding.nextExercise.setVisibility(View.VISIBLE);
                binding.nextExercise.setText("提交答案");
            } else {
                binding.nextExercise.setVisibility(View.INVISIBLE);
            }
        } else {
            binding.nextExercise.setVisibility(View.VISIBLE);
            binding.nextExercise.setText("下一个");
        }
    }

    /*************************************回调*****************************/
    @Override
    public void showSubmitResult(boolean isSuccess) {
        stopLoading();

        if (!isSuccess) {
            ToastUtil.showToast(getActivity(), "提交习题记录失败，请重试");
            return;
        }

        //显示结果
        showExerciseResult();
    }

    /************************************操作********************************/
    //获取数据
    public void getData() {
        structureList = RoomDBManager.getInstance().getConceptVoaStructureData(voaId, lessonType);
        structureList = filterDataList(structureList);
        if (structureList != null && structureList.size() > 0) {
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
    private void submitExercise() {
        startLoading("正在提交做题记录");

        presenter.submitExerciseData(userAnswerMap);
    }

    //显示做题结果
    private void showExerciseResult() {
        //保存在数据库
        int rightCount = 0;
        int totalCount = userAnswerMap.size();
        for (int key : userAnswerMap.keySet()) {
            String resultStr = userAnswerMap.get(key).getAnswerResut();
            if (resultStr.equals("1")) {
                rightCount++;
            }
        }
        ExerciseResultEntity entity = new ExerciseResultEntity(
                voaId,
                GlobalMemory.INSTANCE.getUserInfo().getUid(),
                lessonType,
                TypeLibrary.ExerciseType.Exercise_voaStructure,
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

    private void startLoading(String showMsg) {
        if (loadingMsgDialog == null) {
            loadingMsgDialog = new LoadingMsgDialog(getActivity());
            loadingMsgDialog.create();
        }
        loadingMsgDialog.setMsg(showMsg);
        loadingMsgDialog.show();
    }

    private void stopLoading() {
        if (loadingMsgDialog != null && loadingMsgDialog.isShowing()) {
            loadingMsgDialog.dismiss();
        }
    }

    /********************************************数据格式转换***************************/
    //数据筛选转换操作
    private List<VoaStructureExerciseEntity> filterDataList(List<VoaStructureExerciseEntity> list){
        if (list==null||list.size()==0){
            return list;
        }

        for (int i = 0; i < list.size(); i++) {
            VoaStructureExerciseEntity entity = list.get(i);
            String answer = entity.answer;

            //格式化数据
            answer = answer.replaceAll("’","'");
            answer = answer.replaceAll(" '","'");
            answer = answer.replaceAll("“","\"");

            entity.answer = answer;
            list.set(i,entity);
        }
        return list;
    }

    //检测用户答案和正确答案的核验
    private boolean checkAnswerIsRight(String userAnswer,String rightAnswer){
        if (TextUtils.isEmpty(userAnswer)&&TextUtils.isEmpty(rightAnswer)){
            return false;
        }

        if (!rightAnswer.contains("###")){
            if (rightAnswer.toLowerCase().equals(userAnswer.toLowerCase())){
                return true;
            }
            return false;
        }

        //先拆分正确答案，正确答案以###分割
        String[] rightArray = rightAnswer.split("###");
        //再拆分用户答案
        String[] userArray = userAnswer.split(",");

        if (rightArray.length!=userArray.length){
            return false;
        }

        //同位置进行对比
        for (int i = 0; i < rightArray.length; i++) {
            String rightStr = rightArray[i].toLowerCase();
            String userStr = userArray[i].toLowerCase();
            if (!rightStr.equals(userStr)){
                return false;
            }
        }

        return true;
    }
}
