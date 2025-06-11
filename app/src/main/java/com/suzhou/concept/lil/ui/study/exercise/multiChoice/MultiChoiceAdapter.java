package com.suzhou.concept.lil.ui.study.exercise.multiChoice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemExerciseChoiceBinding;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;

import java.util.List;

public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceAdapter.ChoiceHolder> {

    private Context context;
    private List<String> list;

    //选中的位置
    private int selectIndex = -1;
    //正确答案的位置
    private int rightIndex = -1;

    public MultiChoiceAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseChoiceBinding binding = ItemExerciseChoiceBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ChoiceHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        String showText = list.get(position);
        holder.choice.setText(showText);

        if (selectIndex == position){
            holder.check.setImageResource(R.drawable.ic_choice_selected);
        }else {
            holder.check.setImageResource(R.drawable.ic_choice_select_no);
        }

        if (selectIndex>=0&&rightIndex>=0){
            holder.itemView.setEnabled(false);

            //有一个数据是当前的数据
            if (selectIndex==position || rightIndex==position){
                if (selectIndex==rightIndex){
                    //数据相同，则为选中的正确数据
                    holder.choice.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                }else {
                    //数据不相同，其他一个是当前位置的
                    if (selectIndex==position){
                        holder.choice.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                    }

                    if (rightIndex==position){
                        holder.choice.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    }
                }
            }else {
                //都不是选中的位置，设置为默认颜色
                holder.choice.setTextColor(context.getResources().getColor(R.color.black));
            }
        }else {
            holder.itemView.setEnabled(true);
            holder.choice.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(v->{
            if (selectIndex==position){
                return;
            }

            selectIndex = position;
            notifyDataSetChanged();

            if (onSimpleClickListener!=null){
                onSimpleClickListener.onClick(selectIndex);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ChoiceHolder extends RecyclerView.ViewHolder{

        private ImageView check;
        private TextView choice;

        public ChoiceHolder(ItemExerciseChoiceBinding binding){
            super(binding.getRoot());

            check = binding.check;
            choice = binding.choiceItem;
        }
    }

    //刷新结果数据
    public void refreshData(List<String> refreshList,int curSelectIndex,int curRightIndex){
        this.list = refreshList;
        this.selectIndex = curSelectIndex;
        this.rightIndex = curRightIndex;
        notifyDataSetChanged();
    }

    //回调数据
    private OnSimpleClickListener<Integer> onSimpleClickListener;

    public void setOnSimpleClickListener(OnSimpleClickListener<Integer> onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }
}
