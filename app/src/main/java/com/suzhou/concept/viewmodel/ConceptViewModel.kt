package com.suzhou.concept.viewmodel


import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.BookItem
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.ExerciseEvent
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.LocalCollect
import com.suzhou.concept.bean.PdfResponse
import com.suzhou.concept.bean.PickWord
import com.suzhou.concept.bean.QqResponse
import com.suzhou.concept.bean.ShareContentResponse
import com.suzhou.concept.bean.StrangenessWord
import com.suzhou.concept.bean.StudyRecordResponse
import com.suzhou.concept.bean.WordEvent
import com.suzhou.concept.bean.WordStatus
import com.suzhou.concept.dao.paging.DataSource
import com.suzhou.concept.utils.FlowResult
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.changeEncode
import com.suzhou.concept.utils.changeRoomData
import com.suzhou.concept.utils.getRecordTime
import com.suzhou.concept.utils.logic.ProgressType
import com.suzhou.concept.utils.putAppName
import com.suzhou.concept.utils.putFormat
import com.suzhou.concept.utils.putPlatform
import com.suzhou.concept.utils.timeStampDate
import com.suzhou.concept.utils.toMd5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class ConceptViewModel : BaseViewModel() {

    val switchNextVideo=MutableSharedFlow<FlowResult<Boolean>>()

    /**
     * 切换到下一曲
     * */
    fun requestLocalNextVideo() {
        val item = AppClient.conceptItem
        viewModelScope.launch {
            if (GlobalMemory.currentYoung) {
                getYoungNextVideo(item)
            } else {
                getFourNextVideo(item)
            }.catch {
                switchNextVideo.emit(FlowResult.Error(it))
            }.collect {
                AppClient.conceptItem=it
                switchNextVideo.emit(FlowResult.Success(true))
            }
        }
    }
    /**
     * 全四册的下一曲
     * */
    private fun getFourNextVideo(item:ConceptItem)=Repository.selectSimpleConceptItem(item.bookId, item.language, item.index + 1).flatMapConcat {
        if (it.isEmpty()) {
            Repository.selectSimpleConceptItem(item.bookId, item.language, 0)
        } else {
            flow { emit(it) }
        }
    }.map {
        it[0]
    }
    /**
     * 青少版的下一曲
     * */
    private fun getYoungNextVideo(item:ConceptItem)=Repository.selectSimpleYoungItem(item.bookId, item.index + 1).flatMapConcat {
        if (it.isEmpty()) {
            Repository.selectSimpleYoungItem(item.bookId, 1)
        } else {
            flow { emit(it) }
        }
    }.map {
        it[0].toConceptItem()
    }

    val articleListResult=MutableSharedFlow<FlowResult<List<ConceptItem>>>(10,10)
    /**
     * 请求课文列表
     * */
    fun requestArticleList(type: LanguageType){
        viewModelScope.launch {
            if (GlobalMemory.currentYoung) {
                requestYoungBook(type.bookId)
            } else {
                requestFourBook(type)
            }.onStart {
                articleListResult.emit(FlowResult.Loading(true))
            }.catch {
                articleListResult.emit(FlowResult.Error(it))
            }.collect{
                articleListResult.emit(FlowResult.Success(it))
            }
        }
    }
    /**
     * 请求青少版的课文列表
     * */
    private fun requestYoungBook(bookId:Int)=Repository.selectYoungItemList(bookId).flatMapConcat {
        if (it.isEmpty()) {
            requestNetBookList(bookId.toString())
        } else {
            flow { emit(it) }
        }
    }.map { list ->
        Log.d("1111111111111111", "requestYoungBook: ___________${list.first()}")
        list.map { item ->
            item.toConceptItem()
        }
    }

    val speakList= MutableSharedFlow<FlowResult<List<ConceptItem>>>(10,10)
    val speakTitle=MutableSharedFlow<String>()
    fun requestYoungBookList(bookId:Int=-1){
        viewModelScope.launch {
            if (bookId==-1){
                Repository.getSpeakShow().flatMapConcat {
                    speakTitle.emit(it.language)
                    requestYoungBook(it.bookId)
                }
            }else{
                requestYoungBook(bookId)
            }.onStart {
                speakList.emit(FlowResult.Loading())
            }.catch {
                speakList.emit(FlowResult.Error(it))
            }.collect{
                speakList.emit(FlowResult.Success(it))
            }
        }
    }

    fun saveSpeakShow(speak:LanguageType)=Repository.saveSpeakShow(speak)



    /**
     * 请求全四册的课文列表
     * */
    private fun requestFourBook(type: LanguageType)=
        Repository.selectConceptItemList(type.bookId, type.language).flatMapConcat {
        if (it.isEmpty()) {
            Repository.getConceptListAgain(type.language, type.bookId)
                .flatMapConcat { result ->
                    result.data.changeRoomData(type).let { list ->
                        Repository.insertConceptItem(list)
                        flow { emit(list) }
                    }
                }
        } else {
            flow { emit(it) }
        }
    }

    /**
     * 请求青少版的接口
     * */
    private suspend fun requestNetBookList(id:String):Flow<List<BookItem>>{
        val sign="iyuba${getDayDistance()}series".toMd5()
        dataMap.apply {
            clear()
            putAppId()
            putUserId("uid")
            put("type","title")
            put("sign",sign)
            put("seriesid",id)
        }
        return Repository.getSingleBook(youngListUrl,dataMap).flatMapConcat { result->
            with(result){
                for (i in 1..total){
                    data[i-1].apply {
                        index=i
                        userId=GlobalMemory.userInfo.uid
                    }
                }
                Repository.insertYoungBookItem(data)
                flow { emit(data) }
            }
        }
    }
    /**
     * ------------------------------------------------------------
     * */
    private val qqGroup=MutableSharedFlow<FlowResult<QqResponse>>()
    val lastQQ=qqGroup.asSharedFlow()

    fun requestQQGroup(){
        viewModelScope.launch {
            dataMap.apply {
                clear()
                put("type",deviceName)
                putUserId()
                putAppId("appId")
            }
            Repository.requestQQGroup(qqUrl,dataMap).catch {
                qqGroup.emit(FlowResult.Error(it))
            }.collect{
                qqGroup.emit(FlowResult.Success(it))
            }
        }
    }

    val pdfResult= MutableSharedFlow<FlowResult<PdfResponse>>(10,10)

    fun requestPdf(isEnglish:Int ,youngFlag:Boolean=GlobalMemory.currentYoung,voaId:String=AppClient.conceptItem.voa_id){
        dataMap.apply {
            clear()
            put("type",if (youngFlag) "voa" else AppClient.appName)
            put("voaid",voaId)
            put("isenglish",isEnglish.toString())
        }
        viewModelScope.launch {
            Repository.getPdfFile(getPdfUrl(youngFlag),dataMap).onStart {
                pdfResult.emit(FlowResult.Loading())
            }.catch {
                pdfResult.emit(FlowResult.Error(it))
            }.collect{
                pdfResult.emit(FlowResult.Success(it))
            }
        }
    }


    suspend fun requestPickWord(word: String):Flow<PickWord> {
        dataMap.apply {
            clear()
            put("q",word)
            putUserId("uid")
            putAppId("appId")
            put("TestMode","1")
        }
        return Repository.pickWord("${wordUrl}apiWord.jsp",dataMap)
    }


    fun requestStrangenessWord()=Pager(
        config = PagingConfig(
            pageSize = 8,
            initialLoadSize = 10),
        pagingSourceFactory = { DataSource() }
    ).flow

    fun requestStrangenessConcat():Flow<StrangenessWord>{
        dataMap.apply{
            put("u",GlobalMemory.userInfo.uid.toString())
            put("pageNumber","1")
            put("pageCounts","1")
        }
        return Repository.requestStrangenessSingle(OtherUtils.wordPagingUrl,dataMap)
    }


    suspend fun changeCollectStatus( word: String, mod: Boolean):Flow<WordStatus> {
        val action=if (mod) "delete" else "insert"
        dataMap.apply {
            clear()
            putUserId()
            put("groupName","Iyuba")
            put("word",word)
            put("mod",action)
        }
        return Repository.changeCollectStatus("${wordUrl}updateWord.jsp",dataMap)
    }

    fun insertWord(collect: LocalCollect)=Repository.insertWord(collect)
    fun updateWord(newCollect: LocalCollect)=Repository.updateWord(newCollect.isCollect,newCollect.word)
    fun selectCollectByWord(word:String)=Repository.selectCollectByWord(word)

    suspend fun requestCustomerService() =Repository.requestCustomerService(customerUrl, AppClient.appId)

    val shareResult= MutableSharedFlow<FlowResult<ShareContentResponse>>()

    fun shareContent(srid:Int){
        val time=System.currentTimeMillis().timeStampDate("yyyyMMddHHmmss")
        dataMap.apply {
            clear()
            put("srid",srid.toString())
            putUserId("uid")
            putAppId()
            put("idindex", AppClient.conceptItem.voa_id)
            put("mobile","1")
            put("flag","1234567890${time}")
        }
        viewModelScope.launch {
            Repository.shareContent(shareContentUrl,dataMap).onStart {
                shareResult.emit(FlowResult.Loading())
            }.catch {
                shareResult.emit(FlowResult.Error(it))
            }.collect{
                shareResult.emit(FlowResult.Success(it))
            }
        }
    }

    suspend fun submitStudyRecord(startTime:String,isEnd:Boolean,testWords:String, testNumber:String): Flow<Result<StudyRecordResponse>> {
        val endFlag=if (isEnd)"1" else "0"
        val score="0"
        val testMode="1"
        val userAnswer=""
        val sign=GlobalMemory.userInfo.uid.toString()+startTime+System.currentTimeMillis().timeStampDate("yyyy-MM-dd").toMd5()

        dataMap.apply {
            clear()
            putFormat()
            putPlatform()
            putAppName("appName")
            put("Lesson",AppClient.appName.changeEncode())
            putAppId("appId")
            put("BeginTime",startTime.changeEncode())
            put("EndTime", getRecordTime().changeEncode())
            put("EndFlg",endFlag)
            put("LessonId",AppClient.conceptItem.lessonId)
            put("TestNumber",testNumber)
            put("TestWords",testWords)
            put("TestMode",testMode)
            put("UserAnswer",userAnswer)
            put("Score",score)
            put("DeviceId",deviceName)
            putUserId("uid")
            put("sign",sign)
            put("rewardVersion","1")//增加奖励机制
        }
        return Repository.submitStudyRecord(submitRecordUrl,dataMap)
    }

    /**
     * ----------------------------------更新听力进度时刷新UI----------------------------------
     * */
    private val updateListen=MutableSharedFlow<FlowResult<Int>>()
    val lastUpdateListen=updateListen.asSharedFlow()

    fun updateListenItem(progress: Int) {
        if (AppClient.conceptItem.listenProgress > progress) {
            return
        }
        viewModelScope.launch {
            flow {
                val bookId = AppClient.conceptItem.bookId
                val index = AppClient.conceptItem.index
                if (GlobalMemory.currentYoung) {
                    val userId = GlobalMemory.userInfo.uid
                    Repository.updateItemListen(bookId, index, userId, progress)
                } else {
                    val language = AppClient.conceptItem.language
                    Repository.updateListenConceptItem(bookId, language, index, progress)
                }
                emit(progress)
            }.flowOn(Dispatchers.IO)
                .catch {
                    updateListen.emit(FlowResult.Error(it))
                }.collect {
                    AppClient.conceptItem.listenProgress=progress
                    updateListen.emit(FlowResult.Success(it))
                }
        }
    }

    /**
     * ----------------------------------更新评测进度时刷新UI----------------------------------
     * */
    private val updateEval=MutableSharedFlow<FlowResult<Pair<ProgressType,Int>>>()
    val lastUpdateEval=updateEval.asSharedFlow()

    /**
     * 只有新评测时才执行此方法，再次评测同一句时不执行
     * */
    fun updateEvalItem(){
        val evalFlag=with(AppClient.conceptItem){
            evalSuccess+1>text_num.toInt()
        }
        if (evalFlag){
            //避免出现评测成功数量大于句子总数的情况
            return
        }
        viewModelScope.launch {
            flow {
                val bookId = AppClient.conceptItem.bookId
                val index = AppClient.conceptItem.index
                val evalSuccess=AppClient.conceptItem.evalSuccess+1
                if (GlobalMemory.currentYoung) {
                    val userId = GlobalMemory.userInfo.uid
                    Repository.updateItemEval(bookId, index, userId, evalSuccess)
                } else {
                    val language = AppClient.conceptItem.language
                    Repository.updateEvalConceptItem(bookId, language, index, evalSuccess)
                }
                emit(AppClient.conceptItem.evalSuccess + 1)
            }.catch {
                updateEval.emit(FlowResult.Error(it))
            }.flowOn(Dispatchers.IO)
                .collect {
                    updateEval.emit(FlowResult.Success(Pair(ProgressType.EVAL,it)))
                }
        }
    }

    /**
     *
     * */
    private val goWord=MutableSharedFlow<Int>()
    val lastGoWord=goWord.asSharedFlow()
    fun changeGoWord(){
        viewModelScope.launch {
            goWord.emit(0)
        }
    }

    /**
     * ----------------------------------------------------------------------------------------------
     * */
    private val updateWord=MutableSharedFlow<FlowResult<Pair<ProgressType,Int>>>()
    val lastUpdateWord=updateWord.asSharedFlow()
    fun updateWordItem(rightNum:Int,wordNum:Int,bookId: Int,index:Int){
        if (rightNum>wordNum){
            //避免出现做题成功数量大于句子总数的情况
            return
        }
        viewModelScope.launch {
            flow {
                if (GlobalMemory.currentYoung) {
                    val userId = GlobalMemory.userInfo.uid
                    Repository.updateItemWord(bookId, index, userId, rightNum)
                } else {
                    //这里不区分语言类型，都插入一遍
                    Repository.updateWordConceptItemWithLanguage(bookId, index, rightNum,"US")
                    Repository.updateWordConceptItemWithLanguage(bookId, index, rightNum,"UK")
                }
                emit(rightNum)
            }.catch {
                updateEval.emit(FlowResult.Error(it))
            }.flowOn(Dispatchers.IO)
                .collect {
                    EventBus.getDefault().post(WordEvent(rightNum,wordNum,bookId, index))
                    updateEval.emit(FlowResult.Success(Pair(ProgressType.WORD,it)))
                }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * */
    private val updateExercise=MutableSharedFlow<FlowResult<Pair<ProgressType,Int>>>(10,10)
    val lastUpdateExercise=updateExercise.asSharedFlow()
    fun updateExerciseItem(rightNum:Int){
        val exerciseFlag=with(AppClient.conceptItem){
            rightNum>choice_num.toInt()
        }
        if (exerciseFlag){
            //避免出现做题成功数量大于句子总数的情况
            return
        }
        viewModelScope.launch {
            flow {
                val bookId = AppClient.conceptItem.bookId
                val index = AppClient.conceptItem.index
                val language = AppClient.conceptItem.language
                Repository.updateExerciseConceptItem(bookId, language, index, rightNum)
                emit(rightNum)
            }.catch {
                updateExercise.emit(FlowResult.Error(it))
            }.flowOn(Dispatchers.IO)
                .collect {
                    EventBus.getDefault().post(ExerciseEvent(it))
                    updateExercise.emit(FlowResult.Success(Pair(ProgressType.EXERCISE,it)))
                }
        }
    }
}
