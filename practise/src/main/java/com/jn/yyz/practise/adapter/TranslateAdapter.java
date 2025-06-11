package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.entity.Translate;

import java.util.List;

public class TranslateAdapter extends RecyclerView.Adapter<TranslateAdapter.TranslateViewHolder> {

    private List<Translate> translateList;

    private boolean isChoose = true;

    /**
     * 1:点击消失的类型
     * 2：点击内容消息，背景不消失
     */
    private int flag = 1;

    private ClickCallback clickCallback;

    public TranslateAdapter(List<Translate> translateList, int flag) {
        this.translateList = translateList;
        this.flag = flag;
    }

    @NonNull
    @Override
    public TranslateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_translate, parent, false);
        return new TranslateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TranslateViewHolder holder, int position) {

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

    public class TranslateViewHolder extends RecyclerView.ViewHolder {

        public TextView translate_tv_content;

        public Translate translate;

        public FrameLayout translate_fl_content;

        public FrameLayout translate_fl_all;

        //bg
        public FrameLayout translate_fl_bg;
        public TextView translate_tv_bg;


        public TranslateViewHolder(@NonNull View itemView) {
            super(itemView);
            translate_tv_content = itemView.findViewById(R.id.translate_tv_content);
            translate_fl_content = itemView.findViewById(R.id.translate_fl_content);
            translate_fl_all = itemView.findViewById(R.id.translate_fl_all);
            //bg
            translate_fl_bg = itemView.findViewById(R.id.translate_fl_bg);
            translate_tv_bg = itemView.findViewById(R.id.translate_tv_bg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isChoose) {

                        if (flag == 1) {

                            if (clickCallback != null) {

                                clickCallback.clickRemove(translate);
                            }
                        } else {

                            if (!translate.isCheck()) {//没有选中

                                translate_fl_bg.setVisibility(View.VISIBLE);
                                translate_fl_all.setVisibility(View.VISIBLE);
                                if (clickCallback != null) {

                                    clickCallback.click(translate);
                                }
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
            translate_tv_content.setText(translate.getData());
            translate_tv_bg.setText(translate.getData());
            if (flag == 1) {

                translate_tv_content.setVisibility(View.VISIBLE);
            } else {

                if (translate.isCheck()) {

                    translate_fl_all.setVisibility(View.GONE);
                    translate_fl_bg.setVisibility(View.VISIBLE);
                } else {

                    translate_fl_all.setVisibility(View.VISIBLE);
                    translate_fl_bg.setVisibility(View.GONE);
                }
            }
        }
    }


    public interface ClickCallback {

        void click(Translate translate);

        void clickRemove(Translate translate);
    }
}
