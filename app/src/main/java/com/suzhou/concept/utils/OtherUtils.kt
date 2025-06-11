package com.suzhou.concept.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.suzhou.concept.AppClient

object OtherUtils {
    var iyuba_cn="iyuba.cn"
    var iyuba_com="iyuba.com.cn"
    val i_user_speech="iuserspeech.${iyuba_cn}:9001/"
    val user_speech="userspeech.${iyuba_cn}/"
    const val staticStr="staticvip."
    const val appType="newconcept"
    val wordPagingUrl="http://word.$iyuba_cn/words/wordListService.jsp"
    val splashHead="http://app.$iyuba_cn/dev/"
    val selectArray= mutableListOf("A","B","C","D","E")

    //-------------------------------------------------------------------
    private val permissionArray= arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    fun  checkObtainPermission(requestLaunch:ActivityResultLauncher<Array<String>>?, obtain: () -> Unit){
        val index=permissionArray.size-1
        val check=ContextCompat.checkSelfPermission(AppClient.context, permissionArray[index])
        if (check == PackageManager.PERMISSION_GRANTED) {
            obtain()
        }else{
            requestLaunch?.launch(permissionArray)
        }
    }

    fun isNetworkConnected(): Boolean {
        val manager = AppClient.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val work = manager.activeNetworkInfo
        work?.let {
            return work.isAvailable
        }
        return false
    }
    //-----------------------------------------------------------------------------------------------------
    fun convertInt(book: Int) = "第" + when (book) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        else -> ""
    } + "册"

    //-----------------------------------------------------------------------------------------------------
    fun getScreenW(context: Context):Int{
        val manager=context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics=DisplayMetrics()
        manager.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    fun dip2px(context: Context,dpValue:Float):Int{
        val scale=context.resources.displayMetrics.density
        return (dpValue*scale+0.5f).toInt()
    }
}