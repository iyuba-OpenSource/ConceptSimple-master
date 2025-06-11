package com.suzhou.concept.lil.ui.my.wordNote.wordDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.lil.ui.study.eval.bean.WordExplainBean;

import java.util.List;

public class WordShowNewAdapter extends RecyclerView.Adapter<WordShowNewAdapter.WordNewHolder> {

    private Context context;
    private List<WordExplainBean.SentBean> list;

    public WordShowNewAdapter(Context context, List<WordExplainBean.SentBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordNewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_word_sentence_item2,parent,false);
        return new WordNewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordNewHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordExplainBean.SentBean sentBean = list.get(position);
        holder.indexView.setText(String.valueOf(position+1));
        holder.sentenceView.setText(sentBean.getOrig());
        holder.sentenceCnView.setText(sentBean.getTrans());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordNewHolder extends RecyclerView.ViewHolder{
        private TextView indexView;
        private TextView sentenceView;
        private TextView sentenceCnView;

        public WordNewHolder(View view){
            super(view);

            indexView = view.findViewById(R.id.index);
            sentenceView = view.findViewById(R.id.sentence);
            sentenceCnView = view.findViewById(R.id.sentenceCn);
        }
    }
}
