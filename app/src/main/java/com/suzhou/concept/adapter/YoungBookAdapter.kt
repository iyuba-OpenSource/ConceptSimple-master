package com.suzhou.concept.adapter

import com.suzhou.concept.bean.YoungItem
import com.suzhou.concept.databinding.YoungBookItemBinding
import com.suzhou.concept.utils.SelectBookListener

/**
苏州爱语吧科技有限公司
@Date:  2022/10/15
@Author:  han rong cheng
 */
class YoungBookAdapter:BaseAdapter<YoungItem,YoungBookItemBinding>() {
    lateinit var selectBookListener: SelectBookListener
    override fun YoungBookItemBinding.onBindViewHolder(bean: YoungItem, position: Int) {
        item=bean
        root.setOnClickListener {
            selectBookListener.listener(bean.toLanguage())
        }
    }
}