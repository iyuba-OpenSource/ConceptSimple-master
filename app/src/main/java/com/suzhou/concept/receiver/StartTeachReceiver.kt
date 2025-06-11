package com.suzhou.concept.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.suzhou.concept.AppClient
import com.suzhou.concept.activity.article.TeachMaterialActivity
import com.suzhou.concept.utils.startActivity

/**
苏州爱语吧科技有限公司
@Date:  2022/9/6
@Author:  han rong cheng

 根据AppClient.conceptItem的值来判断是否可点击，最后还是用到了广播
 */
class StartTeachReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, i: Intent?) {
        if (AppClient.conceptItem.isEmpty()){
            return
        }
        context?.startActivity<TeachMaterialActivity>(){
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }
}