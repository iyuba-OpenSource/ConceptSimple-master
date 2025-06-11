package com.suzhou.concept.activity.speaking

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.DubInfoAdapter
import com.suzhou.concept.bean.CloseDubSpeakingEvent
import com.suzhou.concept.bean.WavListItem
import com.suzhou.concept.bean.YoungSentenceItem
import com.suzhou.concept.databinding.ActivityDubSpeakingBinding
import com.suzhou.concept.lil.util.LibRxTimer
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.changeTimeToString
import com.suzhou.concept.utils.changeVideoUrl
import com.suzhou.concept.utils.checkObtainPermission
import com.suzhou.concept.utils.checkPermission
import com.suzhou.concept.utils.getLocalPath
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.logic.DownloadFileManager
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startActivity
import com.suzhou.concept.utils.view.LocalMediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.utils.ToastFactory
import java.io.File

/**
 * 口语秀的配音界面
 */
class DubSpeakingActivity : BaseActivity<ActivityDubSpeakingBinding>() ,
    DownloadFileManager.DownLoadListener , Consumer<Pair<YoungSentenceItem,Int>> {
    private lateinit var operateAdapter:DubInfoAdapter
    private lateinit var downloadManager:DownloadFileManager
    private val show=1
    private val dismiss=2
    private val existLocal=3
    private val recorder by lazy { LocalMediaRecorder() }
    private val filePath by lazy { "${externalCacheDir?.absolutePath}speaking_audio_dub.wav" }
    //视频播放
    private lateinit var videoPlayer:ExoPlayer
    //音频播放
    private lateinit var audioPlayer:ExoPlayer
    //是否已经关闭了界面
    private var isClosePage:Boolean = false

    private lateinit var currentItem:YoungSentenceItem
    private val list= mutableListOf<YoungSentenceItem>()
    private val selectHint by lazy { getString(R.string.choose_speaking_sentence) }
    private val map= mutableMapOf<Int, WavListItem>()
    private lateinit var recordJob:Job
    private var itemClickFlag=0L
    private val requestPermissionLaunch = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.checkPermission { startSpeaking() }
    }

    /**
     * 到底有没有防止重复点击的必要？？？
     * */
    private var timeFlag=0L
    private val handler=Handler(Looper.getMainLooper()){
        when(it.what){
            show->startDownloadDialog(0.0)
            dismiss->stopDownloadDialog()
            existLocal-> bindPlayerView(Uri.parse(it.obj.toString()))
        }
        true
    }
    override fun ActivityDubSpeakingBinding.initBinding() {
        //关掉控制条
        binding.dubPlayer.useController = false

        setTitleText("口语配音")
        installStandRight (R.drawable.preview){
            if (list.none { it.success }){
                "还没有配音哦，点击话筒试试吧".showToast()
                return@installStandRight
            }
            startActivity<PreViewSpeakingActivity> {
                if (list.isNotEmpty()){
                    val fractionList=list.filter { it.fraction.isNotEmpty() }
                    val averageScore=with(fractionList) {
                        var score = 0F
                        forEach {
                            score += it.fraction.toFloat()
                        }
                        score / size
                    }

                    val completePercent= with(fractionList.size.toDouble()/list.size.toDouble()){
                        (this*100).toInt()
                    }
                    putExtra(ExtraKeysFactory.dubAverageScore,averageScore)
                    putExtra(ExtraKeysFactory.completePercent,completePercent)
                }
                val list=ArrayList(map.map { it.value })
                putParcelableArrayListExtra(ExtraKeysFactory.dubWavListItemList,list)
            }
        }

        young.updateItemDownload(GlobalMemory.speakingItem)

        evaluation.requestDubSentence(GlobalMemory.speakingItem.voa_id.toInt())
        operateAdapter= with(DubInfoAdapter()){
            registerItemListener(this@DubSpeakingActivity)
            this
        }

        //播放录音操作
        startRecordSpeaking.apply {
            setOnClickListener {
                //这里判断是否存在录音和视频播放的情况
                //1.如果音频播放，则暂停
                if (::audioPlayer.isInitialized){
                    audioPlayer.pause()
                    playSpeaking.setImageResource(R.drawable.start)
                }

                //2.如果播放视频，则暂停
                if (::videoPlayer.isInitialized){
                    videoPlayer.pause()
                }

                if (!::currentItem.isInitialized){
                    selectHint.showToast()
                    return@setOnClickListener
                }
                requestPermissionLaunch.checkObtainPermission { startSpeaking() }
            }
        }
        dubList.adapter=operateAdapter
        listenDubList()

        //初始化播放器
        videoPlayer = ExoPlayer.Builder(this@DubSpeakingActivity).build()
        videoPlayer.playWhenReady = false
        videoPlayer.addListener(object:Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                when(playbackState){
                    Player.STATE_READY->{

                    }
                    Player.STATE_ENDED->{

                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                ToastUtil.showToast(this@DubSpeakingActivity,"视频播放异常～")
            }
        })
        binding.dubPlayer.player = videoPlayer

        //判断音视频文件存在
        listenFileExist()

        listenEvalResult()
        binding.item= with(YoungSentenceItem()){
            inflateDefault(selectHint)
            this
        }

        audioPlayer = with(ExoPlayer.Builder(this@DubSpeakingActivity).build()) {
            this.addListener(object: Player.Listener{
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when(playbackState){
                        Player.STATE_ENDED-> playSpeaking.setImageResource(R.drawable.start)
                    }
                }
            })
            this
        }

        //评测后的音频播放
        playSpeaking.setOnClickListener {
            //判断是否正在录音
            if (recorder.isRecording()){
                ToastFactory.showShort(this@DubSpeakingActivity,"正在录音中")
                return@setOnClickListener
            }

            //中断视频播放
            if (::videoPlayer.isInitialized){
                videoPlayer.pause()
            }

            //根据状态进行播放/暂停显示
            if (::audioPlayer.isInitialized){
                if (audioPlayer.isPlaying){
                    audioPlayer.pause()
                    playSpeaking.setImageResource(R.drawable.start)
                }else{
                    audioPlayer.setMediaItem(MediaItem.fromUri(currentItem.selfVideoUrl.changeVideoUrl()))
                    audioPlayer.prepare()
                    if (!isClosePage){
                        audioPlayer.play()
                    }
                    playSpeaking.setImageResource(R.drawable.pause)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //设置关闭界面
        isClosePage = true
        //关闭音频
        if (recorder.isRecording()){
            recorder.stopRecord()
        }
        if (::audioPlayer.isInitialized){
            audioPlayer.stop()
        }
        if (::videoPlayer.isInitialized){
            videoPlayer.stop()
        }

        //删除文件
        var localFile = getLocalPath(GlobalMemory.speakingItem.youngChild.youngVideoPath)
        if (!isDownloadFinish){
            localFile.delete()
        }
    }

    /**
     * 录音操作
     */
    private fun startSpeaking(){
//        if (System.currentTimeMillis()-itemClickFlag<currentItem.getDuration()){
//            "请先看完视频片段".showToast()
//            return
//        }

        itemClickFlag=0
        val draw=if (recorder.isRecording()){
            if (::recordJob.isInitialized){
                recordJob.cancel()
            }
            submitEval()
            R.drawable.mike_grey
        }else{
            recordJob=getRecordJob()
            recorder.startPrepare(filePath,lifecycleScope)
            R.drawable.mike_red
        }
        binding.startRecordSpeaking.setImageResource(draw)
    }

    private fun submitEval(){
        recorder.stopRecord()
        young.evalSentence(File(filePath),currentItem)
    }

    private fun getRecordJob()=lifecycleScope.launch {
        val duration=currentItem.getDuration()
        flow {
            while (duration>recorder.getCurrentPosition()){
                delay(LocalMediaRecorder.timeMillis)
                emit(recorder.getCurrentPosition())
            }
        }.collect{
            //显示当前00：00
            binding.currentTimeTv.text=it.toInt().changeTimeToString()
            //显示当前progressBar的进度
            binding.voiceSeek.progress=it.toInt()
            if (it==duration){
                //提交评测
                submitEval()
                binding.startRecordSpeaking.setImageResource( R.drawable.mike_red)
            }
        }
    }


    /**
     * 本地时：
         * index=paraId ，
         * URL=自己的配音，
         * beginTime=currentItem.beginTime,
         * endTime=currentItem.endTime，
         * （duration=对应插入的“新”字段？？？）加他m，和下面的(评测返回逻辑)统一，根据文件长度现计算
     * 评测返回时 ：
         * index=paraId，
         * URL=返回的自己的录音，
         * beginTime=currentItem.beginTime,
         * endTime=(处理好position==0和position==list.length-1时的逻辑).endTime,
         * duration=返回的录音文件的大小
     * */
    private suspend fun buildWavListItem(url:String,begin:Float,end:Float,index:Int){
        withContext(Dispatchers.IO){
            val duration=transformUrlVoiceTime(url)/1000.0F
            map[index]=WavListItem(url,begin,duration,end,index)
        }
    }

    /**
     * 只能用此下下策来计算网络音频的长度
     * 产生了大量的匿名类，，，相信后人的智慧吧
     * */
    private fun transformUrlVoiceTime(url: String):Float  = with(MediaMetadataRetriever()){
        setDataSource(url, mutableMapOf())
        extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toString().let {
            (if (it.isEmpty()) 0F else it.toFloat()/1000.0F)
        }
    }

    /**
     * 点击时是否初始化、点击时时间段视频
     * */
    private fun listenEvalResult(){
        lifecycleScope.launch {
            young.speakingResult.collect{result->
                result.onError {
                    it.judgeType().showToast()
                    stopLoading()
                }.onSuccess {
                    binding.startRecordSpeaking.setImageResource( R.drawable.mike_grey)
                    "评测成功".showToast()
                    val index=list.indexOfFirst {item-> item.EndTiming==it.first }
                    operateAdapter.notifyItemChanged(index)
                    val end=if (list[index].ParaId.toInt()<list.size){
                        list[index].Timing
                    }else{
                        list[index-1].EndTiming
                    }.toFloat()
                    buildWavListItem(it.second.URL.changeVideoUrl(),list[index].Timing.toFloat(),end,currentItem.ParaId.toInt())
                    stopLoading()

                    //这里将默认的数据修改成评测的数据
                    currentItem = list[index]
                    binding.playSpeaking.visibility = View.VISIBLE
                    binding.playSpeaking.setImageResource(R.drawable.start)
                }.onLoading {
                    startLoading("正在评测中~")
                }
            }
        }
    }
    private fun listenDubList(){
        lifecycleScope.launch {
            evaluation.dubList.collect{result->
                result.onSuccess {
                    //这里暂时关闭
//                    dismissLoad()
                    list.addAll(it)
                    operateAdapter.changeData(it)
                    for(i in it.indices){
                        val item=it[0]
                        if (item.selfVideoUrl.isNotEmpty()){
                            buildWavListItem(item.selfVideoUrl.changeVideoUrl(),item.Timing.toFloat(),item.EndTiming.toFloat(),item.ParaId.toInt())
                        }
                    }
                }.onLoading {
                    startLoading("正在评测中～")
                }.onError {
                    stopLoading()
                    ToastFactory.showShort(this@DubSpeakingActivity,"加载配音列表失败")
                }
            }
        }
    }

    private fun bindPlayerView(uri: Uri){
        if (videoPlayer == null){
            return
        }
        with(ExoPlayer.Builder(this).build()) {
            //关闭控制器
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            play()
            this
        }.let {
            binding.dubPlayer.player =it
            videoPlayer=it
        }
    }

    private fun listenFileExist(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val youngVideoPath=GlobalMemory.speakingItem.youngChild.youngVideoPath
                getLocalPath(youngVideoPath).apply {
                    if (exists() ){
                        isDownloadFinish = true
                        val msg= with(Message()){
                            what=existLocal
                            obj=absolutePath
                            this
                        }
                        handler.sendMessage(msg)
                    }else{
                        handler.sendEmptyMessage(show)
                        val url=with(StringBuilder()){
                            append("http://")
                            val vipJudge=if (GlobalMemory.userInfo.isVip()) "staticvip." else "static0."
                            append(vipJudge)
                            append(OtherUtils.iyuba_cn)
                            append(youngVideoPath)
                            toString()
                        }
                        downloadManager= with(DownloadFileManager(this@DubSpeakingActivity)){
                            val urlAndFile= Pair(url,this@apply)
                            val voicePair= with(GlobalMemory.speakingItem.youngChild){
                                Pair(youngBackVoice,getLocalPath(youngBackVoiceEndPath))
                            }
                            GlobalMemory.speakingItem.name.let {
                                downLoad(urlAndFile,voicePair,it,it)
                            }
                            registerListener(this@DubSpeakingActivity)
                            this
                        }
                    }
                }
            }
        }
    }

    //下载完成
    override fun successComplete(uri: Uri) {
        isDownloadFinish = true
        "下载完成".showToast()
        handler.sendEmptyMessage(dismiss)
        bindPlayerView(uri)
    }

    //下载中
    override fun downloadProgress(progress: Double) {
        startDownloadDialog(progress)
    }

    override fun onPause() {
        super.onPause()
        binding.dubPlayer.player?.pause()
    }

    /**
     * 此处是否应该加个防止过快点击
     * */
    override fun accept(item: Pair<YoungSentenceItem,Int>) {
        //判断录音
        if (recorder.isRecording()){
            ToastFactory.showShort(this,"正在录音中...")
            return
        }

        //停止音频播放
        if (::audioPlayer.isInitialized){
            if (audioPlayer.isPlaying){
                audioPlayer.pause()
                binding.playSpeaking.setImageResource(R.drawable.start)
            }
        }

        if (!::videoPlayer.isInitialized){
            return
        }

        currentItem=item.first
        binding.item=currentItem
        val start= item.first.getStart()
        val end= item.first.getEnd()
//        lifecycleScope.launch {
//            videoPlayer.playAToB(start,end)
//                .cancellable()
//                .collect{
//                    if (it){
//                        videoPlayer.pause()
//                        cancel()
//                    }
//                }
//        }

        //重新处理下
        videoPlayer.pause()
        if (!isClosePage){
            videoPlayer.seekTo(start)
            videoPlayer.play()
        }
        LibRxTimer.getInstance().multiTimerInMain("startVideo",0,20L,object:
            LibRxTimer.RxActionListener{
            override fun onAction(number: Long) {
                val curVideoProgress = videoPlayer.currentPosition
                if (curVideoProgress>=end){
                    videoPlayer.pause()
                    LibRxTimer.getInstance().cancelTimer("startVideo")
                }
            }
        })

        val duration=item.first.getDuration().toInt()
        binding.voiceSeek.apply {
            progress=0
            max=duration
        }
        binding.sumTimeTv.text=duration.changeTimeToString()
        binding.currentTimeTv.text=getString(R.string.default_time)
        itemClickFlag=System.currentTimeMillis()
    }

    override fun initEventBus(): Boolean =true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:CloseDubSpeakingEvent){
        if (!isFinishing){
            finish()
        }
    }

    //文件下载加载弹窗
    private lateinit var downloadDialog:LoadingMsgDialog
    //是否下载完成
    private var isDownloadFinish = false

    private fun startDownloadDialog(progress:Double){
        if (!::downloadDialog.isInitialized){
            downloadDialog = LoadingMsgDialog(this)
            downloadDialog.create()
//            downloadDialog.setCancelable(false)
        }

        var showProgress = "正在下载配音相关文件"
        if (progress>0){
            showProgress = "$showProgress\n文件下载进度：$progress%"
        }

        downloadDialog.setMessage(showProgress)
        downloadDialog.show()
    }

    private fun stopDownloadDialog(){
        if (::downloadDialog.isInitialized){
            downloadDialog.dismiss()
        }
    }

    //加载弹窗
    private lateinit var loadingDialog:LoadingMsgDialog

    private fun startLoading(showMsg:String){
        if (!::loadingDialog.isInitialized){
            loadingDialog = LoadingMsgDialog(this@DubSpeakingActivity)
            loadingDialog.create()
        }
        loadingDialog.setMsg(showMsg)
        if (!loadingDialog.isShowing){
            loadingDialog.show()
        }
    }

    private fun stopLoading(){
        if (loadingDialog.isShowing){
            loadingDialog.dismiss()
        }
    }
}