package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.WordItem
import com.suzhou.concept.databinding.KnowledgeItemBinding

/**
苏州爱语吧科技有限公司
@Date:  2023/2/9
@Author:  han rong cheng
 */
class KnowledgeAdapter:BaseAdapter<WordItem,KnowledgeItemBinding>() {
    private lateinit var videoListener:Consumer<String>
    override fun KnowledgeItemBinding.onBindViewHolder(bean: WordItem, position: Int) {
        item=bean
        knowledgeVideo.setOnClickListener {
            videoListener.accept(bean.audio)
        }
    }

    fun inflate(listener:Consumer<String>){
        videoListener=listener
    }

}