package com.suzhou.concept.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.CorrectSoundResponse
import com.suzhou.concept.bean.EvaluationSentenceDataItem
import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.bean.EvaluationSentenceResponse
import com.suzhou.concept.bean.LikeEvaluation
import com.suzhou.concept.bean.MergeResponse
import com.suzhou.concept.bean.RankInfoItem
import com.suzhou.concept.bean.ReleaseResponse
import com.suzhou.concept.bean.YoungSentenceItem
import com.suzhou.concept.bean.YoungSentenceList
import com.suzhou.concept.dao.paging.RankPaging
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.utils.FlowResult
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.changeEncode
import com.suzhou.concept.utils.putAppName
import com.suzhou.concept.utils.putFormat
import com.suzhou.concept.utils.putPlatform
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EvaluationViewModel : BaseViewModel() {


    suspend fun evaluationSentence(
        item: EvaluationSentenceItem,
        fileName: String,
        wordId: String = "0"
    ): Flow<EvaluationSentenceResponse> {
        val file = File(fileName)
        val body = MultipartBody.create(MediaType.parse("application/octet-stream"), file)
        val flg = (if (wordId == "0") "0" else "2")
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("type", AppClient.appName)
            .addFormDataPart("userId", GlobalMemory.userInfo.uid.toString())
            .addFormDataPart("newsId", item.voaid)//titleNum
            .addFormDataPart("paraId", item.Paraid)//senIndex
            .addFormDataPart("IdIndex", item.IdIndex.toString())//senIndex
            .addFormDataPart("sentence", item.Sentence)
            .addFormDataPart("file", file.name, body)
            .addFormDataPart("wordId", wordId)
            .addFormDataPart("flg", flg)
            .addFormDataPart("appId", AppClient.appId.toString())
            .build()
        return Repository.evaluationSentence(evaluationUrl, builder)
    }

    fun startEvalSentence(item: EvaluationSentenceItem, fileName: String, wordId: String = "0") {
        //飞雷神
        val file = File(fileName)
        val body = MultipartBody.create(MediaType.parse("application/octet-stream"), file)
        val flg = (if (wordId == "0") "0" else "2")
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("type", AppClient.appName)
            .addFormDataPart("userId", GlobalMemory.userInfo.uid.toString())
            .addFormDataPart("newsId", item.voaid)//titleNum
            .addFormDataPart("paraId", item.Paraid)//senIndex
            .addFormDataPart("IdIndex", item.IdIndex.toString())//senIndex
            .addFormDataPart("sentence", item.Sentence)
            .addFormDataPart("file", file.name, body)
            .addFormDataPart("wordId", wordId)
            .addFormDataPart("flg", flg)
            .addFormDataPart("appId", AppClient.appId.toString())
            .build()
        viewModelScope.launch {
            Repository.evaluationSentence(evaluationUrl, builder).onStart {

            }.flatMapConcat {
                val response = it.data
                item.apply {
                    success = true
                    fraction = (response.total_score * 20).toInt().toString()
                    selfVideoUrl = response.URL
                }
                item.success = true
                flow { emit(0) }
            }.catch {

            }.flowOn(Dispatchers.IO).collect {

            }
        }
    }

    fun insertEvaluation(resultList: List<EvaluationSentenceDataItem>) =
        Repository.insertEvaluation(resultList)

    val evalList = MutableSharedFlow<FlowResult<List<EvaluationSentenceDataItem>>>(5, 5)
    private suspend fun getEvaluationList(userId: Int, voaId: Int) {
        viewModelScope.launch {
            Repository.selectEvaluationList(userId, voaId).catch {
                evalList.emit(FlowResult.Error(it))
            }.collect {
                evalList.emit(FlowResult.Success(it))
            }
        }
    }

    fun selectSentenceList(userId: Int, voaId: Int) = Repository.selectSentenceList(userId, voaId)
    fun updateEvaluationSentenceItemStatus(item: EvaluationSentenceItem) =
        Repository.updateEvaluationSentenceItemStatus(
            GlobalMemory.userInfo.uid, AppClient.conceptItem.voa_id.toInt(), item
        )

    fun selectEvaluationByKey(onlyKey: String) = Repository.selectEvaluationByKey(onlyKey)
    fun deleteSentenceDataItemByKey(onlyKey: String) =
        Repository.deleteSentenceDataItemByKey(onlyKey)

    fun updateEvaluationChildStatus(score: Float, onlyKey: String, index: Int) =
        Repository.updateEvaluationChildStatus(score, onlyKey, index)

    fun updateYoungSentenceItemStatus(item: EvaluationSentenceItem) =
        Repository.updateYoungSentenceItemStatus(item)

    val sentenceResult = MutableSharedFlow<FlowResult<List<EvaluationSentenceItem>>>(20, 20)

    /**
     * 全四册的List<句子>
     * */
    private suspend fun getFourSentence(voaId: Int, userId: Int) =
        selectSentenceList(userId, voaId).flatMapConcat {
            if (it.isEmpty()) {
                Repository.getEvaluationSentenceList(voaId).flatMapConcat { netData ->
                    val list = with(netData.data) {
                        forEach { item ->
                            item.inflateEmptyValue()
                        }
                        Repository.insertSentence(this)
                        this
                    }
                    flow { emit(list) }
                }
            } else {
                getEvaluationList(userId, voaId)
                flow { emit(it) }
            }
        }

    /**
     * 青少版的List<句子>
     * */
    private fun getYoungSentence(voaId: Int, userId: Int) =
        requestOriginalYoungSentence(voaId, userId).map {
            it.map { item ->
                item.toEvaluationSentenceItem()
            }
        }

    private fun requestOriginalYoungSentence(voaId: Int, userId: Int) =
        Repository.selectClassSentence(voaId).flatMapConcat {
            if (it.isEmpty()) {
                requestYoungSentence(voaId.toString()).flatMapConcat { result ->
                    with(result.voatext) {
                        forEach { item ->
                            item.inflateEmptyValue()
                            item.voaId = voaId.toString()
                        }
                        this
                    }.let { list ->
                        Repository.insertSentenceList(list)
                        flow { emit(list) }
                    }.catch {
                        val test = 0
                    }
                }
            } else {
                getEvaluationList(userId, voaId)
                flow { emit(it) }
            }
        }

    val dubList = MutableSharedFlow<FlowResult<List<YoungSentenceItem>>>()

    /**
     * 口语秀部分
     * 一个GroupList，对应多个ChildList
     * */
    fun requestDubSentence(voaId: Int) {
        viewModelScope.launch {
            val uid = GlobalMemory.userInfo.uid
            requestOriginalYoungSentence(voaId, uid).flatMapConcat { young ->
                Repository.selectEvaluationList(uid, voaId).flatMapConcat {
                    flow { emit(Pair(young, it)) }
                }
            }.map {
                it.first.forEach { item ->
                    item.wordList.addAll(it.second.filter { evalItem ->
                        evalItem.onlyKay == item.onlyKay
                    })
                    item.voaId = voaId.toString()
                }
                it.first
            }.onStart {
                dubList.emit(FlowResult.Loading())
            }.catch {
                dubList.emit(FlowResult.Error(it))
            }.collect {
                dubList.emit(FlowResult.Success(it))
            }
        }
    }

    /**
     * 获取List<句子>
     * */
    fun requestSentenceList() {
        viewModelScope.launch {
            val userId = GlobalMemory.userInfo.uid
            val voaId = if (AppClient.conceptItem.bookId < 0) {
                when {
                    GlobalMemory.currentLanguage.isUK() -> 1001
                    GlobalMemory.currentLanguage.isUS() -> 10010
                    GlobalMemory.currentYoung -> 321001
                    else -> 1001
                }.apply {
                    AppClient.conceptItem.voa_id = this.toString()
                }
            } else {
                AppClient.conceptItem.voa_id.toInt()
            }


            if (GlobalMemory.currentYoung) {
                getYoungSentence(voaId, userId)
            } else {
                getFourSentence(voaId, userId)
            }.onStart {
                sentenceResult.emit(FlowResult.Loading())
            }.catch {
                sentenceResult.emit(FlowResult.Error(it))

                //使用eventbus回调
                EventBus.getDefault().post(RefreshEvent(RefreshEvent.STUDY_FINISH, "加载数据失败，请重试～"))
            }.collect {
                sentenceResult.emit(FlowResult.Success(it))

                //使用eventbus回调
                EventBus.getDefault().post(RefreshEvent(RefreshEvent.STUDY_FINISH, null))
            }
        }
    }

    private fun requestYoungSentence(voaId: String = AppClient.conceptItem.voa_id): Flow<YoungSentenceList> {
        dataMap.apply {
            clear()
            put("voaid", voaId)
        }
        return Repository.getYoungSentence(sentenceUrl, dataMap)
    }

    private fun requestYoungSentenceNew(voaId: String = AppClient.conceptItem.voa_id): Flow<String> {
        dataMap.apply {
            clear()
            put("voaid", voaId)
        }
        return Repository.getYoungSentenceNew(sentenceUrl, dataMap)
    }

    suspend fun mergeVideos(audios: String): Flow<Result<MergeResponse>> {
        dataMap.apply {
            clear()
            put("audios", audios)
            putAppName()
        }
        return Repository.mergeVideos(mergeUrl, dataMap)
    }


    suspend fun releaseMerge(score: String, content: String): Flow<Result<ReleaseResponse>> {
        dataMap.apply {
            clear()
            putAppName("topic")
            putPlatform()
            putFormat()
            putProtocol("60003")
            putUserId("userid")
            put("voaid", AppClient.conceptItem.voa_id)
            put("score", score)
            put("content", content)
            put("shuoshuotype", "4")
        }
        return Repository.releaseMerge(releaseSimple, dataMap)
    }

    val evalInfoResult = MutableSharedFlow<FlowResult<List<RankInfoItem>>>()

    /**
     * 获取排行榜具体信息
     * */
    fun getWorksByUserId(userId: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())
        val sign = "${userId}getWorksByUserId${dateFormat}".toMd5()
        val topicId = AppClient.conceptItem.voa_id
        dataMap.apply {
            clear()
            putAppName("topic")
            put("topicId", topicId)
            put("uid", userId.toString())
            put("sign", sign)
            put("shuoshuoType", "2,4")
        }
        viewModelScope.launch {
            Repository.getWorksByUserId(releaseEvalUrl + "getWorksByUserId.jsp", dataMap).onStart {
                evalInfoResult.emit(FlowResult.Loading())
            }.flatMapMerge { result ->
                result.data.forEach {
                    val item = if (it.shuoshuotype == 4) {
                        if (GlobalMemory.currentYoung) {
                            Repository.selectEvalInfoSentence().toEvaluationSentenceItem()
                        } else {
                            Repository.selectSimpleEvaluation()
                        }
                    } else {
                        try {
                            if (GlobalMemory.currentYoung) {
                                Repository.selectEvalInfoSentence(it.paraid)
                                    .toEvaluationSentenceItem()
//                                Repository.selectEvalInfoSentenceNew(it.paraid,it.idIndex).toEvaluationSentenceItem()
                            } else {
                                //这里根据逻辑处理下，如果paraId>1的话，则对paraId和idIndex互换
                                var searchParaId = it.paraid
                                var searchIdIndex = it.idIndex
                                if (searchParaId > 1 && searchParaId > searchIdIndex) {
                                    //互换数据
                                    searchParaId = it.idIndex
                                    searchIdIndex = it.paraid
                                }

                                Repository.selectSimpleEvaluation(searchIdIndex, searchParaId.toString())
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            if (GlobalMemory.currentYoung) {
                                Repository.selectEvalInfoSentence().toEvaluationSentenceItem()
                            } else {
                                Repository.selectSimpleEvaluation()
                            }
                        }
                    }
                    it.sentenceZh = item.Sentence_cn
                    it.sentenceEn = item.Sentence
                }
                flow { emit(result.data) }
            }.catch {
                evalInfoResult.emit(FlowResult.Error(it))
            }.collect {
                evalInfoResult.emit(FlowResult.Success(it))
            }
        }
    }

    suspend fun releaseSimple(bean: EvaluationSentenceItem): Flow<ReleaseResponse> {
        dataMap.apply {
            clear()
            putAppName("topic")
            putPlatform()
            putProtocol("60002")
            putFormat()
            putUserId("userid")
            put("voaid", AppClient.conceptItem.voa_id)
            put("username", GlobalMemory.userInfo.username)
            put("shuoshuotype", "2")
            put("paraid", bean.Paraid)
            put("idIndex", bean.IdIndex.toString())
            put("score", bean.fraction)
            put("content", bean.selfVideoUrl)
        }
        return Repository.releaseSimple(releaseSimple, dataMap)
    }

    suspend fun likeEvaluation(id: Int): Flow<ReleaseResponse> {
        dataMap.apply {
            clear()
            put("id", id.toString())
            putProtocol("61001")
        }
        return Repository.likeEvaluation(releaseSimple, dataMap)
    }

    suspend fun correctSound(
        word: String,
        item: EvaluationSentenceDataItem
    ): Flow<CorrectSoundResponse> {
        dataMap.apply {
            clear()
            put("q", word)
            put("user_pron", item.user_pron.changeEncode())
            put("ori_pron", item.pron.changeEncode())
        }
        return Repository.correctSound(correctSoundUrl, dataMap)
    }


    fun insertSimpleLikeEvaluation(item: LikeEvaluation) =
        Repository.insertSimpleLikeEvaluation(item)

    fun selectSimpleLikeEvaluation(itemId: Int) =
        Repository.selectSimpleLikeEvaluation(GlobalMemory.userInfo.uid, itemId)


    fun getRankData() = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20
        ),
        pagingSourceFactory = { RankPaging(pagingEvalUrl) }
    ).flow

    suspend fun getRankDataUser() = Repository.getRankData(pagingEvalUrl)

    /**
     * 走出LiveData时代
     * */
//    val transferSentenceListResult = MutableLiveData<List<EvaluationSentenceItem>>()
//    fun transferSentenceList(list: List<EvaluationSentenceItem>) {
//        transferSentenceListResult.value=(list)
//    }

//    val transferEvaluationListResult = MutableLiveData<List<EvaluationSentenceDataItem>>()
//    fun transferEvaluationList(list: List<EvaluationSentenceDataItem>) {
//        transferEvaluationListResult.value=(list)
//    }

}