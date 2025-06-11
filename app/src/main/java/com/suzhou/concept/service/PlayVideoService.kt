package com.suzhou.concept.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.article.TeachMaterialActivity
import com.suzhou.concept.bean.*
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.receiver.CloseServiceReceiver
import com.suzhou.concept.receiver.ControlVideoReceiver
import com.suzhou.concept.receiver.NextVideoReceiver
import com.suzhou.concept.receiver.PreviousVideoReceiver
import com.suzhou.concept.utils.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PlayVideoService : Service() {
    private lateinit var close:CloseServiceReceiver
    private lateinit var control:ControlVideoReceiver
    private lateinit var next:NextVideoReceiver
    private lateinit var previous:PreviousVideoReceiver
    private lateinit var groupView:RemoteViews
    private val closeAction="close_service_receiver"
    private val nextAction="next_service_receiver"
    private val previousAction="previous_service_receiver"
    private val controlAction="control_service_receiver"
    private val binder= ControlVideoBinder()
    private lateinit var manager:NotificationManager
    private lateinit var notification: Notification
    private lateinit var player: MediaPlayer
    private val id=AppClient.appId
    private var currentUrl=""

    override fun onBind(intent: Intent): IBinder = binder

    inner class ControlVideoBinder: Binder(){
        fun changeStatus(playing:Boolean){
            changeControlState(playing)
        }


        fun getCurrPlayPercent()=player.getPercent()

        fun continuePlay(currentPosition:Int){
            GlobalMemory.submitStartTime= getRecordTime()
            if (!playerIsInit()){
                player= MediaPlayer()
            }
            player.apply {
                try {
                    reset()
                    currentUrl=AppClient.videoUrl
                    setDataSource(currentUrl)
                    prepareAsync()
                    setOnPreparedListener {
                        groupView.setImageViewResource(R.id.control_service,R.drawable.pause)
                        notifyRemoteViews()
                        seekTo(currentPosition)
                        start()
                    }
                    setOnCompletionListener {
                        EventBus.getDefault().post(OutServiceEvent())
                        changeControl()
                    }
                }catch (e:Exception){
                    EventBus.getDefault().post(ShowOperateEvent(player.isPlaying))
                }
            }
        }

        fun changeTitle(){
            this@PlayVideoService.changeTitle()
        }

        fun getRealCurrent()=player.currentPosition

        fun getPlayCurrent() :Int{
            val current=if (playerIsInit()) {
                player.pause()
                EventBus.getDefault().post(ListenPlayEvent(player.isPlaying))
                changeControl()
                if (AppClient.videoUrl==currentUrl){
                    player.currentPosition
                }else{
                    -1
                }
            } else {
                -1
            }
            return current
        }

    }

    fun changeControl(playing:Boolean=false){
        val play=if (GlobalMemory.fineListenCreate){
            GlobalMemory.fineListenIsPlay
        }else{
            if (playerIsInit()) {
                player.isPlaying
            } else {
                playing
            }
        }
        changeControlState(play)
    }

    private fun changeTitle(){
        groupView.setTextViewText(R.id.service_title,AppClient.conceptItem.title)
        notifyRemoteViews()
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        createNotification()
        initReceiver()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("UnspecifiedImmutableFlag", "LaunchActivityFromNotification")
    private fun createNotification(){
        val channelId="play_video_service"
        manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel= with(NotificationChannel(channelId,"前台Service通知",NotificationManager.IMPORTANCE_DEFAULT)){
                enableVibration(false)
                vibrationPattern= longArrayOf(0)
                this
            }
            manager.createNotificationChannel(channel)
        }
        groupView= with(RemoteViews(packageName,R.layout.self_service_layout)){
            setOnClickPendingIntent(R.id.close_service,getReceiver(closeAction))
            setOnClickPendingIntent(R.id.control_service,getReceiver(controlAction))
//            setOnClickPendingIntent(R.id.service_next,getReceiver(nextAction))
//            setOnClickPendingIntent(R.id.service_previous,getReceiver(previousAction))

            this
        }

        val intent= Intent(this, TeachMaterialActivity::class.java)

        val pi=PendingIntent.getActivity(this,0,intent,getCurrentFlag())
        notification=NotificationCompat.Builder(this,channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.concept_logo)
            .setContentIntent(pi)
            .setCustomContentView(groupView)
            .setVibrate(longArrayOf(0))
            .build()
        startForeground(id,notification)
        changeTitle()
        //上一曲。下一曲
        //点击pendingIntent跳转activity
    }
    private fun showOperate(){
        if (!::groupView.isInitialized){
            return
        }
        groupView.apply {
            
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getReceiver(type:String):PendingIntent{
        val intent= Intent(type).setPackage(packageName)
        return PendingIntent.getBroadcast(this,0,intent, getCurrentFlag())
    }

    private fun getCurrentFlag()=if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
        PendingIntent.FLAG_MUTABLE
    }else{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
    }

    private fun initReceiver(){
        //关闭前台服务(通知)
        close= CloseServiceReceiver()
        registerReceiver(close,IntentFilter(closeAction))
        //控制▶/⏸
        control= ControlVideoReceiver()
        registerReceiver(control,IntentFilter(controlAction))
        //上一曲
        next= NextVideoReceiver()
        registerReceiver(next, IntentFilter(nextAction))
        //下一曲
        previous= PreviousVideoReceiver()
        registerReceiver(previous, IntentFilter(previousAction))
        //显示播放句子
    }

    private fun playerIsInit()=::player.isInitialized

    //停止音频播放
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event:RefreshEvent){
        if (event.type.equals(RefreshEvent.AUDIO_STOP)){
            if (playerIsInit()){
                player.pause()
            }
        }

        if (event.type.equals(RefreshEvent.AUDIO_INIT)){
            GlobalMemory.submitStartTime= getRecordTime()
            if (!playerIsInit()){
                player= MediaPlayer()
            }
            player.apply {
                try {
                    reset()
                    currentUrl=AppClient.videoUrl
                    setDataSource(currentUrl)
                    prepareAsync()
                    setOnPreparedListener {
                        notifyRemoteViews()
                        seekTo(player.currentPosition)
                        start()
                        pause()
                    }
                    setOnCompletionListener {
                        EventBus.getDefault().post(OutServiceEvent())
                        changeControl()
                    }
                }catch (e:Exception){
                    EventBus.getDefault().post(ShowOperateEvent(player.isPlaying))
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun controlVideo(event: ControlVideoEvent){
        changeStatus()
    }

    private fun changeStatus(){
        if (playerIsInit()){
            player.apply {
                if (!GlobalMemory.fineListenCreate){
                    if (isPlaying){
                        pause()
                    }else {
                        GlobalMemory.submitStartTime= getRecordTime()
                        start()
                    }
                    EventBus.getDefault().post(ListenPlayEvent(player.isPlaying))
                }
            }
        }else{
            changeControl(!GlobalMemory.fineListenIsPlay)
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:ListenPlayImageEvent){
        changeControlState(event.play)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:ListenPlayEvent){
        changeControlState(event.play)
    }

    private fun changeControlState(state:Boolean){
        val srcId= if (state){
            R.drawable.pause
        }else{
            R.drawable.start
        }
        groupView.setImageViewResource(R.id.control_service,srcId)
        notifyRemoteViews()
    }

//    /**
//     * 微课模块播放时暂停音频播放
//     * */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun event(event: ImoocPlayEvent){
//        changeStatus()
//    }
//
//    /**
//     * 视频模块播放时暂停音频播放
//     * */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun event(event: HeadlinePlayEvent){
//        pausePlayer()
//    }

    private fun pausePlayer(){
        if (playerIsInit()){
            player.apply {
                if (!GlobalMemory.fineListenCreate){
                    if (isPlaying)pause()
                    EventBus.getDefault().post(ListenPlayEvent(player.isPlaying))
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: PauseServiceVideoEvent){
        if (playerIsInit()){
            player.apply {
                if (isPlaying)pause()
            }
        }
    }


    private fun notifyRemoteViews(){
        manager.notify(id,notification)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(close)
        unregisterReceiver(control)
        unregisterReceiver(next)
        unregisterReceiver(previous)
        EventBus.getDefault().unregister(this)
        if (!playerIsInit()){
            return
        }
        kotlin.runCatching {
            player.apply {
                stop()
                release()

                //重制数据
                player = MediaPlayer()
            }
        }.onFailure {
            it.judgeType().showToast()
        }
    }

    fun setPlayerInit(){
        if (!::player.isInitialized){
            player = MediaPlayer()
        }
    }
}