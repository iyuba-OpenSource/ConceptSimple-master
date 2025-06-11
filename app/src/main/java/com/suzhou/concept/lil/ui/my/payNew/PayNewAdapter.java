package com.suzhou.concept.lil.ui.my.payNew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemPayMethodNewBinding;

import java.util.List;

public class PayNewAdapter extends RecyclerView.Adapter<PayNewAdapter.PayNewHolder> {

    private Context context;
    private List<Pair<String,Pair<Integer,Pair<String,String>>>> list;

    public PayNewAdapter(Context context, List<Pair<String,Pair<Integer,Pair<String,String>>>> list) {
        this.context = context;
        this.list = list;
    }

    //选中的位置
    private int selectIndex = 0;

    @NonNull
    @Override
    public PayNewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPayMethodNewBinding binding = ItemPayMethodNewBinding.inflate(LayoutInflater.from(context),parent,false);
        return new PayNewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PayNewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        Pair<String,Pair<Integer,Pair<String,String>>> showPair = list.get(position);
        holder.imageView.setImageResource(showPair.second.first);
        holder.payName.setText(showPair.second.second.first);
        holder.payDesc.setText(showPair.second.second.second);

        if (selectIndex == position){
            holder.checkBox.setImageResource(R.drawable.pay_method_checked);
        }else {
            holder.checkBox.setImageResource(R.drawable.pay_method_unchecked);
        }

        holder.checkBox.setOnClickListener(v->{
            selectIndex = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class PayNewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView payName;
        private TextView payDesc;
        private ImageView checkBox;

        public PayNewHolder(ItemPayMethodNewBinding binding) {
            super(binding.getRoot());

            imageView = binding.payMethodIcon;
            payName = binding.payMethodText;
            payDesc = binding.payMethodInfo;
            checkBox = binding.payMethodCheckbox;
        }
    }

    //刷新数据
    public void refreshData(List<Pair<String,Pair<Integer,Pair<String,String>>>> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //获取当前类型
    public String getPayMethod(){
        return list.get(selectIndex).first;
    }
}
