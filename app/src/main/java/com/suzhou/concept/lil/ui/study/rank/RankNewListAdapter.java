package com.suzhou.concept.lil.ui.study.rank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemRankNewBinding;
import com.suzhou.concept.lil.data.remote.bean.Rank_eval;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.util.Glide3Util;
import com.suzhou.concept.utils.OtherUtils;

import java.util.List;

public class RankNewListAdapter extends RecyclerView.Adapter<RankNewListAdapter.ListHolder> {

    private Context context;
    private List<Rank_eval.DataDTO> list;

    public RankNewListAdapter(Context context, List<Rank_eval.DataDTO> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRankNewBinding binding = ItemRankNewBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        if (holder==null){
            return;
        }

        Rank_eval.DataDTO rankBean = list.get(position);

        holder.indexView.setText(String.valueOf(rankBean.getRanking()));
        holder.indexView.setTextColor(context.getResources().getColor(R.color.study_listen_play));
        Glide3Util.loadCircleImg(context,getUserPicUrl(rankBean.getUid()), R.drawable.head_small,holder.picView);
        holder.nameView.setText(rankBean.getName());

        String showMsg = "平均分:0\t\t评测数:0";
        if (rankBean.getCount()>0){
            showMsg = "平均分:"+rankBean.getScores()/rankBean.getCount()+"\t\t评测数:"+rankBean.getCount();
        }
        holder.msgView.setText(showMsg);
        holder.scoreView.setText(String.valueOf(rankBean.getScores()));
        holder.scoreView.setTextColor(context.getResources().getColor(R.color.study_listen_play));
        holder.scoreTips.setTextColor(context.getResources().getColor(R.color.study_listen_play));

        holder.itemView.setOnClickListener(v->{
            if (onSimpleClickListener!=null){
                onSimpleClickListener.onClick(rankBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ListHolder extends RecyclerView.ViewHolder{

        private TextView indexView;
        private ImageView picView;
        private TextView nameView;
        private TextView msgView;
        private TextView scoreView;
        private TextView scoreTips;

        public ListHolder(ItemRankNewBinding binding){
            super(binding.getRoot());

            indexView = binding.indexView;
            picView = binding.picView;
            nameView = binding.nameView;
            msgView = binding.markView;
            scoreView = binding.scoreView;
            scoreTips = binding.scoreTips;
        }
    }

    //刷新数据
    public void refreshList(List<Rank_eval.DataDTO> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //添加数据
    public void addList(List<Rank_eval.DataDTO> addList){
        this.list.addAll(addList);
        notifyItemRangeInserted(list.size()-addList.size(),addList.size());
    }

    //点击回调
    private OnSimpleClickListener<Rank_eval.DataDTO> onSimpleClickListener;

    public void setOnSimpleClickListener(OnSimpleClickListener<Rank_eval.DataDTO> onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }

    //获取用户的头像
    private String getUserPicUrl(int userId){
        return "http://api."+ OtherUtils.INSTANCE.getIyuba_com()+"/v2/api.iyuba?protocol=10005&size=middle&timestamp="+ System.currentTimeMillis()+"&uid="+userId;
    }
}
