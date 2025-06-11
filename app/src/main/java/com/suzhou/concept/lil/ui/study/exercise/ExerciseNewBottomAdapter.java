package com.suzhou.concept.lil.ui.study.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemExerciseBottomBinding;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;

import java.util.List;

/**
 * 底部适配器
 */
public class ExerciseNewBottomAdapter extends RecyclerView.Adapter<ExerciseNewBottomAdapter.BottomHolder> {

    private Context context;
    private List<Pair<String, String>> list;

    //选中位置
    private int selectIndex = 0;

    public ExerciseNewBottomAdapter(Context context, List<Pair<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BottomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseBottomBinding binding = ItemExerciseBottomBinding.inflate(LayoutInflater.from(context),parent,false);
        return new BottomHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        Pair<String,String> pair = list.get(position);
        holder.textView.setText(pair.first);

        if (selectIndex == position){
            holder.textView.setTextColor(context.getResources().getColor(R.color.theme));
        }else {
            holder.textView.setTextColor(context.getResources().getColor(R.color.gray));
        }

        holder.textView.setOnClickListener(v->{
            if (selectIndex==position){
                return;
            }

            if (onSimpleClickListener!=null){
                onSimpleClickListener.onClick(pair);
            }

            selectIndex = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class BottomHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public BottomHolder(ItemExerciseBottomBinding binding){
            super(binding.getRoot());

            textView = binding.text;
        }
    }

    private OnSimpleClickListener<Pair<String,String>> onSimpleClickListener;

    public void setOnSimpleClickListener(OnSimpleClickListener<Pair<String, String>> onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }

    //刷新数据
    public void refreshData(List<Pair<String,String>> pairList){
        this.list = pairList;
        notifyDataSetChanged();
    }
}
