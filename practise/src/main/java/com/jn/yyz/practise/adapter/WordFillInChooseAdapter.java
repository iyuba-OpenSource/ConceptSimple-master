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

import java.util.List;

public class WordFillInChooseAdapter extends RecyclerView.Adapter<WordFillInChooseAdapter.WordFIChViewHolder> {

    private List<Translate> translateList;

    private boolean isChoose = true;


    private ClickCallback clickCallback;

    public WordFillInChooseAdapter(List<Translate> translateList) {

        this.translateList = translateList;
    }

    @NonNull
    @Override
    public WordFIChViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_word_fill_in, parent, false);
        return new WordFIChViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordFIChViewHolder holder, int position) {

        Translate translate = translateList.get(position);
        holder.setData(translate);
    }

    @Override
    public int getItemCount() {
        return translateList.size();
    }


    public List<Translate> getTranslateList() {
        return translateList;
    }

    public void setTranslateList(List<Translate> translateList) {
        this.translateList = translateList;
    }

    public void add(Translate translate) {

        translateList.add(translate);
        notifyDataSetChanged();
    }

    public void add2(Translate translate) {

        translateList.add(translate);
    }

    public ClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public class WordFIChViewHolder extends RecyclerView.ViewHolder {

        TextView wfi_tv_word;

        Translate translate;

        View wfi_v_line;

        public WordFIChViewHolder(@NonNull View itemView) {
            super(itemView);

            wfi_tv_word = itemView.findViewById(R.id.wfi_tv_word);
            wfi_v_line = itemView.findViewById(R.id.wfi_v_line);
            wfi_v_line.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isChoose) {
                        if (!translate.isCheck()) {

                            if (clickCallback != null) {

                                clickCallback.click(translate);
                            }
                        }
                    }
                }
            });
        }

        public Translate getTranslate() {
            return translate;
        }

        public void setTranslate(Translate translate) {
            this.translate = translate;
        }

        private void setData(Translate translate) {

            this.translate = translate;
            wfi_tv_word.setText(translate.getData());
            if (translate.isCheck()) {

                wfi_tv_word.setVisibility(View.INVISIBLE);
                wfi_tv_word.setBackgroundColor(Color.WHITE);
            } else {

                wfi_tv_word.setVisibility(View.VISIBLE);
                wfi_tv_word.setBackgroundResource(R.drawable.shape_rctg_bg_word_fill_in);
            }
        }
    }


    public interface ClickCallback {

        void click(Translate translate);
    }
}
