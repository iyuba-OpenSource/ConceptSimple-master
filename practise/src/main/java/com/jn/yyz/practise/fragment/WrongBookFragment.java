package com.jn.yyz.practise.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.R;
import com.jn.yyz.practise.activity.PractiseActivity;
import com.jn.yyz.practise.adapter.LoadMoreAdapter;
import com.jn.yyz.practise.adapter.WrongBook2Adapter;
import com.jn.yyz.practise.databinding.FragmentWrongBookBinding;
import com.jn.yyz.practise.model.bean.ExamBean;
import com.jn.yyz.practise.util.LineItemDecoration;
import com.jn.yyz.practise.vm.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 错题本
 *
 */
public class WrongBookFragment extends Fragment {

    private FragmentWrongBookBinding binding;

    private HomeViewModel homeViewModel;

    private String type;
    private boolean showToolbar = true;

    private int pageNumber = 1;

    private int pageSize = 20;

    private SharedPreferences wrongSp;

//    private WrongBookAdapter wrongBookAdapter;

    private WrongBook2Adapter wrongBook2Adapter;

    public WrongBookFragment() {
    }

    public static WrongBookFragment newInstance(String type,boolean showToolbar) {
        WrongBookFragment fragment = new WrongBookFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", type);
        bundle.putBoolean("SHOW_TOOLBAR",showToolbar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("TYPE");
            showToolbar = bundle.getBoolean("SHOW_TOOLBAR");
        }

        wrongSp = requireActivity().getSharedPreferences("WRONG_LIST", MODE_PRIVATE);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();

        pageNumber = 1;
        homeViewModel.requestGetWrongExamByUid(PractiseConstant.UID, type, pageNumber, pageSize, 1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWrongBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listenData();

        binding.toolbar.getRoot().setVisibility(showToolbar?View.VISIBLE:View.GONE);
        binding.toolbar.toolbarTvTitle.setText("错题本");
        binding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });

        initView();

        wrongBook2Adapter = new WrongBook2Adapter(new ArrayList<>(), R.layout.practise_item_wrong);
        binding.wbRvWrong.setAdapter(wrongBook2Adapter);
        wrongBook2Adapter.setLoadMoreCallback(new LoadMoreAdapter.LoadMoreCallback() {
            @Override
            public void loadmore() {
                homeViewModel.requestGetWrongExamByUid(PractiseConstant.UID, type, pageNumber, pageSize, 1);
            }
        });
    }


    private void initView() {
        //加载更多
        binding.wbRvWrong.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                    WrongBook2Adapter adapter = (WrongBook2Adapter) recyclerView.getAdapter();

                    if (adapter != null) {

                        if (!adapter.isLoading() && !adapter.isEnd() && !adapter.isFail()) {

                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            if (linearLayoutManager != null) {

                                int lastPos = linearLayoutManager.findLastVisibleItemPosition();
                                if (adapter.getItemCount() - lastPos < 3) {

                                    adapter.setLoading(true);
                                    adapter.notifyItemInserted(adapter.getItemCount());

                                    new Handler().postDelayed(() -> {

                                        pageNumber++;
                                        homeViewModel.requestGetWrongExamByUid(PractiseConstant.UID, type, pageNumber, pageSize, 1);
                                    }, 2000);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //跳转练习
        binding.wbTvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wrongBook2Adapter == null || wrongBook2Adapter.getDataList().isEmpty()) {

                    Toast.makeText(requireActivity(), "没有错题", Toast.LENGTH_SHORT).show();
                } else {

                    wrongSp.edit().putString("DATA", new Gson().toJson(wrongBook2Adapter.getDataList())).apply();

                    PractiseActivity.startActivity(requireActivity(), true, type, 0, "0", PractiseFragment.page_exerciseNote);
                }
            }
        });
        binding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requireActivity().finish();
            }
        });

        LineItemDecoration lineItemDecoration = new LineItemDecoration(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration.setDrawable(binding.getRoot().getContext().getDrawable(R.drawable.shape_wrong_line));
        binding.wbRvWrong.addItemDecoration(lineItemDecoration);
        binding.wbRvWrong.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }

    private void listenData() {

        homeViewModel.getExamBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<ExamBean>() {
                    @Override
                    public void onChanged(ExamBean examBean) {
                        //列表
                        if (examBean.getResult() == 200) {
                            if (examBean.getPageNumber() == 1) {//页数为1，则清理list
                                wrongBook2Adapter.getDataList().clear();
                                List<ExamBean.DataDTO> dataDTOS = examBean.getData();
                                if (dataDTOS.size() < pageSize) {//不够数

                                    wrongBook2Adapter.setFail(false);
                                    wrongBook2Adapter.setLoading(false);
                                    wrongBook2Adapter.setEnd(true);
                                    wrongBook2Adapter.getDataList().addAll(dataDTOS);

                                    //更新LoadingViewHolder
                                    //这个地方存在异常，更换成全部数据刷新使用
                                    wrongBook2Adapter.notifyDataSetChanged();
//                                    wrongBook2Adapter.notifyItemRangeChanged(0, wrongBook2Adapter.getDataList().size() + 1);
                                } else {
                                    wrongBook2Adapter.getDataList().addAll(dataDTOS);
                                    wrongBook2Adapter.notifyDataSetChanged();
                                }
                            } else {
                                //加载更多

                                //列表信息
                                List<ExamBean.DataDTO> dataDTOS = examBean.getData();
                                if (dataDTOS.isEmpty()) {

                                    wrongBook2Adapter.setLoading(false);
                                    wrongBook2Adapter.setEnd(true);
                                    wrongBook2Adapter.notifyDataSetChanged();
                                } else {

                                    if (dataDTOS.size() == pageSize) {

                                        wrongBook2Adapter.setLoading(false);
                                        wrongBook2Adapter.setEnd(false);
                                        int start = wrongBook2Adapter.getDataList().size();
                                        wrongBook2Adapter.getDataList().addAll(examBean.getData());

                                        //更换成下面一个
//                                        wrongBook2Adapter.notifyItemRangeInserted(start + 1, examBean.getData().size());
                                        wrongBook2Adapter.notifyDataSetChanged();
                                    } else {//加载完成

                                        wrongBook2Adapter.setLoading(false);
                                        wrongBook2Adapter.setEnd(true);
                                        int start = wrongBook2Adapter.getDataList().size();
                                        wrongBook2Adapter.getDataList().addAll(examBean.getData());

                                        //两个都更换成下面一个
//                                        wrongBook2Adapter.notifyItemRangeInserted(start + 1, examBean.getData().size());
                                        //更新LoadingViewHolder
//                                        wrongBook2Adapter.notifyItemChanged(wrongBook2Adapter.getItemCount() - 1);
                                        wrongBook2Adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } else if (examBean.getResult() == 20010) {
                            //没有错误数据
                            wrongBook2Adapter.setFail(false);
                            wrongBook2Adapter.setLoading(false);
                            wrongBook2Adapter.setEnd(true);

                            wrongBook2Adapter.getDataList().clear();

                            //更新LoadingViewHolder
//                            wrongBook2Adapter.notifyItemRangeChanged(0, wrongBook2Adapter.getDataList().size() + 1);
                            wrongBook2Adapter.notifyDataSetChanged();
                        } else {
                            //异常
                            wrongBook2Adapter.setFail(true);
                            wrongBook2Adapter.setLoading(false);
                            wrongBook2Adapter.setEnd(false);

                            //更新LoadingViewHolder
//                            wrongBook2Adapter.notifyItemChanged(wrongBook2Adapter.getItemCount() - 1);
                            wrongBook2Adapter.notifyDataSetChanged();
                        }

                        if (examBean.getResult() == 200) {
                            binding.wbTvCount.setText(String.format("%s道错误题", wrongBook2Adapter.getDataList().size()));
                        } else if (examBean.getResult() == 20010) {
                            binding.wbTvCount.setText("0道错误题");
                        }
                    }
                });
        homeViewModel.getIntegerMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {

                        if (integer == 40000) {//请求错题列表失败

                            wrongBook2Adapter.setFail(true);
                            wrongBook2Adapter.setLoading(false);
                            wrongBook2Adapter.setEnd(false);

                            //更新LoadingViewHolder
//                            wrongBook2Adapter.notifyItemChanged(wrongBook2Adapter.getItemCount() - 1);
                            //换成下面这一个吧，保险点
                            wrongBook2Adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}