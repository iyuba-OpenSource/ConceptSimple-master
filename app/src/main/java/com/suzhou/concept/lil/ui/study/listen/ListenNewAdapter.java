package com.suzhou.concept.lil.ui.study.listen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.view.SelectWordTextView;

import java.util.List;

/**
 * @title:
 * @date: 2023/10/19 09:52
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenNewAdapter extends RecyclerView.Adapter<ListenNewAdapter.ListenHolder> {

    private Context context;
    private List<EvaluationSentenceItem> list;

    //选中的位置
    private int selectIndex = 0;
    //是否为双语显示
    private boolean isBilingual = true;

    public ListenNewAdapter(Context context, List<EvaluationSentenceItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listen_new,parent,false);
        return new ListenHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListenHolder holder, int position) {
        if (holder==null){
            return;
        }

        EvaluationSentenceItem item = list.get(position);
        holder.sentenceView.setText(item.getSentence());
        holder.sentenceCnView.setText(item.getSentence_cn());

        if (selectIndex == position){
            holder.sentenceView.setTextSize(20);
            holder.sentenceCnView.setTextSize(16);
            holder.sentenceView.setTextColor(context.getResources().getColor(R.color.study_listen_play));
            holder.sentenceCnView.setTextColor(context.getResources().getColor(R.color.study_listen_play));
        }else {
            holder.sentenceView.setTextSize(18);
            holder.sentenceCnView.setTextSize(14);
            holder.sentenceView.setTextColor(context.getResources().getColor(R.color.black));
            holder.sentenceCnView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        if (isBilingual){
            holder.sentenceCnView.setVisibility(View.VISIBLE);
        }else {
            holder.sentenceCnView.setVisibility(View.INVISIBLE);
        }

        holder.sentenceView.setOnClickWordListener(new SelectWordTextView.OnClickWordListener() {
            @Override
            public void onClickWord(String word) {
                if (onWordClickListener!=null){
                    onWordClickListener.onClick(word);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ListenHolder extends RecyclerView.ViewHolder{

        private SelectWordTextView sentenceView;
        private TextView sentenceCnView;

        public ListenHolder(View rootview){
            super(rootview);

            sentenceView = rootview.findViewById(R.id.sentence);
            sentenceCnView = rootview.findViewById(R.id.sentence_cn);
        }
    }

    //单词选中回调
    private OnSimpleClickListener<String> onWordClickListener;

    public void setOnWordClickListener(OnSimpleClickListener<String> onWordClickListener) {
        this.onWordClickListener = onWordClickListener;
    }

    //刷新数据
    public void refreshData(List<EvaluationSentenceItem> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新选中的位置
    public void refreshSelectIndex(int position){
        this.selectIndex = position;
        notifyDataSetChanged();
    }

    //刷新双语显示
    public void refreshLanguage(boolean isBilingual){
        this.isBilingual = isBilingual;
        notifyDataSetChanged();
    }

    //获取当前的语言状态
    public boolean isBilingual(){
        return isBilingual;
    }
}
