package com.suzhou.concept.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemMainBottomBinding;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;

import java.util.List;

import kotlin.Pair;

/**
 * 首页的底部控件适配器
 */
public class MainBottomAdapter extends RecyclerView.Adapter<MainBottomAdapter.BottomHolder> {

    private Context context;
    private List<Pair<Integer,String>> list;

    //选中的位置
    private int selectIndex = 0;

    public MainBottomAdapter(Context context, List<Pair<Integer, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BottomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMainBottomBinding binding = ItemMainBottomBinding.inflate(LayoutInflater.from(context),parent,false);
        return new BottomHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomHolder holder, int position) {
        if (holder==null){
            return;
        }

        Pair<Integer,String> pair = list.get(position);
        holder.imageView.setImageResource(pair.getFirst());
        holder.textView.setText(pair.getSecond());

        int colorInt = 0;
        if (selectIndex == position){
            colorInt = context.getResources().getColor(R.color.theme);
            holder.textView.setTextSize(14);
        }else {
            colorInt = context.getResources().getColor(android.R.color.darker_gray);
            holder.textView.setTextSize(12);
        }
        holder.imageView.setColorFilter(colorInt);
        holder.textView.setTextColor(colorInt);

        holder.itemView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class BottomHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public BottomHolder(ItemMainBottomBinding binding){
            super(binding.getRoot());

            imageView = binding.imageView;
            textView = binding.textView;
        }
    }

    //设置选中的位置
    public void setSelectPosition(int position){
        this.selectIndex = position;
        notifyDataSetChanged();
    }

    //回调
    private OnSimpleClickListener<Integer> onItemClickListener;

    public void setOnItemClickListener(OnSimpleClickListener<Integer> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
