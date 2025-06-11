package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.YoungSentenceItem
import com.suzhou.concept.databinding.DubInfoItemBinding
import com.suzhou.concept.utils.showSpeakingSpannable
import com.suzhou.concept.utils.visibilityState

/**
苏州爱语吧科技有限公司
@Date:  2022/12/5
@Author:  han rong cheng
 */
class DubInfoAdapter :BaseAdapter<YoungSentenceItem,DubInfoItemBinding>(){
    private lateinit var itemListener:Consumer<Pair<YoungSentenceItem,Int>>
    override fun DubInfoItemBinding.onBindViewHolder(bean: YoungSentenceItem, position: Int) {
        item=bean
        if (bean.wordList.isEmpty()){
            dubText.text=bean.Sentence
        }else{
            //虽然这里用distinct去重解决了评测后出现两个句子的问题，但是最后还是没找到具体原因
            dubText.text = bean.wordList.distinct().showSpeakingSpannable()
        }
        dubFraction.visibilityState(bean.fraction.isEmpty())
        index= with(StringBuilder()){
            append(position+1)
            append("/")
            append(data.size)
            toString()
        }
        root.setOnClickListener {
            if (::itemListener.isInitialized){
                itemListener.accept(Pair(bean,position))
            }
        }
    }

    fun registerItemListener(listener:Consumer<Pair<YoungSentenceItem,Int>>){
        itemListener=listener
    }

}