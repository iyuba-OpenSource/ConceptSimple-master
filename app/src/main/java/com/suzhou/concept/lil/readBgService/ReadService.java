package com.suzhou.concept.lil.readBgService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.suzhou.concept.lil.readBgService.receiver.NotificationCancelReceiver;
import com.suzhou.concept.lil.readBgService.receiver.NotificationCloseReceiver;

import org.greenrobot.eventbus.EventBus;

public class ReadService extends Service {

    private MyBinder myBinder = new MyBinder();
    public MediaPlayer mediaPlayer = null;

    //action
    private String ACTION_CLOSE = "action_close";
    private String ACTION_CANCEL = "action_cancel";

    //接收器
    private NotificationCloseReceiver closeReceiver;
    private NotificationCancelReceiver cancelReceiver;

    public void init(Context context){

    }

    public class MyBinder extends Binder {
        public ReadService getService() {
            return ReadService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);
        registerNotificationReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        unregisterNotificationReceiver();

        stopAudio();
    }

    /*************接收器************/
    //注册接收器
    private void registerNotificationReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_CLOSE);
        this.closeReceiver = new NotificationCloseReceiver();
        registerReceiver(closeReceiver, intentFilter);

        intentFilter = new IntentFilter(ACTION_CANCEL);
        this.cancelReceiver = new NotificationCancelReceiver();
        registerReceiver(cancelReceiver, intentFilter);
    }

    //取消接收器
    private void unregisterNotificationReceiver() {
        try {
            unregisterReceiver(closeReceiver);
            unregisterReceiver(cancelReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /********************************音频播放*****************************/
    //初始化音频
    private void initPlayer(){

    }

    //音频播放
    private void startAudio(){

    }

    //音频暂停
    private void pauseAudio(){
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }


    }

    //音频停止
    private void stopAudio(){
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

            ReadServiceManager.Instace().bindService.stopSelf();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
