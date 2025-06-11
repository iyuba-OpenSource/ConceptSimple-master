package com.jn.yyz.practise.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.model.bean.ExamBean;

import java.util.List;

/**
 * 加载更多
 */
public class WrongBook2Adapter extends LoadMoreAdapter<ExamBean.DataDTO> {

    public WrongBook2Adapter(List<ExamBean.DataDTO> dataList, int mLayoutId) {
        super(dataList, mLayoutId);
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, ExamBean.DataDTO dataDTO) {

        View holderView = holder.itemView;
        ImageView wrong_iv_pic = holderView.findViewById(R.id.wrong_iv_pic);
        TextView wrong_tv_quest = holderView.findViewById(R.id.wrong_tv_quest);
        TextView wrong_tv_content = holderView.findViewById(R.id.wrong_tv_content);

        setData(dataDTO, wrong_tv_quest, wrong_tv_content);
    }


    private void setData(ExamBean.DataDTO dataDTO, TextView wrong_tv_quest, TextView wrong_tv_content) {

        if (dataDTO.getTestType() == 201) {

            wrong_tv_quest.setText("选择正确的翻译");
        } else if (dataDTO.getTestType() == 202) {

            wrong_tv_quest.setText("选择对应的图片");
        } else if (dataDTO.getTestType() == 203) {

            wrong_tv_quest.setText("翻译这句话");
        } else if (dataDTO.getTestType() == 204) {

            wrong_tv_quest.setText("选择听到的内容");
        } else if (dataDTO.getTestType() == 205) {

            wrong_tv_quest.setText("选择配对");
        } else if (dataDTO.getTestType() == 206) {

            wrong_tv_quest.setText("选择配对");
        } else if (dataDTO.getTestType() == 207) {

            wrong_tv_quest.setText("完成翻译");
        } else if (dataDTO.getTestType() == 208) {

            wrong_tv_quest.setText("选择音频填空");
        } else if (dataDTO.getTestType() == 209) {

            wrong_tv_quest.setText("朗读下面的句子");
        } else if (dataDTO.getTestType() == 210) {

            wrong_tv_quest.setText("翻译单词");
        } else if (dataDTO.getTestType() == 211) {

            wrong_tv_quest.setText("选词填空");
        } else if (dataDTO.getTestType() == 212) {

            wrong_tv_quest.setText("输入所缺单词");
        } else if (dataDTO.getTestType() == 213) {

            wrong_tv_quest.setText("选择听到的内容");
        } else if (dataDTO.getTestType() == 214) {

            wrong_tv_quest.setText("选择听到的内容");
        } else if (dataDTO.getTestType() == 215) {

            wrong_tv_quest.setText("选择听到的内容");
        }

        wrong_tv_content.setText(dataDTO.getQuestion());
    }
}
