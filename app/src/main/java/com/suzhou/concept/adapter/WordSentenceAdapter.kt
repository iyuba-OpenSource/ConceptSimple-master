package com.suzhou.concept.adapter

import com.suzhou.concept.bean.PickWordItem
import com.suzhou.concept.databinding.ShowWordSentenceItemBinding

/**
苏州爱语吧科技有限公司
 */
class WordSentenceAdapter :BaseAdapter<PickWordItem,ShowWordSentenceItemBinding>(){
    override fun ShowWordSentenceItemBinding.onBindViewHolder(bean: PickWordItem, position: Int) {
        item=bean
    }
}