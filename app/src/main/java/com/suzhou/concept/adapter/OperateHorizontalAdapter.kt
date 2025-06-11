package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.HorizontalOperateBean
import com.suzhou.concept.databinding.HorizontalOperateItemLayoutBinding
import com.suzhou.concept.utils.logic.OperateType

/**
苏州爱语吧科技有限公司
@Date:  2023/2/6
@Author:  han rong cheng
 */
class OperateHorizontalAdapter:BaseAdapter<HorizontalOperateBean,HorizontalOperateItemLayoutBinding>() {
    private lateinit var clickListener:Consumer<OperateType>

    override fun HorizontalOperateItemLayoutBinding.onBindViewHolder(bean: HorizontalOperateBean, position: Int) {
        item=bean
        root.setOnClickListener {
            clickListener.accept(bean.type)
        }
    }

    fun inflateListener(listener:Consumer<OperateType>){
        clickListener=listener
    }
}