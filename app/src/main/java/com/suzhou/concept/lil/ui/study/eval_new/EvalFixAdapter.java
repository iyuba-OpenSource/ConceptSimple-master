package com.suzhou.concept.lil.ui.study.eval_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemEvalNewBinding;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceTransBean;
import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
import com.suzhou.concept.utils.GlobalMemory;
import com.suzhou.concept.utils.view.RoundProgressBar;

import java.util.List;

public class EvalFixAdapter extends RecyclerView.Adapter<EvalFixAdapter.EvalFixHolder> {

    private Context context;
    private List<SentenceTransBean> list;

    //选中的数据
    private int selectIndex = 0;
    private EvalFixHolder selectHolder;
    private EvalShowBean selectEvalBean;
    private SentenceTransBean selectShowBean;

    public EvalFixAdapter(Context context, List<SentenceTransBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EvalFixHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEvalNewBinding binding = ItemEvalNewBinding.inflate(LayoutInflater.from(context),parent,false);
        return new EvalFixHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EvalFixHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        //显示数据
        SentenceTransBean transBean = list.get(position);
        holder.indexView.setText(transBean.getIdIndex());
        holder.sentenceView.setText(transBean.getSentence());
        holder.sentenceCnView.setText(transBean.getSentenceCn());
        holder.playView.setVisibility(View.VISIBLE);
        holder.recordView.setVisibility(View.VISIBLE);

        //显示评测数据
        EvalShowBean showBean = HelpUtil.transEvalShowData(RoomDBManager.getInstance().getSingleEval(GlobalMemory.INSTANCE.getUserInfo().getUid(),transBean.getVoaId(),transBean.getIdIndex(),transBean.getParaId()));
        if (showBean!=null){
            holder.evalView.setVisibility(View.VISIBLE);
            holder.publishView.setVisibility(View.VISIBLE);
            holder.checkView.setVisibility(View.VISIBLE);
            holder.scoreView.setVisibility(View.VISIBLE);

            //显示分数
            holder.scoreView.setText(String.valueOf((int) (showBean.getTotal_score()*20)));
            //显示文本
            holder.sentenceView.setText(HelpUtil.getSentenceSpan(showBean));
        }else {
            holder.evalView.setVisibility(View.INVISIBLE);
            holder.publishView.setVisibility(View.INVISIBLE);
            holder.checkView.setVisibility(View.INVISIBLE);
            holder.scoreView.setVisibility(View.GONE);
        }

        //当前数据
        if (selectIndex == position){
            selectIndex = position;
            selectHolder = holder;
            selectShowBean = transBean;
            selectEvalBean = showBean;

            holder.bottomLayout.setVisibility(View.VISIBLE);
        }else {
            holder.bottomLayout.setVisibility(View.GONE);
        }

        //其他操作
        holder.itemView.setOnClickListener(v->{
            //切换item

            if (selectIndex == position){
                return;
            }

            if (onEvalClickListener!=null){
                onEvalClickListener.onItemClick(position);
            }
        });
        holder.playView.setOnClickListener(v->{
            //播放原音
            long startTime = (long) (transBean.getTiming()*1000L);
            long endTime = (long) (transBean.getEndTiming()*1000L);

            if (onEvalClickListener!=null){
                onEvalClickListener.onAudioPlay(startTime,endTime);
            }
        });
        holder.recordView.setOnClickListener(v->{
            //录音评测
            long startTime = (long) (transBean.getTiming()*1000L);
            long endTime = (long) (transBean.getEndTiming()*1000L);
            long recordTime = endTime - startTime;

            if (onEvalClickListener!=null){
                onEvalClickListener.onRecord(recordTime,transBean.getVoaId(),transBean.getParaId(),transBean.getIdIndex(),transBean.getSentence());
            }
        });
        holder.evalView.setOnClickListener(v->{
            //播放评测
            if (onEvalClickListener!=null){
                onEvalClickListener.onEvalPlay(showBean.getUrl(),transBean.getVoaId(),transBean.getParaId(),transBean.getIdIndex());
            }
        });
        holder.publishView.setOnClickListener(v->{
            //发布评测
            int showScore = (int) (showBean.getTotal_score()*20);

            if (onEvalClickListener!=null){
                onEvalClickListener.onPublish(transBean.getVoaId(),transBean.getParaId(),transBean.getIdIndex(),showScore,showBean.getUrl());
            }
        });
        holder.checkView.setOnClickListener(v->{
            //纠音
            if (onEvalClickListener!=null){
                onEvalClickListener.onCheckEval(showBean,transBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class EvalFixHolder extends RecyclerView.ViewHolder{

        private TextView indexView;
        private TextView sentenceView;
        private TextView sentenceCnView;
        private RoundProgressBar playView;
        private ImageView recordView;
        private RoundProgressBar evalView;
        private ImageView publishView;
        private TextView scoreView;
        private TextView checkView;

        private LinearLayout bottomLayout;

        public EvalFixHolder(ItemEvalNewBinding binding){
            super(binding.getRoot());

            indexView = binding.indexView;
            sentenceView = binding.sentenceItem;
            sentenceCnView = binding.sentenceCn;
            playView = binding.controlProgress;
            recordView = binding.mike;
            evalView = binding.controlSelf;
            publishView = binding.releaseItem;
            scoreView = binding.score;
            checkView = binding.correctSound;

            bottomLayout = binding.bottomLayout;
        }
    }

    /*****************************************当前数据************************************/
    //当前的展示数据
    public SentenceTransBean getShowData(){
        return selectShowBean;
    }

    /******************************************刷新数据**********************************/
    //刷新数据
    public void refreshData(List<SentenceTransBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新item
    public void refreshShowItem(int position){
        this.selectIndex = position;
        notifyDataSetChanged();
    }

    //刷新数据显示
    public void refreshDataShow(){
        notifyDataSetChanged();
    }

    //刷新播放进度操作
    public void refreshAudioPlay(boolean isPlay,long progressTime,long totalTime){
        if (isPlay){
            selectHolder.playView.setBackgroundResource(R.drawable.pause_evaluation_old);
        }else {
            selectHolder.playView.setBackgroundResource(R.drawable.play_evaluation_old);
        }
    }

    //刷新录音操作
    public void refreshRecord(boolean isRecording){
        if (isRecording){
            selectHolder.recordView.setImageResource(R.drawable.mike_red);
        }else {
            selectHolder.recordView.setImageResource(R.drawable.mike_grey);
        }
    }

    //刷新评测播放操作
    public void refreshEvalPlay(boolean isPlay,long progressTime,long totalTime){
        if (isPlay){
            selectHolder.evalView.setBackgroundResource(R.drawable.pause_evaluation_old);
        }else {
            selectHolder.evalView.setBackgroundResource(R.drawable.play_evaluation_old);
        }
    }

    /*********************************************回调接口**********************************/
    private OnEvalClickListener onEvalClickListener;

    public interface OnEvalClickListener{
        //item点击
        void onItemClick(int position);

        //播放点击
        void onAudioPlay(long startTime,long endTime);

        //录音点击
        void onRecord(long recordTime,String voaId,String paraId,String indexId,String sentence);

        //评测播放
        void onEvalPlay(String playUrl,String voaId,String paraId,String indexId);

        //发布点击
        void onPublish(String voaId,String paraId,String indexId,int totalScore,String evalUrl);

        //纠音点击
        void onCheckEval(EvalShowBean showBean,SentenceTransBean transBean);
    }

    public void setOnEvalClickListener(OnEvalClickListener onEvalClickListener) {
        this.onEvalClickListener = onEvalClickListener;
    }
}
