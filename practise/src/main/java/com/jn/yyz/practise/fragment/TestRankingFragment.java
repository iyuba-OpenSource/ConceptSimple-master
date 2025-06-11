package com.jn.yyz.practise.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jn.yyz.practise.R;
import com.jn.yyz.practise.adapter.TestRankingAdapter;
import com.jn.yyz.practise.databinding.FragmentTestRankingBinding;
import com.jn.yyz.practise.model.bean.TestRankingBean;
import com.jn.yyz.practise.vm.PractiseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜
 *
 */
public class TestRankingFragment extends Fragment {

    private String flg;
    private int pageNumber = 1;
    private int pageSize = 20;
    private boolean showToolbar = true;

    private FragmentTestRankingBinding binding;

    private PractiseViewModel practiseViewModel;

    private TestRankingAdapter testRankingAdapter;

    private View bootomView;

    public TestRankingFragment() {
    }

    public static TestRankingFragment newInstance(String flg, int pageNumber, int pageSize,boolean showToolbar) {

        TestRankingFragment fragment = new TestRankingFragment();
        Bundle args = new Bundle();
        args.putString("FLG", flg);
        args.putInt("PAGE_NUMBER", pageNumber);
        args.putInt("PAGE_SIZE", pageSize);
        args.putBoolean("SHOW_TOOLBAR",showToolbar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {

            flg = bundle.getString("FLG");
            pageNumber = bundle.getInt("PAGE_NUMBER");
            pageSize = bundle.getInt("PAGE_SIZE");
            showToolbar = bundle.getBoolean("SHOW_TOOLBAR");
        }

        practiseViewModel = new ViewModelProvider(this).get(PractiseViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTestRankingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.getRoot().setVisibility(showToolbar?View.VISIBLE:View.GONE);
        binding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });

        binding.toolbar.toolbarTvTitle.setText("练习排行榜");

        List<TestRankingBean.DataDTO> dataDTOList = new ArrayList<>();
        testRankingAdapter = new TestRankingAdapter(dataDTOList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        binding.testrankingRv.setLayoutManager(linearLayoutManager);
        binding.testrankingRv.setAdapter(testRankingAdapter);
        testRankingAdapter.setLoadMoreCallback(new TestRankingAdapter.LoadMoreCallback() {
            @Override
            public void loadmore() {

                testRankingAdapter.setLoading(true);
                testRankingAdapter.setEnd(false);
                testRankingAdapter.setFail(false);
                practiseViewModel.requestEnglishTestRanking(flg, pageNumber, pageSize);
            }
        });

        //加载更多
        binding.testrankingRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                    TestRankingAdapter adapter = (TestRankingAdapter) recyclerView.getAdapter();

                    if (adapter != null) {

                        if (!adapter.isLoading() && !adapter.isEnd() && !adapter.isFail()) {

                            int lastPos = linearLayoutManager.findLastVisibleItemPosition();
                            if (adapter.getItemCount() - lastPos < 3) {

                                adapter.setLoading(true);
                                adapter.notifyItemInserted(adapter.getItemCount());

                                new Handler().postDelayed(() -> {

                                    pageNumber++;
                                    practiseViewModel.requestEnglishTestRanking(flg, pageNumber, pageSize);
                                }, 2000);
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


        practiseViewModel.getTestRankingBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<TestRankingBean>() {
                    @Override
                    public void onChanged(TestRankingBean testRankingBean) {

                        if (testRankingBean.getResult() == 200) {

                            updateMyUi(testRankingBean);
                            if (testRankingBean.getPage() == 1) {//页数为1，则清理list

                                testRankingAdapter.getDataDTOList().clear();
                                testRankingAdapter.getDataDTOList().addAll(testRankingBean.getData());
                                testRankingAdapter.notifyDataSetChanged();
                            } else {//加载更多

                                //列表信息
                                List<TestRankingBean.DataDTO> dataDTOS = testRankingBean.getData();
                                if (dataDTOS.isEmpty()) {

                                    testRankingAdapter.setLoading(false);
                                    testRankingAdapter.setEnd(true);
                                    testRankingAdapter.notifyDataSetChanged();
                                } else {

                                    if (dataDTOS.size() == pageSize) {

                                        testRankingAdapter.setLoading(false);
                                        testRankingAdapter.setEnd(false);
                                        int start = testRankingAdapter.getDataDTOList().size();
                                        testRankingAdapter.getDataDTOList().addAll(testRankingBean.getData());
                                        testRankingAdapter.notifyItemRangeInserted(start + 1, testRankingBean.getData().size());
                                    } else {//加载完成

                                        testRankingAdapter.setLoading(false);
                                        testRankingAdapter.setEnd(true);
                                        int start = testRankingAdapter.getDataDTOList().size();
                                        testRankingAdapter.getDataDTOList().addAll(testRankingBean.getData());
                                        testRankingAdapter.notifyItemRangeInserted(start + 1, testRankingBean.getData().size());

                                        //更新LoadingViewHolder
                                        testRankingAdapter.notifyItemChanged(testRankingAdapter.getItemCount() - 1);
                                    }
                                }
                            }
                        } else {//异常

                            testRankingAdapter.setFail(true);
                            testRankingAdapter.setLoading(false);
                            testRankingAdapter.setEnd(false);

                            //更新LoadingViewHolder
                            testRankingAdapter.notifyItemChanged(testRankingAdapter.getItemCount() - 1);
                        }
                    }
                });

        practiseViewModel.requestEnglishTestRanking(flg, pageNumber, pageSize);

        //天
        binding.testrankingTvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                testRankingAdapter.setEnd(false);
                testRankingAdapter.setFail(false);
                testRankingAdapter.setLoading(false);
                flg = "D";
                pageNumber = 1;
                practiseViewModel.requestEnglishTestRanking(flg, pageNumber, pageSize);
                //日
                binding.testrankingTvDay.setBackgroundResource(R.drawable.shape_rctg_flg_bg2);
                binding.testrankingTvDay.setText("日");
                binding.testrankingTvDay.setTextColor(Color.WHITE);
                //周
                binding.testrankingTvWeek.setBackgroundColor(Color.TRANSPARENT);
                binding.testrankingTvWeek.setText("周");
                binding.testrankingTvWeek.setTextColor(Color.BLACK);
                //月
                binding.testrankingTvMonth.setBackgroundColor(Color.TRANSPARENT);
                binding.testrankingTvMonth.setText("月");
                binding.testrankingTvMonth.setTextColor(Color.BLACK);
            }
        });
        //周
        binding.testrankingTvWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                testRankingAdapter.setEnd(false);
                testRankingAdapter.setFail(false);
                testRankingAdapter.setLoading(false);
                flg = "W";
                pageNumber = 1;
                practiseViewModel.requestEnglishTestRanking(flg, pageNumber, pageSize);

                //日
                binding.testrankingTvDay.setBackgroundColor(Color.TRANSPARENT);
                binding.testrankingTvDay.setText("日");
                binding.testrankingTvDay.setTextColor(Color.BLACK);
                //周
                binding.testrankingTvWeek.setBackgroundResource(R.drawable.shape_rctg_flg_bg2);
                binding.testrankingTvWeek.setText("周");
                binding.testrankingTvWeek.setTextColor(Color.WHITE);
                //月
                binding.testrankingTvMonth.setBackgroundColor(Color.TRANSPARENT);
                binding.testrankingTvMonth.setText("月");
                binding.testrankingTvMonth.setTextColor(Color.BLACK);
            }
        });
        //月
        binding.testrankingTvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                testRankingAdapter.setEnd(false);
                testRankingAdapter.setFail(false);
                testRankingAdapter.setLoading(false);
                flg = "M";
                pageNumber = 1;
                practiseViewModel.requestEnglishTestRanking(flg, pageNumber, pageSize);

                //日
                binding.testrankingTvDay.setBackgroundColor(Color.TRANSPARENT);
                binding.testrankingTvDay.setText("日");
                binding.testrankingTvDay.setTextColor(Color.BLACK);
                //周
                binding.testrankingTvWeek.setBackgroundColor(Color.TRANSPARENT);
                binding.testrankingTvWeek.setText("周");
                binding.testrankingTvWeek.setTextColor(Color.BLACK);
                //月
                binding.testrankingTvMonth.setBackgroundResource(R.drawable.shape_rctg_flg_bg2);
                binding.testrankingTvMonth.setText("月");
                binding.testrankingTvMonth.setTextColor(Color.WHITE);
            }
        });
    }

    /**
     * 更新展示自己的信息
     * @param testRankingBean
     */
    private void updateMyUi(TestRankingBean testRankingBean) {

        //个人信息
        binding.testrankingTvLevel.setText(getLevelName(testRankingBean.getMyExp()));
        binding.testrankingTvExp.setText(testRankingBean.getMyExp() + "经验");
        int d = getNextExp(testRankingBean.getMyExp());
        if (d == -1) {

            binding.testrankingTvTips.setText("满级了");
        } else {

            binding.testrankingTvTips.setText("距离下一等级还差" + getNextExp(testRankingBean.getMyExp()) + "经验");
        }
        //自己的信息
        if (bootomView == null) {

            bootomView = LayoutInflater.from(getContext()).inflate(R.layout.practise_item_test_ranking, null);
            binding.testrankingFlMyself.addView(bootomView);
        }

        TextView testranking_tv_name = bootomView.findViewById(R.id.testranking_tv_name);
        TextView testranking_tv_exp = bootomView.findViewById(R.id.testranking_tv_exp);
        ImageView testranking_iv_index = bootomView.findViewById(R.id.testranking_iv_index);
        TextView testranking_tv_index = bootomView.findViewById(R.id.testranking_tv_index);
        ImageView testranking_iv_avatar = bootomView.findViewById(R.id.testranking_iv_avatar);

        Glide.with(bootomView.getContext()).load("http://static1.iyuba.cn/uc_server/" + testRankingBean.getMyImgSrc()).into(testranking_iv_avatar);
        testranking_tv_name.setText(testRankingBean.getMyusername());
        testranking_tv_exp.setText(String.format("%s经验", testRankingBean.getMyExp()));
        int rank = testRankingBean.getMyranking();
        if (rank <= 3) {

            testranking_iv_index.setVisibility(View.VISIBLE);
            if (rank == 1) {

                testranking_iv_index.setImageResource(R.drawable.top1);
            } else if (rank == 2) {

                testranking_iv_index.setImageResource(R.drawable.top2);
            } else if (rank == 3) {

                testranking_iv_index.setImageResource(R.drawable.top3);
            }
        } else {

            testranking_iv_index.setVisibility(View.GONE);
            testranking_tv_index.setVisibility(View.VISIBLE);
            testranking_tv_index.setText(String.format("%s", rank));
        }
    }

    private String getLevelName(int exp) {

        if (exp <= 50) {

            return "书童";
        } else if (exp <= 200) {

            return "童生";
        } else if (exp <= 500) {

            return "秀才";
        } else if (exp <= 1000) {

            return "举人";
        } else if (exp <= 2000) {

            return "解元";
        } else if (exp <= 4000) {

            return "贡士";
        } else if (exp <= 7000) {

            return "进士";
        } else if (exp <= 12000) {

            return "探花";
        } else if (exp <= 20000) {

            return "榜眼";
        } else {

            return "状元";
        }
    }

    private int getNextExp(int exp) {

        int d = 0;
        if (exp <= 50) {

            d = 51 - exp;
        } else if (exp <= 200) {

            d = 201 - exp;
        } else if (exp <= 500) {

            d = 501 - exp;
        } else if (exp <= 1000) {

            d = 1001 - exp;
        } else if (exp <= 2000) {

            d = 2001 - exp;
        } else if (exp <= 4000) {

            d = 4001 - exp;
        } else if (exp <= 7000) {

            d = 7001 - exp;
        } else if (exp <= 12000) {

            d = 12001 - exp;
        } else if (exp <= 20000) {

            d = 20001 - exp;
        } else {

            d = -1;
        }

        return d;
    }
}