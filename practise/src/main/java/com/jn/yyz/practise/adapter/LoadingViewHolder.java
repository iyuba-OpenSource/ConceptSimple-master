package com.jn.yyz.practise.adapter;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {

    private int pageNumber;
    private int pageSize;
    private String flg;

    public ProgressBar loading_pb_p;
    public TextView loading_tv_content;

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

    private LoadMoreAdapter.LoadMoreCallback loadMoreCallback;

    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);

        loading_pb_p = itemView.findViewById(R.id.loading_pb_p);
        loading_tv_content = itemView.findViewById(R.id.loading_tv_content);
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

    public void update(boolean isLoading, boolean isEnd, boolean isFail) {

        this.isLoading = isLoading;
        this.isEnd = isEnd;
        this.isFail = isFail;

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