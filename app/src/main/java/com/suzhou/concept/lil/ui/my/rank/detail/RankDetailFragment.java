package com.suzhou.concept.lil.ui.my.rank.detail;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.databinding.FragmentRankAllBinding;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.data.remote.bean.Rank_exercise;
import com.suzhou.concept.lil.data.remote.bean.Rank_listen;
import com.suzhou.concept.lil.data.remote.bean.Rank_read;
import com.suzhou.concept.lil.data.remote.bean.Rank_speech;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.lil.util.EncodeUtil;
import com.suzhou.concept.lil.util.Glide3Util;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.utils.GlobalMemory;
import com.suzhou.concept.utils.OtherUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 排行界面
 */
public class RankDetailFragment extends BaseViewBindingFragment<FragmentRankAllBinding> implements RankDetailView {

    //参数
    //开始位置
    private int startIndex = 0;
    //每页数据
    private static final int showCount = 30;

    //刷新的数据周期类型
    private String showDateTitle = "今天";//天-D、周-W、月-M

    //数据
    private RankDetailPresenter presenter;
    //适配器
    private RankDetailAdapter adapter;
    //当前的用户排行数据
    private RankDetailShowBean userShowData;

    public static RankDetailFragment getInstance(String showType) {
        RankDetailFragment fragment = new RankDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.showType, showType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new RankDetailPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
        initClick();

        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    /********************************初始化******************************/
    private void initList() {
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(true);
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后重试～");
                    binding.refreshLayout.finishLoadMore(false);
                    return;
                }

                refreshData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())) {
                    ToastUtil.showToast(getActivity(), "请链接网络后重试～");
                    binding.refreshLayout.finishRefresh(false);
                    return;
                }

                startIndex = 0;
                refreshData();
            }
        });


        //列表
        adapter = new RankDetailAdapter(getActivity(), new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
    }

    private void initClick() {
        binding.searchType.setOnClickListener(v -> {
            if (!NetworkUtil.isConnected(getActivity())) {
                ToastUtil.showToast(getActivity(), "请链接网络后重试～");
                return;
            }

            switchType();
        });
    }

    /*******************************刷新数据*****************************/
    private void refreshData() {
        String showType = getArguments().getString(StrLibrary.showType);

        switch (showType) {
            case RankDetailShowBean.ShowType.listen:
                //听力
                presenter.getListenRankData(GlobalMemory.INSTANCE.getUserInfo().getUid(), getCurSearchType(showDateTitle), startIndex, showCount);
                break;
            case RankDetailShowBean.ShowType.speech:
                //口语
                presenter.getSpeechRankData(GlobalMemory.INSTANCE.getUserInfo().getUid(), getCurSearchType(showDateTitle), startIndex, showCount);
                break;
            case RankDetailShowBean.ShowType.read:
                //阅读
                presenter.getReadRankData(GlobalMemory.INSTANCE.getUserInfo().getUid(), getCurSearchType(showDateTitle), startIndex, showCount);
                break;
            case RankDetailShowBean.ShowType.exercise:
                //练习
                presenter.getExerciseRankData(GlobalMemory.INSTANCE.getUserInfo().getUid(), getCurSearchType(showDateTitle), startIndex, showCount);
                break;
        }
    }

    /*********************************回调数据****************************/
    @Override
    public void showSpeechRankData(Rank_speech rankData) {
        //显示用户数据
        showUserInfo(presenter.transSpeechUserData(rankData));

        //判断数据显示
        if (rankData == null || rankData.getData() == null || rankData.getData().size() == 0) {
            binding.refreshLayout.finishRefresh(false);
            binding.refreshLayout.finishLoadMore(false);
            ToastUtil.showToast(getActivity(), "暂无更多数据~");
            return;
        }

        //关闭刷新和加载
        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);

        //转换数据
        List<RankDetailShowBean> showList = presenter.transSpeechRankData(rankData);

        //显示列表数据
        if (startIndex == 0) {
            adapter.refreshList(showList);
            binding.recyclerView.scrollToPosition(0);

            //刷新第一的头像和名称
            Glide3Util.loadCircleImg(getActivity(), showList.get(0).getImageUrl(), R.drawable.head_small, binding.rankUserImage);
            binding.rankUserName.setText(showList.get(0).getShowName());
        } else {
            adapter.addList(showList);
        }

        //递增位置
        startIndex += showList.size();
    }

    @Override
    public void showListenRankData(Rank_listen rankData) {
        //显示用户数据
        showUserInfo(presenter.transListenUserData(rankData));

        //判断数据显示
        if (rankData == null || rankData.getData() == null || rankData.getData().size() == 0) {
            binding.refreshLayout.finishRefresh(false);
            binding.refreshLayout.finishLoadMore(false);
            ToastUtil.showToast(getActivity(), "暂无更多数据~");
            return;
        }

        //关闭刷新和加载
        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);

        //转换数据
        List<RankDetailShowBean> showList = presenter.transListenRankData(rankData);

        //显示列表数据
        if (startIndex == 0) {
            adapter.refreshList(showList);
            binding.recyclerView.scrollToPosition(0);

            //刷新第一的头像和名称
            Glide3Util.loadCircleImg(getActivity(), showList.get(0).getImageUrl(), R.drawable.head_small, binding.rankUserImage);
            binding.rankUserName.setText(showList.get(0).getShowName());
        } else {
            adapter.addList(showList);
        }

        //递增位置
        startIndex += showList.size();
    }

    @Override
    public void showReadRankData(Rank_read rankData) {
        //显示用户数据
        showUserInfo(presenter.transReadUserData(rankData));

        //判断数据显示
        if (rankData == null || rankData.getData() == null || rankData.getData().size() == 0) {
            binding.refreshLayout.finishRefresh(false);
            binding.refreshLayout.finishLoadMore(false);
            ToastUtil.showToast(getActivity(), "暂无更多数据~");
            return;
        }

        //关闭刷新和加载
        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);

        //转换数据
        List<RankDetailShowBean> showList = presenter.transReadRankData(rankData);

        //显示列表数据
        if (startIndex == 0) {
            adapter.refreshList(showList);
            binding.recyclerView.scrollToPosition(0);

            //刷新第一的头像和名称
            Glide3Util.loadCircleImg(getActivity(), showList.get(0).getImageUrl(), R.drawable.head_small, binding.rankUserImage);
            binding.rankUserName.setText(showList.get(0).getShowName());
        } else {
            adapter.addList(showList);
        }

        //递增位置
        startIndex += showList.size();
    }

    @Override
    public void showExerciseRankData(Rank_exercise rankData) {
        //显示用户数据
        showUserInfo(presenter.transExerciseUserData(rankData));

        //判断数据显示
        if (rankData == null || rankData.getData() == null || rankData.getData().size() == 0) {
            binding.refreshLayout.finishRefresh(false);
            binding.refreshLayout.finishLoadMore(false);
            ToastUtil.showToast(getActivity(), "暂无更多数据~");
            return;
        }

        //关闭刷新和加载
        binding.refreshLayout.finishRefresh(true);
        binding.refreshLayout.finishLoadMore(true);

        //转换数据
        List<RankDetailShowBean> showList = presenter.transExerciseRankData(rankData);

        //显示列表数据
        if (startIndex == 0) {
            adapter.refreshList(showList);
            binding.recyclerView.scrollToPosition(0);

            //刷新第一的头像和名称
            Glide3Util.loadCircleImg(getActivity(), showList.get(0).getImageUrl(), R.drawable.head_small, binding.rankUserImage);
            binding.rankUserName.setText(showList.get(0).getShowName());
        } else {
            adapter.addList(showList);
        }

        //递增位置
        startIndex += showList.size();
    }

    /*********************************其他方法***************************/
    //显示用户信息
    private void showUserInfo(RankDetailShowBean showBean) {
        if (!GlobalMemory.INSTANCE.isLogin() || showBean == null) {
            binding.userImage.setImageResource(R.drawable.head_small);
            binding.userName.setText("未登录");
            binding.userMsg.setText("登录后显示个人排行数据");
            return;
        }

        //设置为全局数据
        userShowData = showBean;

        //显示用户信息
        Glide3Util.loadCircleImg(getActivity(), showBean.getImageUrl(), R.drawable.head_small, binding.userImage);
        binding.userName.setText(showBean.getShowName());

        //显示的信息内容
        String showText = null;
        switch (showBean.getShowType()) {
            case RankDetailShowBean.ShowType.listen:
                //听力
                showText = "时长:" + showBean.getListenTime() / 60 + "分钟，文章数:" + showBean.getListenArticleCount() + "，单词数:" + showBean.getListenWordsCount() + "，排名:" + showBean.getRankIndex();
                break;
            case RankDetailShowBean.ShowType.speech:
                //口语
                showText = "句子数:" + showBean.getSpeechSentenceCount() + "，总分:" + showBean.getSpeechTotalScore() + "，平均分:" + showBean.getSpeechAverageScore() + "，排名:" + showBean.getRankIndex();
                break;
            case RankDetailShowBean.ShowType.read:
                //阅读
                showText = "单词数:" + showBean.getReadWordsCount() + "，文章数:" + showBean.getReadArticleCount() + "，WPM:" + showBean.getReadWpm() + "，排名:" + showBean.getRankIndex();
                break;
            case RankDetailShowBean.ShowType.exercise:
                //练习
                showText = "总题数:" + showBean.getExerciseTotalCount() + "，正确数:" + showBean.getExerciseRightCount() + "，正确率:" + showBean.getExerciseRightRate() + "，排名:" + showBean.getRankIndex();
                break;
        }

        binding.userMsg.setText(showText);
    }

    //切换查询时间
    private void switchType() {
        String[] searchArray = new String[]{"今天", "本周", "当月"};
        int searchIndex = getCurShowIndex(searchArray, showDateTitle);

        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(searchArray, searchIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获取当前的类型
                        String showTitle = searchArray[which];
                        if (showTitle.equals(showDateTitle)) {
                            return;
                        }

                        //设置当前查询类型
                        showDateTitle = showTitle;
                        //设置显示
                        binding.searchType.setText(showDateTitle);
                        //刷新数据
                        binding.refreshLayout.autoRefresh();
                        dialog.dismiss();
                    }
                }).create().show();
    }

    //获取当前类型的位置
    private int getCurShowIndex(String[] showArray, String curTitle) {
        for (int i = 0; i < showArray.length; i++) {
            if (showArray[i] == curTitle) {
                return i;
            }
        }
        return 0;
    }

    //获取当前查询的类型
    private String getCurSearchType(String searchTitle) {
        switch (searchTitle) {
            case "今天":
                return "D";
            case "本周":
                return "W";
            case "当月":
                return "M";
            default:
                return null;
        }
    }

    //显示分享操作
    public void showShare(){
        String showType = getArguments().getString(StrLibrary.showType);


        String showTitleName = null;
        String showTitleType = null;

        switch (showType){
            case RankDetailShowBean.ShowType.listen:
                //听力
                showTitleName = "听力";
                showTitleType = "listening";
                break;
            case RankDetailShowBean.ShowType.speech:
                //口语
                showTitleName = "口语";
                showTitleType = "speaking";
                break;
            case RankDetailShowBean.ShowType.read:
                //阅读
                showTitleName = "阅读";
                showTitleType = "reading";
                break;
            case RankDetailShowBean.ShowType.exercise:
                //练习
                showTitleName = "测试";
                showTitleType = "testing";
                break;
        }

        String showMsg = "我在"+getResources().getString(R.string.app_name)+"的"+showTitleName+"的排行中名列前茅，你也来试试吧";
        if (userShowData!=null&&userShowData.getRankIndex()>0){
            showMsg = "我在"+getResources().getString(R.string.app_name)+"的"+showTitleName+"的排行中位于第"+userShowData.getRankIndex()+"名，你也来试试吧";
        }

        //分享链接
        String sign = GlobalMemory.INSTANCE.getUserInfo().getUid()+"ranking"+ AppClient.appId;
        sign = EncodeUtil.md5(sign);
        String shareUrl = "http://m."+ OtherUtils.INSTANCE.getIyuba_cn()+"/i/getRanking.jsp?uid="+GlobalMemory.INSTANCE.getUserInfo().getUid()+"&appId="+AppClient.appId+"&sign="+sign+"&topic="+AppClient.appName+"&rankingType="+showTitleType;

        //分享操作
        OnekeyShare onekeyShare = new OnekeyShare();
        onekeyShare.setSite(getResources().getString(R.string.app_name)+"排行榜");
        onekeyShare.setTitle(showMsg);
        onekeyShare.setTitleUrl(shareUrl);
        onekeyShare.setUrl(shareUrl);
        onekeyShare.setSiteUrl(shareUrl);
        onekeyShare.setText(showMsg);
        onekeyShare.setImageData(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_new));
        onekeyShare.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                ToastUtil.showToast(getActivity(),"分享成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtil.showToast(getActivity(),"分享失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtil.showToast(getActivity(),"分享取消");
            }
        });
        onekeyShare.show(getActivity());
    }
}
