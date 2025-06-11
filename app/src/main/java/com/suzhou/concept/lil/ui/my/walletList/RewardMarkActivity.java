package com.suzhou.concept.lil.ui.my.walletList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ActivityRewardMarkBinding;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingActivity;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.Glide3Util;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * @title: 钱包列表界面
 * @date: 2023/8/22 18:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RewardMarkActivity extends BaseViewBindingActivity<ActivityRewardMarkBinding> {

    private WalletListAdapter listAdapter;

    //起始数据
    private int pages = 1;
    //每页的数量
    private int pageCount = 20;
    //是否刷新状态
    private boolean isRefresh = true;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, RewardMarkActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToolbar();
        initUserInfo();
        initList();

        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*********************初始化*******************/
    private void initToolbar(){
        binding.toolbar.standardTitle.setText("钱包历史记录");
        binding.toolbar.standardLeft.setBackgroundResource(0);
        binding.toolbar.standardLeft.setImageResource(R.drawable.left);
        binding.toolbar.standardLeft.setOnClickListener(v->{
            finish();
        });
        binding.toolbar.standardRight.setVisibility(View.INVISIBLE);
        binding.toolbar.standardRight.setBackgroundResource(0);
        binding.toolbar.standardRight.setImageResource(R.drawable.tips);
        binding.toolbar.standardRight.setOnClickListener(v->{
            String oldMoney = GlobalMemory.INSTANCE.getUserInfo().getSelf().getMoney();
            float money = Float.parseFloat(TextUtils.isEmpty(oldMoney)?"0":oldMoney);

            String showMsg = "当前钱包金额:" + BigDecimalUtil.trans2Double(money*0.01f) + "元,满10元可在[爱语吧]微信公众号提现(关注绑定爱语吧账号)";

            new AlertDialog.Builder(this)
                    .setTitle("奖励说明")
                    .setMessage(showMsg)
                    .show();
        });

        binding.showTag.type.setText("类型");
        binding.showTag.type.setTextColor(getResources().getColor(R.color.app_color));
        binding.showTag.reward.setText("金额(元)");
        binding.showTag.reward.setTextColor(getResources().getColor(R.color.app_color));
        binding.showTag.time.setText("时间");
        binding.showTag.time.setTextColor(getResources().getColor(R.color.app_color));
    }

    private void initUserInfo(){
        String userIconUrl = GlobalMemory.INSTANCE.getUserInfo().getUserPic();
        Glide3Util.loadCircleImg(this, userIconUrl, R.drawable.head_small, binding.userIcon);
        binding.userName.setText(GlobalMemory.INSTANCE.getUserInfo().getUsername());
        String showTips = "(满1元可以抵扣会员购买，付费全站会员可以提现)";
        double showMoney = 0f;
        String hasMoney = GlobalMemory.INSTANCE.getUserInfo().getSelf().getMoney();
        if (!TextUtils.isEmpty(hasMoney)){
            showMoney = BigDecimalUtil.trans2Double(Integer.parseInt(hasMoney)/100.0f);
        }
        binding.userMoney.setText("金额："+showMoney+"元\n"+showTips);
    }

    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(true);

        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(RewardMarkActivity.this)){
                    stopRefreshAndMore(false);
                    ToastFactory.showShort(RewardMarkActivity.this,"请链接网络后重试~");
                    return;
                }

                isRefresh = false;
                loadData(pages,pageCount);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(RewardMarkActivity.this)){
                    stopRefreshAndMore(false);
                    ToastFactory.showShort(RewardMarkActivity.this,"请链接网络后重试~");
                    return;
                }

                pages = 1;
                isRefresh = true;
                loadData(pages,pageCount);
            }
        });

        listAdapter = new WalletListAdapter(this,new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(listAdapter);
    }

    /**********************刷新数据*****************/
    private void refreshData(){
        binding.refreshLayout.autoRefresh();
    }

    private void stopRefreshAndMore(boolean isFinish){
        binding.refreshLayout.finishRefresh(isFinish);
        binding.refreshLayout.finishLoadMore(isFinish);
    }

    /********************回调数据********************/
    public void showRewardHistory(List<Reward_history> list) {
        if (list==null){
            stopRefreshAndMore(false);
            ToastFactory.showShort(this,"查询奖励的历史记录失败~");
            return;
        }

        stopRefreshAndMore(true);
        if (list.size()==0){
            if (isRefresh){
                ToastFactory.showShort(this,"当前账号暂无奖励记录~");
            }else {
                ToastFactory.showShort(this,"当前账号暂无更多奖励记录~");
            }
            return;
        }

        if (list.size()>0){
            pages++;
        }

        if (isRefresh){
            listAdapter.refreshData(list,false);
        }else {
            listAdapter.refreshData(list,true);
        }
    }

    /**********************加载数据*****************************/
    private Disposable loadDataDis;
    private void loadData(int pageIndex,int pageNum){
        RetrofitUtil.getInstance().getRewardHistoryData(pageIndex, pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean<List<Reward_history>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        loadDataDis = d;
                    }

                    @Override
                    public void onNext(BaseBean<List<Reward_history>> bean) {
                        if (bean!=null&&bean.getResult().equals("200")){
                            showRewardHistory(bean.getData());
                        }else {
                            showRewardHistory(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showRewardHistory(null);
                    }

                    @Override
                    public void onComplete() {
                        if (loadDataDis!=null&&!loadDataDis.isDisposed()){
                            loadDataDis.dispose();
                        }
                    }
                });
    }
}
