package com.suzhou.concept.lil.ui.study.word.train;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.suzhou.concept.R;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.ui.study.word.train.show.WordTrain_enCnFragment;
import com.suzhou.concept.lil.ui.study.word.train.show.WordTrain_listenFragment;
import com.suzhou.concept.lil.ui.study.word.train.show.WordTrain_spellFragment;

/**
 * @title: 训练界面
 * @date: 2023/9/28 14:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordTrainActivity extends AppCompatActivity {

    private static final String SHOW_TYPE = "showType";
    public static final String Train_enToCn = "enToCn";//英汉训练
    public static final String Train_cnToEn = "cnToEn";//汉英训练
    public static final String Word_spell = "wordSpell";//单词拼写
    public static final String Train_listen = "listenTrain";//听力训练

    private View toolbar;
    private TabLayout tabLayout;
    private FrameLayout container;

    private Fragment showFragment = null;

    public static void start(Context context,String showType){
        Intent intent = new Intent();
        intent.setClass(context, WordTrainActivity.class);
        intent.putExtra(SHOW_TYPE,showType);
        context.startActivity(intent);
    }

    public static void start(Context context,String showType,String wordType,int bookId,int voaId){
        Intent intent = new Intent();
        intent.setClass(context, WordTrainActivity.class);
        intent.putExtra(SHOW_TYPE,showType);
        intent.putExtra(StrLibrary.type,wordType);
        intent.putExtra(StrLibrary.bookId,bookId);
        intent.putExtra(StrLibrary.voaId,voaId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_container_tab_title);

        initView();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /****************************初始化***************************/
    private void initView(){
        TextView tvTitle = findViewById(R.id.standard_title);
        String showType = getIntent().getStringExtra(SHOW_TYPE);
        tvTitle.setText(initToolbar(showType));
        ImageView ivBack = findViewById(R.id.standard_left);
        ivBack.setBackgroundResource(0);
        ivBack.setImageResource(R.drawable.left);
        ivBack.setOnClickListener(v->{
            finish();
        });

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);
        container = findViewById(R.id.container);
    }

    private String initToolbar(String showType){
        String showTitle = "英汉训练";
        switch (showType){
            case Train_enToCn:
                showTitle = "英汉训练";
                break;
            case Train_cnToEn:
                showTitle = "汉英训练";
                break;
            case Word_spell:
                showTitle = "单词拼写";
                break;
            case Train_listen:
                showTitle = "听力训练";
                break;
        }
        return showTitle;
    }


    private void initFragment(){
        String wordType = getIntent().getStringExtra(StrLibrary.type);
        int bookId = getIntent().getIntExtra(StrLibrary.bookId,0);
        int voaId = getIntent().getIntExtra(StrLibrary.voaId,0);

        //根据类型显示
        String showType = getIntent().getStringExtra(SHOW_TYPE);
        switch (showType){
            case Train_enToCn:
                //英汉训练
                showFragment = WordTrain_enCnFragment.getInstance(TypeLibrary.WordTrainType.Train_enToCn,wordType,bookId,voaId);
                break;
            case Train_cnToEn:
                //汉英训练
                showFragment = WordTrain_enCnFragment.getInstance(TypeLibrary.WordTrainType.Train_cnToEn,wordType,bookId,voaId);
                break;
            case Word_spell:
                //单词拼写
                showFragment = WordTrain_spellFragment.getInstance(wordType,bookId,voaId);
                break;
            case Train_listen:
                //听力训练
                showFragment = WordTrain_listenFragment.getInstance(wordType,bookId,voaId);
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,showFragment);
        transaction.show(showFragment);
        transaction.commitNowAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (showFragment instanceof WordTrain_enCnFragment){
            WordTrain_enCnFragment enCnFragment = (WordTrain_enCnFragment) showFragment;
            if (!enCnFragment.showExistDialog()){
                super.onBackPressed();
            }
        } else if (showFragment instanceof WordTrain_listenFragment){
            WordTrain_listenFragment listenFragment = (WordTrain_listenFragment) showFragment;
            if (!listenFragment.showExistDialog()){
                super.onBackPressed();
            }
        } else if (showFragment instanceof WordTrain_spellFragment){
            WordTrain_spellFragment spellFragment = (WordTrain_spellFragment) showFragment;
            if (!spellFragment.showExistDialog()){
                super.onBackPressed();
            }
        }
    }
}
