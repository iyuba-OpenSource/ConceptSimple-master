package com.suzhou.concept.lil.ui.my.kouyu.rank;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.suzhou.concept.activity.speaking.OtherOneVideoActivity;
import com.suzhou.concept.bean.YoungRankItem;
import com.suzhou.concept.databinding.LayoutRefreshListBinding;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.utils.ExtraKeysFactory;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 口语秀中的排行数据界面
 */
public class TalkRankFragment extends BaseViewBindingFragment<LayoutRefreshListBinding> implements TalkRankView{

    //开始的页码
    private int startNum = 1;

    //每页的数量
    private static final int showCount = 20;

    //数据
    private TalkRankPresenter presenter;
    //适配器
    private TalkRankAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new TalkRankPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        binding.refreshLayout.autoRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        presenter.detachView();
    }

    private void initList(){
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(true);

        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    binding.refreshLayout.finishLoadMore();
                    return;
                }

                refreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后重试～");
                    binding.refreshLayout.finishRefresh();
                    return;
                }

                startNum = 1;
                refreshData();
            }
        });

        adapter = new TalkRankAdapter(getActivity(),new ArrayList<>());
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnSimpleClickListener(new OnSimpleClickListener<YoungRankItem>() {
            @Override
            public void onClick(YoungRankItem youngRankItem) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), OtherOneVideoActivity.class);
                intent.putExtra(ExtraKeysFactory.youngRankItem, GsonUtils.toJson(youngRankItem));
                startActivity(intent);
            }
        });
    }

    private void refreshData(){
        presenter.getRankData(Integer.parseInt(GlobalMemory.INSTANCE.getSpeakingItem().getVoa_id()),startNum,showCount);
    }


    /******************************************回调数据*************************************/
    @Override
    public void showRankData(List<YoungRankItem> list,String showMsg) {
        if (list==null){
            if (TextUtils.isEmpty(showMsg)){
                ToastUtil.showToast(getActivity(),"暂无更多数据～");
                binding.refreshLayout.finishRefresh(true);
                binding.refreshLayout.finishLoadMore(true);
            }else {
                ToastUtil.showToast(getActivity(),showMsg);
                binding.refreshLayout.finishRefresh(false);
                binding.refreshLayout.finishLoadMore(false);
            }
            return;
        }

        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);

        //刷新数据
        if (startNum==1){
            adapter.refreshData(list);
        }else {
            adapter.addList(list);
        }

        startNum+=1;
    }

    //刷新点赞回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event){
        if (event.getType().equals(RefreshEvent.KOUYU_AGREE)){
            //刷新数据
            binding.refreshLayout.autoRefresh();
        }
    }
}
