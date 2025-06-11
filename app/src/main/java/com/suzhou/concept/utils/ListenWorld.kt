package com.suzhou.concept.utils

import android.widget.ImageView
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.MineReleaseItem
import com.suzhou.concept.bean.RankInfoItem
import com.suzhou.concept.bean.WordItem
import com.suzhou.concept.utils.view.ControlVideoProgressView
import com.suzhou.concept.utils.view.RoundProgressBar
import com.suzhou.concept.utils.view.SelectableTextView

/**
苏州爱语吧科技有限公司
 */
interface SelectBookListener {
    fun listener(languageType: LanguageType)
}
interface StrangeListener{
    fun wordDetailed(word:String)
    fun playVideo(url:String)
}
interface SearchWordListener {
    fun searchListener(word:String,view: SelectableTextView)
}

interface OnEvaluationListener{
    fun showOperate(position:Int)
    fun openMike(position:Int,img:ImageView)
    fun playSelf(position:Int,img: RoundProgressBar)
    fun releaseSimple(bean: EvaluationSentenceItem)
    fun correctSound(bean: EvaluationSentenceItem)
    fun playOriginal(position: Int,progress: ControlVideoProgressView)
}

interface OnClickRankItemListener {
    fun listenItem(userId:Int,userName:String)
}

interface OnEvaluationInfoOperateListener {
    fun playVideo(url:String)
    fun likeItem(item: RankInfoItem)
}

/**
 * 同意隐私时的监听
 * */
interface OnAgreePrivacyListener {
    fun seekPrivacy()
    fun seekProtocol()
    fun agree()
    fun noAgree()
}

interface OnActivityLifecycleListener {
    //TeachMaterialActivity被切换到桌面时
    fun onPauseTeachMaterial()
}


interface OnLoadDialogListener {
    fun showLoad()
    fun dismissLoad()
}

/**
 * 监听广告状态
 * */
interface OnAdvertiseStateListener {
    fun onSuccess()
    fun onError()
}

interface OnStatisticsListener {
    /**
     * 听力进度
     * */
    fun onListen(position:Int,item: ConceptItem)
    /**
     * 评测进度
     * */
    fun onEval(position:Int,item: ConceptItem)
    /**
     * 单词闯关进度
     * */
    fun onWord()
    /**
     * 练习进度
     * */
    fun onExercise(position:Int,item: ConceptItem)
    /**
     * 微课进度
     * */
    fun onMooc(item: ConceptItem)
}


//单词列表界面的操作
interface OnWordListItemListener{
    //播放
    fun onPlay(position:Int)
    //收藏
    fun onCollect(item:WordItem)
}

//口语配音正式界面
interface KouyuItemClickListener{
    //长按
    fun onLongClick(item:MineReleaseItem)

    //短按
    fun  onClick(item: MineReleaseItem)
}