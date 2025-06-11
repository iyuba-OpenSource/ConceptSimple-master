package com.jn.yyz.practise.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jn.yyz.practise.R;
import com.jn.yyz.practise.entity.Pair;

import java.util.List;

/**
 * 音频配对
 */
public class SoundPairAdapter extends RecyclerView.Adapter<SoundPairAdapter.PairViewHolder> {


    private List<Pair> pairList;

    private int position = -1;

    private ClickCallback clickCallback;

    private boolean isChoose = true;

    public SoundPairAdapter(List<Pair> pairList) {
        this.pairList = pairList;
    }

    @NonNull
    @Override
    public PairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_pair, parent, false);
        return new PairViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PairViewHolder holder, int position) {

        Pair pair = pairList.get(position);
        holder.setData(pair);
    }

    @Override
    public int getItemCount() {
        return pairList.size();
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public ClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Pair> getPairList() {
        return pairList;
    }

    public void setPairList(List<Pair> pairList) {
        this.pairList = pairList;
    }

    public class PairViewHolder extends RecyclerView.ViewHolder {

        ImageView pair_iv_sound;
        TextView pair_tv_content;
        FrameLayout pair_fl_content;
        Pair pair;

        public PairViewHolder(@NonNull View itemView) {
            super(itemView);
            pair_tv_content = itemView.findViewById(R.id.pair_tv_content);
            pair_fl_content = itemView.findViewById(R.id.pair_fl_content);
            pair_iv_sound = itemView.findViewById(R.id.pair_iv_sound);

            pair_fl_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (isChoose) {

                        if (!pair.isPair()) {

                            position = getAdapterPosition();
                            notifyDataSetChanged();
                            if (clickCallback != null) {

                                clickCallback.click(pair);
                            }
                        }
                    }
                }
            });
        }

        public void setData(Pair pair) {

            this.pair = pair;

            if (pair.getSound() == null) {//文本

                pair_iv_sound.setVisibility(View.INVISIBLE);
                pair_tv_content.setText(pair.getName());
                if (pair.isError()) {

                    pair_tv_content.setVisibility(View.VISIBLE);
                    pair_tv_content.setTextColor(Color.WHITE);
                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_error);
                } else if (pair.isPair()) {
//                    pair_tv_content.setVisibility(View.INVISIBLE);
//                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_paired);
                    // TODO: 2025/4/23 原来使用上边的内容，但是群里说成功后需要修改成不可点击+文字显示样式
                    pair_tv_content.setTextColor(Color.WHITE);
                    pair_tv_content.setVisibility(View.VISIBLE);
                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_green);
                } else if (position == getAdapterPosition()) {

                    pair_tv_content.setVisibility(View.VISIBLE);
                    pair_tv_content.setTextColor(Color.WHITE);
                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_choosed);
                } else {

                    pair_tv_content.setVisibility(View.VISIBLE);
                    pair_tv_content.setTextColor(Color.BLACK);
                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_content);
                }
            } else {//音频

                pair_iv_sound.setVisibility(View.VISIBLE);
                pair_tv_content.setVisibility(View.GONE);
                if (pair.isError()) {

                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_error);
                } else if (pair.isPair()) {

                    // TODO: 2025/4/23 更换成下面的样式处理
//                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_paired);
                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_green);
                } else if (position == getAdapterPosition()) {

                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_choosed_sound);
                } else {

                    pair_fl_content.setBackgroundResource(R.drawable.shape_pair_content);
                }
            }

            if (pair.isPlaying()) {

                Glide.with(itemView.getContext()).load(R.mipmap.gif_laba2).into(pair_iv_sound);
            } else {

                pair_iv_sound.setImageResource(R.mipmap.laba);
            }
        }
    }

    public interface ClickCallback {

        void click(Pair pair);
    }
}
