package com.suzhou.concept.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.EvaluationSentenceData
import com.suzhou.concept.bean.LikeYoung
import com.suzhou.concept.bean.LikeYoungResponse
import com.suzhou.concept.bean.MineReleaseItem
import com.suzhou.concept.bean.PostItem
import com.suzhou.concept.bean.WavListItem
import com.suzhou.concept.bean.YoungItem
import com.suzhou.concept.bean.YoungSentenceItem
import com.suzhou.concept.dao.paging.YoungSpeakingRank
import com.suzhou.concept.utils.FlowResult
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.putAppName
import com.suzhou.concept.utils.putProtocol
import com.suzhou.concept.utils.toMd5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.UUID

/**
苏州爱语吧科技有限公司
@Date:  2022/10/15
@Author:  han rong cheng
 */
class YoungViewModel:BaseViewModel() {

    val bookList=MutableSharedFlow<FlowResult<List<YoungItem>>>()

    /**
     * 请求青少版课本网络信息并存储到本地room中
     * */
    private suspend fun requestNetBook():Flow<List<YoungItem>>{
        val sign="iyuba${getDayDistance()}series".toMd5()
        dataMap.apply {
            clear()
            put("type","category")
            put("category","321")
            putAppId()
            putUserId("uid")
            put("sign",sign)
        }
        return Repository.getBookList(youngListUrl,dataMap).flatMapMerge { net->
            with(net){
                Repository.insertYoungItem(data)
                flow { emit(data) }
            }
        }
    }
    fun requestBookList(){
//        viewModelScope.launch {
//            Repository.selectAllYoungItem().onStart {
//                bookList.emit(FlowResult.Loading())
//            }.catch {
//                bookList.emit(FlowResult.Error(it))
//            }.flatMapMerge {
//                if (it.isEmpty()){
//                    requestNetBook()
//                }else{
//                    flow { emit(it) }
//                }
//            }.collect{
//                bookList.emit(FlowResult.Success(it))
//            }
//        }

        viewModelScope.launch {
            try {
                Repository.selectAllYoungItem().onStart {
                    bookList.emit(FlowResult.Loading())
                }.flatMapMerge {
                    if (it.isEmpty()){
                        requestNetBook()
                    }else{
                        flow { emit(it) }
                    }
                }.collect{
                    bookList.emit(FlowResult.Success(it))
                }
            }catch (e:Exception){
                Log.d("异常信息", "requestBookList: --"+e.message)
            }
        }
    }


    val youngItem=MutableSharedFlow<ConceptItem>(5,5)
    fun emitYoungItem(item:ConceptItem){
        viewModelScope.launch {
            youngItem.emit(item)
        }
    }

    fun requestRankList(voaId:String)=Pager(
        config = PagingConfig(
           pageSize = 20,
           initialLoadSize = 10
        ),
        pagingSourceFactory = {YoungSpeakingRank(releaseSimple,voaId)}
    ).flow


    fun updateItemCollect(bookId:Int,index:Int,isCollect:Boolean){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                Repository.updateItemCollect(bookId, index, isCollect)
            }
        }
    }

    fun updateItemDownload(item:ConceptItem)=item.apply {
        Repository.updateItemDownload(bookId,index,true)
    }

    //查询点赞同意的数据
    fun getSimpleYoungAgreeData(otherId: Int):List<LikeYoung> = Repository.selectYoungAgreeData(otherId)

    val speakingResult=MutableSharedFlow<FlowResult<Pair<String,EvaluationSentenceData>>>()
    /**
     * 根据角标刷新，
     * YoungSentenceItem内置Builder？？？
     * 课文部分与口语秀部分的并不一样，所以用不同规则的key来区分
     * */
    fun evalSentence(videoFile:File,item: YoungSentenceItem){
        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), videoFile)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("type", AppClient.appName)
            .addFormDataPart("sentence", item.Sentence)
            .addFormDataPart("userId", GlobalMemory.userInfo.uid.toString())
            .addFormDataPart("newsId", item.voaId)
            .addFormDataPart("paraId", item.ParaId)
            .addFormDataPart("IdIndex", item.IdIndex)
            .addFormDataPart("file", videoFile.name, fileBody)
            .build()
        viewModelScope.launch {
            Repository.evalSentence(speakingSentenceUrl,requestBody)
                .onStart {
                    speakingResult.emit(FlowResult.Loading())
                }.map {
                    it.data
                }.map {
                    var key=UUID.randomUUID().toString()+javaClass.simpleName
                    item.apply {
                        success=true
                        if (onlyKay.isEmpty()){
                            onlyKay=key
                        }else{
                            key=item.onlyKay
                        }
                        selfVideoUrl=it.URL
                        fraction=(it.total_score * 20).toInt().toString()
                        wordList.apply {
                            clear()
                            addAll(it.words)
                        }
                    }
                    Repository.updateYoungSentenceItemStatus(item.toEvaluationSentenceItem())
                    Repository.selectEvaluationByKey(key).apply {
                        if (isNotEmpty()){
                            Repository.deleteSentenceDataItemByKey(key)
                        }
                    }
                    it.words.forEach {word->
                        word.voaId=item.voaId.toInt()
                        word.userId=item.userId
                        word.onlyKay=key
                    }
                    Repository.insertEvaluation(it.words)
                    Pair(item.EndTiming,it)
                }.flowOn(Dispatchers.IO)
                .catch {
                    speakingResult.emit(FlowResult.Error(it))
                }.collect{
                    speakingResult.emit(FlowResult.Success(it))
                }
        }
    }


    val likeYoungResult=MutableSharedFlow<FlowResult<LikeYoungResponse>>()
    /**
     * 点赞别人的口语秀视频
     * */
    fun likeOtherOneYoung(otherId: Int) {
        viewModelScope.launch {
            Repository.selectSimple(otherId)
                .catch {
                    likeYoungResult.emit(FlowResult.Error(it))
                }.flatMapConcat {
                    if (it.isEmpty()) {
                        Repository.insertSimpleLikeYoung(LikeYoung(GlobalMemory.userInfo.uid, otherId))
                        likeOtherSpeaking(otherId.toString())
                    } else {
                        flow { emit(LikeYoungResponse()) }
                    }
                }.flowOn(Dispatchers.IO)
                .collect {
                    likeYoungResult.emit(FlowResult.Success(it))
                }
        }
    }

    private fun likeOtherSpeaking(otherId: String):Flow<LikeYoungResponse>{
        dataMap.apply {
            clear()
            putProtocol("61001")
            put("id",otherId)
        }
        return Repository.likeOtherSpeaking(releaseSimple,dataMap)
    }

    val mineReleasedResult=MutableSharedFlow<FlowResult<List<MineReleaseItem>>>()

    fun requestMineReleased(){
        dataMap.apply {
            clear()
            putUserId("uid")
            putAppName("appname")
        }
        viewModelScope.launch {
            Repository.getMintReleased(mineReleasedUrl,dataMap).onStart {
                mineReleasedResult.emit(FlowResult.Loading())
            }.catch {
                mineReleasedResult.emit(FlowResult.Error(it))
            }.collect{
                mineReleasedResult.emit(FlowResult.Success(it.data))
            }
        }
    }

    private val collectResult=MutableSharedFlow<FlowResult<List<ConceptItem>>>(6,6)
    private val downloadResult=MutableSharedFlow<FlowResult<List<ConceptItem>>>(6,6)

    fun judgeFlow(isCollect:Boolean)=(if (isCollect) collectResult else downloadResult)

    fun selectCollectOrDownload(isCollect:Boolean){
        val lastFlow=judgeFlow(isCollect)
        viewModelScope.launch {
            if (isCollect){
                Repository.selectCollected()
            }else{
                Repository.selectDownLoaded()
            }.onStart {
                lastFlow.emit(FlowResult.Loading())
            }.catch {
                lastFlow.emit(FlowResult.Error(it))
            }.map {
                it.map { item->
                    item.toConceptItem()
                }
            }.collect{
                lastFlow.emit(FlowResult.Success(it))
            }
        }
    }

    val mergeReleaseResult=MutableSharedFlow<FlowResult<String>>()

    fun mergeReleaseSpeaking(score:Int,list:List<WavListItem>){
        dataMap.apply {
            clear()
            putProtocol("60002")
            putUserId("userid")
            put("content","3")
        }
        val userName=GlobalMemory.userInfo.username
        val voaId=GlobalMemory.speakingItem.voa_id.toInt()
        val sound=GlobalMemory.speakingItem.youngChild.youngBackVoiceEndPath

        //这里处理下评测的音频文件
        val formatStr = "http://${OtherUtils.user_speech}voa/"
        for (i in list.indices){
            var item:WavListItem = list[i]
            item.URL = item.URL.replace(formatStr,"")
        }

        val item=PostItem(score,sound,userName,voaId, list)
        viewModelScope.launch {
            Repository.mergeReleaseSpeaking(releaseEvalUrl+"UnicomApi2",dataMap,item.toBody()).onStart {
                mergeReleaseResult.emit(FlowResult.Loading())
            }.catch {
                mergeReleaseResult.emit(FlowResult.Error(it))
            }.collect{
//                if (it.isError()){
                    mergeReleaseResult.emit(FlowResult.Success("恭喜您发布成功~\n积分 +5"))
//                }else{
//                    mergeReleaseResult.emit(FlowResult.Error(Throwable("发布失败~")))
//                }
            }
        }
    }
}