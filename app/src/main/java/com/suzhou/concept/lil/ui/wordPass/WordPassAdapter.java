package com.suzhou.concept.lil.ui.wordPass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.activity.dollar.MemberCentreActivity;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.ItemWordBinding;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.word.pass.bean.WordPassBean;
import com.suzhou.concept.lil.listener.OnSimpleClickListener;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.List;

public class WordPassAdapter extends RecyclerView.Adapter<WordPassAdapter.PassHolder> {

    private Context context;
    private List<WordPassBean> list;

    public WordPassAdapter(Context context, List<WordPassBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordBinding binding = ItemWordBinding.inflate(LayoutInflater.from(context),parent,false);
        return new PassHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PassHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordPassBean bean = list.get(position);
        //显示单元
        int showIndex = bean.getId();
        showIndex = showIndex%1000;

        String showTitle = "";
        if (bean.getType().equals(TypeLibrary.BookType.conceptFour)){
            showTitle = "Lesson "+showIndex;
        }else if (bean.getType().equals(TypeLibrary.BookType.conceptJunior)){
            showTitle = "Unit "+showIndex;
        }
        if (showIndex>100){
            holder.unitView.setTextSize(13);
            holder.progressView.setTextSize(13);
        }else if (showIndex>10){
            holder.unitView.setTextSize(14);
            holder.progressView.setTextSize(14);
        }else {
            holder.unitView.setTextSize(15);
            holder.progressView.setTextSize(15);
        }

        holder.unitView.setText(showTitle);
        //显示进度数据
        String showPass = bean.getRightCount()+"/"+ bean.getTotalCount();
        holder.progressView.setText(showPass);

        //进度如果达到80%，则显示不同的颜色
        if (bean.getPassState() == 0){
            holder.layoutView.setBackgroundResource(R.drawable.shape_corner_gray_10dp);
        }else {
            holder.layoutView.setBackgroundResource(R.drawable.shape_corner_theme_10dp);
        }

        //单独处理下判断(上一个如果是通过并且当前这个不通过，则同样显示)
        if (position!=0 && bean.getPassState()==0 && list.get(position-1).getPassState()==1){
            holder.layoutView.setBackgroundResource(R.drawable.shape_corner_theme_10dp);
        }

        holder.itemView.setOnClickListener(v->{
            //登录判断
            if (!GlobalMemory.INSTANCE.isLogin()){
                context.startActivity(new Intent(context, LoginActivity.class));
                return;
            }

            if (position>0){
                //判断前一个是否通关
                WordPassBean preBean = list.get(position-1);
                if (preBean.getPassState() == 0){
                    ToastUtil.showToast(context,"通关上个单元后解锁此单元内容");
                    return;
                }

                //第一个免费，后面的收费
                if (!GlobalMemory.INSTANCE.getUserInfo().isVip()){
                    new AlertDialog.Builder(context)
                            .setTitle("购买会员")
                            .setMessage("单词闯关的首关免费体验，其他关卡需要开通会员使用，是否开通会员?")
                            .setCancelable(false)
                            .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(new Intent(context, MemberCentreActivity.class));
                                }
                            }).setNegativeButton("暂不使用",null)
                            .create().show();
                    return;
                }
            }

            if (onItemClickListener!=null){
                onItemClickListener.onClick(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class PassHolder extends RecyclerView.ViewHolder{

        private View layoutView;
        private TextView unitView;
        private TextView progressView;

        public PassHolder(ItemWordBinding binding){
            super(binding.getRoot());

            layoutView = binding.layoutView;
            unitView = binding.unitView;
            progressView = binding.progressView;
        }
    }

    //刷新数据
    public void refreshData(List<WordPassBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //设置接口回调
    public OnSimpleClickListener<WordPassBean> onItemClickListener;

    public void setOnItemClickListener(OnSimpleClickListener<WordPassBean> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
