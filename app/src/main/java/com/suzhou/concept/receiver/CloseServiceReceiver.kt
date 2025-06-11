package com.suzhou.concept.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.suzhou.concept.bean.CloseServiceEvent
import com.suzhou.concept.utils.showToast
import org.greenrobot.eventbus.EventBus

/**
苏州爱语吧科技有限公司
 */
class CloseServiceReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //关闭后台播放的前台服务
        EventBus.getDefault().post(CloseServiceEvent())
    }
}