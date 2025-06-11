package com.suzhou.concept.adapter

import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.databinding.EvaluationSentenceItemBinding
import com.suzhou.concept.utils.OnEvaluationListener
import com.suzhou.concept.utils.showSpannable
import com.suzhou.concept.utils.view.ControlVideoProgressView
import kotlin.math.floor

/**
苏州爱语吧科技有限公司
 */
class EvaluationSentenceAdapter : BaseAdapter<EvaluationSentenceItem, EvaluationSentenceItemBinding>() {
    lateinit var onClickListener: OnEvaluationListener
    val videoList= mutableListOf<ControlVideoProgressView>()


    override fun EvaluationSentenceItemBinding.onBindViewHolder(bean: EvaluationSentenceItem, position: Int) {
        item=bean

        if (bean.success){
            sentenceItem.text= AppClient.evaluationMap.showSpannable(bean.onlyKay)
            controlSelf.setOnClickListener {
                onClickListener.playSelf(position,controlSelf)
            }
//            controlSelf.setBackgroundResource(R.drawable.pause_evaluation_old)
        }else{
            sentenceItem.text=bean.Sentence
        }

        controlProgress.apply {
            onClickListener.playOriginal(position,this)
            injectVideoUrl(AppClient.videoUrl, floor((bean.Timing*1000).toDouble()), floor((bean.EndTiming*1000).toDouble()))
            videoList.add(this)
        }

        releaseItem.setOnClickListener {
            onClickListener.releaseSimple(bean)
        }

        root.setOnClickListener {
            onClickListener.showOperate(position)
        }

        mike.setBackgroundResource(R.drawable.mike_grey)
        mike.setOnClickListener {
            onClickListener.openMike(position,mike)
        }
        correctSound.setOnClickListener {
            onClickListener.correctSound(bean)
        }
    }
}