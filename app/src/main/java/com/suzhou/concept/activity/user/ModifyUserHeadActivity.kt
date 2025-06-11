package com.suzhou.concept.activity.user

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.databinding.ActivityModifyUserHeadBinding
import com.suzhou.concept.utils.*
import com.suzhou.concept.utils.logic.ChoosePhotoContracts
import com.suzhou.concept.utils.logic.ImageUtil
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.Q)
class ModifyUserHeadActivity : BaseActivity<ActivityModifyUserHeadBinding>(), View.OnClickListener {
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File
    private val requestTakePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.checkPermission { operatePhoto() }
    }
    private val requestTakeData = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) takePicture()
    }
    private val requestChooseData = registerForActivityResult(ChoosePhotoContracts()) {
        if (it!=null) choosePicture(it)
    }
    private val requestChoosePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.checkPermission { requestChooseData.launch(null) }
    }

    override fun ActivityModifyUserHeadBinding.initBinding() {
        setTitleText("更改头像")
        nowHead.load(GlobalMemory.userInfo.imgSrc)
        albumSelection.setOnClickListener(this@ModifyUserHeadActivity)
        finishChoose.setOnClickListener(this@ModifyUserHeadActivity)
        photograph.setOnClickListener(this@ModifyUserHeadActivity)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.album_selection -> requestChoosePermission.checkObtainPermission { requestChooseData.launch(null) }
            R.id.standard_left -> finish()
            R.id.finish_choose -> startUpload()
            R.id.photograph -> requestTakePermission.checkObtainPermission { operatePhoto() }
        }
    }

    private fun choosePicture(data: Uri) {
        val bitmap = ImageUtil.getBitmapFromUri(data, this)
        binding.nowHead.load(bitmap)
        val fileName = uriToFileQ(this,data)
        fileName?.let { outputImage =fileName }
    }

    private fun startUpload() {
        if (!::outputImage.isInitialized){
            "请选择图片".showToast()
            return
        }
        lifecycleScope.launch {
            val part= ImageUtil.compressFile(outputImage,this@ModifyUserHeadActivity)
            userAction.uploadPhoto(part)
                .onStart {
                    showLoad()
                }.flatMapConcat {
                    if (it.isSuccess){
                        val headUrl=it.getOrNull()?.bigUrl.toString()
                        GlobalMemory.userInfo.imgSrc=headUrl
                        userAction.modifyLocalHead(headUrl)
                    }else{
                        false.emitFlow()
                    }
                }.collect{
                    dismissLoad()
                    ("更改" + if (it) {
                        finish()
                        "成功"
                    } else "失败").showToast()
                }
        }
    }

    private fun takePicture() {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        binding.nowHead.load(bitmap)
    }

    private fun operatePhoto() {
        //创建file对象，用于存储拍照后的照片
        outputImage = File(externalCacheDir, "output_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, resources.getString(R.string.authorities), outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        //启动相机程序
        requestTakeData.launch(imageUri)
    }

    private fun uriToFileQ(context: Context, uri: Uri): File? =
        if (uri.scheme == ContentResolver.SCHEME_FILE)
            File(requireNotNull(uri.path))
        else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件保存到沙盒
            val contentResolver = context.contentResolver
            val displayName = "${System.currentTimeMillis()}${Random.nextInt(0, 9999)}.${
                MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))}"
            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        } else null


}