package com.suzhou.concept.lil.ui.study.rank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.iyuba.module.user.LoginEvent;
import com.iyuba.sdk.other.NetworkUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.article.EvaluationInfoActivity;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.FragmentRankNewBinding;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.remote.bean.Rank_eval;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.util.Glide3Util;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.NoScrollLinearLayoutManager;
import com.suzhou.concept.utils.ExtraKeysFactory;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class RankNewFragment extends BaseViewBindingFragment<FragmentRankNewBinding> implements RankNewView{

    //每页数据
    private static final int showCount = 20;
    //当前页码
    private int startIndex = 0;

    //数据
    private RankNewPresenter presenter;
    //适配器
    private RankNewListAdapter adapter;

    public static RankNewFragment getInstance(int voaId){
        RankNewFragment fragment = new RankNewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(StrLibrary.voaId,voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        presenter = new RankNewPresenter();
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

    /****************************初始化******************************/
    private void initList(){
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(true);
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后刷新数据~");
                    binding.refreshLayout.finishLoadMore();
                    return;
                }

                refreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.showToast(getActivity(),"请链接网络后刷新数据~");
                    binding.refreshLayout.finishRefresh();
                    return;
                }

                startIndex = 0;
                refreshData();
            }
        });

        adapter = new RankNewListAdapter(getActivity(),new ArrayList<>());
        binding.showView.setLayoutManager(new NoScrollLinearLayoutManager(getActivity(),false));
        binding.showView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        binding.showView.setAdapter(adapter);
        adapter.setOnSimpleClickListener(new OnSimpleClickListener<Rank_eval.DataDTO>() {
            @Override
            public void onClick(Rank_eval.DataDTO dataDTO) {
                //直接跳转，登陆操作留到详情界面
                Intent intent = new Intent();
                intent.setClass(getActivity(), EvaluationInfoActivity.class);
                intent.putExtra(ExtraKeysFactory.userId,dataDTO.getUid());
                intent.putExtra(ExtraKeysFactory.userName,dataDTO.getName());
                startActivity(intent);
            }
        });

        //底部跳转
        binding.bottomView.getRoot().setOnClickListener(v->{
            //登陆判断
            if (!GlobalMemory.INSTANCE.isLogin()){
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }

            //直接跳转，登陆操作留到详情界面
            Intent intent = new Intent();
            intent.setClass(getActivity(), EvaluationInfoActivity.class);
            intent.putExtra(ExtraKeysFactory.userId,GlobalMemory.INSTANCE.getUserInfo().getUid());
            intent.putExtra(ExtraKeysFactory.userName,GlobalMemory.INSTANCE.getUserInfo().getUsername());
            startActivity(intent);
        });
    }

    /*******************************刷新数据*************************/
    private void refreshData(){
        if (!NetworkUtil.isConnected(getActivity())){
//            updateUI(false,"请链接网络后重试～");
            binding.refreshLayout.finishRefresh();
            binding.refreshLayout.finishLoadMore();
            return;
        }

        int voaId = getArguments().getInt(StrLibrary.voaId);
        presenter.getRankData(startIndex,showCount,voaId,GlobalMemory.INSTANCE.getUserInfo().getUid());
    }

    /*******************************界面刷新*************************/
    /*private void updateUI(boolean isLoading,String showMsg){
        if (isLoading){
            binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
            binding.loadingLayout.loadingView.setVisibility(View.VISIBLE);
            binding.loadingLayout.loadingBtn.setVisibility(View.GONE);
            binding.loadingLayout.loadingMsg.setText("正在加载排行数据～");
        }else {
            if (TextUtils.isEmpty(showMsg)){
                binding.loadingLayout.getRoot().setVisibility(View.GONE);
            }else {
                binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
                binding.loadingLayout.loadingView.setVisibility(View.INVISIBLE);
                binding.loadingLayout.loadingBtn.setVisibility(View.VISIBLE);
                binding.loadingLayout.loadingMsg.setText(showMsg);
                binding.loadingLayout.loadingBtn.setOnClickListener(v->{
                    binding.refreshLayout.autoRefresh();
                });
            }
        }
    }*/

    /*******************************回调数据********************************/
    @Override
    public void showUserRankData(Rank_eval rankData) {
        if (!GlobalMemory.INSTANCE.isLogin()){
            binding.bottomView.indexView.setText("0");
            binding.bottomView.nameView.setText("未登录");
            binding.bottomView.markView.setText("登录后显示个人数据");
            binding.bottomView.picView.setImageResource(R.drawable.head_small);
            binding.bottomView.scoreView.setText("0");
            return;
        }

        Glide3Util.loadCircleImg(getActivity(), rankData.getMyimgSrc(),R.drawable.head_small,binding.bottomView.picView);
        binding.bottomView.indexView.setText(String.valueOf(rankData.getMyranking()));
        binding.bottomView.nameView.setText(rankData.getMyname());

        String showMsg = "平均分:0\t\t评测数:0";
        if (rankData.getMycount()>0){
            showMsg = "平均分:"+rankData.getMyscores()/rankData.getMycount()+"\t\t评测数:"+rankData.getMycount();
        }
        binding.bottomView.markView.setText(showMsg);
        binding.bottomView.scoreView.setText(String.valueOf(rankData.getMyscores()));
        binding.bottomView.scoreView.setTextColor(getResources().getColor(R.color.answer_wrong));
        binding.bottomView.scoreTips.setTextColor(getResources().getColor(R.color.answer_wrong));

        //设置样式
        binding.bottomView.getRoot().setBackgroundResource(R.drawable.shape_corner_border_5dp);
    }

    @Override
    public void showAllRankData(List<Rank_eval.DataDTO> list) {
        if (list==null || list.size()==0){
            binding.refreshLayout.finishRefresh(false);
            binding.refreshLayout.finishLoadMore(false);


            //如果是第一个，则显示信息
//            if (startIndex == 0){
//                updateUI(false,"暂无排行数据");
//            }else {
//                ToastUtil.showToast(getActivity(),"暂无更多数据～");
//            }
            ToastUtil.showToast(getActivity(),"暂无更多数据～");
            return;
        }

        //关闭刷新
        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);
        //显示数据
//        updateUI(false,null);

        if (startIndex==0){
            //去掉前三个数据
            List<Rank_eval.DataDTO> showList = splitTopData(list);
            if (showList==null){
                //关闭显示
                adapter.refreshList(new ArrayList<>());
            }else {
                //刷新显示
                adapter.refreshList(showList);
            }
            //单独展示前三个数据
            showTopView(list);
        }else {
            adapter.addList(list);
        }

        //增加index
        startIndex+=20;
    }

    //登陆后回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event){
        binding.refreshLayout.autoRefresh();
    }

    //排行榜界面刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event){
        if (event.getType().equals(RefreshEvent.STUDY_RANK_REFRESH)){
            binding.refreshLayout.autoRefresh();
        }
    }

    /**********************************其他方法****************************/
    //去掉前三个数据
    private List<Rank_eval.DataDTO> splitTopData(List<Rank_eval.DataDTO> list){
        if (list==null||list.size()==0){
            return list;
        }

        if (list.size()<3){
            return null;
        }

        List<Rank_eval.DataDTO> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i < 3){
                continue;
            }

            newList.add(list.get(i));
        }

        return newList;
    }

    //展示前三个数据
    private void showTopView(List<Rank_eval.DataDTO> list){
        binding.leftLayout.setVisibility(View.INVISIBLE);
        binding.middleLayout.setVisibility(View.INVISIBLE);
        binding.rightLayout.setVisibility(View.INVISIBLE);

        if (list==null || list.size()==0){
            return;
        }

        if (list.size()>0){
            Rank_eval.DataDTO showData = list.get(0);

            binding.middleLayout.setVisibility(View.VISIBLE);
            binding.middleRank.setText("NO.1");
            binding.middleName.setText(showData.getName());
            Glide3Util.loadCircleImg(getActivity(), presenter.getUserPicUrl(showData.getUid()),R.drawable.head_small,binding.middlePic);
            String showMsg = "句子数:0\n平均分:0\n总分:0";
            if (showData.getCount()>0){
                showMsg = "句子数:"+showData.getCount()+"\n平均分:"+showData.getScores()/showData.getCount()+"\n总分:"+showData.getScores();
            }
            binding.middleMsg.setText(showMsg);

            binding.middleLayout.setOnClickListener(v->{
                //直接跳转，登陆操作留到详情界面
                Intent intent = new Intent();
                intent.setClass(getActivity(), EvaluationInfoActivity.class);
                intent.putExtra(ExtraKeysFactory.userId,showData.getUid());
                intent.putExtra(ExtraKeysFactory.userName,showData.getName());
                startActivity(intent);
            });
        }

        if (list.size()>1){
            Rank_eval.DataDTO showData = list.get(1);

            binding.leftLayout.setVisibility(View.VISIBLE);
            binding.leftRank.setText("NO.2");
            binding.leftName.setText(showData.getName());
            Glide3Util.loadCircleImg(getActivity(), presenter.getUserPicUrl(showData.getUid()),R.drawable.head_small,binding.leftPic);
            String showMsg = "句子数:0\n平均分:0\n总分:0";
            if (showData.getCount()>0){
                showMsg = "句子数:"+showData.getCount()+"\n平均分:"+showData.getScores()/showData.getCount()+"\n总分:"+showData.getScores();
            }
            binding.leftMsg.setText(showMsg);

            binding.leftLayout.setOnClickListener(v->{
                //直接跳转，登陆操作留到详情界面
                Intent intent = new Intent();
                intent.setClass(getActivity(), EvaluationInfoActivity.class);
                intent.putExtra(ExtraKeysFactory.userId,showData.getUid());
                intent.putExtra(ExtraKeysFactory.userName,showData.getName());
                startActivity(intent);
            });
        }

        if (list.size()>2){
            Rank_eval.DataDTO showData = list.get(2);

            binding.rightLayout.setVisibility(View.VISIBLE);
            binding.rightRank.setText("NO.3");
            binding.rightName.setText(showData.getName());
            Glide3Util.loadCircleImg(getActivity(), presenter.getUserPicUrl(showData.getUid()),R.drawable.head_small,binding.rightPic);
            String showMsg = "句子数:0\n平均分:0\n总分:0";
            if (showData.getCount()>0){
                showMsg = "句子数:"+showData.getCount()+"\n平均分:"+showData.getScores()/showData.getCount()+"\n总分:"+showData.getScores();
            }
            binding.rightMsg.setText(showMsg);

            binding.rightLayout.setOnClickListener(v->{
                //直接跳转，登陆操作留到详情界面
                Intent intent = new Intent();
                intent.setClass(getActivity(), EvaluationInfoActivity.class);
                intent.putExtra(ExtraKeysFactory.userId,showData.getUid());
                intent.putExtra(ExtraKeysFactory.userName,showData.getName());
                startActivity(intent);
            });
        }
    }
}
