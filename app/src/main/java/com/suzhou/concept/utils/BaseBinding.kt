package com.suzhou.concept.utils

import androidx.databinding.ViewDataBinding

/**
苏州爱语吧科技有限公司
 */
interface BaseBinding<VB : ViewDataBinding> {
    fun VB.initBinding()
}
