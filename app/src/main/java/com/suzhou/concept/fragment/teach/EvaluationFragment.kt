package com.suzhou.concept.fragment.teach

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.article.TeachMaterialActivity
import com.suzhou.concept.activity.dollar.MemberCentreActivity
import com.suzhou.concept.adapter.EvaluationSentenceAdapter
import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.bean.MergeResponse
import com.suzhou.concept.databinding.EvaluationFragmentLayoutBinding
import com.suzhou.concept.dialog.CorrectSoundDialog
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.utils.*
import com.suzhou.concept.utils.logic.GlobalPlayManager
import com.suzhou.concept.utils.logic.VoiceStatus
import com.suzhou.concept.utils.view.ControlVideoProgressView
import com.suzhou.concept.utils.view.LocalMediaRecorder
import com.suzhou.concept.utils.view.RoundProgressBar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*

/**
 * 评测界面
 */
class EvaluationFragment : BaseFragment<EvaluationFragmentLayoutBinding>(), OnEvaluationListener,View.OnClickListener {
    private lateinit var currentRecorder: ImageView
    //遍寻CSDN与掘金，不去Android开发者官方网站也枉然
    private val fileName by lazy { "${requireContext().externalCacheDir?.absolutePath}audio_record_test.wav" }
    private val list = mutableListOf<EvaluationSentenceItem>()
    private val evalAdapter = EvaluationSentenceAdapter()
    private val recorder by lazy { LocalMediaRecorder() }
    private var currentPosition = 0
    private lateinit var mergeResult:MergeResponse
    private val  dialog by lazy { CorrectSoundDialog() }
    private lateinit var roundBar:RoundProgressBar
    private lateinit var originalView: ControlVideoProgressView
    private val requestPermissionLaunch = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.checkPermission { showFirstHint() }
    }

    //改完这个player后，修改评测后再次进来不显示字？然后修改评测接口逻辑
    override fun EvaluationFragmentLayoutBinding.initBinding() {
        mergeLayout.visibility=View.INVISIBLE
        synthesis.setOnClickListener(this@EvaluationFragment)
        release.setOnClickListener(this@EvaluationFragment)
        controlMerge.setOnClickListener(this@EvaluationFragment)
        evalAdapter.onClickListener = this@EvaluationFragment
        sentenceList.apply {
            adapter = evalAdapter
            addDefaultDecoration()
        }
        lifecycleScope.launch {
            evaluation.sentenceResult.collect{result->
                result.onSuccess {
                    list.apply {
                        clear()
                        addAll(it)
                    }
                    evalAdapter.changeData(list)
                    refreshSentence()
                    dismissActivityLoad<TeachMaterialActivity>()
                }.onError {
                    it.judgeType().showToast()
                    dismissActivityLoad<TeachMaterialActivity>()
                }.onLoading {
                    showActivityLoad<TeachMaterialActivity>()
                }
            }
        }

        lifecycleScope.launch {
            GlobalPlayManager.statusFlow.collect{result->
                result.onReady {
                    when(it){
                        VoiceStatus.EVAL_MERGE->listenMergePrepared()
                        VoiceStatus.EVAL_ORIGINAL->{
                            listenOriginal()
                        }
                        VoiceStatus.EVAL_SELF->listenSelfPrepared()
                        else -> {}
                    }
                }.onEnded {
                    when(it){
                        VoiceStatus.EVAL_MERGE->{}
                        VoiceStatus.EVAL_ORIGINAL->{
                            originalView.controlVideo.setBackgroundResource(R.drawable.play_evaluation_old)
                            Log.d("进度数据展示", "initBinding: --测试1")
                        }
                        VoiceStatus.EVAL_SELF-> listenSelfCompleted()
                        else -> {}
                    }
                }.onIsPlaying {
                    when(it.first){
                        VoiceStatus.EVAL_SELF-> changeRoundBar(it.second)
                        VoiceStatus.EVAL_MERGE-> changeMergeBack(it.second)
                        VoiceStatus.EVAL_ORIGINAL-> {
                            originalView.controlVideo.setBackgroundResource(R.drawable.pause_evaluation_old)
                            Log.d("进度数据展示", "initBinding: --测试2")
                            evalAdapter.notifyItemChanged(currentPosition)
                        }
                        else -> {}
                    }
                }.onNotPlaying {
                    when(it.first){
                        VoiceStatus.EVAL_SELF-> changeRoundBar(it.second)
                        VoiceStatus.EVAL_MERGE-> changeMergeBack(it.second)
                        VoiceStatus.EVAL_ORIGINAL-> {
                            originalView.controlVideo.setBackgroundResource(R.drawable.play_evaluation_old)
                            Log.d("进度数据展示", "initBinding: --测试3")
                            evalAdapter.notifyItemChanged(currentPosition)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun listenOriginal(){
        if (!::originalView.isInitialized){
            return
        }
        val item=list[currentPosition]
        val end= item.EndTiming*1000
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                flow {
                    while (true){
                        kotlinx.coroutines.delay(20)
                        emit(0)
                    }
                }.collect{
                    if (end==0F){
                        return@collect
                    }
                    val position=GlobalPlayManager.getCurrentPosition()
                    val stopPoint=(position-end)>1
                    if (stopPoint){
                        GlobalPlayManager.pause()
                        cancel()
                        originalView.controlVideo.inflateProgress(0)
                    }else{
                        originalView.controlVideo.inflateProgress(position.toInt())
                    }
//                    originalView.controlVideo.setBackgroundResource(R.drawable.pause_evaluation_old)
                    Log.d("进度数据展示", "initBinding: --测试4")
                    evalAdapter.notifyItemChanged(currentPosition)
                }
            }
        }
    }

    private fun changeRoundBar(playing:Boolean){
        if (!::roundBar.isInitialized){
            return
        }
        val roundBack=getPlayIcon(playing)
        roundBar.setBackgroundResource(roundBack)
    }

    private fun changeMergeBack(playing:Boolean){
        val back=getPlayIcon(playing)
        bind.controlMerge.setBackgroundResource(back)
    }

    private fun getPlayIcon(playing: Boolean)=if (playing){
        R.drawable.pause_evaluation_old
    }else{
        R.drawable.play_evaluation_old
    }

    private fun listenMergePrepared() {
        val globalDuration=GlobalPlayManager.getDuration().toInt()
        bind.sumTime.text=globalDuration.changeTimeToString()
        bind.evaluationSeek.max=GlobalPlayManager.getDuration().toInt()
        lifecycleScope.launch {
            flow {
                while (true){
                    kotlinx.coroutines.delay(20)
                    emit(0)
                }
            }.collect {
                val current = GlobalPlayManager.getCurrentPosition().toInt()
                bind.nowTime.text = current.changeTimeToString()
                bind.evaluationSeek.progress = current
            }
        }
    }

    private fun listenSelfPrepared() {
        val globalDuration = GlobalPlayManager.getDuration().toInt()
        GlobalMemory.isPlayingSelf = true
        if (!::roundBar.isInitialized) {
            return
        }
        roundBar.inflateMax(globalDuration / 1000)
        lifecycleScope.launch {
            flow {
                while (true) {
                    kotlinx.coroutines.delay(20)
                    emit(0)
                }
            }.collect {
                val progress = GlobalPlayManager.getCurrentPosition()
                roundBar.inflateProgress(progress.toInt())
            }
        }
    }
    private fun listenSelfCompleted() {
        GlobalMemory.isPlayingSelf = false
        if (!::roundBar.isInitialized) {
            return
        }
        roundBar.inflateProgress(0)
        evalAdapter.notifyItemChanged(currentPosition)
    }

    private fun refreshSentence() {
        lifecycleScope.launch {
            evaluation.evalList.collect{result->
                result.onError {
                    it.judgeType().showToast()
                }.onSuccess {
                    list.forEach {item->
                        AppClient.evaluationMap[item.Paraid.toInt()]=it.filter {evalItem-> evalItem.onlyKay==item.onlyKay  }
                    }
                }
            }
        }
        /**
         * 如何以一个正确的合适的方式用本地数据刷新TextView的颜色?,回调adapter不可行，adapter回调本界面不可取
         * 根据相同的onlyKey从父list里找到index，然后根据index刷新adapter，怎么刷新？何时刷新？？？
         * 为了安全，只能在获取父list后才能获取子list，现在只能嵌套liveData
         *
         * 最后只能向全局变量妥协
         * */
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showOperate(position: Int) {
        currentPosition=position
        for (i in list.indices) {
            list[i].showOperate = ((list[i].IdIndex - 1) == position)
        }
        evalAdapter.notifyDataSetChanged()
    }


    private fun showFirstHint() {
        val login = GlobalMemory.userInfo
        if (login.isEmpty()) {
            activity?.showGoLoginDialog()
            return
        }
        val synthesisLimit = 3
        val successLength = list.filter { it.success }.size
        val indexOut = successLength >= synthesisLimit
        if (!login.isVip() && indexOut) {
            "本篇你已评测3句！成为VIP后可评测更多".showDialog(requireContext()) {
                val i = Intent(requireContext(), MemberCentreActivity::class.java)
                startActivity(i)
            }
            return
        }
        if (GlobalMemory.isPlayingOriginal) {
            "正在播放句子".showToast()
            return
        }
        if (GlobalMemory.isPlayingSelf) {
            "正在播放评测".showToast()
            return
        }
        if (AppClient.showEvaluationHint) {
            "再次点击即可停止录音，完成评测".showDialog(requireContext()) { startRecording() }
            AppClient.showEvaluationHint = false
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        recorder.startPrepare(fileName,lifecycleScope){
            GlobalMemory.isRecording = true
        }
        currentRecorder.setBackgroundResource(R.drawable.mike_red)
    }

    private fun stopRecord() {
        showActivityLoad<TeachMaterialActivity>()
        if (GlobalMemory.isRecording) {
            GlobalMemory.isRecording = false
            recorder.stopRecord()
        }
        lifecycleScope.launch {
            val item=list[currentPosition]
            val success=item.success
            val response = evaluation.evaluationSentence(item, fileName).first().data
            var onlyKey = UUID.randomUUID().toString()
            item.success = true
            if (item.onlyKay.isEmpty()){
                item.onlyKay = onlyKey
            }else{
                onlyKey=item.onlyKay
            }
            item.fraction = (response.total_score * 20).toInt().toString()
            item.selfVideoUrl = response.URL
            //更新父状态
            if (GlobalMemory.currentYoung){
                evaluation.updateYoungSentenceItemStatus(item)
            }else{
                evaluation.updateEvaluationSentenceItemStatus(item).first()
            }
            val resultList=evaluation.selectEvaluationByKey(onlyKey)
            if (resultList.isNotEmpty()){
                evaluation.deleteSentenceDataItemByKey(onlyKey)
            }
            response.words.forEach {
                it.voaId= AppClient.conceptItem.voa_id.toInt()
                it.userId= GlobalMemory.userInfo.uid
                it.onlyKay=onlyKey
            }
            AppClient.evaluationMap[item.IdIndex] = response.words
            /**
             * 根据EvaluationSentenceDataItem的去重-------------->HashMap简单粗暴
             * */
            evaluation.insertEvaluation(response.words)
            /**
             * update EvaluationSentenceItem(大)信息----->userId&&voaId&&idIndex
             * val list=select EvaluationSentenceDataItem(小)----->onlyKey
             * if(!list.isEmpty()){
             *      delete()
             * }
             * insert(相同的key)
             * */
            if (!success) {
                conceptViewModel.updateEvalItem()
            }
            evalAdapter.notifyItemChanged(currentPosition)
            dismissActivityLoad<TeachMaterialActivity>()
            "评测成功".showToast()
        }
    }

    private fun synthesisVideos() {
        val synthesisLimit = 2
        val successLength = list.filter { it.success }.size
        if (successLength < 2) {
            "至少读${synthesisLimit}句方可合成".showToast()
            return
        }
        var isCurrentMonth=true
        var itemIndex=0
        for (i in list.indices){
            val item=list[i]
            if (item.success){
                val videoUrl=item.selfVideoUrl
                val start=videoUrl.indexOf("/")+1
                val end=videoUrl.indexOf("/concept")
                val result=videoUrl.substring(start,end)
                val year=result.substring(0,4)
                val month=result.substring(4)
                val currentYear=System.currentTimeMillis().timeStampDate("yyyy")
                val currentMonth=System.currentTimeMillis().timeStampDate("MM")
                isCurrentMonth=(year==currentYear&&month==currentMonth)
                if (!isCurrentMonth){
                    itemIndex=i
                    break
                }
            }
        }
        if(!isCurrentMonth){
            "当前合成录音中含非本月的录音数据, 第${itemIndex + 1}句;请重新录制后再进行合成".showToast()
            return
        }
        val builder = StringBuilder()
        var totalScore = 0
        list.forEach {
            if (it.success) {
                builder.append(it.selfVideoUrl + ",")
                totalScore += it.fraction.toInt()
            }
        }
        lifecycleScope.launch {
            evaluation.mergeVideos(builder.toString()).collect{
                it.onSuccess {result->
                    mergeResult=result
                    "合成成功".showToast()
                    bind.synthesisScore.text = (totalScore / successLength).toString()
                    bind.mergeLayout.visibility = View.VISIBLE
                }.onFailure {error->
                    "合成失败  ${error.judgeType()}".showToast()
                }
            }
        }
        //合成之后可以试听
    }
    private fun controlMerge(){
        if (GlobalPlayManager.isPlaying()){
            GlobalPlayManager.pause()
            bind.controlMerge.setBackgroundResource(R.drawable.play_evaluation_old)
        }else{
            if (GlobalPlayManager.isEnd()){
                val pair=Pair(VoiceStatus.EVAL_MERGE,mergeResult.URL.changeVideoUrl())
                GlobalPlayManager.addUrl(pair)
                bind.evaluationSeek.max=GlobalPlayManager.getDuration().toInt()
            }else{
                GlobalPlayManager.start()
            }
            bind.controlMerge.setBackgroundResource(R.drawable.pause_evaluation_old)
        }
    }

    private fun releaseMergeResult() {
        lifecycleScope.launch {
            val score=bind.synthesisScore.text.toString()
            evaluation.releaseMerge(score, mergeResult.URL).collect {
                it.onSuccess { result ->
                    ("语音发送" + if (result.isNotEmpty()) "成功" else "失败").showToast()
                }.onFailure { e ->
                    "加载失败${e.message}".showToast()
                }
            }
        }
    }
    override fun openMike(position: Int, img: ImageView) {
        currentRecorder = img
        if (GlobalMemory.isRecording) {
            stopRecord()
            img.setBackgroundResource(R.drawable.mike_grey)
            return
        }
        requestPermissionLaunch.checkObtainPermission { showFirstHint() }
    }

    override fun playSelf(position: Int, img: RoundProgressBar) {
        if (GlobalMemory.isRecording){
            "正在录音".showToast()
            return
        }
        if (GlobalMemory.isPlayingOriginal){
            "正在播放句子".showToast()
            return
        }

        roundBar=img
        val pair=Pair(VoiceStatus.EVAL_SELF,list[position].selfVideoUrl.changeVideoUrl())
        GlobalPlayManager.addUrl(pair)
    }

    override fun releaseSimple(bean: EvaluationSentenceItem) {
        lifecycleScope.launch {
            try {
                val result=evaluation.releaseSimple(bean).first()
                ("分享排行榜"+if (result.isNotEmpty()) "成功" else "失败").showToast()
            }catch (e:Exception){
                "加载失败${e.message}".showToast()
            }
        }
    }

    override fun correctSound(bean: EvaluationSentenceItem) {
        dialog.showNow(childFragmentManager,"")
        dialog.changeContent(bean)
    }

    override fun playOriginal(position: Int, progress: ControlVideoProgressView) {
        originalView=progress
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.synthesis->synthesisVideos()
            R.id.release->releaseMergeResult()
            R.id.control_merge-> controlMerge()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalPlayManager.executeDestroy()
    }
}