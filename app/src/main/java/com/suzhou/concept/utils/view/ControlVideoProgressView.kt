package com.suzhou.concept.utils.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.suzhou.concept.R
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.logic.GlobalPlayManager
import com.suzhou.concept.utils.logic.VoiceStatus
import com.suzhou.concept.utils.showToast
import java.util.*

class ControlVideoProgressView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {
    val controlVideo: RoundProgressBar

    /**封成方法*/
    private var localStart:Long =0
    private var localEnd:Long =0

    private val timer: Timer


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.control_video_progress, this)
        controlVideo = view.findViewById(R.id.control_video)
        timer = Timer(true)
    }

    fun injectVideoUrl(url: String, start: Double, end: Double) {
        controlVideo.apply {
            setBackgroundResource(R.drawable.play_evaluation_old)
            val max=(end.toLong()-start.toLong()).toInt()

            var progress = (GlobalPlayManager.getCurrentPosition()-start.toLong()).toInt()
            if (progress>=max){
                progress = max
            }
            inflateProgress(progress)
            inflateMax(max)

            Log.d("进度条显示", "start--"+start+"--end--"+end+"--max--"+max+"--progress--"+progress)

            setOnClickListener { videoClick(url) }
        }
        localStart = start.toLong()
        localEnd = end.toLong()
    }

    //如何在不使用静态变量的情况下使音频播放与录音互斥？
    private fun videoClick(url:String){
        if (GlobalMemory.isRecording){
            "正在录音".showToast()
            return
        }
        if (GlobalMemory.isPlayingSelf){
            "正在播放评测".showToast()
            return
        }

        if (GlobalPlayManager.isPlaying()){
            GlobalPlayManager.pause()
        }else{
            if (GlobalPlayManager.isPause()){
                GlobalPlayManager.start()
            }else{
                val pair=Pair(VoiceStatus.EVAL_ORIGINAL,url)
                GlobalPlayManager.addUrl(pair)
            }
            GlobalPlayManager.seekTo(localStart)
        }
    }

}