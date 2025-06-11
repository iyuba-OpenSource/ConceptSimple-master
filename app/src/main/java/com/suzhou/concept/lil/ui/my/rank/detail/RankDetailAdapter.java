package com.suzhou.concept.lil.ui.my.rank.detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemRankAllBinding;
import com.suzhou.concept.lil.util.Glide3Util;

import java.util.List;

public class RankDetailAdapter extends RecyclerView.Adapter<RankDetailAdapter.DetailHolder> {

    private Context context;

    private List<RankDetailShowBean> list;

    public RankDetailAdapter(Context context, List<RankDetailShowBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRankAllBinding binding = ItemRankAllBinding.inflate(LayoutInflater.from(context),parent,false);
        return new DetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailHolder holder, int position) {
        if (holder==null){
            return;
        }

        RankDetailShowBean showBean = list.get(position);
        //排行
        int rankIndex = showBean.getRankIndex();
        if (rankIndex==1){
            holder.indexView.setVisibility(View.INVISIBLE);
            holder.indexImage.setVisibility(View.VISIBLE);
            holder.indexImage.setImageResource(R.drawable.rank_gold);
        }else if (rankIndex==2){
            holder.indexView.setVisibility(View.INVISIBLE);
            holder.indexImage.setVisibility(View.VISIBLE);
            holder.indexImage.setImageResource(R.drawable.rank_silvery);
        }else if (rankIndex==3){
            holder.indexView.setVisibility(View.INVISIBLE);
            holder.indexImage.setVisibility(View.VISIBLE);
            holder.indexImage.setImageResource(R.drawable.rank_copper);
        }else {
            holder.indexView.setVisibility(View.VISIBLE);
            holder.indexImage.setVisibility(View.GONE);
            holder.indexView.setText(String.valueOf(showBean.getRankIndex()));
        }

        //其他数据
        Glide3Util.loadCircleImg(context,showBean.getImageUrl(),R.drawable.head_small,holder.picView);
        holder.nameView.setText(showBean.getShowName());
        showRankInfo(showBean.getShowType(), showBean,holder.bottomShowView,holder.msgShowView);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class DetailHolder extends RecyclerView.ViewHolder{

        private TextView indexView;
        private ImageView indexImage;

        private ImageView picView;
        private TextView nameView;

        private TextView bottomShowView;
        private TextView msgShowView;

        public DetailHolder(ItemRankAllBinding binding){
            super(binding.getRoot());

            indexView = binding.rankLogoText;
            indexImage = binding.rankLogoImage;

            picView = binding.userImage;

            nameView = binding.rankUserName;

            bottomShowView = binding.rankUserWords;
            msgShowView = binding.rankUserInfo;
        }
    }


    //显示不同的数据
    private void showRankInfo(String showType,RankDetailShowBean showBean,TextView bottomMsgView,TextView msgShowView){
        if (TextUtils.isEmpty(showType)||showBean==null){
            return;
        }

        String msgText = null;
        String showText = null;

        switch (showType){
            case RankDetailShowBean.ShowType.listen:
                //听力
                msgText = showBean.getListenTime()+"分钟";
                showText = "文章数:"+showBean.getListenArticleCount()+"\n单词数:"+showBean.getListenWordsCount();
                break;
            case RankDetailShowBean.ShowType.speech:
                //口语
                msgText = "句子数:"+showBean.getSpeechSentenceCount();
                showText = "总分:"+showBean.getSpeechTotalScore()+"\n平均分:"+showBean.getSpeechAverageScore();
                break;
            case RankDetailShowBean.ShowType.read:
                //阅读
                msgText = "单词数:"+showBean.getReadWordsCount();
                showText = "文章数:"+showBean.getReadArticleCount()+"\nWPM:"+showBean.getReadWpm();
                break;
            case RankDetailShowBean.ShowType.exercise:
                //练习
                msgText = "总题数:"+showBean.getExerciseTotalCount();
                showText = "正确数:"+showBean.getExerciseRightCount()+"\n正确率:"+showBean.getExerciseRightRate();
                break;
        }

        bottomMsgView.setText(msgText);
        msgShowView.setText(showText);
    }


    //刷新数据
    public void refreshList(List<RankDetailShowBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //添加数据
    public void addList(List<RankDetailShowBean> addList){
        this.list.addAll(addList);
        notifyItemRangeInserted(list.size()-addList.size(),addList.size());
    }
}
