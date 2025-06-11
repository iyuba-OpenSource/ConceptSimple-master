package com.suzhou.concept.utils.logic

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.lil.util.Glide3Util
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType
import okhttp3.MultipartBody
import java.io.File

/**
苏州爱语吧科技有限公司
 */
object ImageUtil {

    @JvmStatic
    @BindingAdapter("loadUrl")
    fun loadUrl(view: ImageView, url: String?) {
        url?.let {
            if (it.isEmpty()){
                view.load(R.drawable.head)
            }else{
//                view.load(it)
                Glide3Util.loadImg(AppClient.context,url,R.drawable.head_small,view)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("loadRoundUrl")
    fun loadRoundUrl(view: ImageView, url: String?) {
        url?.let {
            if (it.isEmpty()){
                view.load(R.drawable.head)
            }else{
                Glide3Util.loadRoundImg(AppClient.context,url,R.drawable.head_small,10,view)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("loadRoundUrlWithPlaceId")
    fun loadRoundUrlWithPlaceId(view: ImageView, url: String?) {
        url?.let {
            if (it.isEmpty()){
                view.load(R.drawable.head)
            }else{
                Glide3Util.loadRoundImg(AppClient.context,url,R.drawable.ic_picture_bg,10,view)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("loadCircleUrl")
    fun loadCircleUrl(view: ImageView, url: String?) {
        url?.let {
            if (it.isEmpty()){
                view.load(R.drawable.head)
            }else{
                Glide3Util.loadCircleImg(AppClient.context,url,R.drawable.head_small,view)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("loadDrawable")
    fun loadDrawable(view: ImageView, url: Int) {
        view.load(url)
    }

    @JvmStatic
    @BindingAdapter("judgeLayoutBackGround")
    fun judgeLayoutBackGround(layout: LinearLayout, flag:Boolean) {
        if (flag){
            layout.setBackgroundResource(R.drawable.bg_green)
        }else{
            layout.setBackgroundResource(R.drawable.bg_gray)
        }
    }

    @JvmStatic
    @BindingAdapter("judgeTextBackGround")
    fun judgeTextBackGround(layout: TextView, flag:Boolean) {
        if (flag){
            layout.setBackgroundResource(R.drawable.bg_green)
        }else{
            layout.setBackgroundResource(R.drawable.bg_gray)
        }
    }

    @JvmStatic
    @BindingAdapter("judgeEvaluationLayout")
    fun judgeEvaluationLayout(layout: RelativeLayout, flag:Boolean) {
        if (flag){
            layout.setBackgroundResource(R.drawable.border_layout)
        }else{
            layout.setBackgroundResource(R.drawable.transparent_bg)
        }
    }

    fun getBitmapFromUri(uri: Uri, activity: Activity) =
        activity.contentResolver.openFileDescriptor(uri, "r")?.use {
            //use标准函数
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height,
            matrix, true
        )
        bitmap.recycle() // 将不再需要的Bitmap对象回收
        return rotatedBitmap
    }
    fun rotateIfRequired(bitmap: Bitmap, outputImage: File): Bitmap {
        val exif = ExifInterface(outputImage.path)
        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }
    suspend fun compressFile(file: File, context: Context): MultipartBody.Part{
        var lastFile = Compressor.compress(context, file, Dispatchers.IO)
        while (lastFile.length() > 500 * 1000) {
            //将图片压缩至500KB以内
            lastFile = Compressor.compress(context, lastFile, Dispatchers.IO)
        }
        val body = MultipartBody.create(MediaType.parse("image/*"), lastFile)
        return MultipartBody.Part.createFormData("path", lastFile.name, body)
    }
}