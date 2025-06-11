package com.suzhou.concept.utils.advertise

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.util.Consumer
import coil.load
import com.suzhou.concept.utils.visibilityState
import com.youdao.sdk.nativeads.*

/**
苏州爱语吧科技有限公司
@Date:  2022/12/20
@Author:  han rong cheng
 */
class YouDaoBanner (private val context: Context, private val view: ViewGroup,private val  image: ImageView):YouDaoNative.YouDaoNativeNetworkListener{
    private lateinit var errorListener:Consumer<String>
    private val youDaoNative:YouDaoNative = YouDaoNative(context,"230d59b7c0a808d01b7041c2d127da95",this)
    override fun onNativeLoad(response: NativeResponse) {
        image.setOnClickListener {
            response.handleClick(image)
        }
        val list=mutableListOf(response.mainImageUrl)
        ImageService.get(context,list,object:ImageService.ImageServiceListener{
            override fun onSuccess(bitmaps: MutableMap<String, Bitmap>) {
                if (response.mainImageUrl.isNotEmpty()){
                    bitmaps[response.mainImageUrl]?.let {
                        view.visibilityState(false)
                        image.visibilityState(false)
                        image.load(it)
                        response.recordImpression(image)
                    }
                }
            }

            override fun onFail() {
                view.visibilityState(true)
            }
        } )
    }

    override fun onNativeFail(p0: NativeErrorCode) {
        view.visibilityState(true)
        if (::errorListener.isInitialized){
            errorListener.accept(p0.toString())
        }
    }

    fun registerErrorListener(listener:Consumer<String>){
        errorListener=listener
    }
    fun loadYouDaoBanner(){
        youDaoNative.makeRequest(RequestParameters.RequestParametersBuilder().build())
    }

}