package com.suzhou.concept


import com.suzhou.concept.bean.BookItem
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.EvaluationSentenceDataItem
import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.LikeEvaluation
import com.suzhou.concept.bean.LikeYoung
import com.suzhou.concept.bean.LocalCollect
import com.suzhou.concept.bean.LoginResponse
import com.suzhou.concept.bean.RankResponse
import com.suzhou.concept.bean.ReDoBean
import com.suzhou.concept.bean.SelfResponse
import com.suzhou.concept.bean.WordItem
import com.suzhou.concept.bean.YoungItem
import com.suzhou.concept.bean.YoungSentenceItem
import com.suzhou.concept.bean.YoungWordItem
import com.suzhou.concept.dao.AppDatabase
import com.suzhou.concept.dao.UserDao
import com.suzhou.concept.net.AdService
import com.suzhou.concept.net.ConceptService
import com.suzhou.concept.net.EvaluationService
import com.suzhou.concept.net.ExerciseService
import com.suzhou.concept.net.ServiceCreator
import com.suzhou.concept.net.UserActionService
import com.suzhou.concept.net.WordService
import com.suzhou.concept.net.YoungService
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.emitFlow
import com.suzhou.concept.utils.nowTime
import com.suzhou.concept.utils.toMd5
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody


object Repository {
    private val conceptService = ServiceCreator.create<ConceptService>()
    private val userActionService = ServiceCreator.create<UserActionService>()
    private val wordService = ServiceCreator.create<WordService>()
    private val evaluationService = ServiceCreator.create<EvaluationService>()
    private val youngService = ServiceCreator.create<YoungService>()
    private val adService = ServiceCreator.create<AdService>()
    private val exerciseService = ServiceCreator.create<ExerciseService>()

    private val evaluationItemDao=AppDatabase.getDatabase(AppClient.context).evaluationItemDao()
    private val localSentenceDao=AppDatabase.getDatabase(AppClient.context).localSentenceDao()
    private val collectDao=AppDatabase.getDatabase(AppClient.context).collectDao()
    private val wordDao=AppDatabase.getDatabase(AppClient.context).wordDao()
    private val likeDao=AppDatabase.getDatabase(AppClient.context).likeDao()
    private val conceptDao=AppDatabase.getDatabase(AppClient.context).conceptDao()
    private val youngSentenceDao=AppDatabase.getDatabase(AppClient.context).youngSentenceDao()
    private val youngItemDao=AppDatabase.getDatabase(AppClient.context).youngItemDao()
    private val youngWordDao=AppDatabase.getDatabase(AppClient.context).youngWordDao()
    private val youngBookDao=AppDatabase.getDatabase(AppClient.context).youngBookDao()
    private val youngLikeDao=AppDatabase.getDatabase(AppClient.context).youngLikeDao()
    private val reDoDao=AppDatabase.getDatabase(AppClient.context).reDoDao()
    //单词闯关的操作
    private val wordBreakDao =AppDatabase.getDatabase(AppClient.context).wordBreakDao()

    fun getConceptListAgain(language: String, book: Int) = flow { emit(conceptService.getConceptList(language, book)) }
    fun getPdfFile(url: String,map: Map<String, String>) = flow { emit(conceptService.getPdfFile(url, map)) }
    suspend fun pickWord(url: String, map: Map<String, String>) = flow { emit(conceptService.pickWord(url,map)) }
    fun requestQQGroup(url:String, map: Map<String, String>)= flow { emit( conceptService.requestQQGroup(url, map)) }
    /**
     * 分页
     * */
    suspend fun requestStrangenessWord(url: String, map: Map<String, String>) = conceptService.requestStrangenessWord(url,map)
    fun requestStrangenessSingle(url: String, map: Map<String, String>)= flow { emit(requestStrangenessWord(url, map)) }
    suspend fun getRankData(url: String,currentPage:Int=0 ,pageSize:Int=0):RankResponse {
        val userId=GlobalMemory.userInfo.uid.toString()
        val topicId=AppClient.conceptItem.voa_id
        val topic=AppClient.appName
        val sign=(userId +topic+topicId+currentPage+pageSize+ nowTime()).toMd5()
        val dataMap= mutableMapOf<String,String>().apply {
            put("topic",topic)
            put("topicid",topicId)
            put("uid",userId)
            put("start",(currentPage*pageSize).toString())
            put("total",pageSize.toString())
            put("sign",sign)
            put("type","D")
        }
        return conceptService.getRankData(url, dataMap)
    }
    /***/
    suspend fun changeCollectStatus(url: String, map: Map<String, String>) = flow { emit(conceptService.changeCollectStatus(url,map)) }

    suspend fun requestCustomerService(url: String, appid: Int) = flow { emit(runCatching { conceptService.requestCustomerService(url,appid) }) }

    suspend fun mergeVideos(url: String, map: Map<String, String>) = flow { emit(kotlin.runCatching { conceptService.mergeVideos(url,map) }) }
    suspend fun releaseMerge(url: String, map: Map<String, String>) = flow { emit(runCatching { conceptService.releaseMerge(url, map) }) }
    fun getWorksByUserId(url:String, map: Map<String,String>) = flow { emit(conceptService.getWorksByUserId(url,map)) }
    fun releaseSimple(url:String,map: Map<String,String>) = flow { emit(conceptService.releaseSimple(url,map)) }
    fun likeEvaluation(url:String,map: Map<String,String>) = flow { emit(conceptService.likeEvaluation(url,map)) }
    fun correctSound(url:String,map: Map<String,String>) = flow { emit(conceptService.correctSound(url,map)) }
    fun shareContent(url:String,map: Map<String,String>) = flow { emit(conceptService.shareContent(url,map)) }
    fun evaluationSentence(url: String, body: RequestBody) = flow { emit(conceptService.evaluationSentence(url, body)) }
    suspend fun submitStudyRecord(url: String, map: Map<String,String>) = flow { emit(runCatching { conceptService.submitStudyRecord(url, map) }) }
    suspend fun signEveryDay(url: String, map: Map<String,String>) = flow { emit(runCatching { conceptService.signEveryDay(url, map) }) }
    //

    suspend fun login(url: String, map: Map<String, String>) = flow { emit(kotlin.runCatching { userActionService.login(url,map) }) }
    fun login1(url: String, map: Map<String, String>) = flow { emit(userActionService.login(url,map)) }

    suspend fun modifyUserName(url: String, map: Map<String, String>) = flow { emit(runCatching { userActionService.modifyUserName(url,map) }) }

    suspend fun logoutUser(url: String,map: Map<String, String>) = flow { emit(runCatching { userActionService.logoutUser(url,map) }) }

    suspend fun uploadPhoto(url: String,  part:MultipartBody.Part)= flow { emit(runCatching { userActionService.uploadPhoto(url,part) }) }
    suspend fun getRegisterStatus(url: String,  map: Map<String, String>)= flow { emit(runCatching { userActionService.getRegisterStatus(url,map) }) }
    suspend fun register(url: String,map: Map<String, String>)= flow { emit(runCatching { userActionService.register(url,map) }) }
    suspend fun refreshSelf(url: String,map: Map<String, String>) = flow { emit(runCatching { userActionService.refreshSelf(url,map) }) }

    fun requestPayVip(url: String, map: Map<String, String>) = flow { emit( userActionService.requestPayVip(url, map) ) }
    fun payVip(url: String, data:String) = flow { emit(userActionService.payVip(url,data)) }
    suspend fun secondVerify(url: String,map:Map<String,String>)= flow { emit(userActionService.secondVerify(url,map)) }
    suspend fun shareAddScore(url: String,map:Map<String,String>)= flow { emit(runCatching { userActionService.shareAddScore(url,map) }) }
    suspend fun getTopicRanking(url: String, map: Map<String, String>) = userActionService.getTopicRanking(url,map)
    fun getTopicRankingCount(url: String, map: Map<String, String>) = flow { emit(userActionService.getTopicRanking(url,map)) }
    fun getStudyListenRankingCount(url: String, map: Map<String, String>) = flow { emit(userActionService.getStudyListenRanking(url,map)) }
    suspend fun getStudyListenRanking(url: String, map: Map<String, String>) =userActionService.getStudyListenRanking(url,map)

    //

    fun getConceptWord(bookId:Int)= flow { emit(wordService.getConceptWord(bookId)) }
    fun getYoungWord(bookId:Int,url:String)= flow { emit(wordService.getYoungWord(url,bookId)) }
//
    fun getEvaluationSentenceList(voaId: Int) = flow { emit(evaluationService.getConceptSentenceList(voaId)) }

    //
    fun getBookList(url: String, map: Map<String, String>) = flow { emit(youngService.getBookList(url, map)) }
    fun getSingleBook(url: String, map: Map<String, String>) = flow { emit(youngService.getSingleBook(url, map))}
    fun getYoungSentence(url: String, map: Map<String, String>) = flow { emit(youngService.getYoungSentence(url, map)) }
    fun getYoungSentenceNew(url: String, map: Map<String, String>) = flow { emit(youngService.getYoungSentenceNew(url, map)) }
    suspend fun getYoungSpeakingRank(url: String, map: Map<String, String>) = youngService.getYoungSpeakingRank(url, map)
    suspend fun downloadFile(url: String) = youngService.downloadFile(url)
    fun evalSentence(url: String,body: RequestBody) = flow { emit(youngService.evalSentence(url,body)) }
    fun likeOtherSpeaking(url: String, map: Map<String, String>) = flow { emit(youngService.likeOtherSpeaking(url, map)) }
    fun getMintReleased(url: String, map: Map<String, String>) = flow { emit(youngService.getMintReleased(url, map)) }
    fun mergeReleaseSpeaking(url: String, map: Map<String, String>,body: RequestBody) = flow { emit(youngService.mergeReleaseSpeaking(url, map,body)) }

    fun requestAdType(url: String, map: Map<String, String>) = flow { emit(adService.requestAdType(url, map)) }
    fun getConceptExercise(url: String, bookNum:String) = flow { emit(exerciseService.getConceptExercise(url, bookNum)) }
    fun submitExerciseRecord(url: String,map:Map<String,String>) = flow { emit(exerciseService.submitExerciseRecord(url, map)) }
    fun requestTestRecord(url: String,map:Map<String,String>) = flow { emit(exerciseService.requestTestRecord(url, map)) }


    /**
     *
     * */
    fun getLanguage()= flow { emit(UserDao.getLanguage()) }
    fun saveLanguage(type: LanguageType)= flow { emit(UserDao.saveLanguage(type)) }
    fun getLoginResponse()= flow { emit(UserDao.getLoginResponse()) }
    fun saveLoginResponse(login: LoginResponse)= UserDao.saveLoginResponse(login)
    fun exitLogin()= flow { emit(UserDao.exitLogin()) }
    fun isFirstLogin()= flow { emit(UserDao.isFirstLogin()) }
    fun saveFirstLogin(isFirstLogin: Boolean)= flow { emit(UserDao.saveFirstLogin(isFirstLogin)) }
    fun modifyHead(url: String)= flow { emit(UserDao.modifyHead(url)) }
    fun modifyName(name: String)= UserDao.modifyName(name).emitFlow()
    fun saveSelf(self: SelfResponse) = flow { emit(UserDao.saveSelf(self)) }
    fun saveThroughWordId(wordId:Int) = UserDao.saveThroughWordId(wordId)
    fun getThroughWordId() = flow { emit(UserDao.getThroughWordId()) }
    fun saveSendSms() = flow { emit(UserDao.saveSendSms()) }
    fun getSendSmsStatus() = flow { emit(UserDao.getSendSmsStatus()) }
    fun getSplash() = UserDao.getSplash()
    fun saveSplash(url:String) = flow { emit(UserDao.saveSplash(url)) }
    fun saveSpeakShow(speak:LanguageType) = UserDao.saveSpeakShow(speak)
    fun getSpeakShow() = flow { emit(UserDao.getSpeakShow()) }
    //
    fun insertEvaluation(resultList: List<EvaluationSentenceDataItem>)= evaluationItemDao.insertEvaluation(resultList)
    fun selectEvaluationList(userId:Int,voaId:Int)= evaluationItemDao.selectEvaluationList(userId, voaId)
    fun selectEvaluationByKey(onlyKey:String)= evaluationItemDao.selectEvaluationByKey(onlyKey)
    fun deleteSentenceDataItemByKey(onlyKey:String)= evaluationItemDao.deleteSentenceDataItemByKey(onlyKey)
    fun updateEvaluationChildStatus(score:Float,onlyKey: String,index:Int)= flow { emit(evaluationItemDao.updateEvaluationChildStatus(score, onlyKey, index)) }

    fun insertSentence(sentenceResult: List<EvaluationSentenceItem>)= localSentenceDao.insertSentence(sentenceResult)
    fun selectSentenceList(userId:Int,voaId:Int)= flow { emit(localSentenceDao.selectSentenceList(userId, voaId)) }
    fun updateEvaluationSentenceItemStatus(userId:Int,voaId:Int,item: EvaluationSentenceItem)=
       flow { emit(localSentenceDao.updateEvaluationSentenceItemStatus(userId, voaId,item.IdIndex,item.onlyKay,item.success,item.fraction,item.selfVideoUrl)) }
    fun selectSimpleEvaluation(idIndex: Int=1, paraId: String="1",voaId: String=AppClient.conceptItem.voa_id) = localSentenceDao.selectSimpleEvaluation(voaId, idIndex, paraId)[0]
    //
    fun insertWord(collect: LocalCollect)= collectDao.insertWord(collect)
    fun updateWord(isCollect:Boolean,word:String)= flow { emit(collectDao.updateWord(isCollect, word)) }
    fun selectCollectByWord(word:String)= collectDao.selectCollectByWord(word)
    //
    fun insertWordList(word: List<WordItem>)= wordDao.insertWord(word)
    fun selectByBookIdAndUserId(bookId:Int,userId:Int)= flow { emit(wordDao.selectByBookId(bookId,userId)) }
    fun updateRightStatus(bookId:Int,position:Int,flag:Boolean,userId:Int,unitInt: Int)= wordDao.updateRightStatus(bookId,  position, flag,userId,unitInt)
    //查询全四册的正确单词数量
    fun selectRightWordCount(voaId: Int,userId: Int,correct:Boolean) = wordDao.selectRightWordNum(voaId, userId,correct)
    //查询全四册的单词数量
    fun selectWordCount(voaId: Int) = wordDao.selectWordCount(voaId)
    //
    fun insertSimpleLikeEvaluation(item:LikeEvaluation)= flow { emit(likeDao.insertSimple(item)) }
    fun selectSimpleLikeEvaluation(userId:Int,itemId:Int)= flow { emit(likeDao.selectSimple(userId, itemId)) }
    //
    fun insertConceptItem(list:List<ConceptItem>)= conceptDao.insertConceptItem(list)
    fun selectConceptItemList(bookId:Int,language:String)= flow { emit(conceptDao.selectConceptItemList(bookId, language)) }
    fun selectSimpleConceptItem(bookId:Int,language:String,index:Int)= flow { emit(conceptDao.selectSimpleConceptItem(bookId, language,index)) }
    fun updateListenConceptItem(bookId:Int,language:String,index:Int,progress:Int)= conceptDao.updateListenConceptItem(bookId, language,index,progress)
    fun updateEvalConceptItem(bookId:Int,language:String,index:Int,progress:Int)= conceptDao.updateEvalConceptItem(bookId, language,index,progress)
    fun updateWordConceptItem(bookId:Int,index:Int,progress:Int)= conceptDao.updateWordConceptItem(bookId,index,progress)
    fun updateWordConceptItemWithLanguage(bookId:Int,index:Int,progress:Int,language: String)=
        conceptDao.updateWordConceptItemWithLanguage(bookId,index,progress,language)
    fun updateExerciseConceptItem(bookId:Int,language:String,index:Int,progress:Int)= conceptDao.updateExerciseConceptItem(bookId, language,index,progress)
    //
    fun selectClassSentence(voaId:Int,userId:Int=GlobalMemory.userInfo.uid)= flow { emit(youngSentenceDao.selectClassSentence(userId,voaId)) }

    fun selectEvalInfoSentence(paraId:Int=1)= youngSentenceDao.selectEvalInfoSentence(paraId)[0]
    fun selectEvalInfoSentenceNew(paraId: Int,idIndex: Int)= youngSentenceDao.selectEvalInfoSentenceNew(paraId, idIndex)
    fun insertSentenceList(list: List<YoungSentenceItem>)= youngSentenceDao.insertSentenceList(list)
    fun updateYoungSentenceItemStatus(item: EvaluationSentenceItem) =
        youngSentenceDao.updateYoungSentenceItemStatus(
            item.Paraid.toInt(),
            item.onlyKay,
            item.success,
            item.fraction,
            item.selfVideoUrl,
            item.userId,
            item.voaid.toInt()
        )
    //
    fun selectAllYoungItem()= youngItemDao.selectAllYoungItem()
    fun insertYoungItem(list:List<YoungItem>)= youngItemDao.insertYoungItem(list)
    //
    fun insertYoungWordItem(list:List<YoungWordItem>)= youngWordDao.insertYoungWordItem(list)
    //这里不需要使用真实的userid，只操作使用0即可
    fun selectYoungWordById(voaId:Int,userId:Int=GlobalMemory.userInfo.uid)= flow { emit(youngWordDao.selectYoungWordById(userId, voaId)) }
    fun updateWordRightStatus(bookId:Int, position:Int, flag:Boolean, unitId: Int, userId:Int=GlobalMemory.userInfo.uid)=
        youngWordDao.updateWordRightStatus(bookId,  position, flag,userId,unitId)

    //查询单词闯关的正确单词数量
    fun selectWordBreakRightCount(type:String,bookId:Int,unitId: Int,userId: Int):Int= wordBreakDao.getRightWordCount(type, bookId, unitId, userId)
    //查询青少版的正确单词数量
    fun selectYoungRightWordCount(voaId: Int,userId: Int,correct: Boolean):Int=
        youngWordDao.selectYoungRightWordCount(voaId, userId, correct)
    //查询青少版的所有单词数量
    fun  selectYoungWordCount(voaId: Int):Int=
        youngWordDao.selectYoungWordCount(voaId)
    //
    fun insertYoungBookItem(list:List<BookItem>)= youngBookDao.insertYoungItem(list)
    fun selectYoungItemList(series:Int)= flow { emit(youngBookDao.selectYoungItemList(series,GlobalMemory.userInfo.uid)) }
    fun selectSimpleYoungItem(bookId:Int,index:Int)= flow { emit(youngBookDao.selectSimpleYoungItem(bookId, index)) }
    fun updateItemCollect(bookId:Int,index:Int,isCollect:Boolean)= youngBookDao.updateItemCollect(bookId, index,GlobalMemory.userInfo.uid,isCollect)
    fun updateItemDownload(bookId:Int,index:Int,isDownload:Boolean)= youngBookDao.updateItemDownload(bookId, index,GlobalMemory.userInfo.uid,isDownload)
    fun selectCollected()= flow { emit(youngBookDao.selectCollected(GlobalMemory.userInfo.uid)) }
    fun selectDownLoaded()= flow { emit(youngBookDao.selectDownLoaded(GlobalMemory.userInfo.uid)) }
    fun updateItemListen(bookId:Int,index:Int,userId: Int,progress:Int)= youngBookDao.updateItemListen(bookId, index, userId, progress)
    fun updateItemEval(bookId:Int,index:Int,userId: Int,progress:Int)= youngBookDao.updateItemEval(bookId, index, userId, progress)
    fun updateItemWord(bookId:Int,index:Int,userId: Int,progress:Int)= youngBookDao.updateItemWord(bookId, index, userId, progress)
    //
    fun insertSimpleLikeYoung(item: LikeYoung)= youngLikeDao.insertSimple(item)
    fun selectSimple(otherId:Int,userId:Int=GlobalMemory.userInfo.uid)= flow { emit(youngLikeDao.selectSimple(userId, otherId)) }
    //查询当前的点赞信息
    fun selectYoungAgreeData(otherId: Int,userId:Int=GlobalMemory.userInfo.uid):List<LikeYoung> =
        youngLikeDao.selectSimple(userId, otherId)

    fun selectByNumber(number:String)= flow { emit(reDoDao.selectByNumber(number)) }
    fun insertRedo(item:ReDoBean)= reDoDao.insertRedo(item)
    fun updateRedoMultipleStatus(number:String,flag:Boolean)= reDoDao.updateRedoMultipleStatus(number,flag)
    fun updateRedoStructureStatus(number:String,flag:Boolean)= reDoDao.updateRedoStructureStatus(number,flag)
}