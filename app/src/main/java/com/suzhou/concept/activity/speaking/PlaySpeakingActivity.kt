package com.suzhou.concept.activity.speaking

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.tabs.TabLayoutMediator
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.PagerAdapter
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.TitlePopBean
import com.suzhou.concept.databinding.ActivityPlaySpeakingNewBinding
import com.suzhou.concept.fragment.speaking.SpeakingDescFragment
import com.suzhou.concept.lil.ui.my.kouyu.rank.TalkRankFragment
import com.suzhou.concept.lil.util.PermissionDialogUtil
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.TitlePopupWindow
import com.suzhou.concept.utils.addStringTab
import com.suzhou.concept.utils.getLocalPath
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.judgeVip
import com.suzhou.concept.utils.logic.DownloadFileManager
import com.suzhou.concept.utils.showPrivacyDialog
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startShareWorld
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

/**
 * 口语秀播放界面
 */
class PlaySpeakingActivity : BaseActivity<ActivityPlaySpeakingNewBinding>() ,Consumer<Int>, View.OnClickListener,
    PlatformActionListener , DownloadFileManager.DownLoadListener {
    private lateinit var mediator: TabLayoutMediator
    private var isCollect=false
    private var youngItem=ConceptItem()
    private lateinit var popupWindow:TitlePopupWindow
    private lateinit var oks:OnekeyShare
    private var existVideoFile=false
    private lateinit var urlAndFile:Pair<String,File>
    private lateinit var fileManager: DownloadFileManager

    override fun ActivityPlaySpeakingNewBinding.initBinding() {
        GlobalMemory.speakingItem.let {
            speakRating.rating= it.judgeYoungSeries()
            setTitleText(it.title_cn)
            young.emitYoungItem(it)
            youngItem=it
            isCollect=it.youngChild.isCollect
            speakCollect.setImageResource(judgeCollectImage(it.youngChild.isCollect))
        }

        lifecycleScope.launch {
            val youngVideoPath=GlobalMemory.speakingItem.youngChild.youngVideoPath
            flow {
                val lastPath=getLocalPath(youngVideoPath).let {
                    if (it.exists() && GlobalMemory.speakingItem.youngChild.isDownload){
                        existVideoFile=true
                        it.absolutePath
                    }else{
                        with(StringBuilder()){
                            append("http://")
                            val vipJudge=if (GlobalMemory.userInfo.isVip()) "staticvip." else "static0."
                            append(vipJudge)
                            append(OtherUtils.iyuba_cn)
                            append(youngVideoPath)
                            urlAndFile= Pair(this.toString(),it)
                            urlAndFile.first
                        }
                    }
                }
                emit(lastPath)
            }.flowOn(Dispatchers.IO).map {
                with(ExoPlayer.Builder(this@PlaySpeakingActivity).build()){
                    setMediaItem(MediaItem.fromUri(it))
                    prepare()
                    play()
                    this
                }
            }.collect{
                speakPlay.player=it
            }
        }
        speakCollect.setOnClickListener (this@PlaySpeakingActivity)
        speakOther.setOnClickListener (this@PlaySpeakingActivity)
        startSpeaking.setOnClickListener (this@PlaySpeakingActivity)
        speakPager.apply {
//            val array=mutableListOf(SpeakingDescFragment(), SpeakingRankFragment())
            val array=mutableListOf(SpeakingDescFragment(), TalkRankFragment())
            offscreenPageLimit= ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            adapter= PagerAdapter(this@PlaySpeakingActivity,array)
        }
        val titleArray=arrayOf("简介","排行")
        speakingTab.addStringTab(titleArray)
        val autoRefresh=false
        val smoothScroll=false
        mediator= with(TabLayoutMediator(speakingTab,speakPager,autoRefresh,smoothScroll) { tab, position ->
            tab.text = titleArray[position]
        }){
            attach()
            this
        }
        listenOutPdf()
    }

    private fun listenOutPdf(){
        lifecycleScope.launch {
            conceptViewModel.pdfResult.collect{result->
                result.onError {
                    it.judgeType().showToast()
                }.onSuccess {
                    if (it.isEmpty()){
                        "生成失败".showToast()
                        return@onSuccess
                    }
                    val url="http://apps.${OtherUtils.iyuba_cn}/iyuba${it.path}"
                    val clipBoardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData= ClipData.newRawUri("concept_pdf", Uri.parse(url))
                    clipBoardManager.setPrimaryClip(clipData)
                    "${url}链接已复制".showPrivacyDialog(
                        this@PlaySpeakingActivity,
                        "PDF链接生成成功", "下载", "取消", {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }, {})
                }
            }
        }
    }

    private fun judgeCollectImage(flag:Boolean)=(if (flag) R.drawable.star else R.drawable.un_star)

    override fun onDestroy() {
        super.onDestroy()
        mediator.detach()
        if (::fileManager.isInitialized){
            fileManager.cancelJob()
        }
    }

    override fun onPause() {
        super.onPause()

        if (binding.speakPlay.player?.isPlaying == true){
            binding.speakPlay.player?.pause()
        }
    }


    override fun accept(t: Int) {
        when(t){
            //分享
            0->{
                if (!::oks.isInitialized){
                    val url = "http://m." + OtherUtils.iyuba_cn + "/voaS/playPY.jsp?id=" + youngItem.voa_id + "&apptype=newConceptTalk"
                    oks=startShareWorld(youngItem.name,url,this)
                }
                oks.show(this)
            }
            //导出Pdf
            1->{
                judgeVip("导出PDF"){
                    "请选择需要导出的PDF的形式".showPrivacyDialog(this,"提示","导出英文","导出中英双语",{
                        conceptViewModel.requestPdf(1,true,youngItem.voa_id)
                    },{
                        conceptViewModel.requestPdf(0,true,youngItem.voa_id)
                    })
                }
            }
            //下载
            2->{
                if (existVideoFile||!::urlAndFile.isInitialized){
                    return
                }

                //显示下载权限操作
                val pairList: MutableList<android.util.Pair<String, android.util.Pair<String, String>>> = ArrayList()
                pairList.add(android.util.Pair(Manifest.permission.WRITE_EXTERNAL_STORAGE, android.util.Pair("存储权限", "保存下载的文件，用于播放和离线使用")))

                PermissionDialogUtil.getInstance().showMsgDialog(this,pairList,object:PermissionDialogUtil.OnPermissionResultListener{
                    override fun onGranted(isSuccess: Boolean) {
                        if (isSuccess){

                            startDownloadDialog(0.0)
                            val voicePair= with(GlobalMemory.speakingItem.youngChild){
                                Pair(youngBackVoice,getLocalPath(youngBackVoiceEndPath))
                            }
                            fileManager= with(DownloadFileManager(this@PlaySpeakingActivity)){
                                downLoad(urlAndFile,voicePair,youngItem.name,youngItem.name)
                                registerListener(this@PlaySpeakingActivity)
                                this
                            }

                            popupWindow.dismiss()

                            /*startLoading("正在下载相关配音文件～")

                            //使用新的下载方式
                            val downloadVideoUrl = "http://staticvip."+OtherUtils.iyuba_cn+"/video/voa/"+GlobalMemory.speakingItem.bookId+"/"+GlobalMemory.speakingItem.voa_id+".mp4"
                            val saveVideoPath = DownloadNewFileManager.getInstance().getFileSavePath(GlobalMemory.speakingItem.voa_id.toInt(),DownloadNewFileEvent.type_video)

                            var downloadList = mutableListOf<android.util.Pair<String,android.util.Pair<String,String>>>()
                            downloadList.add(android.util.Pair(DownloadNewFileEvent.type_video, android.util.Pair(downloadVideoUrl,saveVideoPath)))
                            DownloadNewFileManager.getInstance().downloadFile(downloadList)*/
                        }
                    }
                })
            }
        }
    }


    override fun onClick(p0: View) {
        when(p0.id){
            R.id.speak_collect->collectContent()
            R.id.speak_other->showPopupWindow(p0)
            R.id.start_speaking-> {
                //增加权限弹窗显示
                val pairList: MutableList<android.util.Pair<String, android.util.Pair<String, String>>> = ArrayList()
                pairList.add(android.util.Pair(Manifest.permission.RECORD_AUDIO, android.util.Pair("麦克风权限", "录制评测时朗读的音频，用于评测打分使用")))
                pairList.add(android.util.Pair(Manifest.permission.WRITE_EXTERNAL_STORAGE, android.util.Pair("存储权限", "保存下载和评测的音视频文件，用于评测打分使用")))

                PermissionDialogUtil.getInstance().showMsgDialog(this@PlaySpeakingActivity, pairList,object:PermissionDialogUtil.OnPermissionResultListener{
                    override fun onGranted(isSuccess: Boolean) {
                        if (isSuccess){
                            startActivity(Intent(this@PlaySpeakingActivity,DubSpeakingActivity::class.java))
                        }
                    }
                })
            }
        }
    }

    private fun showPopupWindow(v:View){
        initPopWindow()
        popupWindow.showAsDropDown(v,50,0)
    }

    private fun collectContent(){
        isCollect=!isCollect
        young.updateItemCollect(youngItem.bookId,youngItem.index,isCollect)
        binding.speakCollect.setImageResource(judgeCollectImage(isCollect))
    }

    private fun initPopWindow(flag:Boolean=existVideoFile){
        val list=mutableListOf(
            TitlePopBean("分享",R.drawable.share),
            TitlePopBean("导出Pdf",R.drawable.pdf),
            TitlePopBean((if (flag) "已" else "")+"下载",R.drawable.download)
        )
        if (!::popupWindow.isInitialized){
            //如何正确处理两处逻辑相同，接口&地址不同的分享&导出PDF？
            popupWindow= with(TitlePopupWindow(this)){
                registerItemListener(this@PlaySpeakingActivity)
                this
            }
        }
        popupWindow.addItem(list)
    }

    override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
        "分享成功".showToast()
    }

    override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
        "分享失败".showToast()
    }

    override fun onCancel(p0: Platform?, p1: Int) {
        "分享失败".showToast()
    }

    //下载完成
    override fun successComplete(uri: Uri) {
        if (!::popupWindow.isInitialized){
            //如何正确处理两处逻辑相同，接口&地址不同的分享&导出PDF？
            popupWindow= with(TitlePopupWindow(this)){
                registerItemListener(this@PlaySpeakingActivity)
                this
            }
        }
        initPopWindow(true)
        existVideoFile=true
        young.updateItemDownload(youngItem)
        stopDownloadDialog()
        "下载完成".showToast()
    }

    //下载中
    override fun downloadProgress(progress: Double) {
        startDownloadDialog(progress)
    }

    //文件下载加载弹窗
    private lateinit var downloadDialog: LoadingMsgDialog

    private fun startDownloadDialog(progress:Double){
        if (!::downloadDialog.isInitialized){
            downloadDialog = LoadingMsgDialog(this)
            downloadDialog.create()
        }

        var showProgress = "正在下载必需的音视频文件，请耐心等待"
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

    /***************************************下载状态******************************************/
    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(event: DownloadNewFileEvent){
        if (event.downloadStatus == DownloadNewFileEvent.state_downloading){
            //下载中
            startLoading(event.showMsg)
        }

        if (event.downloadStatus == DownloadNewFileEvent.state_error){
            //下载异常
            stopLoading()
            //显示提示信息
            ToastUtil.showToast(this,"视频下载异常，请重试～")
            //删除文件
            DownloadNewFileManager.getInstance().cancelDownload()
        }

        if (event.downloadStatus == DownloadNewFileEvent.state_finish){
            //下载完成
            stopLoading()
            //显示提示信息
            ToastUtil.showToast(this,"视频下载完成～")
        }
    }

    //下载弹窗
    private lateinit var loadingMsgDialog: LoadingMsgDialog

    private fun startLoading(showMsg:String){
        Log.d("下载进度", "弹窗--"+::loadingMsgDialog.isInitialized)

        if (!::loadingMsgDialog.isInitialized){
            loadingMsgDialog = LoadingMsgDialog(this)
            loadingMsgDialog.create()

            Log.d("下载进度", "弹窗2--"+::loadingMsgDialog.isInitialized)
        }

        loadingMsgDialog.setMsg(showMsg)

        if (!loadingMsgDialog.isShowing){
            loadingMsgDialog.show()
        }
    }

    private fun stopLoading(){
        if (loadingMsgDialog.isShowing){
            loadingMsgDialog.dismiss()
        }
    }*/
}