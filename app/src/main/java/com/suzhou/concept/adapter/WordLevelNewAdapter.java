package com.suzhou.concept.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @title: 新的单词列表适配器
 * @date: 2023/10/9 13:59
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordLevelNewAdapter extends RecyclerView.Adapter<WordLevelNewAdapter.NewLevelHolder> {

    @NonNull
    @Override
    public NewLevelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NewLevelHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class NewLevelHolder extends RecyclerView.ViewHolder{

        public NewLevelHolder(View view){
            super(view);
        }
    }
}
