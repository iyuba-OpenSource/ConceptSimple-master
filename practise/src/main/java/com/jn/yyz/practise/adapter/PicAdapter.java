package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jn.yyz.practise.R;

import java.util.List;

/**
 * 图片选择
 */
public class PicAdapter extends RecyclerView.Adapter<PicAdapter.PicViewHolder> {


    private List<String> stringLis;

    private int pos = -1;

    private ClickCallback clickCallback;

    private boolean isCheck = true;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public ClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public List<String> getStringLis() {
        return stringLis;
    }

    public void setStringLis(List<String> stringLis) {
        this.stringLis = stringLis;
    }

    public PicAdapter(List<String> stringLis) {
        this.stringLis = stringLis;
    }

    @NonNull
    @Override
    public PicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_pic, parent, false);
        return new PicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PicViewHolder holder, int position) {

        String string = stringLis.get(position);
        holder.setData(string);
    }

    @Override
    public int getItemCount() {
        return stringLis.size();
    }

    public class PicViewHolder extends RecyclerView.ViewHolder {

        String data;

        ImageView pic_iv_img;

        LinearLayout pic_ll_img;

        public PicViewHolder(@NonNull View itemView) {
            super(itemView);
            pic_iv_img = itemView.findViewById(R.id.pic_iv_img);
            pic_ll_img = itemView.findViewById(R.id.pic_ll_img);
            pic_ll_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isCheck) {

                        pos = getAdapterPosition();
                        if (clickCallback != null) {

                            clickCallback.click(data);
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        }

        public void setData(String data) {

            this.data = data;
            Glide.with(pic_iv_img.getContext()).load(data).into(pic_iv_img);
            if (pos == getAdapterPosition()) {

                pic_ll_img.setBackgroundResource(R.drawable.shape_pic_blue);
            } else {

                pic_ll_img.setBackgroundResource(R.drawable.shape_pic_gray);
            }
        }
    }


    public interface ClickCallback {

        void click(String data);
    }
}
