package com.jn.yyz.practise.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.entity.Translate;
import com.jn.yyz.practise.entity.WordFillIn;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 选择单词，飞到线上去
 */
public class WordFillInAdapter extends RecyclerView.Adapter<WordFillInAdapter.WordFillInViewHolder> {

    private List<WordFillIn> wordFillInList;

    private boolean isChoose = true;

    private Callback callback;

    private Pattern pattern;

    public WordFillInAdapter(List<WordFillIn> wordFillInList) {

        this.wordFillInList = wordFillInList;
        pattern = Pattern.compile("_+");
    }

    @NonNull
    @Override
    public WordFillInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_word_fill_in, parent, false);
        return new WordFillInViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordFillInViewHolder holder, int position) {

        WordFillIn wordFillIn = wordFillInList.get(position);
        holder.setData(wordFillIn);
    }

    @Override
    public int getItemCount() {
        return wordFillInList.size();
    }


    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public List<WordFillIn> getWordFillInList() {
        return wordFillInList;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public class WordFillInViewHolder extends RecyclerView.ViewHolder {

        private WordFillIn wordFillIn;
        public TextView wfi_tv_word;
        public View wfi_v_line;

        public WordFillInViewHolder(@NonNull View itemView) {
            super(itemView);

            wfi_v_line = itemView.findViewById(R.id.wfi_v_line);
            wfi_tv_word = itemView.findViewById(R.id.wfi_tv_word);
            wfi_tv_word.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isChoose) {

                        String wordStr = wordFillIn.getWord();
                        Matcher matcher = pattern.matcher(wordStr);
                        if (matcher.find()) {

                            if (callback != null) {

                                callback.click(wordFillIn, wordFillIn.getTranslate());
                            }
                        }
                    }
                }
            });
        }

        public WordFillIn getWordFillIn() {
            return wordFillIn;
        }

        public void setData(WordFillIn wordFillIn) {

            this.wordFillIn = wordFillIn;
            String wordStr = wordFillIn.getWord();
            Translate translate = wordFillIn.getTranslate();

            Matcher matcher = pattern.matcher(wordStr);
            if (matcher.find()) {

                wfi_v_line.setVisibility(View.VISIBLE);
            } else {

                wfi_v_line.setVisibility(View.INVISIBLE);
            }

            if (translate != null) {//有选择的单词

                wfi_tv_word.setVisibility(View.VISIBLE);
                wfi_tv_word.setText(translate.getData());
                wfi_tv_word.setBackgroundResource(R.drawable.shape_rctg_bg_word_fill_in);
            } else {

                Matcher matcher2 = pattern.matcher(wordStr);
                if (matcher2.find()) {

                    wfi_tv_word.setVisibility(View.INVISIBLE);
                } else {

                    wfi_tv_word.setVisibility(View.VISIBLE);
                }
                wfi_tv_word.setText(wordStr);
                wfi_tv_word.setBackgroundColor(Color.WHITE);
            }
        }
    }


    public interface Callback {

        void click(WordFillIn wordFillIn, Translate translate);
    }

}
