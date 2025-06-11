package com.jn.yyz.practise.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.entity.ListShowBean;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    private Context context;
    private List<ListShowBean> list;

    public ListAdapter(Context context, List<ListShowBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        if (holder==null){
            return;
        }

        ListShowBean showBean = list.get(position);
        holder.index.setText("Lesson "+showBean.getUnit());
        holder.show.setText(showBean.getTitle());

        if (showBean.getType().equals("lesson")){
            holder.show.setTextColor(Color.BLACK);

            if (showBean.isPass()){
                holder.pic.setImageResource(R.mipmap.icon_point_enable);
            }else {
                //根据上一个判断
                if (position>0){
                    boolean isPrePass = list.get(position-1).isPass();
                    if (isPrePass){
                        holder.pic.setImageResource(R.mipmap.icon_point_enable);
                    }else {
                        holder.pic.setImageResource(R.mipmap.icon_point);
                    }
                }else {
                    holder.pic.setImageResource(R.mipmap.icon_point_enable);
                }
            }
        }else if (showBean.getType().equals("box")){
            if (showBean.isPass()){
                holder.show.setText("单元"+showBean.getUnit()+"已开启宝箱");
                holder.show.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                holder.pic.setImageResource(R.mipmap.icon_box_open);
            }else {
                holder.show.setText("单元"+showBean.getUnit()+"未开启宝箱");
                holder.show.setTextColor(context.getResources().getColor(android.R.color.darker_gray));

                //根据上一个判断
                if (position>0){
                    boolean isPrePass = list.get(position-1).isPass();
                    if (isPrePass){
                        holder.pic.setImageResource(R.mipmap.icon_box_gold);
                    }else {
                        holder.pic.setImageResource(R.mipmap.icon_box_gray);
                    }
                }else {
                    holder.pic.setImageResource(R.mipmap.icon_box_gold);
                }
            }
        }

        holder.itemView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                if (showBean.isPass()){
                    onItemClickListener.onClick(true,showBean);
                }else {
                    //判断上一个数据
                    if (position>0){
                        boolean isPrePass = list.get(position-1).isPass();
                        if (isPrePass){
                            onItemClickListener.onClick(true,showBean);
                        }else {
                            onItemClickListener.onClick(false,showBean);
                        }
                    }else {
                        onItemClickListener.onClick(true,showBean);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ListHolder extends RecyclerView.ViewHolder{

        private ImageView pic;

        private TextView index;
        private TextView show;

        public ListHolder(View view){
            super(view);

            pic = view.findViewById(R.id.passPic);
            index = view.findViewById(R.id.unitIndex);
            show = view.findViewById(R.id.showTitle);
        }
    }

    //刷新数据
    public void refreshData(List<ListShowBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //获取数据
    public List<ListShowBean> getShowList(){
        return list;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(boolean isCanExercise,ListShowBean showBean);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
