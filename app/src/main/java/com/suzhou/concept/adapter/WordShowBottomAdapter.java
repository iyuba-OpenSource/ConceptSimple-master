package com.suzhou.concept.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;

import java.util.List;

/**
 * @title: 知识界面底部适配器
 * @date: 2023/8/15 11:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordShowBottomAdapter extends RecyclerView.Adapter<WordShowBottomAdapter.WordShowBottomHolder> {

    private Context context;
//    private List<Pair<String,Pair<Integer,String>>> list;//类型-图片,文字
    private List<kotlin.Pair<String, kotlin.Pair<Integer,String>>> list;//类型-图片,文字

    public WordShowBottomAdapter(Context context, List<kotlin.Pair<String, kotlin.Pair<Integer,String>>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordShowBottomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View showView = LayoutInflater.from(context).inflate(R.layout.item_word_bottom,parent,false);
        return new WordShowBottomHolder(showView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordShowBottomHolder holder, int position) {
        if (holder==null){
            return;
        }

        kotlin.Pair<String, kotlin.Pair<Integer,String>> pairPair = list.get(position);
        holder.icon.setImageResource(pairPair.getSecond().getFirst());
        holder.text.setText(pairPair.getSecond().getSecond());

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(pairPair.getFirst());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordShowBottomHolder extends RecyclerView.ViewHolder{

        private ImageView icon;
        private TextView text;

        public WordShowBottomHolder(View showView){
            super(showView);

            icon = showView.findViewById(R.id.icon);
            text = showView.findViewById(R.id.text);
        }
    }

    //回调
    private OnSimpleClickListener<String> listener;

    public interface OnSimpleClickListener<T>{
        void onClick(T t);
    }

    public void setListener(OnSimpleClickListener<String> listener) {
        this.listener = listener;
    }
}
