package com.suzhou.concept.activity.other

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.databinding.ActivityUseInstructionsBinding
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.OtherUtils
import org.greenrobot.eventbus.EventBus

/**
 * 网页界面
 */
class UseInstructionsActivity : BaseActivity<ActivityUseInstructionsBinding>() {

    //设置固定的类型(当进入这个类型的时候，点击确定后刷新用户信息)
    private var privacyUrl:String = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun ActivityUseInstructionsBinding.initBinding() {
        privacyUrl = "http://iuserspeech.${OtherUtils.iyuba_cn}:9001/api/logout.jsp"

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return try {
                    val url = request.url.toString()
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url)
                        true
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                        startActivity(intent)
                        true
                    }
                } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    false
                }
            }
        }

        webView.webChromeClient = object :WebChromeClient(){
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                if (url.equals(privacyUrl)){
                    //根据信息进行处理
                    if (message!!.contains("注销成功")){
                        //取消用户显示
                        EventBus.getDefault().post(RefreshEvent(RefreshEvent.USER_LOGOUT,""))
                    }
                }

                //单按钮弹窗
                AlertDialog.Builder(this@UseInstructionsActivity)
                    .setMessage(message)
                    .setPositiveButton("确定",object:DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            result!!.confirm()

                            if (url.equals(privacyUrl)){
                                //根据信息进行处理
                                if (message!!.contains("注销成功")){
                                    //取消用户显示(这里有个问题，如果在刚开始的弹窗中操作了，则直接退出app，怪怪的)
                                    finish()
                                }
                            }
                        }
                    }).setCancelable(false)
                    .create().show()
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                //多按钮弹窗
                AlertDialog.Builder(this@UseInstructionsActivity)
                    .setMessage(message)
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            result!!.confirm()
                        }
                    }).setNegativeButton("取消",object:DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            result!!.cancel()
                        }
                    })
                    .setCancelable(false)
                    .create().show()
                return true
            }

            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }
        }

        webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            domStorageEnabled = true
        }
        intent.getStringExtra(ExtraKeysFactory.webUrlOut)?.let {
            webView.loadUrl(it)
        }

        //初始化标题
        var title = intent.getStringExtra("name")
        if (TextUtils.isEmpty(title)){
            title = intent.getStringExtra(ExtraKeysFactory.webUrlOut)
        }
        setTitleText(title!!,true)
    }
}