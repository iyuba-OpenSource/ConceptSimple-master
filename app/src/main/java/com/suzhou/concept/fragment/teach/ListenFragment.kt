//package com.suzhou.concept.fragment.teach
//
//import android.annotation.SuppressLint
//import android.content.ComponentName
//import android.content.ServiceConnection
//import android.media.MediaPlayer
//import android.net.Uri
//import android.os.Build
//import android.os.Handler
//import android.os.IBinder
//import android.os.Looper
//import android.text.TextUtils
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//import android.view.WindowManager
//import android.widget.SeekBar
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AlertDialog
//import androidx.lifecycle.lifecycleScope
//import com.google.android.exoplayer2.ExoPlayer
//import com.google.android.material.snackbar.Snackbar
//import com.suzhou.concept.AppClient
//import com.suzhou.concept.R
//import com.suzhou.concept.activity.article.TeachMaterialActivity
//import com.suzhou.concept.adapter.FineListenAdapter
//import com.suzhou.concept.bean.CloseServiceEvent
//import com.suzhou.concept.bean.ControlVideoEvent
//import com.suzhou.concept.bean.EvaluationSentenceItem
//import com.suzhou.concept.bean.ListenPlayImageEvent
//import com.suzhou.concept.databinding.FineListenFragmentBinding
//import com.suzhou.concept.fragment.BaseFragment
//import com.suzhou.concept.lil.ad.ADLocalUtil
//import com.suzhou.concept.lil.ad.ADLocalUtil.AdType
//import com.suzhou.concept.lil.event.RefreshEvent
//import com.suzhou.concept.lil.service.ListenPlayManager
//import com.suzhou.concept.lil.service.data.ListenPlayEvent
//import com.suzhou.concept.lil.ui.study.eval.util.RxTimer
//import com.suzhou.concept.lil.util.Glide3Util
//import com.suzhou.concept.lil.util.ScreenUtils
//import com.suzhou.concept.service.PlayVideoService
//import com.suzhou.concept.utils.*
//import com.suzhou.concept.utils.view.SelectableTextView
//import com.yd.saas.base.interfaces.AdViewBannerListener
//import com.yd.saas.config.exception.YdError
//import com.yd.saas.ydsdk.YdBanner
//import com.youdao.sdk.nativeads.NativeErrorCode
//import com.youdao.sdk.nativeads.NativeResponse
//import com.youdao.sdk.nativeads.RequestParameters.RequestParametersBuilder
//import com.youdao.sdk.nativeads.YouDaoNative
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import personal.iyuba.personalhomelibrary.utils.ToastFactory
//import java.util.*
//
///**
// * 原文界面
// */
//@RequiresApi(Build.VERSION_CODES.N)
//@SuppressLint("NotifyDataSetChanged")
//class ListenFragment: BaseFragment<FineListenFragmentBinding>() , View.OnClickListener ,
//    SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener, SearchWordListener,
//    MediaPlayer.OnCompletionListener, ServiceConnection {
//    private var startTime=""
//    private var isCn = true
//    private val list = mutableListOf<EvaluationSentenceItem>()
//    private val adapter = FineListenAdapter()
//    private lateinit var player: MediaPlayer
//    private val timer= Timer(true)
//    private lateinit var dialog: AlertDialog
//    private lateinit var binder: PlayVideoService.ControlVideoBinder
//
//    private val handler= Handler(Looper.myLooper()!!) {
//        var index =0
//        kotlin.runCatching {
//            player.isPlaying
//        }.onSuccess { flag->
//            if (flag) {
//                bind.videoSeek.progress = player.currentPosition
//                bind.nowTime.text = player.currentPosition.changeTimeToString()
//            }
//        }
//        for (i in list.indices){
//            val item=list[i]
//            val current=bind.videoSeek.progress
//            item.currentBlue = item.isCurrentSentence(current)
//            if (item.currentBlue)index=list.indexOfFirst { item == it }
//        }
//        bind.sentenceList.post {
//            if (index>0){
//                bind.sentenceList.smoothScrollToPosition(index)
//            }
//        }
//        adapter.notifyDataSetChanged()
//        true
//    }
//
//    override fun FineListenFragmentBinding.initBinding() {
//        //设置图标为暂停
//        bind.controlPlay.setImageResource(R.drawable.start)
//
//        startTime= getRecordTime()
//        GlobalMemory.fineListenIsPlay=true
//        GlobalMemory.fineListenCreate=true
//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        listenSentenceList()
//        listenNextVideo()
//
//        //获取广告信息
//        if (!GlobalMemory.userInfo.isVip()&&!ADLocalUtil.isADBlock()){
//            loadADType()
//        }
//    }
//
//    private fun listenSentenceList(){
//        lifecycleScope.launch {
//            evaluation.sentenceResult.collect{result->
//                result.onSuccess {
//                    initSome()
//                    initDialog()
//                    list.clear()
//                    list.addAll(it)
//                    changeLanguage()
////                    loadUrl(list[0].voaid.toInt())
////                    val intent= Intent(requireContext(),PlayVideoService::class.java)
////                    requireActivity().bindService(intent,this@ListenFragment, Context.BIND_AUTO_CREATE)
//
//                    dismissActivityLoad<TeachMaterialActivity>()
//                }.onError {
//                    it.judgeType().showToast()
//                    dismissActivityLoad<TeachMaterialActivity>()
//                }.onLoading {
//                    showActivityLoad<TeachMaterialActivity>()
//                }
//            }
//        }
//    }
//
//    private fun loadUrl(voaId: Int) {
//
//        //点击通知直接进入activity
//        val url = GlobalMemory.getVideoUrl(voaId)
//        Log.d("播放链接", "loadUrl:--$url")
//
//        if (url.isNotEmpty()){
//            AppClient.videoUrl = url
//
//            if (::player.isInitialized){
//                player.reset()
//                player.setDataSource(requireActivity(), Uri.parse(url))
//                player.prepareAsync()
//            }
//        }
//    }
//
//    private fun initSome() {
//        val time = "00:00"
//        bind.nowTime.text = time
//        bind.sumTime.text = time
//        adapter.wordListener=this
//        bind.sentenceList.adapter = adapter
//        bind.changeLanguage.setOnClickListener(this)
//        bind.settingWindow.setOnClickListener(this)
//        bind.changeLanguage.setBackgroundResource(R.drawable.cn)
//        bind.controlPlay.setOnClickListener(this)
//        bind.controlPlay.setImageResource(R.drawable.pause)
//        if (!::player.isInitialized){
//            player = MediaPlayer()
//        }
//        bind.videoSeek.setOnSeekBarChangeListener(this)
//        player.setOnPreparedListener (this)
//    }
//
//    private fun initDialog(){
//        var position=0
//        val array=resources.getStringArray(R.array.speed)
//        dialog=AlertDialog.Builder(requireContext())
//            .setSingleChoiceItems(array,0) { _, p1 ->
//                position=p1
//            }.setPositiveButton("确定"){ _, _ ->
//                changePlaySpeed(0.5f*(position+1) )
//                array[position].showToast()
//            }.create()
//    }
//    private fun changePlaySpeed(speed:Float){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            try {
//                val params=player.playbackParams
//                params.speed = speed
//                player.playbackParams=params
//            }catch (e:Exception){
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun changeLanguage() {
//        for (i in list.indices){
//            list[i].showCn = isCn
//        }
//        adapter.changeData(list)
//    }
//
//    private fun showWindow(){
//        if (!dialog.isShowing){
//            dialog.show()
//        }
//    }
//    private fun changeBinderStatus(){
//        if (::binder.isInitialized){
//            binder.changeStatus(player.isPlaying)
//        }
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun controlVideo(event: ControlVideoEvent){
//        changePlayStatus()
//    }
//
//    private fun changePlayStatus(){
//        if (player.isPlaying) {
//            pausePlay()
//        } else {
//            startPlay()
//        }
//
//        EventBus.getDefault().post(ListenPlayImageEvent(player.isPlaying))
//    }
//
//
//    private fun upLoadStudyRecord(isEnd:Boolean=false){
//        if (!GlobalMemory.isLogin()){
//            return
//        }
//        lifecycleScope.launch {
//            val percent=player.getPercent()
//            //第几句
//            val testNumber=list.getCurrentSentenceId(player.currentPosition)
//            //已学习单词数
//            val testWords=(percent*list.getAllWords()).toInt().toString()
//            conceptViewModel.updateListenItem((percent*100).toInt())
//            conceptViewModel.submitStudyRecord(startTime,isEnd,testWords,testNumber)
//                .collect{
//                    it.printSubmitResult(isEnd)
//                    delay(1000)
//                    startTime= getRecordTime()
//                }
//        }
//    }
//
//    private fun pausePlay(){
//        player.pause()
//        bind.controlPlay.setImageResource(R.drawable.start)
//        GlobalMemory.fineListenIsPlay=false
//        upLoadStudyRecord()
//
//        binder.changeStatus(player.isPlaying)
//    }
//
//    private fun startPlay(){
//        //设置为当前界面
//        isCurPage = true
//
//        startTime= getRecordTime()
//        player.start()
//        bind.controlPlay.setImageResource(R.drawable.pause)
//        GlobalMemory.fineListenIsPlay=true
//
//        binder.changeStatus(player.isPlaying)
//    }
//
//    override fun onClick(p0: View?) {
//        when (p0?.id) {
//            R.id.change_language -> {
//                isCn = !isCn
//                if (isCn) {
//                    bind.changeLanguage.setBackgroundResource(R.drawable.cn)
//                } else {
//                    bind.changeLanguage.setBackgroundResource(R.drawable.us)
//                }
//                changeLanguage()
//            }
//            R.id.control_play -> {
//                changePlayStatus()
//                changeBinderStatus()
//            }
//            R.id.setting_window->activity?.judgeVip("调速"){ showWindow() }
//        }
//    }
//
//    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//
//    }
//
//    override fun onStartTrackingTouch(p0: SeekBar?) {
//
//    }
//
//    override fun onStopTrackingTouch(p0: SeekBar?) {
//        p0?.let { player.seekTo(it.progress) }
//    }
//
//    /**
//     * 此处需要去掉binder的话岂不是需要两处LiveData?
//     * 不是，不止，跨activity
//     * */
//    override fun onPrepared(p0: MediaPlayer?) {
//        if (::binder.isInitialized){
//            val current=binder.getPlayCurrent()
//            if (current>0){
//                player.seekTo(current)
//            }
//        }
//
//        bind.videoSeek.max = player.duration
//        bind.sumTime.text = player.duration.changeTimeToString()
//        timer.schedule(object : TimerTask() {
//            override fun run() { handler.sendEmptyMessage(0) }}, 0, 200)
//        player.setOnCompletionListener (this)
//
//        //如果刚开始进入别的界面，则需要禁止播放，点击才能播放
//        if (!isCurPage){
//            bind.controlPlay.setImageResource(R.drawable.start)
//            onPause()
//            return
//        }
//
//        //如果切换了界面，也需要禁止播放
//        if (!isCanPlay){
//            bind.controlPlay.setImageResource(R.drawable.start)
//            onPause()
//            return
//        }
//
//        player.start()
//        changeBinderStatus()
//    }
//
//    override fun initEventBus(): Boolean =true
//
//    override fun searchListener(word: String, view: SelectableTextView) {
//        lifecycleScope.launch {
//            delay(500)
//            view.dismissSelected()
//        }
//        Snackbar.make(bind.root,"是否查询$word?", Snackbar.LENGTH_LONG)
//            .setAction("查询"){
//                activity?.startShowWordActivity(word)
//            }.show()
//    }
//    override fun onPause() {
//        super.onPause()
//
//        if (!::player.isInitialized){
//            return
//        }
//
//        if (player.isPlaying){
//            GlobalMemory.currentPosition = player.currentPosition
//        }else{
//            GlobalMemory.currentPosition = -1
//        }
//
//        if (player.isPlaying){
//            pausePlay()
//        }
//
//        isCanPlay = false
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        handler.removeCallbacksAndMessages(null)
//        kotlin.runCatching {
//            with(player) {
//                stop()
//                release()
//                isPlaying
//            }
//        }.onSuccess {
//            if (it) binder.continuePlay(player.currentPosition)
//        }
//
//        GlobalMemory.fineListenIsPlay=false
//        GlobalMemory.fineListenCreate=false
//
//        //有道广告
//        youDaoNative?.destroy()
//        iyubaSdkAD?.destroy()
//    }
//
//    private fun listenNextVideo(){
//        val currentActivity = (activity as TeachMaterialActivity)
//        lifecycleScope.launch {
//            conceptViewModel.switchNextVideo.collect{result->
//                result.onSuccess {
//                    binder.changeTitle()
//                    currentActivity.setTitleText(AppClient.conceptItem.realTitle())
//                    currentActivity.requestSentenceList()
//                }.onError { e->
//                    e.judgeType().showToast()
//                }
//            }
//        }
//    }
//
//    override fun onCompletion(p0: MediaPlayer?) {
//        val isGroupActivity = activity is TeachMaterialActivity
//        if (!isGroupActivity) {
//            return
//        }
//        upLoadStudyRecord(true)
//
//        //将样式切换回原来的
//        val time = "00:00"
//        bind.nowTime.text = time
//        bind.videoSeek.progress = 0
//
//        //请求下一个
////        conceptViewModel.requestLocalNextVideo()
//
//        EventBus.getDefault().post(RefreshEvent(RefreshEvent.AUDIO_STOP,null))
//        //这里处理下，回调响应，根据类型模式处理下
//        EventBus.getDefault().post(RefreshEvent(RefreshEvent.STUDY_AUDIO_SWITCH,""))
//    }
//
//    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        binder=service as PlayVideoService.ControlVideoBinder
//        binder.changeTitle()
//    }
//
//    override fun onServiceDisconnected(name: ComponentName?) {
//
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun closeService(event: CloseServiceEvent){
//        requireActivity().unbindService(this)
//    }
//
//    /*********************广告模式数据************************/
//    //加载广告
//    private fun loadADType() {
//        Log.d("广告显示", "banner: --加载广告")
//        //请求接口
//        adModel.requestAdType("4")
//        //接口回调
//        lifecycleScope.launch {
//            adModel.adResult.collect { result ->
//                result.onError {
//                    it.judgeType().showToast()
//                    loadWebAD("", "")
//                }.onLoading {
//
//                }.onSuccess {
//
//                    //判断广告类型显示
//                    when (it.type) {
//                        "ads1", "ads3", "ads4", "ads5" -> loadIyubaAD(it.startuppic, it.startuppic_Url)
//                        "youdao" -> loadYoudaoAD(it.startuppic, it.startuppic_Url)
//                        "ads2", "web" -> loadWebAD(it.startuppic, it.startuppic_Url)
//                        else -> loadWebAD("", "")
//                    }
//
//                }
//            }
//        }
//    }
//
//    //加载爱语吧sdk广告
//    private var iyubaSdkAD: YdBanner?=null
//    private fun loadIyubaAD(picUrl: String, jumpUrl: String) {
//        Log.d("广告显示", "开屏--爱语吧sdk广告--$picUrl--$jumpUrl")
//
//        activity?.runOnUiThread {
//            try {
//                //获取现在的
//                val dm = resources.displayMetrics
//                val width:Int = dm.widthPixels-20
//                val height:Int = dm.widthPixels * 3 / 20
//                //设置宽高
//                val params: ViewGroup.LayoutParams = bind.frAd.layoutParams
//                params.width = width
//                params.height = height
//                bind.frAd.layoutParams = params
//
//                var key = ADLocalUtil.AdKey.getBannerAdKey(AdType.AD_Ads1);
//
//                iyubaSdkAD = YdBanner.Builder(activity)
//                    .setKey(key)
//                    .setMaxTimeoutSeconds(5)
//                    .setWidth(width.toFloat())
//                    .setHeight(height.toFloat())
//                    .setBannerListener(object :AdViewBannerListener{
//                        override fun onAdFailed(ydError: YdError?) {
//                            Log.d("广告显示", "banner--爱语吧sdk广告--" + ydError?.code + "--" + ydError?.msg + "--" + ydError?.errorType)
//                            loadWebAD(picUrl, jumpUrl)
//                        }
//
//                        override fun onReceived(p0: View?) {
//                            bind.frAd.addView(p0)
//                            bind.frAd.visibility = View.VISIBLE
//                        }
//
//                        override fun onAdExposure() {
//
//                        }
//
//                        override fun onAdClick(p0: String?) {
//
//                        }
//
//                        override fun onClosed() {
//                            bind.frAd.visibility = View.GONE
//                        }
//
//                    }).build()
//                iyubaSdkAD!!.requestBanner()
//            } catch (e: java.lang.Exception) {
//                loadWebAD(picUrl, jumpUrl)
//            }
//        }
//    }
//
//    //加载有道广告
//    private var youDaoNative: YouDaoNative? = null
//    private fun loadYoudaoAD(picUrl: String, jumpUrl: String) {
//        Log.d("广告显示", "banner--有道广告--$picUrl--$jumpUrl")
//
//        try {
//            youDaoNative = YouDaoNative(activity,"230d59b7c0a808d01b7041c2d127da95",object:
//                YouDaoNative.YouDaoNativeNetworkListener{
//                override fun onNativeLoad(response: NativeResponse?) {
//                    if (response == null) {
//                        loadWebAD(picUrl, jumpUrl)
//                        return
//                    }
//
//                    //显示
//                    bind.frAd.visibility = View.VISIBLE
//                    bind.closeAd.visibility = View.VISIBLE
//                    bind.viewYips.visibility = View.VISIBLE
//
//                    /*Glide.clear(bind.imgAd)
//                    Glide.with(activity)
//                        .load(response.mainImageUrl)
//                        .asBitmap()
//                        .into(bind.imgAd)*/
//                    Glide3Util.loadImg(activity,response.mainImageUrl,ADLocalUtil.AdUrl.localBannerADPic,bind.imgAd)
//                    response.recordImpression(bind.imgAd)
//                    bind.imgAd.setOnClickListener {
//                        response.handleClick(bind.imgAd)
//                    }
//                    bind.closeAd.setOnClickListener {
//                        bind.frAd.visibility = View.GONE
//                    }
//                }
//
//                override fun onNativeFail(nativeErrorCode: NativeErrorCode?) {
//                    Log.d("广告显示", "banner--有道广告--" + nativeErrorCode?.code)
//                    loadWebAD(picUrl, jumpUrl)
//                }
//            })
//
//            val requestParameters = RequestParametersBuilder().build()
//            youDaoNative!!.makeRequest(requestParameters)
//        } catch (e: Exception) {
//            loadWebAD(picUrl, jumpUrl)
//        }
//    }
//
//    //加载web类型广告
//    private fun loadWebAD(picUrl: String, jumpUrl: String) {
//        Log.d("广告显示", "banner--web广告--$picUrl--$jumpUrl")
//
//        var newPicUrl = ""
//        var newJumpUrl = ""
//
//        if (!TextUtils.isEmpty(picUrl)) {
//            newPicUrl = ADLocalUtil.AdUrl.fixPicUrl(picUrl);
//            newJumpUrl = ADLocalUtil.AdUrl.fixJumpUrl(jumpUrl)
//        } else {
//            newPicUrl = ADLocalUtil.AdUrl.localBannerADPicUrl()
//            newJumpUrl = ADLocalUtil.AdUrl.localBannerADJumpUrl()
//        }
//
//        loadLocalAD(newPicUrl, newJumpUrl)
//    }
//
//    //加载本地广告
//    private fun loadLocalAD(picUrl: String, jumpUrl: String) {
//        Log.d("广告显示", "banner--最终广告--$picUrl--$jumpUrl")
//
//        bind.apply {
//            frAd.visibility = View.VISIBLE
//            closeAd.visibility = View.VISIBLE
//            viewYips.visibility = View.VISIBLE
//
//            //设置宽高
//            val params: ViewGroup.LayoutParams = frAd.layoutParams
//            val dm = resources.displayMetrics
//            params.width = dm.widthPixels
//            params.height = ScreenUtils.dip2px(activity, 50f)
//            frAd.layoutParams = params
//
//            /*Glide.clear(imgAd)
//            Glide.with(activity)
//                .load(ADLocalUtil.localBannerADPicUrl())
//                .asBitmap()
//                .error(ADLocalUtil.localSplashADPic)
//                .into(imgAd)*/
//            Glide3Util.loadImg(activity,ADLocalUtil.AdUrl.localBannerADPicUrl(),ADLocalUtil.AdUrl.localSplashADPic,imgAd)
//
//            closeAd.setOnClickListener {
//                frAd.visibility = View.GONE
//            }
//            imgAd.setOnClickListener {
//                if (TextUtils.isEmpty(jumpUrl)){
//                    ToastFactory.showShort(activity, "暂无内容")
//                }else{
//                    activity?.startWeb(jumpUrl)
//                }
//            }
//        }
//    }
//
//    //设置停止播放操作
//    private var isCanPlay = true
//    //设置进入时是否为当前界面
//    private var isCurPage = true
//
//    public fun setCanPlay(canPlay:Boolean){
//        isCanPlay = canPlay
//    }
//
//    public fun setCurPage(curPage:Boolean){
//        isCurPage = curPage
//    }
//
//    /*******************************新的音频播放操作**************************/
//    //新的播放器
//    private val exoPlayer: ExoPlayer = ListenPlayManager.getInstance().playService.player
//
//    //播放音频
//    private fun startAudio(isFirstPlay:Boolean,isContinue:Boolean) {
//        if (isFirstPlay){
//            ListenPlayManager.getInstance().playService.playAudio()
//        }else{
//            ListenPlayManager.getInstance().playService.continuePlay(isContinue)
//
//            startTimer()
//        }
//    }
//
//    //暂停播放
//    private fun pauseAudio(){
//        ListenPlayManager.getInstance().playService.pauseAudio()
//        //停止计时
//        RxTimer.cancel()
//        //文章停止
//        handler.removeCallbacksAndMessages(null)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEvent(event: ListenPlayEvent){
//        if (event.showType.equals(ListenPlayEvent.PLAY_prepare_finish)){
//            //加载完成
//            bind.controlPlay.setImageResource(R.drawable.pause)
//            //进度设置
//            bind.videoSeek.max = exoPlayer.duration.toInt()
//            //开启计时
//            startTimer()
//        }
//
//        if (event.showType.equals(ListenPlayEvent.PLAY_complete_finish)){
//            //播放完成
//            bind.controlPlay.setImageResource(R.drawable.start)
//            //停止计时
//            RxTimer.cancel()
//            //停止滚动
//            bind.videoSeek.progress = 0
//            //文章停止
//            handler.removeCallbacksAndMessages(null)
//
//            //选择下一个进行播放
//
//        }
//    }
//
//    //开启计时操作
//    private fun startTimer() {
//        //总时长
//        bind.sumTime.text = exoPlayer.duration.toInt().changeTimeToString()
//
//        //开启计时器
//        RxTimer.multiTimerInMain(0,200L,object:RxTimer.RxAction{
//            override fun action(number: Long) {
//                //当前进度
//                val curProgress = exoPlayer.currentPosition.toInt()
//                //进度显示
//                bind.videoSeek.progress = curProgress
//                //播放时间
//                bind.nowTime.text = exoPlayer.currentPosition.toInt().changeTimeToString()
//                //文章滚动
//                scrollArticleText()
//
//                if (exoPlayer.currentPosition >= exoPlayer.duration){
//                    pauseAudio()
//                }
//            }
//        })
//    }
//
//    //文章滚动显示
//    private fun scrollArticleText() {
//        var index = 0
//        for (i in list.indices){
//            val item=list[i]
//            val current=bind.videoSeek.progress
//            item.currentBlue = item.isCurrentSentence(current)
//            if (item.currentBlue)index=list.indexOfFirst { item == it }
//        }
//        bind.sentenceList.post {
//            if (index>0){
//                bind.sentenceList.smoothScrollToPosition(index)
//            }
//        }
//        adapter.notifyDataSetChanged()
//    }
//}