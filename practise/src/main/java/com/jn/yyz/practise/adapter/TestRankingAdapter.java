package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jn.yyz.practise.R;
import com.jn.yyz.practise.model.bean.TestRankingBean;

import java.util.List;

public class TestRankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TestRankingBean.DataDTO> dataDTOList;


    /**
     * 是够在加载
     */
    private boolean isLoading = false;

    /**
     * 结束
     */
    private boolean isEnd = false;

    /**
     * 加载更多异常
     */
    private boolean isFail = false;

    private int failPosition = 0;

    private LoadMoreCallback loadMoreCallback;


    public boolean isFail() {
        return isFail;
    }

    public void setFail(boolean fail) {
        isFail = fail;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public TestRankingAdapter(List<TestRankingBean.DataDTO> dataDTOList) {
        this.dataDTOList = dataDTOList;
    }


    public LoadMoreCallback getLoadMoreCallback() {
        return loadMoreCallback;
    }

    public void setLoadMoreCallback(LoadMoreCallback loadMoreCallback) {
        this.loadMoreCallback = loadMoreCallback;
    }

    @Override
    public int getItemViewType(int position) {

        if (position < dataDTOList.size()) {

            return 0;
        } else if (isLoading || isEnd || isFail) {

            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 0) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_test_ranking, parent, false);
            return new TestRankingViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TestRankingViewHolder) {

            TestRankingViewHolder testRankingViewHolder = (TestRankingViewHolder) holder;
            TestRankingBean.DataDTO dataDTO = dataDTOList.get(position);
            testRankingViewHolder.setData(dataDTO);
        } else if (holder instanceof LoadingViewHolder) {

            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.update();
        }
    }

    @Override
    public int getItemCount() {

        if (isLoading) {

            return dataDTOList.size() + 1;
        } else if (isEnd) {

            return dataDTOList.size() + 1;
        } else if (isFail) {

            return dataDTOList.size() + 1;
        } else {

            return dataDTOList.size();
        }
    }

    public void setDataDTOList(List<TestRankingBean.DataDTO> dataDTOList) {
        this.dataDTOList = dataDTOList;
    }

    public List<TestRankingBean.DataDTO> getDataDTOList() {
        return dataDTOList;
    }


    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private int pageNumber;
        private int pageSize;
        private String flg;

        public ProgressBar loading_pb_p;
        public TextView loading_tv_content;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);

            loading_pb_p = itemView.findViewById(R.id.loading_pb_p);
            loading_tv_content = itemView.findViewById(R.id.loading_tv_content);

            update();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isFail) {

                        if (loadMoreCallback != null) {

                            loadMoreCallback.loadmore();
                        }
                    }
                }
            });
        }

        public void update() {

            if (isEnd) {

                loading_pb_p.setVisibility(View.GONE);
                loading_tv_content.setText("没有更多数据了");
            } else if (isLoading) {

                loading_pb_p.setVisibility(View.VISIBLE);
                loading_tv_content.setText("正在加载...");
            } else if (isFail) {

                loading_pb_p.setVisibility(View.GONE);
                loading_tv_content.setText("加载数据异常，点击重新加载");
            }
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public String getFlg() {
            return flg;
        }

        public void setFlg(String flg) {
            this.flg = flg;
        }
    }

    public class TestRankingViewHolder extends RecyclerView.ViewHolder {

        private TextView testranking_tv_index;
        private ImageView testranking_iv_avatar;
        private TextView testranking_tv_name;
        private TextView testranking_tv_exp;

        public ImageView testranking_iv_index;

        private TestRankingBean.DataDTO dataDTO;

        public TestRankingViewHolder(@NonNull View itemView) {
            super(itemView);

            testranking_tv_index = itemView.findViewById(R.id.testranking_tv_index);
            testranking_iv_avatar = itemView.findViewById(R.id.testranking_iv_avatar);
            testranking_tv_name = itemView.findViewById(R.id.testranking_tv_name);
            testranking_tv_exp = itemView.findViewById(R.id.testranking_tv_exp);
            testranking_iv_index = itemView.findViewById(R.id.testranking_iv_index);
        }

        //http://static1.iyuba.cn/uc_server/head/2024/10/7/9/33/23/83e3fbb1-22a1-4abe-95ec-dbede531209a-m.jpg
        public void setData(TestRankingBean.DataDTO dataDTO) {

            this.dataDTO = dataDTO;
            Glide.with(testranking_iv_avatar.getContext()).load("http://static1.iyuba.cn/uc_server/" + dataDTO.getImage()).into(testranking_iv_avatar);
            testranking_tv_name.setText(dataDTO.getUsername());
            testranking_tv_exp.setText(dataDTO.getExp() + "经验");

            if (dataDTO.getRanking() <= 3) {

                testranking_iv_index.setVisibility(View.VISIBLE);
                if (dataDTO.getRanking() == 1) {

                    testranking_iv_index.setImageResource(R.drawable.top1);
                } else if (dataDTO.getRanking() == 2) {

                    testranking_iv_index.setImageResource(R.drawable.top2);
                } else if (dataDTO.getRanking() == 3) {

                    testranking_iv_index.setImageResource(R.drawable.top3);
                }
            } else {

                testranking_iv_index.setVisibility(View.GONE);
                testranking_tv_index.setVisibility(View.VISIBLE);
                testranking_tv_index.setText(dataDTO.getRanking() + "");
            }

        }

    }


    public interface LoadMoreCallback {


        void loadmore();
    }
}
