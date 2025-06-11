package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;

import java.util.List;

/**
 * 关卡
 */
public class HomePointAdapter extends RecyclerView.Adapter<HomePointAdapter.PointViewHolder> {

    private List<HomeTestTitleBean.Unity.Level> levelList;

    private HomeAdapter.Callback callback;
    /**
     * 0 向右偏
     * 1 向左偏
     */
    private int flag = 0;

    private int biasPos = 0;

    //记录下当前的单元id(用于回调接口使用处理，判断宝箱是否可以开启)
    private int unitIndex = 0;

    private float[] biasList = new float[]{0.5f, 0.7f, 0.9f, 0.7f, 0.5f, 0.3f, 0.1f, 0.3f};

    public HomeAdapter.Callback getCallback() {
        return callback;
    }

    public void setCallback(HomeAdapter.Callback callback) {
        this.callback = callback;
    }

    public HomePointAdapter(List<HomeTestTitleBean.Unity.Level> levelList, int flag, int biasPos,int unitIndex) {
        this.levelList = levelList;
        this.flag = flag;
        this.biasPos = biasPos;
        this.unitIndex = unitIndex;

        if (flag == 0) {
            for (int i = 0; i < levelList.size(); i++) {
                HomeTestTitleBean.Unity.Level level = levelList.get(i);
                if (i < 5) {
                    int pos = (biasPos + i) % biasList.length;
                    level.setBias(biasList[pos]);
                } else {
                    level.setBias(0.5f);
                }
            }
        } else {//向左
            if (biasPos == 0) {
                biasPos = 4;
            }
            for (int i = 0; i < levelList.size(); i++) {
                HomeTestTitleBean.Unity.Level level = levelList.get(i);
                if (i < 5) {
                    int pos = (biasPos + i) % biasList.length;
                    level.setBias(biasList[pos]);
                } else {
                    level.setBias(0.5f);
                }
            }
        }
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_home_title, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        HomeTestTitleBean.Unity.Level level = levelList.get(position);
        holder.setData(level);
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }


    public class PointViewHolder extends RecyclerView.ViewHolder {

        ImageView ht_iv_point;
        HomeTestTitleBean.Unity.Level level;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);

            ht_iv_point = itemView.findViewById(R.id.ht_iv_point);
            ht_iv_point.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.getLevel(ht_iv_point, level,unitIndex);
                    }
                }
            });
        }
        public void setData(HomeTestTitleBean.Unity.Level level) {

            this.level = level;

            if (level.getType().equals("lesson")) {

                if (level.isUnlock() || level.getIsPass() == 1) {

                    ht_iv_point.setImageResource(R.drawable.selete_pass);
                } else {

                    ht_iv_point.setImageResource(R.drawable.selete_start);
                }
            } else if (level.getType().equals("box")) {


                if (level.getIsPass() == 1) {
                    ht_iv_point.setImageResource(R.mipmap.icon_box_open);
                } else {
                    boolean isFinish5 = true;
                    for (int i = 0; i < levelList.size(); i++) {

                        HomeTestTitleBean.Unity.Level level1 = levelList.get(i);
                        if (level1.getIsPass() == 0 && level1.getType().equals("lesson")) {

                            isFinish5 = false;
                            break;
                        }
                    }
                    if (isFinish5) {
                        ht_iv_point.setImageResource(R.mipmap.icon_box_gold);
                    } else {
                        ht_iv_point.setImageResource(R.mipmap.icon_box_gray);
                    }
                }
            }


            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ht_iv_point.getLayoutParams();
            layoutParams.horizontalBias = level.getBias();
           /* int p = getAdapterPosition();
            float bias;
            if (flag == 0) {//向右偏

                if (p < 3) {

                    bias = (float) (0.5 + p * 0.2); //0.5 0.7 0.9
                } else {

                    bias = (float) (0.5 + (4 - p) * 0.2);
                }
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ht_iv_point.getLayoutParams();
                layoutParams.horizontalBias = bias;
            } else {//向左偏

                if (p < 3) {

                    bias = (float) (0.5 - p * 0.2);
                } else {
                    bias = (float) (0.5 - (4 - p) * 0.2);
                }
                if (bias > 0.5) {

                    bias = 0.5f;
                }
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ht_iv_point.getLayoutParams();
                layoutParams.horizontalBias = bias;
            }*/
        }
    }
}