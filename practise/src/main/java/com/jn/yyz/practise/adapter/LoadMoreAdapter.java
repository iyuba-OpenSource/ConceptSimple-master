package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;

import java.util.List;


public abstract class LoadMoreAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<T> dataList;

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

    private int mLayoutId;

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

    public LoadMoreCallback getLoadMoreCallback() {
        return loadMoreCallback;
    }

    public void setLoadMoreCallback(LoadMoreCallback loadMoreCallback) {
        this.loadMoreCallback = loadMoreCallback;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }


    public LoadMoreAdapter(List<T> dataList, int mLayoutId) {

        this.dataList = dataList;
        this.mLayoutId = mLayoutId;
    }

    @Override
    public int getItemViewType(int position) {

        if (position < dataList.size()) {

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

            return BaseViewHolder.get(parent.getContext(), parent, mLayoutId);

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof BaseViewHolder) {

            convert(holder, dataList.get(position));
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.update(isLoading, isEnd, isFail);
        }
    }

    public abstract void convert(RecyclerView.ViewHolder holder, T t);

    @Override
    public int getItemCount() {

        if (isLoading) {

            return dataList.size() + 1;
        } else if (isEnd) {

            return dataList.size() + 1;
        } else if (isFail) {

            return dataList.size() + 1;
        } else {

            return dataList.size();
        }
    }


    public interface LoadMoreCallback {


        void loadmore();
    }
}
