package com.suzhou.concept.activity.other

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import coil.load
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.SignResponse
import com.suzhou.concept.databinding.ActivitySignBinding
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.changeString
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.safeToFloat
import com.suzhou.concept.utils.showPositiveDialog
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.visibilityState
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import personal.iyuba.personalhomelibrary.utils.ToastFactory
import java.util.Calendar
import java.util.Locale

class SignActivity : BaseActivity<ActivitySignBinding>(),PlatformActionListener {
    private lateinit var oks: OnekeyShare
    override fun ActivitySignBinding.initBinding() {
        item=GlobalMemory.userInfo
        val day=Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH)
        val back="http://${OtherUtils.staticStr}${OtherUtils.iyuba_cn}/images/mobile/${day}.jpg"
        signBack.load(back)
        (intent.getSerializableExtra(ExtraKeysFactory.signResult) as SignResponse).apply {
            todayWords.text=getTodayWords()
            studyDays.text=getStudyDays()
            overPercent.text=getOverPercent()
            signQr.loadQRCode(qrIconUrl)
        }
        sign.setOnClickListener { startSign() }
    }

    private fun startSign(){
        lifecycleScope.launch {
            flow {
                binding.sign.visibilityState(true)
                //莫名其妙的延时？？？
                kotlinx.coroutines.delay(50)
                emit(0)
            }.onStart {
                showLoad()
            }.collect{

                //先判断微信是否安装
                val platform:Platform = ShareSDK.getPlatform(Wechat.NAME)
                platform.isClientValid {
                    if (!it){
                        dismissLoad()
                        binding.sign.visibilityState(true)
                        ToastFactory.showShort(this@SignActivity,"当前未安装微信，请安装微信后进行打卡")
                    }else{
                        captureView {
                            startShareWechatMoments(it)
                        }
                    }
                }

            }
        }
    }

    private fun captureView(bitmapCallback: (Bitmap) -> Unit) {
        val view = binding.signLayout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val location = IntArray(2)
            view.getLocationInWindow(location)
            PixelCopy.request(window, location.toRect(view.width, view.height), bitmap, {
                if (it == PixelCopy.SUCCESS) {
                    bitmapCallback.invoke(bitmap)
                }
            }, Handler(Looper.getMainLooper()!!))
        } else {
            val tBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(tBitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
            bitmapCallback.invoke(tBitmap)
        }
    }

    private fun IntArray.toRect(width:Int,height:Int)=Rect(this[0],this[1],this[0]+width,this[1]+height)

    private fun startShareWechatMoments(bitmap: Bitmap){
        if (!::oks.isInitialized){
            oks= with(OnekeyShare()){
                disableSSOWhenAuthorize()
                setPlatform(WechatMoments.NAME)
                val titleText="我在${getString(R.string.app_name)}完成了打卡"
                text=titleText
                title=titleText
                setSilent(true)
                callback=this@SignActivity
                this
            }
        }

        oks.apply {
            setImageData(bitmap)
            show(this@SignActivity)
        }
    }

    private fun ImageView.loadQRCode(url:String){
        val w=90
        val h=90
        val bitMatrix=QRCodeWriter().encode(url, BarcodeFormat.QR_CODE,w,h, mutableMapOf(EncodeHintType.CHARACTER_SET to "utf-8"))
        val pixels=IntArray(w*h)
        for (y in 0 until h){
            for (x in 0 until w){
                pixels[y * w+x]=if (bitMatrix.get(x,y)){
                    0xff000000
                }else{
                    0xffffffff
                }.toInt()
            }
        }
        val bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels,0,w,0,0,w,h)
        load(bitmap)
    }

    private fun shareWechatMomentsSuccess(){
        lifecycleScope.launch {
            userAction.shareAddScore().onStart {
                showLoad()
            }.collect{
                dismissLoad()
                it.onSuccess {result->
                    result.apply {
                        userAction.refreshUserInfo()
                        if (this.result=="200"){
                            "打卡成功，您已连续打卡${days}天，"+if (money.safeToFloat()>0){
                                "获得${money.safeToFloat().changeString()}元，总计${totalcredit.safeToFloat().changeString()}元，满十元可在\"爱语吧\"公众号提现"
                            }else{
                                "获得${addcredit}积分，总积分:${totalcredit}"
                            }
                        }else{
                            "今日已打卡，重复打卡不能再次获取红包或积分哦！"
                        }.showPositiveDialog(this@SignActivity) { finish() }
                    }
                }.onFailure {e->
                    e.judgeType().showToast()
                }
            }
        }
    }

    override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
        dismissLoad()
        shareWechatMomentsSuccess()
        binding.sign.visibilityState(false)
    }

    override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
        dismissLoad()
        binding.sign.visibilityState(false)
    }

    override fun onCancel(p0: Platform?, p1: Int) {
        dismissLoad()
        binding.sign.visibilityState(false)
    }

}