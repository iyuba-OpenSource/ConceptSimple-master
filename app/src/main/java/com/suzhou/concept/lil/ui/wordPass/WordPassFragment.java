package com.suzhou.concept.lil.ui.wordPass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.module.user.LogoutEvent;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.FragmentWordPassBinding;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.word.pass.bean.WordPassBean;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.ui.wordPass.data.WordConfigData;
import com.suzhou.concept.lil.ui.wordPass.list.WordListActivity;
import com.suzhou.concept.lil.util.ScreenUtil;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词闯关主界面
 */
public class WordPassFragment extends BaseViewBindingFragment<FragmentWordPassBinding> implements WordPassView{

    //数据
    private WordPassPresenter presenter;
    //适配器
    private WordPassAdapter adapter;
    //弹窗
    private AlertDialog chooseDialog;

    public static WordPassFragment getInstance(){
        WordPassFragment fragment = new WordPassFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new WordPassPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initDialog();
        initList();
        initClick();

        //获取数据
        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        presenter.detachView();
    }

    /****************************************初始化********************************/
    //初始化弹窗
    private void initDialog(){
        List<Pair<String,Pair<Integer,String>>> bookList = new ArrayList<>();
        bookList.add(new Pair<>("第一册",new Pair<>(1, TypeLibrary.BookType.conceptFour)));
        bookList.add(new Pair<>("第二册",new Pair<>(2, TypeLibrary.BookType.conceptFour)));
        bookList.add(new Pair<>("第三册",new Pair<>(3, TypeLibrary.BookType.conceptFour)));
        bookList.add(new Pair<>("第四册",new Pair<>(4, TypeLibrary.BookType.conceptFour)));
        bookList.add(new Pair<>("StarterA",new Pair<>(278, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("StarterB",new Pair<>(279, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版1A",new Pair<>(280, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版1B",new Pair<>(281, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版2A",new Pair<>(282, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版2B",new Pair<>(283, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版3A",new Pair<>(284, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版3B",new Pair<>(285, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版4A",new Pair<>(286, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版4B",new Pair<>(287, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版5A",new Pair<>(288, TypeLibrary.BookType.conceptJunior)));
        bookList.add(new Pair<>("青少版5B",new Pair<>(289, TypeLibrary.BookType.conceptJunior)));

        int selectIndex = 0;
        String[] showArray = new String[bookList.size()];
        for (int i = 0; i < bookList.size(); i++) {
            showArray[i] = bookList.get(i).first;

            if (bookList.get(i).second.first == WordConfigData.getInstance().getShowBookId()){
                selectIndex = i;
            }
        }

        chooseDialog = new AlertDialog.Builder(getActivity())
                .setTitle("选择书籍")
                .setSingleChoiceItems(showArray, selectIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //如果当前数据和之前选中的数据一致，则不处理
                        int selectBookId = bookList.get(which).second.first;
                        if (selectBookId == WordConfigData.getInstance().getShowBookId()){
                            return;
                        }

                        //设置选中的数据
                        Pair<String,Pair<Integer,String>> selectPair = bookList.get(which);
                        WordConfigData.getInstance().setShowType(selectPair.second.second);
                        WordConfigData.getInstance().setShowName(selectPair.first);
                        WordConfigData.getInstance().setShowBookId(selectPair.second.first);

                        //刷新内容
                        refreshData();
                    }
                }).create();
    }

    //初始化数据
    private void initData(){
        binding.toolbar.standardTitle.setText("单词");
        binding.toolbar.standardRight.setImageResource(R.drawable.another_menu);
        binding.toolbar.standardRight.setOnClickListener(v->{
            if (chooseDialog==null){
                ToastUtil.showToast(getActivity(),"未初始化书籍数据");
                return;
            }

            chooseDialog.show();
            //设置显示高度
            int width = ScreenUtil.getScreenH(getActivity());
            WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
            lp.height = (int) (width*0.6);
            chooseDialog.getWindow().setAttributes(lp);
        });

        //默认不显示同步按钮
        binding.wordPassSync.setVisibility(View.INVISIBLE);
    }

    //初始化列表
    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    binding.refreshLayout.finishRefresh(false);
                    ToastUtil.showToast(getActivity(),"请连接到可用网络后使用");
                    return;
                }

                presenter.getRemoteWordData(WordConfigData.getInstance().getShowType(), WordConfigData.getInstance().getShowBookId());
            }
        });

        adapter = new WordPassAdapter(getActivity(),new ArrayList<>());
        binding.showView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        binding.showView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnSimpleClickListener<WordPassBean>() {
            @Override
            public void onClick(WordPassBean bean) {
                WordListActivity.start(getActivity(),bean.getType(),bean.getBookId(),bean.getId());
            }
        });
    }

    //初始化点击
    private void initClick(){
        binding.wordPassSync.setOnClickListener(v->{
            if (!GlobalMemory.INSTANCE.isLogin()){
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }

            //开始加载
            startLoading("正在同步单词进度~");
            //刷新进度
            presenter.getWordPassData(WordConfigData.getInstance().getShowType(),WordConfigData.getInstance().getShowBookId());
        });
    }

    /****************************************刷新数据********************************/
    private void refreshData(){
        presenter.getWordData(WordConfigData.getInstance().getShowType(), WordConfigData.getInstance().getShowBookId());
    }

    /************************************回调*************************************/
    //刷新闯关进度数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event){
        if (event.getType().equals(RefreshEvent.WORD_PASS_REFRESH)){
            //停止获取数据操作
            presenter.cancelRemoteWordData();
            //关闭自动加载数据操作
            binding.refreshLayout.finishRefresh();
            //闯关进度刷新
            refreshData();
        }

        if (event.getType().equals(RefreshEvent.USER_LOGOUT)){
            //账号登出
            refreshData();
        }
    }

    //登出后刷新闯关进度
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LogoutEvent event){
        refreshData();
    }

    @Override
    public void loadRemoteData() {
        //加载远程数据
        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void showWordPassResult(boolean isSuccess, String showMsg, List<WordPassBean> passList) {
        //关闭加载
        stopLoading();
        //获取单词的进度数据
        if (isSuccess){
            adapter.refreshData(passList);
        }else {
            ToastUtil.showToast(getActivity(),showMsg);
        }
    }

    @Override
    public void showWordData(List<WordPassBean> list, String showMsg) {
        //数据失败显示
        if (list==null){
            binding.refreshLayout.finishRefresh(false);
            ToastUtil.showToast(getActivity(),showMsg);
            return;
        }

        //数据成功显示
        binding.refreshLayout.finishRefresh(true);
        adapter.refreshData(list);

        //计算总的单词数据并显示
        int showWordCount = 0;
        for (int i = 0; i < list.size(); i++) {
            showWordCount+=list.get(i).getTotalCount();
        }
        binding.wordDesc.setText(WordConfigData.getInstance().getShowName()+"（"+showWordCount+"个单词）");

        //显示同步按钮
        binding.wordPassSync.setVisibility(View.VISIBLE);
    }

    //加载弹窗
    private LoadingMsgDialog loadingDialog;

    private void startLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingMsgDialog(getActivity());
            loadingDialog.create();;
        }
        loadingDialog.setMessage(showMsg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }
}
