package com.suzhou.concept.dialog

import android.media.MediaRecorder
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.bean.EvaluationSentenceDataItem
import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.databinding.CorrectSoundLayoutBinding
import com.suzhou.concept.utils.OnWordClickListener
import com.suzhou.concept.utils.changeVideoUrl
import com.suzhou.concept.utils.logic.GlobalPlayManager
import com.suzhou.concept.utils.logic.VoiceStatus
import com.suzhou.concept.utils.showSpannable
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.viewmodel.EvaluationViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
 */
class CorrectSoundDialog : DialogFragment(), View.OnClickListener {
    private lateinit var fileName: String
    private val recorder by lazy { MediaRecorder() }
    private lateinit var bind: CorrectSoundLayoutBinding
    private var isRecording = false
    private lateinit var onWordClick: OnWordClickListener
    private val finalList = mutableListOf<EvaluationSentenceDataItem>()
    private val evaluation by lazy { ViewModelProvider(requireActivity())[EvaluationViewModel::class.java] }
    private var videoUrl = ""
    private var wordUrl=""
    private lateinit var groupItem: EvaluationSentenceItem
    private lateinit var currentItem:EvaluationSentenceDataItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bind = DataBindingUtil.inflate(inflater, R.layout.correct_sound_layout, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onWordClick = object : OnWordClickListener() {
            override fun onNoDoubleClick(str: String) {
                bind.title = str
                loadWord(str){
                    playVideo()
                }
            }
        }
        bind.closeCorrectSound.setOnClickListener(this)
        bind.playCorrect.setOnClickListener(this)
        bind.listenOriginal.setOnClickListener(this)
        bind.clickStart.setOnClickListener(this)
        bind.seekLisa.setOnClickListener(this)
        bind.wordScore.apply {
            visibility=View.INVISIBLE
            setOnClickListener(this@CorrectSoundDialog)
        }
        bind.contentEvaluation.setOnWordClickListener(onWordClick)
        fileName = "${requireContext().externalCacheDir?.absolutePath}audio_record_word.wav"
    }

    fun changeContent(bean: EvaluationSentenceItem) {
        val map = AppClient.evaluationMap
        val sumList = mutableListOf<EvaluationSentenceDataItem>()
        map.values.forEach { sumList.addAll(it) }
        groupItem=bean
        finalList.clear()
        finalList.addAll(sumList.filter { it.onlyKay == bean.onlyKay })
        bind.contentEvaluation.apply {
            text = map.showSpannable(bean.onlyKay)
            val selectTextColor= ContextCompat.getColor(requireContext(),R.color.bookChooseUncheck)
            setSelectTextBackColor(selectTextColor)
        }
        if (finalList.isNotEmpty()) {
            val red=finalList.findRedError()
            val word = red.ifEmpty {
                finalList[0].content
            }
            bind.title = word
            loadWord(word)
        }
    }

    private fun MutableList<EvaluationSentenceDataItem>.findRedError():String{
        forEach {
            if (it.score<2.5){
                return it.content
            }
        }
        return ""
    }

    private fun loadWord(word: String,otherMethod:()->Unit={}) {
        lifecycleScope.launch {
            currentItem = finalList.filter { it.content.contains(word) }[0]
            evaluation.correctSound(word, currentItem).collect {
                bind.correctPronunciation.text = it.realOri
                bind.yourPronunciation.text = it.realUserPron
                val wordDefinition=resources.getString(R.string.word_definition)+ (it.def?:"暂无")
                bind.wordDefinition.text=wordDefinition
                bind.playCorrect.visibility= when {
                    it.audio==null -> View.GONE
                    it.audio.isNotEmpty() -> {
                        videoUrl = it.audio
                        View.VISIBLE
                    }
                    else -> View.GONE
                }
                otherMethod()
            }
        }
    }

    private fun playVideo(url:String=videoUrl) {
        if (url.isEmpty()){
            return
        }
        GlobalPlayManager.addUrl(Pair(VoiceStatus.EVAL,url))
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.close_correct_sound -> dismiss()
            R.id.play_correct -> playVideo()
            R.id.listen_original -> playVideo()
            R.id.word_score -> playVideo(wordUrl.changeVideoUrl())
            R.id.click_start -> clickStart()
            R.id.seek_lisa->{}
        }
    }
    private fun clickStart(){
        bind.clickStart.text=if (isRecording) {
            "结束评测".showToast()
            stopRecord()
            resources.getString(R.string.click_start)
        } else {
            startRecording()
            "开始评测".showToast()
            resources.getString(R.string.click_stop)
        }
    }
    private fun startRecording() {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            prepare()
            start()
            isRecording = true
        }
    }
    private fun stopRecord() {
        if (isRecording) {
            isRecording = false
            recorder.setOnErrorListener(null)
            recorder.setOnInfoListener(null)
            recorder.setPreviewDisplay(null)
            recorder.stop()
            recorder.reset()
        }
        lifecycleScope.launch {
            try {
                val index=currentItem.index.toString()
                val result=evaluation.evaluationSentence(groupItem,fileName,index).first()
                //userId->voaId->index
                val data=result.data
                val item=data.words[0]
                wordUrl=data.URL
                evaluation.updateEvaluationChildStatus(item.score,groupItem.onlyKay,currentItem.index).first()
                bind.wordScore.apply {
                    visibility=View.VISIBLE
                    text=data.realScopes
                }
            }catch (e:Exception){
                "请求失败".showToast()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dw = dialog?.window
        dw!!.setBackgroundDrawableResource(R.drawable.bg_black_round_10dp) //一定要设置背景
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        val params = dw.attributes
        //屏幕底部显示
        params.gravity = Gravity.CENTER
        //设置屏幕宽度高度
        params.width = (dm.widthPixels / 1.1f).toInt()//屏幕宽度
        params.height = (dm.heightPixels / 1.8f).toInt() //屏幕高度的1/3
        dw.attributes = params
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalPlayManager.executeDestroy()
    }
}