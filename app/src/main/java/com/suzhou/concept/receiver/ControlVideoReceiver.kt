package com.suzhou.concept.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.suzhou.concept.bean.ControlVideoEvent
import com.suzhou.concept.utils.GlobalMemory
import org.greenrobot.eventbus.EventBus

/**
苏州爱语吧科技有限公司
 */
class ControlVideoReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        if (GlobalMemory.fineListenIsPlay){
//            return
//        }
        EventBus.getDefault().post(ControlVideoEvent())
    }
}