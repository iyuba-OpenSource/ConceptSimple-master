package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.WordOptions
import com.suzhou.concept.databinding.WordOptionItemBinding

/**
苏州爱语吧科技有限公司
 */
class WordOptionAdapter:BaseAdapter<WordOptions,WordOptionItemBinding>() {
    lateinit var clickListener:Consumer<Int>
    override fun WordOptionItemBinding.onBindViewHolder(bean: WordOptions, position: Int) {
        this.item=bean
        this.root.setOnClickListener {
            clickListener.accept(position)
        }
    }
}