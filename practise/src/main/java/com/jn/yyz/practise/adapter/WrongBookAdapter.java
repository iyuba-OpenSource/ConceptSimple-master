package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.model.bean.ExamBean;

import java.util.List;

public class WrongBookAdapter extends RecyclerView.Adapter<WrongBookAdapter.WrongBookViewHolder> {

    private List<ExamBean.DataDTO> dataDTOList;


    public List<ExamBean.DataDTO> getDataDTOList() {
        return dataDTOList;
    }

    public void setDataDTOList(List<ExamBean.DataDTO> dataDTOList) {
        this.dataDTOList = dataDTOList;
    }

    public WrongBookAdapter(List<ExamBean.DataDTO> dataDTOList) {
        this.dataDTOList = dataDTOList;
    }

    @NonNull
    @Override
    public WrongBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_wrong, parent, false);
        return new WrongBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WrongBookViewHolder holder, int position) {

        ExamBean.DataDTO dataDTO = dataDTOList.get(position);
        holder.setData(dataDTO);
    }

    @Override
    public int getItemCount() {
        return dataDTOList.size();
    }

    public class WrongBookViewHolder extends RecyclerView.ViewHolder {

        ImageView wrong_iv_pic;
        TextView wrong_tv_quest;
        TextView wrong_tv_content;

        public WrongBookViewHolder(@NonNull View itemView) {
            super(itemView);
            wrong_iv_pic = itemView.findViewById(R.id.wrong_iv_pic);
            wrong_tv_quest = itemView.findViewById(R.id.wrong_tv_quest);
            wrong_tv_content = itemView.findViewById(R.id.wrong_tv_content);
        }


        private void setData(ExamBean.DataDTO dataDTO) {

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
}
