package com.suzhou.concept.lil.ui.study.exercise;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.databinding.FragmentExerciseNewBinding;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.ui.study.exercise.multiChoice.MultiChoiceFragment;
import com.suzhou.concept.lil.ui.study.exercise.voaStructure.VoaStructureFragment;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的练习题界面
 *
 * 如果数据库中没有数据，则使用接口加载数据；有数据的话则显示习题界面
 */
public class ExerciseNewFragment extends BaseViewBindingFragment<FragmentExerciseNewBinding> implements ExerciseNewView{

    private ExerciseNewBottomAdapter bottomAdapter;
    private ExerciseNewPresenter presenter;

    //习题界面
    private MultiChoiceFragment choiceFragment;
    private VoaStructureFragment structureFragment;

    //界面数据
    private List<Pair<String,String>> pairList;

    public static ExerciseNewFragment getInstance(){
        ExerciseNewFragment fragment = new ExerciseNewFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new ExerciseNewPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        //先获取数据，再展示界面
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    /****************************初始化*************************/
    private void initView(){
        bottomAdapter = new ExerciseNewBottomAdapter(getActivity(),new ArrayList<>());
        binding.bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setOnSimpleClickListener(new OnSimpleClickListener<Pair<String, String>>() {
            @Override
            public void onClick(Pair<String, String> pair) {
                switchFragment(pair.second);
            }
        });
    }

    /******************************加载数据**********************/
    //获取数据-先从本地，再从网络
    private void getData(){
        //当前暂时先显示了选择题和填空题
        updateUi(true,"正在获取习题数据");

        presenter.getExerciseData(getVoaId());
    }

    //刷新数据-从网络端
    public void refreshData(){
        //当前暂时先显示了选择题和填空题
        updateUi(true,"正在获取习题数据");

        presenter.getExerciseDataFromServer(getVoaId(),getExerciseType());
    }

    /******************************回调数据***********************/
    @Override
    public void showExercise(boolean isSuccess) {
        if (isSuccess){
            updateUi(false,null);

            if (choiceFragment!=null||structureFragment!=null){
                refreshFragment();
            }else {
                showFragment();
            }
        }else {
            updateUi(false,"加载习题数据失败，请重试～");
        }
    }

    /*****************************其他方法************************/
    //显示界面
    private void showFragment(){
        pairList = new ArrayList<>();
        //选择题
        pairList.add(new Pair<>("选择题",TypeLibrary.ExerciseType.Exercise_multiChoice));
        //关键句型
        pairList.add(new Pair<>("关键句型",TypeLibrary.ExerciseType.Exercise_voaStructure));

        //显示第一个
        switchFragment(TypeLibrary.ExerciseType.Exercise_multiChoice);

        //如果只有一个，则隐藏底部
        binding.bottomView.setLayoutManager(new GridLayoutManager(getActivity(),pairList.size()));
        bottomAdapter.refreshData(pairList);
        if (pairList.size()>1){
            binding.bottomView.setVisibility(View.VISIBLE);
        }else {
            binding.bottomView.setVisibility(View.GONE);
        }
    }

    //刷新界面中的内容
    private void refreshFragment(){
        //两个界面都刷新数据，默认显示第一题
        if (choiceFragment!=null){
            choiceFragment.getData();
        }

        if (structureFragment!=null){
            structureFragment.getData();
        }
    }

    //切换界面显示
    private void switchFragment(String showTag){
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment1 = manager.findFragmentByTag(TypeLibrary.ExerciseType.Exercise_multiChoice);
        Fragment fragment2 = manager.findFragmentByTag(TypeLibrary.ExerciseType.Exercise_voaStructure);

        if (fragment1!=null){
            choiceFragment = (MultiChoiceFragment) fragment1;
        }
        if (fragment2!=null){
            structureFragment = (VoaStructureFragment) fragment2;
        }

        switch (showTag){
            case TypeLibrary.ExerciseType.Exercise_multiChoice:
                if (choiceFragment==null){
                    choiceFragment = MultiChoiceFragment.getInstance(getVoaId(),getExerciseType());
                    hideFragment(transaction,structureFragment);
                    transaction.add(R.id.container,choiceFragment,TypeLibrary.ExerciseType.Exercise_multiChoice);
                }else {
                    hideFragment(transaction,structureFragment);
                    transaction.show(choiceFragment);
                }
                break;
            case TypeLibrary.ExerciseType.Exercise_voaStructure:
                if (structureFragment==null){
                    structureFragment = VoaStructureFragment.getInstance(getVoaId(),getExerciseType());
                    hideFragment(transaction,choiceFragment);
                    transaction.add(R.id.container,structureFragment,TypeLibrary.ExerciseType.Exercise_voaStructure);
                }else {
                    hideFragment(transaction,choiceFragment);
                    transaction.show(structureFragment);
                }
                break;
        }
        transaction.commitNowAllowingStateLoss();
    }

    //隐藏fragment
    private void hideFragment(FragmentTransaction transaction, Fragment... fragments) {
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
    }

    //更新ui显示
    private void updateUi(boolean isLoading,String showMsg){
        if (isLoading){
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.proLoading.setVisibility(View.VISIBLE);
            binding.proMsg.setText(showMsg);
        }else {
            if (!TextUtils.isEmpty(showMsg)){
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.proLoading.setVisibility(View.INVISIBLE);
                binding.proMsg.setText(showMsg);
            }else {
                binding.loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    //获取当前的课程id
    private String getVoaId(){
        String voaId = AppClient.Companion.getConceptItem().getVoa_id();
        return voaId;
    }

    //获取当前的课程类型
    private String getExerciseType(){
        String languageType = TypeLibrary.BookType.conceptFourUS;
        if (GlobalMemory.INSTANCE.getCurrentYoung()){
            languageType = TypeLibrary.BookType.conceptJunior;
        }else {
            String curLanguage = GlobalMemory.INSTANCE.getCurrentLanguage().getLanguage();
            if (curLanguage.equals("US")){
                languageType = TypeLibrary.BookType.conceptFourUS;
            }else if (curLanguage.equals("UK")){
                languageType = TypeLibrary.BookType.conceptFourUK;
            }
        }
        return languageType;
    }
}
