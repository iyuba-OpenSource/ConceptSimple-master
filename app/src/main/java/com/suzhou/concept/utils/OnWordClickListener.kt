package com.suzhou.concept.utils

import java.util.Calendar

/**
苏州爱语吧科技有限公司
 */
abstract class OnWordClickListener {
    private var lastClickTime = 0L
    fun onClick(word: String) {
        //这里设置的时间慢点，因为时间长了外面处理完成后会导致下一个单词查询错误
        val current = Calendar.getInstance().timeInMillis
        if (current - lastClickTime > 500L) {
            lastClickTime = current
            onNoDoubleClick(word)
        }

        onNoDoubleClick(word)
    }

    protected abstract fun onNoDoubleClick(str: String)
}