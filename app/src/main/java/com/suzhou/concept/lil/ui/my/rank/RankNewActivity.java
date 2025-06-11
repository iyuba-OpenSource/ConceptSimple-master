package com.suzhou.concept.lil.ui.my.rank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.suzhou.concept.R;
import com.suzhou.concept.databinding.LayoutVp2TabTitleBinding;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingActivity;
import com.suzhou.concept.lil.ui.my.rank.detail.RankDetailFragment;
import com.suzhou.concept.lil.ui.my.rank.detail.RankDetailShowBean;

import java.util.ArrayList;
import java.util.List;

public class RankNewActivity extends BaseViewBindingActivity<LayoutVp2TabTitleBinding> {

    //界面数据
    private List<Fragment> fragmentList;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, RankNewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToolbar();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*******************************初始化***************************/
    private void initToolbar() {
        binding.toolbar.standardLeft.setImageResource(R.drawable.left);
        binding.toolbar.standardLeft.setOnClickListener(v->{
            finish();
        });
        binding.toolbar.standardTitle.setText("排行榜");
        binding.toolbar.standardRight.setImageResource(R.drawable.rank_share);
        binding.toolbar.standardRight.setOnClickListener(v->{
            showShare();
        });
    }

    private void initView(){
        //列表数据
        List<Pair<String,String>> titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();

        //听力
        titleList.add(new Pair<>(RankDetailShowBean.ShowType.listen,"听力"));
        fragmentList.add(RankDetailFragment.getInstance(RankDetailShowBean.ShowType.listen));

        //口语
        titleList.add(new Pair<>(RankDetailShowBean.ShowType.speech,"口语"));
        fragmentList.add(RankDetailFragment.getInstance(RankDetailShowBean.ShowType.speech));

        //阅读
        titleList.add(new Pair<>(RankDetailShowBean.ShowType.read,"阅读"));
        fragmentList.add(RankDetailFragment.getInstance(RankDetailShowBean.ShowType.read));

        //练习
        titleList.add(new Pair<>(RankDetailShowBean.ShowType.exercise,"测试"));
        fragmentList.add(RankDetailFragment.getInstance(RankDetailShowBean.ShowType.exercise));

        //设置样式
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //显示
        RankNewAdapter newAdapter = new RankNewAdapter(this,fragmentList);
        binding.container.setAdapter(newAdapter);

        //绑定数据
        new TabLayoutMediator(binding.tabLayout, binding.container, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                tab.setText(titleList.get(i).second);
            }
        }).attach();
    }

    /*******************************分享操作*************************/
    private void showShare(){
        //根据当前位置，显示需要哪个操作
        int curIndex = binding.tabLayout.getSelectedTabPosition();
        RankDetailFragment detailFragment = (RankDetailFragment) fragmentList.get(curIndex);
        detailFragment.showShare();
    }
}