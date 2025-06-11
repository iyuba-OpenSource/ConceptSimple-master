package com.suzhou.concept.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.suzhou.concept.utils.showToast

/**
苏州爱语吧科技有限公司
 */
class NextVideoReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        "下一曲".showToast()
    }
}