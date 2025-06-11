package com.suzhou.concept.lil.ui.study.read;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.lil.manager.StudyDataManager;

import java.util.List;

/**
 * @title:
 * @date: 2023/9/27 13:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.ReadHolder> {

    private Context context;
    private List<Pair<String,String>> list;

    //当前语言显示
    private boolean isShowCn = StudyDataManager.getInstance().getReadShowCn();

    public ReadAdapter(Context context, List<Pair<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fix_read,parent,false);
        return new ReadHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadHolder holder, int position) {
        if (holder==null){
            return;
        }

        Pair<String,String> pair = list.get(position);
        holder.sentence.setText(pair.first);
        holder.sentence_cn.setText(pair.second);

        if (isShowCn){
            holder.sentence_cn.setVisibility(View.VISIBLE);
        }else {
            holder.sentence_cn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ReadHolder extends RecyclerView.ViewHolder{

        private TextView sentence;
        private TextView sentence_cn;

        public ReadHolder(View view){
            super(view);

            sentence = view.findViewById(R.id.sentence);
            sentence_cn = view.findViewById(R.id.sentenceCn);
        }
    }

    //刷新数据
    public void refreshData(List<Pair<String,String>> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新语言显示
    public void refreshLanguage(boolean showCn){
        this.isShowCn = showCn;
        notifyDataSetChanged();
    }
}
