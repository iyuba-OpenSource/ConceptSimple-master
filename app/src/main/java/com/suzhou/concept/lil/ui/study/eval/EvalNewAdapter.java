//package com.suzhou.concept.lil.ui.study.eval;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Pair;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.efs.sdk.base.core.util.NetworkUtil;
//import com.suzhou.concept.R;
//import com.suzhou.concept.activity.dollar.MemberCentreActivity;
//import com.suzhou.concept.activity.user.LoginActivity;
//import com.suzhou.concept.lil.data.newDB.RoomDBManager;
//import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
//import com.suzhou.concept.lil.event.LocalEvalDataRefreshEvent;
//import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
//import com.suzhou.concept.lil.ui.study.eval.bean.SentenceTransBean;
//import com.suzhou.concept.lil.ui.study.eval.util.FileManager;
//import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
//import com.suzhou.concept.lil.ui.study.eval.util.RecordManager;
//import com.suzhou.concept.lil.ui.study.eval.util.RxTimer;
//import com.suzhou.concept.lil.util.PermissionDialogUtil;
//import com.suzhou.concept.utils.GlobalMemory;
//import com.suzhou.concept.utils.view.RoundProgressBar;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import personal.iyuba.personalhomelibrary.utils.ToastFactory;
//
//public class EvalNewAdapter extends RecyclerView.Adapter<EvalNewAdapter.EvalNewHolder> {
//
//    private Context context;
//    private List<SentenceTransBean> list;
//
//    private int selectPosition = 0;//选中的位置
//    private SentenceTransBean selectSentence;//选中的句子
//    private EvalNewHolder selectHolder;//选中的布局
//    private EvalShowBean selectBean;//选中的评测数据
//
//    //原文播放器
//    private MediaPlayer readPlayer;
//    //录音器
//    private RecordManager recordManager;
//    //评测播放器
//    private MediaPlayer evalPlayer;
//
//    //是否正在录音
//    private boolean isEval = false;
//    //是否正在评测
//    private boolean isRecord = false;
//    //原文是否加载完成
//    private boolean isPrepare = false;
//
//    public EvalNewAdapter(Context context, List<SentenceTransBean> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    public void setPlayer(MediaPlayer sentencePlayer,MediaPlayer evalPlayer){
//        this.readPlayer = sentencePlayer;
//        this.evalPlayer = evalPlayer;
//    }
//
//    @NonNull
//    @Override
//    public EvalNewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_eval_new,parent,false);
//        return new EvalNewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EvalNewHolder holder, @SuppressLint("RecyclerView") int position) {
//        if (holder==null){
//            return;
//        }
//
//        //句子数据
//        SentenceTransBean bean = list.get(position);
//        holder.index.setText(String.valueOf(position+1));
//        holder.sentence.setText(bean.getSentence());
//        holder.sentenceCn.setText(bean.getSentenceCn());
//
//        //评测数据
//        EvalShowBean showBean = HelpUtil.transEvalShowData(RoomDBManager.getInstance().getSingleEval(GlobalMemory.INSTANCE.getUserInfo().getUid(),bean.getVoaId(),bean.getIdIndex(),bean.getParaId()));
//        if (showBean!=null){
//            holder.share.setVisibility(View.VISIBLE);
//            holder.checkEval.setVisibility(View.VISIBLE);
//            holder.eval.setVisibility(View.VISIBLE);
//            holder.score.setVisibility(View.VISIBLE);
//
//            holder.sentence.setText(HelpUtil.getSentenceSpan(showBean));
//            holder.score.setText(String.valueOf((int) (showBean.getTotal_score()*20)));
//        }else {
//            holder.eval.setVisibility(View.INVISIBLE);
//            holder.share.setVisibility(View.INVISIBLE);
//            holder.checkEval.setVisibility(View.INVISIBLE);
//            holder.score.setVisibility(View.INVISIBLE);
//        }
//
//        if (selectPosition == position){
//            holder.bottomLayout.setVisibility(View.VISIBLE);
//
//            selectHolder = holder;
//            selectSentence = bean;
//            selectBean = showBean;
//        }else {
//            holder.bottomLayout.setVisibility(View.GONE);
//        }
//
//        holder.itemView.setOnClickListener(v->{
//            if (isRecord) {
//                ToastFactory.showShort(context, "正在录音中～");
//                return;
//            }
//
//            if (!isEval()) {
//                return;
//            }
//
//            if (onCallBackListener != null) {
//                onCallBackListener.onWarn();
//            }
//
//            if (selectPosition!=position){
//                selectPosition = position;
//                notifyDataSetChanged();
//            }
//        });
//        holder.play.setOnClickListener(v->{
//            //原音播放
//            if (!isPlay()) {
//                return;
//            }
//
//            if (evalPlayer != null && evalPlayer.isPlaying()) {
//                evalPause();
//            }
//
//            if (onCallBackListener != null) {
//                onCallBackListener.onWarn();
//            }
//
//            if (readPlayer != null) {
//                if (readPlayer.isPlaying()) {
//                    handler.sendEmptyMessage(PLAY_FINISH);
//                } else {
//                    readPlay();
//                }
//            }
//        });
//        holder.record.setOnClickListener(v->{
//            if (!GlobalMemory.INSTANCE.isLogin()) {
//                context.startActivity(new Intent(context,LoginActivity.class));
//                ToastFactory.showShort(context, "请先登录");
//                return;
//            }
//
//            //数据库中评测的数量
//            int evalSize = RoomDBManager.getInstance().getEvalResultData(GlobalMemory.INSTANCE.getUserInfo().getUid(), String.valueOf(HelpUtil.getVoaId())).size();
//            //当前是否已经评测
//            EvalResultBean resultBean = RoomDBManager.getInstance().getSingleEval(GlobalMemory.INSTANCE.getUserInfo().getUid(), bean.getVoaId(), bean.getIdIndex(), bean.getParaId());
//            if (!GlobalMemory.INSTANCE.getUserInfo().isVip() && evalSize>=3 && resultBean==null) {
//                new AlertDialog.Builder(context)
//                        .setTitle("评测限制")
//                        .setMessage("普通用户至多评测三句，会员无限制，是否开通会员？")
//                        .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                context.startActivity(new Intent(context, MemberCentreActivity.class));
//                            }
//                        }).setNegativeButton("取消", null)
//                        .setCancelable(false)
//                        .show();
//                return;
//            }
//
//            if (!isEval()) {
//                return;
//            }
//
//            if (onCallBackListener != null) {
//                onCallBackListener.onWarn();
//            }
//
//
//            //增加权限弹窗显示
//            List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
//            pairList.add(new Pair<>(Manifest.permission.RECORD_AUDIO,new Pair<>("麦克风权限","录制评测时朗读的音频，用于评测打分使用")));
//            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","保存评测的音频文件，用于评测打分使用")));
//
//            PermissionDialogUtil.getInstance().showMsgDialog(context, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
//                @Override
//                public void onGranted(boolean isSuccess) {
//                    if (isSuccess){
//                        if (isRecord) {
//                            handler.sendEmptyMessage(RECORD_FINISH);
//                        } else {
//                            handler.sendEmptyMessage(RECORD);
//                        }
//                    }
//                }
//            });
//        });
//        holder.eval.setOnClickListener(v->{
//            if (!isPlay()) {
//                return;
//            }
//
//            if (readPlayer != null && readPlayer.isPlaying()) {
//                handler.sendEmptyMessage(PLAY_FINISH);
//            }
//
//            if (onCallBackListener != null) {
//                onCallBackListener.onWarn();
//            }
//
//            if (evalPlayer != null) {
//                if (evalPlayer.isPlaying()) {
//                    handler.sendEmptyMessage(EVAL_FINISH);
//                } else {
//                    evalPlay();
//                }
//            }
//        });
//        holder.share.setOnClickListener(v->{
//            if (!isEval()) {
//                return;
//            }
//
//            if (isRecord) {
//                ToastFactory.showShort(context,"请等待评测完成后发布");
//                return;
//            }
//
//            if (onCallBackListener!=null){
//                int score = (int) (selectBean.getTotal_score()*20);
//                onCallBackListener.publish(selectSentence.getVoaId(),selectSentence.getIdIndex(),selectSentence.getParaId(),score,selectBean.getUrl());
//            }
//        });
//        holder.checkEval.setOnClickListener(v->{
//            if (!isPlay()) {
//                return;
//            }
//
//            if (evalPlayer != null && evalPlayer.isPlaying()) {
//                evalPause();
//            }
//
//            if (onCallBackListener != null) {
//                onCallBackListener.onWarn();
//
//                onCallBackListener.checkEval(selectBean,selectSentence);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list==null?0:list.size();
//    }
//
//    class EvalNewHolder extends RecyclerView.ViewHolder{
//
//        private TextView index;
//        private TextView sentence;
//        private TextView sentenceCn;
//
//        private LinearLayout bottomLayout;
//        private RoundProgressBar play;
//        private ImageView record;
//        private RoundProgressBar eval;
//        private TextView score;
//
//        private ImageView share;
//        private TextView checkEval;
//
//        public EvalNewHolder(View itemView){
//            super(itemView);
//
//            index = itemView.findViewById(R.id.indexView);
//            sentence = itemView.findViewById(R.id.sentence_item);
//            sentenceCn = itemView.findViewById(R.id.sentence_cn);
//
//            bottomLayout = itemView.findViewById(R.id.bottomLayout);
//            play = itemView.findViewById(R.id.control_progress);
//            record = itemView.findViewById(R.id.mike);
//            eval = itemView.findViewById(R.id.control_self);
//            score = itemView.findViewById(R.id.score);
//
//            share = itemView.findViewById(R.id.release_item);
//            checkEval = itemView.findViewById(R.id.correct_sound);
//        }
//    }
//
//    /***************播放**************/
//    //原文播放
//    private void readPlay() {
//        if (!isPrepare) {
//            ToastFactory.showShort(context, "正在加载音频内容，请稍后~");
//            return;
//        }
//
//        if (onCallBackListener != null) {
//            onCallBackListener.onOpen(false);
//        }
//
//        handler.sendEmptyMessage(PLAY);
//    }
//
//    //原文暂停
//    private void readPause() {
//        if (onCallBackListener != null) {
//            onCallBackListener.onOpen(true);
//        }
//
//        if (readPlayer != null && readPlayer.isPlaying()) {
//            readPlayer.pause();
//        }
//        selectHolder.play.setBackgroundResource(R.drawable.play_evaluation_old);
//    }
//
//    /**************录音***************/
//    private void startRecord(long totalTime) {
//        String savePath = FileManager.getInstance().getCourseEvalAudioPath(selectSentence.getVoaId(), selectSentence.getParaId(), selectSentence.getIdIndex());
//        File file = new File(savePath);
//        try {
//            if (file.exists()) {
//                file.delete();
//            } else {
//                if (!file.getParentFile().exists()) {
//                    file.getParentFile().mkdirs();
//                }
//                file.createNewFile();
//            }
//
//            if (onCallBackListener != null) {
//                onCallBackListener.onOpen(false);
//            }
//
//            recordManager = new RecordManager(file);
//            recordManager.startRecord();
//
//            RxTimer.timerInMain(totalTime, new RxTimer.RxAction() {
//                @Override
//                public void action(long number) {
//                    handler.sendEmptyMessage(RECORD_FINISH);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecord() {
//        if (onCallBackListener != null) {
//            onCallBackListener.onOpen(true);
//        }
//
//        RxTimer.cancel();
//        handler.removeMessages(RECORD_FINISH);
//
//        isRecord = false;
//        if (recordManager != null) {
//            recordManager.stopRecord();
//        }
//    }
//
//    /**************评测*************/
//    //评测播放
//    private void evalPlay() {
//        evalPause();
//
//        try {
//            evalPlayer = new MediaPlayer();
//            //判断是否存在
//            String filePath = FileManager.getInstance().getCourseEvalAudioPath(selectSentence.getVoaId(), selectSentence.getParaId(), selectSentence.getIdIndex());
//            File file = new File(filePath);
//            if (file.exists()) {
//                evalPlayer.setDataSource(file.getPath());
//            } else {
//                evalPlayer.setDataSource(context, Uri.parse(selectBean.getUrl()));
//            }
//            evalPlayer.prepare();
//
//            evalPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    if (onCallBackListener != null) {
//                        onCallBackListener.onOpen(false);
//                    }
//
//                    evalPlayer.start();
//                    handler.sendEmptyMessage(EVAL);
//                }
//            });
//            evalPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    handler.sendEmptyMessage(EVAL_FINISH);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //评测暂停
//    private void evalPause() {
//        if (onCallBackListener != null) {
//            onCallBackListener.onOpen(true);
//        }
//
//        if (evalPlayer != null) {
//            if (evalPlayer.isPlaying()) {
//                evalPlayer.pause();
//            }
//        }
//        selectHolder.eval.setBackgroundResource(R.drawable.play_evaluation_old);
//    }
//
//    private void startEval() {
//        if (onCallBackListener != null) {
//            onCallBackListener.onOpen(false);
//        }
//
//        selectHolder.record.setImageResource(R.drawable.mike_grey);
//        isEval = true;
//
//        String filePath = FileManager.getInstance().getCourseEvalAudioPath(selectSentence.getVoaId(), selectSentence.getParaId(), selectSentence.getIdIndex());
//        if (onCallBackListener != null) {
//            onCallBackListener.onEval(true, selectSentence.getVoaId(), selectSentence.getParaId(), selectSentence.getIdIndex(), selectSentence.getSentence(), filePath);
//        }
//    }
//
//    private void stopEval() {
//        if (onCallBackListener != null) {
//            onCallBackListener.onOpen(true);
//        }
//
//        selectHolder.record.setImageResource(R.drawable.mike_grey);
//        isEval = false;
//    }
//
//    /******判断条件******/
//    //评测条件
//    private boolean isEval() {
//        if (isEval) {
//            ToastFactory.showShort(context, "正在评测中~");
//            return false;
//        }
//
//        if (readPlayer != null && readPlayer.isPlaying()) {
//            handler.sendEmptyMessage(PLAY_FINISH);
//        }
//
//        if (evalPlayer != null && evalPlayer.isPlaying()) {
//            handler.sendEmptyMessage(EVAL_FINISH);
//        }
//
//        if (!NetworkUtil.isConnected(context)) {
//            ToastFactory.showShort(context, "暂无网络连接");
//            return false;
//        }
//
//        return true;
//    }
//
//    //播放条件
//    private boolean isPlay() {
//        if (isRecord) {
//            ToastFactory.showShort(context, "正在录音中~");
//            return false;
//        }
//
//        if (isEval) {
//            ToastFactory.showShort(context, "正在评测中~");
//            return false;
//        }
//
//        return true;
//    }
//
//    private static final int PLAY = 1;
//    private static final int PLAY_FINISH = 2;
//    private static final int RECORD = 3;
//    private static final int RECORD_FINISH = 4;
//    private static final int EVAL = 5;
//    private static final int EVAL_FINISH = 6;
//    private static final int RECORD_ANIM_START = 10;
//    private static final int RECORD_ANIM_FINISH = 11;
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what) {
//                case PLAY:
//                    selectHolder.play.setBackgroundResource(R.drawable.pause_evaluation_old);
//                    if (readPlayer != null) {
//                        if (!readPlayer.isPlaying()) {
//                            readPlayer.seekTo((int) getStartTime());
//                            readPlayer.start();
//                        }
//                    }
//                    //计算时间
//                    if (readPlayer.getCurrentPosition() <= getEndTime()) {
//                        handler.sendEmptyMessageDelayed(PLAY, 100L);
//                    } else {
//                        handler.sendEmptyMessage(PLAY_FINISH);
//                    }
//                    break;
//                case PLAY_FINISH:
//                    readPause();
//                    selectHolder.play.setBackgroundResource(R.drawable.play_evaluation_old);
//                    handler.removeMessages(PLAY);
//                    break;
//                case RECORD:
//                    long endTime = getRecordEndTime();
//                    long startTime = getStartTime();
//                    long totalTime = (endTime - startTime)+3000L;
//                    isRecord = true;
//
//                    startRecord(totalTime);
//                    handler.sendEmptyMessage(RECORD_ANIM_START);
//                    break;
//                case RECORD_FINISH:
//                    stopRecord();
//                    handler.sendEmptyMessage(RECORD_ANIM_FINISH);
//
//                    //进行评测
//                    startEval();
//                    break;
//                case EVAL:
//                    selectHolder.eval.setBackgroundResource(R.drawable.pause_evaluation_old);
//                    break;
//                case EVAL_FINISH:
//                    evalPause();
//                    selectHolder.eval.setBackgroundResource(R.drawable.play_evaluation_old);
//                    handler.removeMessages(EVAL);
//                    break;
//
//                case RECORD_ANIM_START:
//                    selectHolder.record.setImageResource(R.drawable.mike_red);
//                    break;
//                case RECORD_ANIM_FINISH:
//                    handler.removeMessages(RECORD_ANIM_START);
//                    selectHolder.record.setImageResource(R.drawable.mike_grey);
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//    /*******************时间***************/
//    //获取开始时间
//    private long getStartTime() {
//        return (long) (selectSentence.getTiming()*1000);
//    }
//
//    //获取停止时间
//    private long getEndTime() {
//        if (selectPosition == list.size()-1){
//            return getPlayerEndTime();
//        }
//
//        return (long) (selectSentence.getEndTiming()*1000);
//    }
//
//    //获取录音停止时间
//    private long getRecordEndTime() {
//        if (selectPosition == list.size() - 1) {
//            return getPlayerEndTime();
//        }
//
//        long endTime = (long) (list.get(selectPosition + 1).getTiming()*1000);
//        return endTime;
//    }
//
//    //获取播放器时间
//    private long getPlayerEndTime() {
//        if (readPlayer == null) {
//            return 0;
//        }
//
//        return readPlayer.getDuration();
//    }
//
//    /*******************接口***************/
//    //接口
//    public OnCallBackListener onCallBackListener;
//
//    public interface OnCallBackListener {
//        //禁止操作
//        void onWarn();
//
//        //放开操作
//        void onOpen(boolean isOpen);
//
//        //评测
//        void onEval(boolean isSentence, String voaId, String paraId, String indexId, String sentence, String filePath);
//
//        //发布
//        void publish(String voaId,String idIndex,String paraId,int score,String url);
//
//        //纠音
//        void checkEval(EvalShowBean showBean,SentenceTransBean transBean);
//    }
//
//    public void setOnCallBackListener(OnCallBackListener onCallBackListener) {
//        this.onCallBackListener = onCallBackListener;
//    }
//
//
//    /**************数据回调*****************/
//    //回调评测数据
//    public void showEvalResult(EvalShowBean bean) {
//        if (bean != null) {
//            stopEval();
//            //保存在数据库
//            String filePath = FileManager.getInstance().getCourseEvalAudioPath(selectSentence.getVoaId(), selectSentence.getParaId(), selectSentence.getIdIndex());
//            RoomDBManager.getInstance().saveSingleEval(selectSentence.getVoaId(), selectSentence.getParaId(), selectSentence.getIdIndex(), bean, filePath);
//
//            //刷新
//            notifyDataSetChanged();
//            //刷新首页数据
//            EventBus.getDefault().post(new LocalEvalDataRefreshEvent());
//        } else {
//            stopEval();
//            ToastFactory.showShort(context, "评测失败，请重试");
//        }
//    }
//
//    public void initReadPlayer(boolean isPrepare) {
//        this.isPrepare = isPrepare;
//    }
//
//    public void stopReadHandler() {
//        handler.sendEmptyMessage(PLAY_FINISH);
//    }
//
//    public void stopRecordHandler() {
//        selectHolder.record.setImageResource(R.drawable.mike_grey);
//        stopRecord();
//
//        handler.sendEmptyMessage(PLAY_FINISH);
//
//        handler.sendEmptyMessage(EVAL_FINISH);
//    }
//
//    //刷新数据
//    public void refreshData(List<SentenceTransBean> refreshList){
//        this.list = refreshList;
//        notifyDataSetChanged();
//    }
//
//    //停止当前录音和音频播放
//    public void stopRecordAndPlay(){
//        if (selectHolder!=null){
//            //停止录音
//            stopRecord();
//            handler.sendEmptyMessage(RECORD_ANIM_FINISH);
//
//            //停止播放
//            readPause();
//            selectHolder.play.setBackgroundResource(R.drawable.play_evaluation_old);
//            handler.removeMessages(PLAY);
//        }
//    }
//}
