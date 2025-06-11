package com.suzhou.concept

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.suzhou.concept.activity.article.EvaluationInfoActivity
import com.suzhou.concept.activity.article.TeachMaterialActivity
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.EvaluationSentenceDataItem
import com.suzhou.concept.bean.RankResponse
import com.suzhou.concept.bean.WordItem
import com.suzhou.concept.lil.util.OAIDNewHelper
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OnActivityLifecycleListener
import com.suzhou.concept.utils.OtherUtils.iyuba_cn
import com.tencent.vasdolly.helper.ChannelReaderUtil
import com.umeng.commonsdk.UMConfigure
import timber.log.Timber

/**
苏州爱语吧科技有限公司
 */
class AppClient:Application() ,Application.ActivityLifecycleCallbacks{
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var conceptItem=ConceptItem()
        var curShowBookId:Int = 1
        //当前的列表数据的位置
        var curListIndex:Int = 0
        const val appName = "concept"
        const val appId = 222
        val forgotPassword = "http://m.$iyuba_cn/m_login/inputPhonefp.jsp"
        val webRegister = "http://m.$iyuba_cn/m_login/inputPhone.jsp"
        var action = 1
        val wordList= mutableListOf<WordItem>()
        val resultWordList= mutableListOf<WordItem>()

        var videoUrl=""
        var evaluationMap= mutableMapOf<Int, List<EvaluationSentenceDataItem>>()
        var rankResponse = RankResponse(data = mutableListOf())
        var showEvaluationHint=true
        //------------------------------------------------------------------------
        fun addWordResult(item: WordItem) {
            resultWordList.add(item)
        }

        fun addWordData(list: List<WordItem>) {
            wordList.clear()
            wordList.addAll(list)
        }
    }


    private lateinit var lifecycleOutListener: OnActivityLifecycleListener

    fun registerLifecycleOutListener(listener: OnActivityLifecycleListener){
        lifecycleOutListener=listener
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        logDebug()
        registerActivityLifecycleCallbacks(this)

        //前置加载oaid操作
        OAIDNewHelper.loadLibrary()
        //预初始化友盟
        val chanel = ChannelReaderUtil.getChannel(this)
        UMConfigure.preInit(this,"5d831637570df34afd00091d",chanel)
    }

    /**
     * debug版本打印日志
     * */
    private fun logDebug(){
        if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)!=0){
            Timber.plant(Timber.DebugTree())
        }
    }
    private fun judgeProtocolCompany(type: String) = when (type) {
        "爱语吧" -> 1
        "画笙" -> 2
        "爱语言" -> 3
        else -> 0
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {


    }

    override fun onActivityResumed(activity: Activity) {


    }

    override fun onActivityPaused(activity: Activity) {


    }

    override fun onActivityStopped(activity: Activity) {
//        if (activity is TeachMaterialActivity){
//            activity.finish()
//        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {


    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is TeachMaterialActivity){
            if (GlobalMemory.currentPosition<=0){
                return
            }
            if (GlobalMemory.startRankInfo){
                return
            }
            if (::lifecycleOutListener.isInitialized){
                lifecycleOutListener.onPauseTeachMaterial()
            }
        }
        if (activity is EvaluationInfoActivity){
            if (::lifecycleOutListener.isInitialized){
                lifecycleOutListener.onPauseTeachMaterial()
            }
        }
    }

    /**
     * 华为-张超男
     * 小米-李英杰
     * vivo-姜亚楠
     * OPPO/应用宝-孟裴瑞
     * 魅族-刘隽萌 vivo
     * 百度-张蕊 OPPO
     * 360-黎佳林 小米
     * 联想-李正红 OPPO
     * 三星-施穆苏 小米
     * */

    /**
     * 去掉大部分LiveData
     * 某些初始化的类，授权初始一次、每次打开需要授权混乱
     * 全局只有一个Service实例即可
     * 建议把Timer改为Flow的delay()
     * @RequiresApi(Build.VERSION_CODES.*) 加判断而不是注解
     * 两处simple.ifEmpty
     * */
}