package com.suzhou.concept.lil.ui.study.word;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.databinding.ItemWordBottomBinding;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;

import java.util.List;

public class KnowledgeBottomAdapter extends RecyclerView.Adapter<KnowledgeBottomAdapter.BottomHolder> {

    private Context context;
    private List<Pair<String, Pair<Integer,String>>> pairList;

    public KnowledgeBottomAdapter(Context context, List<Pair<String, Pair<Integer, String>>> pairList) {
        this.context = context;
        this.pairList = pairList;
    }

    @NonNull
    @Override
    public BottomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordBottomBinding binding = ItemWordBottomBinding.inflate(LayoutInflater.from(context),parent,false);
        return new BottomHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomHolder holder, int position) {
        if (holder==null){
            return;
        }

        Pair<String,Pair<Integer,String>> pair = pairList.get(position);
        holder.iconView.setImageResource(pair.second.first);
        holder.textView.setText(pair.second.second);

        holder.itemView.setOnClickListener(v->{
            if (onSimpleClickListener!=null){
                onSimpleClickListener.onClick(pair.first);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pairList==null?0:pairList.size();
    }

    class BottomHolder extends RecyclerView.ViewHolder{

        private ImageView iconView;
        private TextView textView;

        public BottomHolder(ItemWordBottomBinding binding){
            super(binding.getRoot());

            iconView = binding.icon;
            textView = binding.text;
        }
    }

    //回调
    private OnSimpleClickListener<String> onSimpleClickListener;

    public void setOnSimpleClickListener(OnSimpleClickListener<String> onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }
}
