package com.suzhou.concept.activity.speaking

import android.net.Uri
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.CloseDubSpeakingEvent
import com.suzhou.concept.bean.WavListItem
import com.suzhou.concept.databinding.ActivityPreViewSpeakingBinding
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.getLocalPath
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * 配音预览界面
 */
class PreViewSpeakingActivity : BaseActivity<ActivityPreViewSpeakingBinding>() {


    override fun ActivityPreViewSpeakingBinding.initBinding() {
        setTitleText("预览配音")

        val averageScore=intent.getFloatExtra(ExtraKeysFactory.dubAverageScore,0F)
        val completePercent=intent.getIntExtra(ExtraKeysFactory.completePercent,0)
        accuracy=averageScore.toInt()
        completed=completePercent

        val wavList=intent.getParcelableArrayListExtra<WavListItem>(ExtraKeysFactory.dubWavListItemList)
        returnChange.setOnClickListener {
            finish()
        }
        returnSpeaking.setOnClickListener {
            finish()
            EventBus.getDefault().post(CloseDubSpeakingEvent())
        }

        releaseShare.setOnClickListener {
            if (wavList != null) {
                young.mergeReleaseSpeaking(averageScore.toInt(),wavList)
            }
        }
        lifecycleScope.launch {
            young.mergeReleaseResult.collect{result->
                result.onError {
                    dismissLoad()
                    it.judgeType().showToast()
                }.onLoading {
                    showLoad()
                }.onSuccess {
                    dismissLoad()
                    it.showToast()
                }
            }
        }
        with(ExoPlayer.Builder(this@PreViewSpeakingActivity).build()) {
            val endPath=GlobalMemory.speakingItem.youngChild.youngVideoPath
            val uri= Uri.parse(getLocalPath(endPath).absolutePath)
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            volume=0f
            play()
            this
        }.let {
            preViewPlayer.player =it
        }
    }
}