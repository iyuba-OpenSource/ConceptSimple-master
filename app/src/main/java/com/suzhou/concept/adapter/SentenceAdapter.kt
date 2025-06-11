package com.suzhou.concept.adapter

import android.graphics.Typeface
import android.util.Log
import android.widget.TextView
import androidx.core.util.Consumer
import com.iyuba.imooclib.Constant
import com.iyuba.imooclib.data.local.IMoocDBManager
import com.iyuba.imooclib.data.model.StudyProgress
import com.suzhou.concept.AppClient
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.dao.AppDatabase
import com.suzhou.concept.databinding.SentenceItemLayoutNewBinding
import com.suzhou.concept.lil.data.library.TypeLibrary
import com.suzhou.concept.lil.data.newDB.RoomDBManager
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.util.Glide3Util
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.GlobalMemory.currentLanguage
import com.suzhou.concept.utils.GlobalMemory.currentYoung
import com.suzhou.concept.utils.GlobalMemory.isLogin
import com.suzhou.concept.utils.OnStatisticsListener
import com.suzhou.concept.utils.visibilityState
import org.greenrobot.eventbus.EventBus

/**
苏州爱语吧科技有限公司
 */
class SentenceAdapter :BaseAdapter<ConceptItem, SentenceItemLayoutNewBinding>() {

    lateinit var itemListener:Consumer<Pair<Int,ConceptItem>>

    private lateinit var statisticsListener: OnStatisticsListener

    //标题的控件长度
    private var titleViewLength:Int = 0

    override fun SentenceItemLayoutNewBinding.onBindViewHolder(bean: ConceptItem, position: Int) {
        Log.d("数据显示和样式", bean.title)

        val typeface=Typeface.createFromAsset(AppClient.context.assets,"fonts/Helvetica Bold.ttf")
        sentenceLesson.typeface=typeface
        item=bean
        root.setOnClickListener {
            EventBus.getDefault().post(RefreshEvent(RefreshEvent.AUDIO_STOP,null))
            itemListener.accept(Pair(position,bean))
        }

        bean.youngChild.youngPic.isNotEmpty().apply {
            sentenceLesson.visibilityState(this)
            youngPic.visibilityState(!this)
            changeView(this)
        }
        inflateProgress(position,bean)
    }


    fun inflateStatisticsListener(listener: OnStatisticsListener){
        statisticsListener=listener
    }

    private fun SentenceItemLayoutNewBinding.inflateProgress(positionInList:Int,bean: ConceptItem){
        /*if (bean.choice_num.isEmpty()) {
            //青少版这里处理
            //格式化标题显示
            if (titleViewLength == 0){
                sentenceTitle.viewTreeObserver.addOnGlobalLayoutListener(object:ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        sentenceTitle.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        titleViewLength = sentenceTitle.width

                        showComputeAndShowTitle(sentenceTitle,bean.title)
                    }
                })
            }else{
                showComputeAndShowTitle(sentenceTitle,bean.title)
            }
            return
        }*/


        sentenceTitle.text = bean.title
        Glide3Util.loadRoundImg(AppClient.context,bean.youngChild.youngPic,0,10,youngPic)

        if (bean.choice_num.isEmpty()){
            return
        }


        /***************************原文数据显示********************/
        sentenceListenProgress.apply {
//            inflateMax(100)
//            inflateProgress(bean.listenProgress*1000)
            //使用新的显示操作
            var progressTime = 0
            val totalTime = 100

            val userId = GlobalMemory.userInfo.uid
            val bookId = bean.bookId
            val indexId = bean.index
            val language = bean.language

            if (isLogin()){
                if (GlobalMemory.currentYoung){
                    val data = AppDatabase.getDatabase(context).youngBookDao().selectSingleYoungItem(bookId,indexId,userId)
                    if (data!=null){
                        progressTime = (data.listenProgress*1000L).toInt()
                    }
                }else{
                    val dataList = AppDatabase.getDatabase(context).conceptDao().selectListenConceptItem(bookId,language,indexId)
                    if (dataList!=null&& dataList.isNotEmpty()){
                        progressTime = (dataList[0].listenProgress*1000L).toInt()
                    }
                }
            }

            inflateMax(totalTime)
            inflateProgress(progressTime)

            setOnClickListener {
                statisticsListener.onListen(positionInList,bean)
            }
        }

        /**************************评测数据显示*********************/
        //这里直接从新的数据库中获取数据并且展示处理啊
        var evalNum:String = bean.text_num
        var evalSize:Int = RoomDBManager.getInstance().getEvalResultData(GlobalMemory.userInfo.uid,bean.voa_id).size
        evalText.text = "$evalSize/$evalNum"
        sentenceEvalProgress.apply {
            inflateMax(bean.text_num.toInt())
//            inflateProgress(bean.evalSuccess*1000)
            Log.d("数据显示", "num--"+bean.text_num+"--size--"+evalSize+"--voaid--"+bean.voa_id)
            inflateProgress(evalSize*1000)
            setOnClickListener {
                statisticsListener.onEval(positionInList,bean)
            }
        }

        /**************************微课数据显示******************/
        val array=bean.titleid.split(",")
        val uid=GlobalMemory.userInfo.uid
        val moocPair=when {
            array.isEmpty() -> Pair("0%",0)
            array.size==1 -> IMoocDBManager.getInstance().findStudyProgress(uid,array.first().toInt()).countProgressPair(bean.totalTime)
            else -> IMoocDBManager.getInstance().findStudyProgress(uid,array.last().toInt()).countProgressPair(bean.totalTime)
        }
        sentenceMoocProgress.apply {
            inflateMax(100)
            inflateProgress(moocPair.second*1000)
            setOnClickListener {
                statisticsListener.onMooc(bean)
            }
        }
        moocText.text=moocPair.first

        /***********************练习题数据显示********************/
        //青少版 区分微课，练习
        //choice_num，练习数量，只需要再存个成功数量
        sentenceExerciseProgress.apply {
//            inflateMax(bean.choice_num.toInt())
//            inflateProgress(bean.exerciseRight*1000)
            //进度数据
            var rightCount = 0
            var totalCount = 0

            //这里需要同时设置选择题和关键句型题目的进度
            val userId = GlobalMemory.userInfo.uid
            val voaId = bean.voa_id
            val lessonType = getExerciseType()


            if (userId>0){
                //选择题
                val choiceType = TypeLibrary.ExerciseType.Exercise_multiChoice
                val choiceEntity = RoomDBManager.getInstance().getConceptExerciseResultData(voaId, userId, lessonType, choiceType)
                //关键句型题
                val structureType = TypeLibrary.ExerciseType.Exercise_voaStructure
                val structureEntity = RoomDBManager.getInstance().getConceptExerciseResultData(voaId, userId, lessonType, structureType)

                if (choiceEntity!=null){
                    rightCount += choiceEntity.rightCount
                    totalCount += choiceEntity.totalCount
                }
                if (structureEntity!=null){
                    rightCount += structureEntity.rightCount
                    totalCount += structureEntity.totalCount
                }
            }

            inflateMax(totalCount)
            inflateProgress(rightCount*1000)
        }
        sentenceExerciseProgress.apply {
            setOnClickListener {
                statisticsListener.onExercise(positionInList,bean)
            }
        }

        /***********************单词闯关数据显示********************/
        //单词因为只有美音单词，没有英音单词，因此直接查询出单词总数和单词的正确数量进行显示
        //区分青少版、英音和美音
        /*if (!GlobalMemory.currentYoung){
            var wordCount:Int = 0;
            var wordRight:Int = 0

            var isUS:Boolean = GlobalMemory.currentLanguage.isUS()
            if (isUS){
                wordCount = AppDatabase.getDatabase(AppClient.context).wordDao().selectWordCount(bean.voa_id.toInt())
                wordRight = AppDatabase.getDatabase(AppClient.context).wordDao().selectRightWordNum(bean.voa_id.toInt(),GlobalMemory.userInfo.uid,true)
            }else{
                wordCount = AppDatabase.getDatabase(AppClient.context).wordDao().selectWordCount(bean.voa_id.toInt()/10)
                wordRight = AppDatabase.getDatabase(AppClient.context).wordDao().selectRightWordNum(bean.voa_id.toInt()/10,GlobalMemory.userInfo.uid,true)
            }

            sentenceWordProgress.apply {
                inflateMax(wordCount)
                inflateProgress(wordRight*1000)
            }
            wordText.text = "$wordRight/$wordCount"
        }*/

        var rightCount = 0
        var totalCount = 0

        if (!currentYoung){
            val wordPassData = RoomDBManager.getInstance().getWordPassData(TypeLibrary.BookType.conceptFour,bean.bookId,bean.voa_id.toInt(),GlobalMemory.userInfo.uid)
            if (wordPassData!=null){
                rightCount = wordPassData.rightCount
                totalCount = wordPassData.totalCount
            }
        }
        wordText.text = "$rightCount/$totalCount"

        sentenceWordProgress.apply {
            //数据显示
            inflateMax(totalCount)
            inflateProgress(rightCount*1000)

            //点击操作
            setOnClickListener {
                statisticsListener.onWord()
            }
        }


        //格式化标题显示
        /*if (titleViewLength == 0){
            sentenceTitle.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    sentenceTitle.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    titleViewLength = sentenceTitle.width

                    showComputeAndShowTitle(sentenceTitle,bean.title)
                }
            })
        }else{
            showComputeAndShowTitle(sentenceTitle,bean.title)
        }*/
    }

    private fun SentenceItemLayoutNewBinding.changeView(flag:Boolean){
        sentenceListenProgress.visibilityState(flag)
        sentenceEvalProgress.visibilityState(flag)
        sentenceWordProgress.visibilityState(flag)
        sentenceExerciseProgress.visibilityState(flag)
        sentenceMoocProgress.visibilityState(flag)
    }

    private fun StudyProgress?.countProgressPair(totalTime: Int) = if (this != null) {
        if (endFlag == 1) {
            Pair("100%", 100)
        } else {
            try {
                val sdf = Constant.SDF
                val time = (sdf.parse(endTime)?.time ?: 0) - (sdf.parse(startTime)?.time ?: 0) / 1000
                val progressTime = (time * 100 / totalTime).toInt()
                Pair("${progressTime}%", progressTime)
            } catch (e: Exception) {
                Pair("0%", 0)
            }
        }
    } else {
        Pair("0%", 0)
    }

    //计算并显示标题
    private fun showComputeAndShowTitle(titleView:TextView,titleStr:String){
        Log.d("文本显示和操作", "显示文本--"+titleStr)

        val newTitleStr = titleStr.trim()
        //获取文本大小
        var textSize = 26
        //分割并循环计算可以显示的数据
        var showText = newTitleStr.replace("/","/ ")
        showText = showText.replace("!","! ")
        showText = showText.replace("?","? ")
        var showArray = showText.split(" ")

        var buffer:StringBuffer = StringBuffer()
        var showTitle = ""
        for (i in showArray.indices){
            showTitle=showTitle+" "+showArray[i]
            //计算
            var tempLength = showTitle.length*textSize
            if (tempLength<titleViewLength){
                if (i != 0){
                    buffer.append(" ")
                }
                buffer.append(showArray[i])
            }else{
                buffer.append("...")
                break
            }

            //这里将非必要空格删除
            showTitle = showTitle.trim()
            showTitle = showTitle.replace("/ ","/")
            showTitle = showTitle.replace("? ","?")
            showTitle = showTitle.replace("! ","!")
        }

        //显示文本
        var titleShowText = buffer.toString()
        titleShowText = titleShowText.replace("/ ","/")
        titleShowText = titleShowText.replace("? ","?")
        titleShowText = titleShowText.replace("! ","!")
        titleView.text = titleShowText
    }

    //获取当前的课程类型
    private fun getExerciseType(): String? {
        var languageType = TypeLibrary.BookType.conceptFourUS
        if (currentYoung) {
            languageType = TypeLibrary.BookType.conceptJunior
        } else {
            val curLanguage = currentLanguage.language
            if (curLanguage == "US") {
                languageType = TypeLibrary.BookType.conceptFourUS
            } else if (curLanguage == "UK") {
                languageType = TypeLibrary.BookType.conceptFourUK
            }
        }
        return languageType
    }
}