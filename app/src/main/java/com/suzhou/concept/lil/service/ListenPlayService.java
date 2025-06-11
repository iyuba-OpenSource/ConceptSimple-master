package com.suzhou.concept.lil.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.article.TeachMaterialActivity;
import com.suzhou.concept.lil.service.data.ListenPlayEvent;
import com.suzhou.concept.lil.service.temp.ListenPlaySession;
import com.suzhou.concept.utils.ExtraKeysFactory;

import org.greenrobot.eventbus.EventBus;

import personal.iyuba.personalhomelibrary.utils.ToastFactory;

/**
 * @title: 原文后台播放的设置
 * @date: 2023/10/18 11:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenPlayService extends Service {

    //通知的id
    private static final int NOTIFICATION_ID = 223;
    //播放器
    private ExoPlayer exoPlayer;
    //是否可以播放
    private boolean isPrepare = false;
    //绑定的数据
    private MyBinder myBinder = new MyBinder();

    //绑定服务
    public class MyBinder extends Binder{
        public ListenPlayService getService(){
            return ListenPlayService.this;
        }
    }

    //绑定
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    //解除绑定
    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化和通知栏
        initPlayer();
        String appName = AppClient.context.getResources().getString(R.string.app_name);
        updateNotification(appName,appName+"正在运行中",false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**************************音频操作****************************/
    //获取播放器
    public ExoPlayer getPlayer(){
        return exoPlayer;
    }

    //初始化播放器
    private void initPlayer(){
        if (exoPlayer==null){
            exoPlayer = new ExoPlayer.Builder(AppClient.context).build();
        }
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        //准备完成，进行播放
                        isPrepare = true;

                        //这里不进行播放，需要外面判断是否播放
                        EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_prepare_finish));
                        break;
                    case Player.STATE_ENDED:
                        //查找下一个进行处理
                        EventBus.getDefault().post(new ListenPlayEvent(ListenPlayEvent.PLAY_complete_finish));
                        break;
                }
            }



            @Override
            public void onPlayerError(PlaybackException error) {
                isPrepare = false;
            }
        });
    }

    //播放音频
    public void playAudio(){
        if (exoPlayer==null){
            ToastFactory.showShort(AppClient.context,"未初始化音频组件");
            return;
        }

        String playUrl = ListenPlaySession.getInstance().getTempBean().getAudioUrl();
        MediaItem mediaItem = MediaItem.fromUri(playUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    //继续/重新播放音频
    public void continuePlay(boolean isContinue){
        if (!isPrepare){
            ToastFactory.showShort(AppClient.context,"正在加载音频内容...");
            return;
        }

        if (exoPlayer!=null){
            if (!isContinue){
                exoPlayer.seekTo(0);
            }

            exoPlayer.play();
        }

        updateNotification(ListenPlaySession.getInstance().getTempBean().getTitle(),null,true);
    }

    //暂停音频
    public void pauseAudio(){
        if (exoPlayer!=null){
            exoPlayer.pause();
        }
    }

    //停止音频
    public void stopAudio(){
        if (exoPlayer!=null){
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    //设置播放器进度
    public void setProgress(int progress){
        if (exoPlayer!=null){
            exoPlayer.seekTo(progress);
        }
    }

    //设置播放器倍速
    public void setSpeed(float speed){
        if (exoPlayer!=null){
            exoPlayer.setPlaybackSpeed(speed);
        }
    }

    //是否初始化完成
    public boolean isPrepare(){
        return isPrepare;
    }

    //获取当前播放器进度
    public long getPlayerProgress(){
        if (exoPlayer!=null){
            return exoPlayer.getCurrentPosition();
        }
        return 0;
    }

    //获取当前播放器总时长
    public long getPlayerDuration(){
        if (exoPlayer!=null){
            return exoPlayer.getDuration();
        }
        return 0;
    }

    /****************************回调操作**************************/

    /*****************************通知栏***************************/
    //更新通知栏消息
    public void updateNotification(String title,String content,boolean isJump){
        //判断是否暂停播放
        if (isJump){
            if (exoPlayer!=null){
                if (exoPlayer.isPlaying()){
                    content = "正在播放";
                }else {
                    content = "暂停播放";
                }
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(NOTIFICATION_ID), AppClient.context.getResources().getString(R.string.app_name)+"通知", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(AppClient.context.getResources().getString(R.string.app_name)+"的通知消息");
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, String.valueOf(NOTIFICATION_ID))
                    .setOngoing(true)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(isJump?startStudyActivity():null)
                    .setSmallIcon(R.mipmap.app_logo)
                    .setTicker(title)
//                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
//                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())
                    .build();
            startForeground(NOTIFICATION_ID, notification);
        } else {
            Notification.Builder localBuilder = new Notification.Builder(this);
            localBuilder.setOngoing(true)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(isJump?startStudyActivity():null)
                    .setSmallIcon(R.mipmap.app_logo)
                    .setTicker(title);
//                    .addAction(android.R.drawable.ic_media_play, "播放", receivePlayIntent())
//                    .addAction(android.R.drawable.ic_media_pause, "暂停", receivePauseIntent())

            startForeground(NOTIFICATION_ID, localBuilder.build());
        }
    }

    //跳转到学习界面
    private PendingIntent startStudyActivity(){
        if (ListenPlaySession.getInstance().getTempBean()!=null){
            Intent intent = new Intent();
            intent.setClass(this, TeachMaterialActivity.class);
            intent.putExtra(ExtraKeysFactory.position,ListenPlaySession.getInstance().getTempBean().getShowIndex());
            return PendingIntent.getActivity(this,0,intent,getPendFlag());
        }

        return null;
    }

    //获取版本的标志
    private int getPendFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.FLAG_IMMUTABLE;
        } else {
            return PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }

}
