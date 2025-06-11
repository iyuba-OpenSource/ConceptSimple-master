package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;

import java.util.List;

/**
 * 213
 * 单词选择
 */
public class WordChooseAdapter extends RecyclerView.Adapter<WordChooseAdapter.WordChooseViewHolder> {

    private int position = -1;

    private List<String> stringList;

    private boolean isCheck = true;

    private Callback callback;


    public WordChooseAdapter(List<String> stringList) {
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public WordChooseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_word_choose, parent, false);
        return new WordChooseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordChooseViewHolder holder, int position) {

        String str = stringList.get(position);
        holder.setData(str);
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class WordChooseViewHolder extends RecyclerView.ViewHolder {

        LinearLayout wc_ll_word;
        TextView wc_tv_word;
        String data;

        public WordChooseViewHolder(@NonNull View itemView) {
            super(itemView);
            wc_ll_word = itemView.findViewById(R.id.wc_ll_word);
            wc_tv_word = itemView.findViewById(R.id.wc_tv_word);

            wc_ll_word.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isCheck) {

                        position = getAdapterPosition();
                        notifyDataSetChanged();
                        if (callback != null) {

                            callback.click(data);
                        }
                    }
                }
            });
        }

        public void setData(String data) {

            this.data = data;
            wc_tv_word.setText(data);

            if (position == getAdapterPosition()) {

                wc_ll_word.setBackgroundResource(R.drawable.shape_phonetic_bg_word_blue);
            } else {

                wc_ll_word.setBackgroundResource(R.drawable.shape_phonetic_bg_word_gray);
            }
        }
    }

    public interface Callback {

        void click(String data);
    }
}
