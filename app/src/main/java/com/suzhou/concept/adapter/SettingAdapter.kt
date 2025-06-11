package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.SettingItem
import com.suzhou.concept.databinding.SettingItemLayoutBinding

/**
苏州爱语吧科技有限公司
 */
class SettingAdapter : BaseAdapter<SettingItem, SettingItemLayoutBinding>() {
    lateinit var positionListener: Consumer<SettingItem>

    override fun SettingItemLayoutBinding.onBindViewHolder(bean: SettingItem, position: Int) {
        item=bean
        root.setOnClickListener { positionListener.accept(bean) }
        settingItemIcon.setBackgroundResource(bean.icon)
    }
}