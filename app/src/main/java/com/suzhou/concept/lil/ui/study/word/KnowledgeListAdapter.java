package com.suzhou.concept.lil.ui.study.word;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.databinding.ItemWordNoteBinding;
import com.suzhou.concept.lil.data.bean.WordShowBean;

import java.util.List;

public class KnowledgeListAdapter extends RecyclerView.Adapter<KnowledgeListAdapter.ListHolder> {


    private Context context;
    private List<WordShowBean> list;

    public KnowledgeListAdapter(Context context, List<WordShowBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordNoteBinding binding = ItemWordNoteBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordShowBean showBean = list.get(position);
        holder.wordView.setText(showBean.getWord());
        if (TextUtils.isEmpty(showBean.getPron())){
            holder.pornView.setText("");
        }else {
            holder.pornView.setText("["+showBean.getPron()+"]");
        }
        if (TextUtils.isEmpty(showBean.getDef())){
            holder.defView.setText("");
        }else {
            holder.defView.setText(showBean.getDef());
        }

        holder.audioView.setOnClickListener(v->{
            if (onSimpleClickListener!=null){
                onSimpleClickListener.onPlay(showBean.getWordAudioUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ListHolder extends RecyclerView.ViewHolder{

        private TextView wordView;
        private ImageView audioView;
        private TextView pornView;
        private TextView defView;
        private ImageView checkView;

        public ListHolder(ItemWordNoteBinding binding){
            super(binding.getRoot());

            wordView = binding.word;
            audioView = binding.audio;
            pornView = binding.pron;
            defView = binding.def;
            checkView = binding.check;
            checkView.setVisibility(View.GONE);
        }
    }

    //接口回调
    private OnItemClickListener onSimpleClickListener;

    public interface OnItemClickListener{
        //播放音频
        void onPlay(String playUrl);
    }

    public void setOnSimpleClickListener(OnItemClickListener onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }

    //刷新数据
    public void refreshData(List<WordShowBean> refeshList){
        this.list = refeshList;
        notifyDataSetChanged();
    }
}
