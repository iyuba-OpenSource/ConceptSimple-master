package com.jn.yyz.practise.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;

import java.util.List;

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ChooseViewHolder> {

    private List<String> chooseList;

    private int pos = -1;

    private ClickCallback clickCallback;

    /**
     * 是否可选
     */
    private boolean isChoose = true;


    public ChooseAdapter(List<String> chooseList) {
        this.chooseList = chooseList;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public List<String> getChooseList() {
        return chooseList;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @NonNull
    @Override
    public ChooseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_choose, parent, false);
        return new ChooseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseViewHolder holder, int position) {

        String date = chooseList.get(position);
        holder.setData(date);
    }

    @Override
    public int getItemCount() {
        return chooseList.size();
    }

    public ClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public class ChooseViewHolder extends RecyclerView.ViewHolder {

        private TextView choose_tv_c;
        private String data;

        public ChooseViewHolder(@NonNull View itemView) {
            super(itemView);

            choose_tv_c = itemView.findViewById(R.id.choose_tv_c);
            choose_tv_c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isChoose) {

                        pos = getAdapterPosition();
                        notifyDataSetChanged();
                        clickCallback.click(data);
                    }
                }
            });
        }

        public void setData(String data) {

            this.data = data;
            choose_tv_c.setText(data);

            if (pos == getAdapterPosition()) {

                choose_tv_c.setBackgroundResource(R.drawable.shape_choose_check);
                choose_tv_c.setTextColor(Color.WHITE);
            } else {

                choose_tv_c.setBackgroundResource(R.drawable.shape_choose_uncheck);
                choose_tv_c.setTextColor(Color.BLACK);
            }
        }
    }


    public interface ClickCallback {


        void click(String data);
    }
}
