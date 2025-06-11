package com.jn.yyz.practise.adapter;

import static androidx.recyclerview.widget.RecyclerView.Adapter;
import static androidx.recyclerview.widget.RecyclerView.GONE;
import static androidx.recyclerview.widget.RecyclerView.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;

import java.util.List;

public class HomeAdapter extends Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeTestTitleBean.Unity> unityList;

    private Callback callback;


    public List<HomeTestTitleBean.Unity> getUnityList() {
        return unityList;
    }

    public void setUnityList(List<HomeTestTitleBean.Unity> unityList) {
        this.unityList = unityList;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public HomeAdapter(List<HomeTestTitleBean.Unity> unityList) {
        this.unityList = unityList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeTestTitleBean.Unity unity = unityList.get(position);
        holder.setData(unity);
    }

    @Override
    public int getItemCount() {
        return unityList.size();
    }

    public class HomeViewHolder extends ViewHolder {

        HomeTestTitleBean.Unity unity;

        TextView home_tv_title;

        ImageView home_iv_point;

        RecyclerView home_rv_point;

        LinearLayout home_ll_title;

        HomePointAdapter homePointAdapter;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            home_tv_title = itemView.findViewById(R.id.home_tv_title);
            home_iv_point = itemView.findViewById(R.id.home_iv_point);
            home_rv_point = itemView.findViewById(R.id.home_rv_point);
            home_ll_title = itemView.findViewById(R.id.home_ll_title);
        }

        public void setData(HomeTestTitleBean.Unity unity) {

            this.unity = unity;
            if (unity.isShowTitle()) {

                home_ll_title.setVisibility(VISIBLE);
                home_tv_title.setText(unity.getDesc());
            } else {

                home_ll_title.setVisibility(GONE);
                home_tv_title.setText(unity.getDesc());
            }

            int mod = getAdapterPosition() % 2;

            homePointAdapter = new HomePointAdapter(unity.getData(), mod, unity.getBiasPos(),unity.getUnit());
            home_rv_point.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            home_rv_point.setAdapter(homePointAdapter);
            if (callback != null) {
                homePointAdapter.setCallback(callback);
            }

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) home_iv_point.getLayoutParams();
            if (mod == 0) {
                layoutParams.horizontalBias = 0.1f;
            } else {
                layoutParams.horizontalBias = 0.9f;
            }
        }
    }

    public interface Callback {
        void getLevel(View itemView, HomeTestTitleBean.Unity.Level level,int unitIndex);
    }
}
