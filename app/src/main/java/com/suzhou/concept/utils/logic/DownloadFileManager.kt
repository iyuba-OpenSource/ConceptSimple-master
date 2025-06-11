package com.suzhou.concept.utils.logic

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.suzhou.concept.lil.util.LibRxTimer
import com.suzhou.concept.utils.createDownloadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat

/**
苏州爱语吧科技有限公司
@Date:  2022/12/5
@Author:  han rong cheng
使用DownloadManager下载TimeOutException的大文件
 */
class DownloadFileManager (private val context: Context){
    //进度计时器
    private val timer_progress = "timer_progress"

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var loadingStatusJob:Job
    private lateinit var listener: DownLoadListener
    fun downLoad(videoPair:Pair<String, File>,voicePair:Pair<String, File>, title:String, desc:String){
        val videoRequest=videoPair.createDownloadRequest(title)
//        val voiceRequest=voicePair.createDownloadRequest(desc)
        val downloadManager=context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        loadingStatusJob=uiScope.launch {
            val videoRequestId=downloadManager.enqueue(videoRequest)
//            val voiceRequestId=downloadManager.enqueue(voiceRequest)
            flow {
                while (true){
                delay(20)
                    emit(0)
                }
            }.collect{
                listBytesAndStatus(videoRequestId)
            }
        }
        context.registerReceiver(object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(p1?.action){
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE->{
                        val id=p1.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
                        val uri=downloadManager.getUriForDownloadedFile(id)
                        cancelJob()
                        if (::listener.isInitialized){
                            LibRxTimer.getInstance().cancelTimer(timer_progress)
                            if (uri!=null){
                                listener.successComplete(uri)
                            }
                        }
                    }
                    DownloadManager.ACTION_NOTIFICATION_CLICKED->{

                    }
                }
            }
        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun listBytesAndStatus(downLoadId:Long){
        val query=DownloadManager.Query().setFilterById(downLoadId)
        val format= with(DecimalFormat("#.#%")){
            applyPattern("#.#%")
            this
        }
        val downLoadManager=context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        downLoadManager.query(query).use {
            if (it.moveToFirst()){
                val downloaded=it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)).toDouble()
                val total=it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)).toDouble()
                val progress= (downloaded/total)

                //每隔500毫秒发送进度显示
                if (::listener.isInitialized){
                    LibRxTimer.getInstance().multiTimerInMain(timer_progress,0,1000L,object:
                        LibRxTimer.RxActionListener{
                        override fun onAction(number: Long) {
                            //进度显示不太对，暂时不显示
//                            listener.downloadProgress(BigDecimalUtil.trans2Double(progress))
//
//                            if (progress == 1.0){
//                                RxTimer.cancel()
//                            }
                        }
                    })
                }
            }
        }
    }

    fun cancelJob(){
        if (::loadingStatusJob.isInitialized){
            loadingStatusJob.cancel()
        }
    }

    fun registerListener(listener: DownLoadListener){
        this.listener=listener
    }

    interface DownLoadListener{
        //下载完成
        fun successComplete(uri: Uri)
        //下载进度
        fun downloadProgress(progress:Double)
    }

    private fun createDownloadRequest(url:String){

    }
}