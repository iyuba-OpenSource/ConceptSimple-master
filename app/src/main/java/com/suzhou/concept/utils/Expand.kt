package com.suzhou.concept.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.iyuba.headlinelibrary.data.model.Headline
import com.iyuba.module.dl.BasicDLPart
import com.iyuba.module.favor.data.model.BasicFavorPart
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.dollar.MemberCentreActivity
import com.suzhou.concept.activity.other.SendBookActivity
import com.suzhou.concept.activity.other.UseInstructionsActivity
import com.suzhou.concept.activity.user.LoginActivity
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.EvaluationSentenceDataItem
import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.LoginResponse
import com.suzhou.concept.bean.StudyRecordResponse
import com.suzhou.concept.bean.TestRecordItem
import com.suzhou.concept.bean.WordItem
import com.suzhou.concept.bean.WordOptions
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.ui.my.wordNote.wordDetail.WordShowNewActivity
import com.suzhou.concept.lil.util.BigDecimalUtil
import com.suzhou.concept.utils.logic.VoiceStatus
import com.suzhou.concept.viewmodel.UserActionViewModel
import data.ConfigData
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.net.UnknownHostException
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
苏州爱语吧科技有限公司
kotlin的扩展方法
 */
fun String.changeEncode(): String = URLEncoder.encode(this, "utf-8")
fun String.toMd5(): String {
    val hash = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return with(StringBuilder()) {
        hash.forEach {
            val i = it.toInt() and (0xFF)
            var temp = Integer.toHexString(i)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            this.append(temp)
        }
        this.toString()
    }
}

fun String.checkPhone() = (this.length == 11)

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        Toast.makeText(AppClient.context, this, duration).show()
    } else {
        Looper.prepare()
        Toast.makeText(AppClient.context, this, duration).show()
        Looper.loop()
    }
}

fun String.showDialog(context: Context, positive: String = "确定", method: () -> Unit) {
    showPrivacyDialog(context, "提示", positive, "取消", method, null)
}

fun CharSequence.showPrivacyDialog(
    context: Context,
    title: CharSequence,
    positive: CharSequence,
    negative: CharSequence,
    positiveMethod: () -> Unit,
    negativeMethod: (() -> Unit)?
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(this)
        .setPositiveButton(positive) { _, _ ->
            positiveMethod()
        }
        .setNegativeButton(negative) { _, _ ->
            negativeMethod?.invoke()
        }
        .show()
}

fun Activity.startShowWordActivity(s: String, star: Boolean = false) {
//    startActivity<ShowWordActivity> {
//        putExtra(ExtraKeysFactory.definitionWord,s)
//        putExtra(ExtraKeysFactory.listWord,star)
//    }
    //切换界面
    startActivity<WordShowNewActivity> {
        putExtra(ExtraKeysFactory.definitionWord, s)
        putExtra(ExtraKeysFactory.listWord, star)
    }
}

fun Activity.startQQ(qq: Int) {
    val url = "mqqwpa://im/chat?chat_type=wpa&uin=$qq"
    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(i)
}

fun Long.timeStampDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val format = SimpleDateFormat(pattern, Locale.CHINA)
    return format.format(Date(this))
}

fun RadioGroup.addRadioButton(selectArray: Array<String>, enable: Boolean = true) {
    for (i in selectArray.indices) {
        val showText = OtherUtils.selectArray[i] + "." + selectArray[i]
        this.addSingleRadioButton(context, showText, i, enable = enable)
    }
}

fun RadioGroup.addSingleRadioButton(
    context: Context,
    text: String,
    id: Int,
    margin: Int = 10,
    enable: Boolean = true
) {
    val radioButton = RadioButton(context)
    val line = View(context)
    line.setBackgroundColor(Color.BLACK)
    radioButton.isClickable = enable
    radioButton.id = id
    val span = SpannableString(text)
    if (margin == 20) {
        val fore = ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow))
        span.setSpan(fore, text.indexOf(":"), text.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    } else {
        val absolute = AbsoluteSizeSpan(22, true)
        span.setSpan(absolute, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    radioButton.text = span
    radioButton.textSize = 18f
    val params = RadioGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    val lineParams = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
    params.setMargins(margin)
    this.addView(radioButton, params)
    this.addView(line, lineParams)
}

fun Activity.showGoLoginDialog() {
    "您还未登录，是否跳转登录界面？".showDialog(this, "跳转登录") {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}

fun nowTime() = System.currentTimeMillis().timeStampDate("yyyy-MM-dd")

inline fun FragmentActivity.judgeLogin(
    userAction: UserActionViewModel,
    crossinline isLogin: (login: LoginResponse) -> Unit
) {
    lifecycleScope.launch {
        val localLogin = userAction.getLoginResponse().first()
        if (localLogin.isEmpty()) {
            showGoLoginDialog()
        } else {
            isLogin(localLogin)
        }
    }
}

inline fun FragmentActivity.judgeLogin(isLogin: () -> Unit) {
    if (GlobalMemory.isLogin()) {
        isLogin.invoke()
    } else {
        showGoLoginDialog()
    }
}

inline fun <reified T : Activity> FragmentActivity.judgeLogin() {
    if (!GlobalMemory.isLogin()) {
        showGoLoginDialog()
        return
    }
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T : AppCompatActivity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = with(Intent(this, T::class.java)) {
        block()
        this
    }
    startActivity(intent)
}


inline fun FragmentActivity.judgeVip(features: String, isVip: () -> Unit) {
    judgeLogin {
        if (GlobalMemory.userInfo.isVip()) {
            isVip()
        } else {
            "会员可以无限制$features，要去开通会员吗？".showDialog(this@judgeVip) {
                startActivity<MemberCentreActivity> { }
            }
        }
    }
}

@SuppressLint("CheckResult")
fun SubsamplingScaleImageView.loadLargeImage(res: Int) {
    isQuickScaleEnabled = true
    maxScale = 15F
    isZoomEnabled = true
    setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
    Glide.with(this.context).load(res).downloadOnly(object : SimpleTarget<File>() {
        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(resource?.absolutePath, options)
            val sWidth = options.outWidth
            val sHeight = options.outHeight
            options.inJustDecodeBounds = false
            val wm = ContextCompat.getSystemService(context, WindowManager::class.java)
//            val width = wm?.defaultDisplay?.width ?: 0
            val height = wm?.defaultDisplay?.height ?: 0
            if (sHeight >= height && sHeight / sWidth >= 3) {
                setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                setImage(
                    ImageSource.uri(Uri.fromFile(resource)),
                    ImageViewState(0.5f, PointF(0f, 0f), 0)
                )
            } else {
                setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
                setImage(ImageSource.uri(Uri.fromFile(resource)))
                setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE)
            }
        }
    })
}

fun TextView.judgeBookType(
    bookType: String = "英文名著",
    book: String = "全套26本的英文名著电子书",
    activity: Activity
) {
    val qqCode = SendBookActivity.qqCode
    val str = "送${bookType}啦!" +
            "\n只要在应用商店中对本应用进行五星好评，并截图发给QQ：${qqCode}，即可获得3天的会员试用资格或${book}哦。" +
            "\n机会难得，不容错过，小伙伴们赶快行动吧!"
    val builder = SpannableStringBuilder()
    builder.append(str)
    val index = str.indexOf("QQ：") + 3
    builder.setSpan(object : ClickableSpan() {
        override fun onClick(p0: View) {
            activity.startQQ(qqCode)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
            ds.color = ContextCompat.getColor(this@judgeBookType.context, R.color.main)
        }
    }, index, index + 10, 0)
    this.movementMethod = LinkMovementMethod.getInstance()
    this.setText(builder, TextView.BufferType.SPANNABLE)
}

fun Map<String, Boolean>.checkPermission(successOperate: () -> Unit) {
    var count = 0
    this.values.forEach {
        if (it) count++
    }
    if (count == this.size) {
        successOperate()
    } else {
        "申请权限被拒绝".showToast()
    }
}

fun ActivityResultLauncher<Array<String>>.checkObtainPermission(obtain: () -> Unit) {
    OtherUtils.checkObtainPermission(this, obtain)
}

fun Map<Int, List<EvaluationSentenceDataItem>>.showSpannable(onlyKey: String): SpannableStringBuilder {
    val sumList = mutableListOf<EvaluationSentenceDataItem>()
    this.values.forEach { sumList.addAll(it) }
    val finalList = sumList.filter { it.onlyKay == onlyKey }
    val build = SpannableStringBuilder()
    for (i in finalList.indices) {
        val item = finalList[i]
        val itemWord = item.content + " "
        build.append(itemWord)
        val green = Color.rgb(39, 124, 60)
        val colorSpan = when {
            item.score > 4 -> ForegroundColorSpan(green)
            item.score < 2.5 -> ForegroundColorSpan(Color.RED)
            else -> ForegroundColorSpan(Color.BLACK)
        }
        if (i == 0) {
            build.setSpan(colorSpan, 0, itemWord.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        } else {
            val start = build.length - itemWord.length
            val end = start + itemWord.length
            build.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }
    return build
}

fun String.changeVideoUrl() = "http://${OtherUtils.user_speech}voa/$this"

fun Int.changeTimeToString(): String {
    val second = this / 1000
    val end = (second % 60)
    return if (end >= 10) {
        (second / 60).toString() + ":" + end
    } else {
        (second / 60).toString() + ":" + "0" + end
    }
}

fun FragmentActivity.startWeb(url: String) {
    startActivity<UseInstructionsActivity> {
        putExtra(ExtraKeysFactory.webUrlOut, url)
    }
}

//隐私政策
fun Context.getProtocolUrl(): String {
    val appType = resources.getString(R.string.app_name)
    /*return if (appType.contains("极速版")) {
        "http://${OtherUtils.i_user_speech}api/protocolpri.jsp?apptype=${appType.changeEncode()}&company=${ConfigData.company_type}"
    } else {
        "http://${OtherUtils.i_user_speech}api/protocolpri.jsp?apptype=${appType.changeEncode()}&company=${ConfigData.company_type}"
    }*/
    return "http://${OtherUtils.i_user_speech}api/protocolpri.jsp?apptype=${appType.changeEncode()}&company=${ConfigData.privacy_company_type}"
}
//用户协议
fun Context.getPolicyUrl(): String {
    val appType = resources.getString(R.string.app_name)
    /*return if (appType.contains("极速版")) {
        "http://${OtherUtils.i_user_speech}api/protocoluse.jsp?apptype=${appType.changeEncode()}&company=${ConfigData.company_type}"
    } else {
        "http://${OtherUtils.i_user_speech}api/protocoluse.jsp?apptype=${appType.changeEncode()}&company=${ConfigData.company_type}"
    }*/
    return "http://${OtherUtils.i_user_speech}api/protocoluse.jsp?apptype=${appType.changeEncode()}&company=${ConfigData.privacy_company_type}"
}
//vip会员协议
fun Context.getVipAgreement(): String {
    val appType = resources.getString(R.string.app_name)
    return "http://${OtherUtils.i_user_speech}api/vipServiceProtocol.jsp?company=${ConfigData.vip_company_type}&type=app"
}
//第三方信息共享清单
fun Context.getTiredSdkInfoUrl():String{
    val appType = resources.getString(R.string.app_name)
    return "https://ai.${OtherUtils.iyuba_cn}/api/thirdSDKInfosharing.jsp?apptype=${appType}"
}
//个人信息收集清单
fun Context.getPersonalInfoUrl():String{
    val appType = resources.getString(R.string.app_name)
    return "http://iuserspeech.${OtherUtils.iyuba_cn}:9001/api/personalInfoList.jsp?apptype=${appType}"
}


fun Context.requestPermission(permission: String, success: () -> Unit, fail: () -> Unit) {
    val check = ActivityCompat.checkSelfPermission(this, permission)
    if (PackageManager.PERMISSION_GRANTED == check) {
        success()
    } else {
        fail()
    }
}


fun Throwable.judgeType() = when (this) {
    is NullPointerException -> "空指针异常"
    is UnknownHostException -> "请检查网络状态"
    is IllegalStateException -> "非法状态异常"
    else -> "未知异常\n${this}"
}

fun String.safeToInt() = if (isEmpty()) 0 else toInt()
fun String.safeToLong() = if (isEmpty()) 0 else toLong()
fun String.safeToFloat() = if (isEmpty()) 0F else toFloat()

fun RadioGroup.getSingleRadio(flag: Boolean = false): List<RadioButton> {
    val list = ArrayList<View>(childCount)
    for (i in 0..childCount) {
        list.add(getChildAt(i))
    }
    return with(list.filterIsInstance<RadioButton>()) {
        (if (flag) this else filter { it.isChecked })
    }
}

fun Boolean.getServiceImage() = if (!this) R.drawable.start else R.drawable.pause

/*fun Activity.startPayActivity(payType: String, payPrice: String, buyType: Boolean = false) {
    val intent = with(Intent(this, PayActivity::class.java)) {
        putExtra(ExtraKeysFactory.payType, payType)
        putExtra(ExtraKeysFactory.payPrice, payPrice)
        putExtra(ExtraKeysFactory.buyType, buyType)
        this
    }
    startActivity(intent)
}*/


fun View.visibilityState(flag: Boolean) {
    visibility = if (flag) View.GONE else View.VISIBLE
}


fun TextInputLayout.showUserNameEmpty(
    hint: String = "用户名长度为3~15个字符",
    flag: Boolean,
    method: () -> Unit
) {
    this.error = if (flag) {
        hint
    } else {
        method()
        ""
    }
}

fun TextInputLayout.showPassWordEmpty(flag: Boolean, method: () -> Unit) {
    this.showUserNameEmpty("密码为6~15个字符", flag, method)
}

fun <T> T.emitFlow() = flow { emit(this@emitFlow) }

fun RecyclerView.addDefaultDecoration() {
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

fun getRecordTime(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(Date())

fun List<EvaluationSentenceItem>.getAllWords(): Int {
    var count = 0
    forEach {
        val sentenceArray = it.Sentence.split(" ")
        count += sentenceArray.size
    }
    return count
}

fun MediaPlayer.getPercent() = (currentPosition.toFloat()) / (duration.toFloat())

fun Result<StudyRecordResponse>.printSubmitResult(isEnd: Boolean) {
    val tag = "听力记录上传结果"
    onSuccess { data ->
        val result = if (data.result == "1") {
            ("数据提交成功" + if (data.scores == "0") "" else "，恭喜您获得了${data.scores}分")
        } else {
            if (data.result == "0") data.message else "数据提交异常"
        }
        Timber.tag(tag).d("result_____________________${result}")

        //增加奖励显示显示
        if (data.result == "1" && isEnd) {
            val money: Double =
                if (TextUtils.isEmpty(data.reward)) 0.0 else BigDecimalUtil.trans2Double(data.reward.toDouble() * 0.01f)
            if (money > 0) {
                val showMsg = String.format("本次学习获得${money}元,已自动存入您的钱包账户")
                EventBus.getDefault().post(RefreshEvent(RefreshEvent.SHOW_TOAST, showMsg))
            }
        }
    }.onFailure {
        Timber.tag(tag).d("error_____________________${it}")
    }
}

fun List<EvaluationSentenceItem>.getCurrentSentenceId(currentPosition: Int): String {
    forEach {
        if (it.isCurrentSentence(currentPosition)) {
            return it.IdIndex.toString()
        }
    }
    return "1"
}

fun Float.changeString(): String = DecimalFormat("0.00").format(this * 0.01)

fun String.showPositiveDialog(context: Context, positiveMethod: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("提示")
        .setMessage(this)
        .setCancelable(false)
        .setPositiveButton("确定") { _, _ ->
            positiveMethod()
        }
        .show()
}

/**
 * 设置AlertDialog的尺寸比例
 * */
fun AlertDialog.showNow(display: DisplayMetrics) {
    show()
    val width = display.widthPixels
    val height = (display.heightPixels * 0.8F).toInt()
    window?.setLayout(width, height)
}

/**
 * 部分青少本课本分上(A)下(B)册
 * */
fun List<WordItem>.toWordMap(
    cursorIndex: Int = 0,
    youngFlag: Boolean = false
): Map<Int, WordOptions> {
    val wordMap = mutableMapOf<Int, WordOptions>()
    forEach { item ->
        val index = with(item) {
            //区别青少版飞雷神
            (if (youngFlag) unitId else voa_id)
        }
        wordMap[index]?.let { word ->
            word.total += 1
            word.voaId = index
            word.finished = filter {
                val sameUser = (it.userId == GlobalMemory.userInfo.uid)
                val groupId = with(it) {
                    //区别青少版飞雷神
                    (if (youngFlag) unitId else voa_id)
                }
                val sameVoaId = (groupId == word.voaId)
                sameUser && sameVoaId && it.correct
            }.size
            return@forEach
        }
        wordMap[index] = WordOptions()
    }
    var index = 0
    wordMap.keys.forEach { key ->
        ++index
        wordMap[key]?.index = (index + cursorIndex)
    }
    return wordMap
}

fun List<WordOptions>.toWordItemList(netList: List<WordItem>): List<WordItem> {
    val fatherList = mutableListOf<WordItem>()
    forEach { option ->
        val childList = netList.filter { item ->
            item.voa_id == option.voaId
        }.map { word ->
            word.unitId = option.index
            word
        }
        fatherList.addAll(childList)
    }
    return fatherList
}


fun BasicFavorPart.changeHeadline() = with(Headline()) {
    this.type = this@changeHeadline.type
    this.titleCn = this@changeHeadline.titleCn
    this.title = this@changeHeadline.title
    this.sound = this@changeHeadline.sound
    this.id = this@changeHeadline.id
    this.pic = this@changeHeadline.pic
    this
}

fun BasicDLPart.changeHeadline() = with(Headline()) {
    this.type = this@changeHeadline.type
    this.titleCn = this@changeHeadline.titleCn
    this.title = this@changeHeadline.title
    this.id = this@changeHeadline.id
    this.pic = this@changeHeadline.pic
    this
}


fun List<ConceptItem>.changeRoomData(type: LanguageType): List<ConceptItem> {
    for (i in indices) {
        val item = this[i]
        item.apply {
            changeLesson("Lesson${i + 1} ")
            bookId = type.bookId
            language = type.language
            index = i
            if (type.isUK()) {
                voa_id = (voa_id.toInt() * 10).toString()
            }
        }
    }
    return this
}


fun FragmentActivity.clickSkipWeb(img: ImageView, otherOperate: () -> Unit = {}) {
    img.setOnClickListener {
        otherOperate.invoke()
//        startWeb("http://www.iyuba.cn")
        startWeb("http://app.${OtherUtils.iyuba_cn}")
//        startWeb("http://app.iyuba.cn/")
    }
}

fun TabLayout.addStringTab(descArray: Array<out String>) {
    for (i in descArray.indices) {
        val tab = with(newTab()) {
            text = descArray[i]
            id = i
            this
        }
        addTab(tab)
    }
}

fun MutableMap<String, String>.putFormat() = put("format", "json")
fun MutableMap<String, String>.putPlatform() = put("platform", "android")
fun MutableMap<String, String>.putProtocol(value: String) = put("protocol", value)
fun MutableMap<String, String>.putAppName(key: String = "type") = put(key, AppClient.appName)


fun Context.startShareWorld(
    shareTitle: String,
    shareUrl: String,
    callbackOut: PlatformActionListener,
    icon: String = ""
) = with(OnekeyShare()) {
    disableSSOWhenAuthorize()
    setTitle(shareTitle)
    setTitleUrl(shareUrl)
    text = shareTitle
    if (icon.isEmpty()) {
        setImageData(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_new))
    } else {
        setImagePath(icon)
    }
    setUrl(shareUrl)
    setSite(resources.getString(R.string.app_name))
    setSiteUrl(shareUrl)
    callback = callbackOut
    this
}


/**
 * /storage/emulated/0/Android/data/com.suzhou.concept/cache/yongSpeaking/video/voa/321/321002.mp4
 * /storage/emulated/0/Android/data/com.suzhou.concept/cache/yongSpeaking/sounds/voa/202005/321001.mp3
 * */
fun Context.getLocalPath(endPath: String): File {
    var showEndPath = endPath

    //这里设置下本地的路径操作(因为voaid都是不一样的，因此直接使用url的最后一个处理即可)
    if (TextUtils.isEmpty(endPath)){
        return File("")
    }

    //如果数据不存在，则从辅助内容中查询数据使用
//    if (TextUtils.isEmpty(showEndPath)){
//        if (TextUtils.isEmpty(DownloadHelpSession.getInstance().helpPath)){
//            return File("")
//        }
//
//        //否则从辅助数据中操作
//        showEndPath = DownloadHelpSession.getInstance().helpPath
//    }

    //设置固定的路径
    var suffixPath = ""
    if (showEndPath.endsWith(".mp3")){
        val index = showEndPath.lastIndexOf("/");
        suffixPath = showEndPath.substring(index);
        suffixPath = "/audio"+suffixPath;
    }

    if (showEndPath.endsWith(".mp4")){
        val index = showEndPath.lastIndexOf("/");
        suffixPath = showEndPath.substring(index);
        suffixPath = "/video"+suffixPath;
    }

    val local = externalCacheDir?.absolutePath
    val separator = File.separatorChar
    val path = with(StringBuilder()) {
        append(local)
        append(separator)
        append("yongSpeaking")
        append(suffixPath)
        toString()
    }

    Log.d("下载文件路径", "当前文件--"+path)

    return File(path)
}


/**
 *  mkdirs()可以建立多级文件夹， mkdir()只会建立一级的文件夹
 * */
fun File.downLoadVideo(body: ResponseBody) {
    absolutePath.let { path ->
        val dirName = path.substring(0 until (path.lastIndexOf("/"))) + File.separatorChar
        File(dirName).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    //下载时没有事务性操作
    if (exists()) {
        delete()
    } else {
        createNewFile()
    }
    //关闭外层流的同时，内层流也会自动的进行关闭。关于内层流的关闭，可以省略
    val input = body.byteStream()
    val out = FileOutputStream(this)
    BufferedInputStream(input).use { inBuff ->
        BufferedOutputStream(out).use { outBuff ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = inBuff.read(buffer)
            while (bytes >= 0 && NonCancellable.isActive) {
                outBuff.apply {
                    write(buffer, 0, bytes)
                    flush()
                }
                bytes = inBuff.read(buffer)
            }
        }
    }
}


fun changeTimeToInt(time: String): Int {
    val array = time.split(".")
    return if (array.size == 2) {
        val second = array[0].toInt()
        val milliSecond = array[1].toInt()
        second * 1000 + milliSecond * 100
    } else {
        0
    }
}

/**
 * 从A点播放到B点
 * */
fun ExoPlayer.playAToB(start: Long, end: Long): Flow<Boolean> {
    seekTo(start)
    play()
    return flow {
        while (true) {
            kotlinx.coroutines.delay(50)
            emit(end - currentPosition < 10)
        }
    }
}


fun List<EvaluationSentenceDataItem>.showSpeakingSpannable() = with(SpannableStringBuilder()) {
    for (i in this@showSpeakingSpannable.indices) {
        val item = this@showSpeakingSpannable[i]
        val itemWord = item.content + " "
        append(itemWord)
        val green = Color.rgb(39, 124, 60)
        val colorSpan = when {
            item.score > 4 -> ForegroundColorSpan(green)
            item.score < 2.5 -> ForegroundColorSpan(Color.RED)
            else -> ForegroundColorSpan(Color.BLACK)
        }
        if (i == 0) {
            setSpan(colorSpan, 0, itemWord.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        } else {
            val start = length - itemWord.length
            val end = start + itemWord.length
            setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }
    this
}

fun Pair<String, File>.createDownloadRequest(titleAndDesc: String) =
    with(DownloadManager.Request(Uri.parse(first))) {
        val fileUri = Uri.fromFile(second)
        setTitle(titleAndDesc)
        setDescription(titleAndDesc)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        setDestinationUri(fileUri)
        this
    }

fun List<EvaluationSentenceDataItem>.changeToString() = StringBuilder().let { builder ->
    forEach {
        builder.append(it.content)
    }
    builder.toString()
}

fun changeTimeToLong(time: String) =
    (if (time.contains(".")) changeTimeToInt(time).toLong() else (time.toLong() * 1000))

/**
 * first-->播放类型
 * second-->开始播放时间
 * third-->播放地址
 * */
fun Pair<VoiceStatus, String>.changeTimeTriple() = Triple(first, getRecordTime(), second)

fun MutableMap<String, String>.putUserId(key: String = "userId") =
    put(key, GlobalMemory.userInfo.uid.toString())

fun signDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(Date())

/**
 * 当角标 (it!=-1) 时即为Success
 * */
fun Int.findIndexSuccess() = (this != -1)

fun List<TestRecordItem>.filterStructure(flag: Boolean) = filter {
    if (flag) {
        !OtherUtils.selectArray.contains(it.UserAnswer)
    } else {
        OtherUtils.selectArray.contains(it.UserAnswer)
    }
}


