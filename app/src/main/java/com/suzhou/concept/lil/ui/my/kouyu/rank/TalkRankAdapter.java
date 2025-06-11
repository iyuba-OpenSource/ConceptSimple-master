package com.suzhou.concept.lil.ui.my.kouyu.rank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.bean.YoungRankItem;
import com.suzhou.concept.databinding.ItemTalkRankBinding;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.util.Glide3Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TalkRankAdapter extends RecyclerView.Adapter<TalkRankAdapter.RankHolder> {

    private Context context;
    private List<YoungRankItem> list;

    public TalkRankAdapter(Context context, List<YoungRankItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RankHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTalkRankBinding binding = ItemTalkRankBinding.inflate(LayoutInflater.from(context),parent,false);
        return new RankHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RankHolder holder, int position) {
        if (holder==null){
            return;
        }

        YoungRankItem item = list.get(position);
        holder.rankView.setText(String.valueOf(position+1));
        Glide3Util.loadCircleImg(context,item.getImage(), R.drawable.head_small,holder.picView);
        holder.nameView.setText(item.getUserName());
        holder.dateView.setText(item.getCreateDate());
        holder.scoreView.setText(item.getScore()+"分");
        holder.agreeView.setText(item.getAgreeCount());

        holder.itemView.setOnClickListener(v->{
            if (onSimpleClickListener!=null){
                onSimpleClickListener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class RankHolder extends RecyclerView.ViewHolder{

        private TextView rankView;
        private CircleImageView picView;
        private TextView nameView;
        private TextView dateView;
        private TextView scoreView;
        private TextView agreeView;

        public RankHolder(ItemTalkRankBinding binding){
            super(binding.getRoot());

            rankView = binding.speakingItemIndex;
            picView = binding.speakingItemHead;
            nameView = binding.speakingItemName;
            dateView = binding.speakingItemTime;
            scoreView = binding.speakingItemScore;
            agreeView = binding.speakingItemLike;
        }
    }

    //刷新数据
    public void refreshData(List<YoungRankItem> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //增加数据
    public void addList(List<YoungRankItem> addList){
        this.list.addAll(addList);
        notifyItemRangeInserted(list.size()-addList.size(),addList.size());
    }

    //回调接口
    private OnSimpleClickListener<YoungRankItem> onSimpleClickListener;

    public void setOnSimpleClickListener(OnSimpleClickListener<YoungRankItem> onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }
}
