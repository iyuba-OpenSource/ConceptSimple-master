package com.suzhou.concept.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.showDialog

class NetChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!OtherUtils.isNetworkConnected()){
            "网络不给力，请检查网络设置".showDialog(context,"去检查"){
                context.startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }
    }
}