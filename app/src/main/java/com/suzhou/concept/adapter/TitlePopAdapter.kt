package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.TitlePopBean
import com.suzhou.concept.databinding.TitlePopupItemBinding

/**
苏州爱语吧科技有限公司
@Date:  2022/12/2
@Author:  han rong cheng
 */
class TitlePopAdapter:BaseAdapter<TitlePopBean,TitlePopupItemBinding>() {
    lateinit var itemListener:Consumer<Int>
    override fun TitlePopupItemBinding.onBindViewHolder(bean: TitlePopBean, position: Int) {
        item=bean
        root.setOnClickListener {
            itemListener.accept(position)
        }
    }
}