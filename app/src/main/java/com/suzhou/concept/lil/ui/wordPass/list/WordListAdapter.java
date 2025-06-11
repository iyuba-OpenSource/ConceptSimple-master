package com.suzhou.concept.lil.ui.wordPass.list;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemWordListBinding;
import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ListHolder> {

    private Context context;
    private List<WordShowBean> list;

    public WordListAdapter(Context context, List<WordShowBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordListBinding binding = ItemWordListBinding.inflate(LayoutInflater.from(context),parent,false);
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

        //判断是否收藏单词
        WordCollectBean collectBean = RoomDBManager.getInstance().getSingleWordCollectData(GlobalMemory.INSTANCE.getUserInfo().getUid(), showBean.getWord());
        if (collectBean==null){
            holder.collectView.setImageResource(R.drawable.ic_collect_no);
        }else {
            holder.collectView.setImageResource(R.drawable.ic_collected);
        }

        holder.collectView.setOnClickListener(v->{
            if (onWordListClickListener!=null){
                onWordListClickListener.onCollect(collectBean!=null,position,showBean);
            }
        });
        holder.playView.setOnClickListener(v->{
            if (onWordListClickListener!=null){
                onWordListClickListener.onPlay(showBean.getWordAudioUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ListHolder extends RecyclerView.ViewHolder{

        private TextView wordView;
        private ImageView collectView;
        private TextView pornView;
        private ImageView playView;
        private TextView defView;

        public ListHolder(ItemWordListBinding binding){
            super(binding.getRoot());

            wordView = binding.wordView;
            collectView = binding.collectView;
            pornView = binding.pornView;
            playView = binding.playView;
            defView = binding.defView;

        }
    }

    //刷新数据
    public void refreshData(List<WordShowBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //回调接口

    private OnWordListClickListener onWordListClickListener;
    public interface OnWordListClickListener{
        //收藏
        void onCollect(boolean hasCollected,int position,WordShowBean showBean);
        //播放
        void onPlay(String playUrl);
    }

    public void setOnWordListClickListener(OnWordListClickListener onWordListClickListener) {
        this.onWordListClickListener = onWordListClickListener;
    }
}
