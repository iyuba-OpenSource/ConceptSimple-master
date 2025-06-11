package com.suzhou.concept.viewmodel

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mob.secverify.datatype.VerifyResult
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.AddScoreResponse
import com.suzhou.concept.bean.GroupRankItem
import com.suzhou.concept.bean.GroupRankResponse
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.LoginBean
import com.suzhou.concept.bean.LoginResponse
import com.suzhou.concept.bean.LogoutUserResponse
import com.suzhou.concept.bean.ModifyUserNameResponse
import com.suzhou.concept.bean.RankItem
import com.suzhou.concept.bean.RankResponse
import com.suzhou.concept.bean.RequestPayResponse
import com.suzhou.concept.bean.SecondVerifyResponse
import com.suzhou.concept.bean.SelfResponse
import com.suzhou.concept.bean.SignResponse
import com.suzhou.concept.dao.paging.RankStudyPaging
import com.suzhou.concept.dao.paging.RankTopicPaging
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.utils.FlowResult
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.changeEncode
import com.suzhou.concept.utils.nowTime
import com.suzhou.concept.utils.putAppName
import com.suzhou.concept.utils.putFormat
import com.suzhou.concept.utils.putPlatform
import com.suzhou.concept.utils.putProtocol
import com.suzhou.concept.utils.signDate
import com.suzhou.concept.utils.toMd5
import data.ConfigData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.greenrobot.eventbus.EventBus
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
苏州爱语吧科技有限公司
 */
class UserActionViewModel :BaseViewModel() {

    suspend fun loginUser(login: LoginBean) :Flow<Result<LoginResponse>>{
        dataMap.apply {
            clear()
            put("username",login.username.changeEncode())
            put("password",login.password.toMd5())
            putAppName("app")
            put("token",login.token)
            putFormat()
            putAppId()
            putProtocol("11001")
            put("sign",login.getSign())
        }
        return Repository.login(operateUserUrl,dataMap)
    }

    val loginResult=MutableSharedFlow<FlowResult<LoginResponse>>(5,5)
    fun loginUser1(login: LoginBean) {
        dataMap.apply {
            clear()
            put("username",login.username.changeEncode())
            put("password",login.password.toMd5())
            putAppName("app")
            put("token",login.token)
            putFormat()
            putAppId()
            putProtocol("11001")
            put("sign",login.getSign())
        }
        viewModelScope.launch {
            Repository.login1(operateUserUrl,dataMap).onStart {
                loginResult.emit(FlowResult.Loading())
            }.catch {
                loginResult.emit(FlowResult.Error(it))
            }.collect{
                loginResult.emit(FlowResult.Success(it))
                refreshUserInfo()
                saveLogin(it)
            }
        }
    }

    suspend fun modifyUserName( login: LoginResponse,newName:String):Flow<Result<ModifyUserNameResponse>> {
        dataMap.apply {
            clear()
            put("username",newName)
            put("oldUsername",login.username)
            putFormat()
            putUserId("uid")
            putProtocol("10012")
            put("sign",login.getSign())
        }
        return Repository.modifyUserName(operateUserUrl,dataMap)
    }
    //冷流热流
    suspend fun logoutUser( login: LoginBean):Flow<Result<LogoutUserResponse>> {
        login.protocol="11005"
        dataMap.apply {
            clear()
            put("username",login.username.changeEncode())
            put("password",login.password.toMd5())
            putFormat()
            putProtocol(login.protocol)
            put("sign",login.getSign())
        }
        return Repository.logoutUser(operateUserUrl,dataMap)
    }
    //post请求居然拼接字符串？？？
    suspend fun uploadPhoto(part:MultipartBody.Part)= Repository.uploadPhoto(uploadPhotoUrl,part)


    fun fetchLanguageType()=Repository.getLanguage()
    fun saveLanguage(type:LanguageType)=Repository.saveLanguage(type)

    suspend fun updateLocalName(name:String)=Repository.modifyName(name).first()

    val initResult= MutableSharedFlow<Pair<Boolean,Boolean>>(10,10)

    fun initSomeThing(){
        //第一个为是否同意隐私，第二个为是否记载广告
        viewModelScope.launch {
            Repository.isFirstLogin().flatMapMerge {agree->
                Repository.getLoginResponse().flatMapConcat {
                    GlobalMemory.inflateLoginInfo(it)
                    refreshUserInfo()
                    flow { emit(Pair(agree,agree||it.isVip())) }
                }
            }.collect{
                initResult.emit(it)
            }
        }
    }
    fun saveLogin(login: LoginResponse)=Repository.saveLoginResponse(login)


    fun exitLogin()=Repository.exitLogin()

    fun saveIsFirstLogin(isFirstLogin: Boolean)=Repository.saveFirstLogin(isFirstLogin)
    fun modifyLocalHead(url: String)= Repository.modifyHead(url)



    suspend fun getRegisterStatus( username:String) :Flow<Result<LoginResponse>>{
        dataMap.apply {
            clear()
            putProtocol("10009")
            put("username",username)
            putFormat()
        }
        return Repository.getRegisterStatus(operateUserUrl,dataMap)
    }


    private suspend fun refreshSelf(id:String):Flow<Result<SelfResponse>> {
        val protocol= "20001"
        val sign="${protocol}${id}iyubaV2".toMd5()
        dataMap.apply {
            clear()
            putPlatform()
            putFormat()
            putProtocol(protocol)
            put("id",id)
            put("myid",id)
//            putUserId("id")
//            putUserId("myid")
            putAppId()
            put("sign",sign)
        }
        return Repository.refreshSelf(operateUserUrl,dataMap)
    }
    fun refreshUserInfo(){
        if (!GlobalMemory.isLogin()){
            return
        }
        viewModelScope.launch {
            val userId = GlobalMemory.userInfo.uid.toString()
//            val userId = "15198673"
            refreshSelf(userId)
                .collect {
                    it.onSuccess { result ->
                        if (result.result == "201") {
                            //合并到数据中
                            GlobalMemory.userInfo.vipStatus = result.vipStatus
                            GlobalMemory.userInfo.expireTime = result.expireTime
                            GlobalMemory.userInfo.self=result
                            saveSelf(result).collect {}

                            //刷新状态
                            EventBus.getDefault().post(RefreshEvent(RefreshEvent.USER_VIP,null))
                            //刷新单词状态
                            EventBus.getDefault().post(RefreshEvent(RefreshEvent.WORD_PASS_REFRESH,null))
                        }else{
                            //刷新状态
                            EventBus.getDefault().post(RefreshEvent(RefreshEvent.USER_VIP,"未获取到用户信息，请重新登录"))
                        }
                    }

                    it.onFailure {
                        //刷新状态
                        EventBus.getDefault().post(RefreshEvent(RefreshEvent.USER_VIP,"未获取到用户信息，请重新登录"))
                    }
                }
        }
    }
    fun getLoginResponse()=Repository.getLoginResponse()
    fun payVip(data:String)=Repository.payVip("${payUrl}notifyAliNew.jsp",data)

     fun requestPayVip( widTotalFee:String, amount:String, productId:String,cate:String,widBody:String):Flow<RequestPayResponse>{
        val uid=GlobalMemory.userInfo.uid.toString()
        val code = "${uid}iyuba${nowTime()}".toMd5()
        dataMap.apply {
            clear()
            putAppId("app_id")
            putUserId()
            put("code",code)
            put("WIDtotal_fee",widTotalFee)
            put("amount",amount)
            put("product_id",productId)
            put("WIDbody",widBody)
            put("WIDsubject",cate.changeEncode())
        }
        return Repository.requestPayVip("${payUrl}alipay.jsp", dataMap)
    }
    /**
     * 移动：CMCC
     * 电信：CTCC
     * */
    suspend fun secondVerify(verify: VerifyResult): Flow<SecondVerifyResponse> {

        //这里数据不对啊，难怪错误啊，哪怕你配置成动态文件的也行啊
        //先配置成默认的
//        val appKey=when(AppClient.context.packageName){
//            "com.iyuba.newconcepttop"->"32af3fb02e6c0"
//            "com.suzhou.concept"->"351c56226df5e"
//            else ->""
//        }
        val appKey = ConfigData.mobKey
        dataMap.apply {
            clear()
            putProtocol("10010")
            putAppId("appId")
            put("appkey",appKey)
            put("opToken",verify.opToken.changeEncode())
            put("operator",verify.operator.changeEncode())
            try {
                put("token",verify.token.changeEncode())
            }catch (e: UnsupportedEncodingException){
                put("token",verify.token)
            }
        }

        return Repository.secondVerify(secondVerifyUrl, dataMap)
    }

    suspend fun fastRegister(phone:String,item:LoginBean):Flow<Result<LoginResponse>>{
        dataMap.apply {
            clear()
            putProtocol(item.protocol)
            put("username",item.username.changeEncode())
            putFormat()
            putAppId()
            putAppName("app")
            put("sign",item.getSign())
            put("mobile",phone)
            put("password",item.password.toMd5())
        }
        return Repository.register(operateUserUrl,dataMap)
    }
    private fun saveSelf(self: SelfResponse)=Repository.saveSelf(self)
    fun saveSendSms()=Repository.saveSendSms()
    fun getSendSmsStatus()=Repository.getSendSmsStatus()

    val controlResponse=MutableLiveData<Boolean>()

    fun controlShow(flag:Boolean){
        controlResponse.value=flag
    }

    suspend fun signEveryDay(): Flow<Result<SignResponse>>{
        dataMap.apply {
            clear()
            putUserId("uid")
            put("day",getDayDistance().toString())
            put("flg","1")
        }
        return Repository.signEveryDay(signUrl,dataMap)
    }

    suspend fun shareAddScore(): Flow<Result<AddScoreResponse>> {
        val sdf=SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
        val dateString=sdf.format(Date())
        val flag=Base64.encodeToString(dateString.toByteArray(),Base64.NO_WRAP)
        dataMap.apply {
            clear()
            put("srid","81")
            putUserId("uid")
            putAppId()
            put("mobile","1")
            put("flag",flag)
        }
        return Repository.shareAddScore(shareContentUrl,dataMap)
    }

    /**
     * ----------------------------------------------------------------------------------------------
     * */
    private fun getRankingByType(flag: Boolean)= Pager(
        config = PagingConfig(
            pageSize = 30,
            initialLoadSize = 15
        ),
        pagingSourceFactory = { RankTopicPaging(flag) }
    ).flow
    private val topicRankResult=MutableSharedFlow<FlowResult<PagingData<RankItem>>>()
    private val testRankResult=MutableSharedFlow<FlowResult<PagingData<RankItem>>>()

    private val topicErrorRankResult=MutableSharedFlow<RankResponse>()
    private val testErrorRankResult=MutableSharedFlow<RankResponse>()

    fun judgeTopicErrorIn(flag: Boolean) =if (flag) topicErrorRankResult else testErrorRankResult
    fun judgeTopicRankIn(flag: Boolean) =if (flag) topicRankResult else testRankResult


    fun loadTopicRank(flag: Boolean){
        viewModelScope.launch {
            val date= signDate()
            val errorFlow=judgeTopicErrorIn(flag)
            val rankFlow=judgeTopicRankIn(flag)
            getTopicRanking(date = date, flag = flag).flatMapConcat {
                if (it.data.isEmpty()){
                    errorFlow.emit(it)
                    rankFlow.emit(FlowResult.Error(Throwable()))
                }
                getRankingByType(flag)
            }.catch {
                rankFlow.emit(FlowResult.Error(it))
            }.collect{
                rankFlow.emit(FlowResult.Success(it))
            }
        }
    }

    private fun getTopicRanking(start:String="0", total:String="30", date: String,flag:Boolean): Flow<RankResponse> {
        val topicId="0"
        val sign=with(StringBuilder()){
            append(GlobalMemory.userInfo.uid)
            append(AppClient.appName)
            append(topicId)
            append(start)
            append(total)
            append(date)
            toString().toMd5()
        }
        dataMap.apply {
            clear()
            putUserId("uid")
            put("type","D")
            put("start",start)
            put("total",total)
            put("sign",sign)
            put("topic", AppClient.appName)
            put("topicid",topicId)
            put("shuoshuotype","4")
        }
        val url=if (flag) GlobalMemory.topicRankUrl else GlobalMemory.testRankUrl
        return Repository.getTopicRankingCount(url,dataMap)
    }

    private val studyErrorRankResult=MutableSharedFlow<GroupRankResponse>()
    private val listenErrorRankResult=MutableSharedFlow<GroupRankResponse>()
    private val studyRankResult=MutableSharedFlow<FlowResult<PagingData<GroupRankItem>>>()
    private val listenRankResult=MutableSharedFlow<FlowResult<PagingData<GroupRankItem>>>()

    fun judgeRankFlow(flag:Boolean) =(if (flag) studyRankResult else listenRankResult)
    fun judgeErrorFlow(flag:Boolean) =(if (flag) studyErrorRankResult else listenErrorRankResult)


    fun loadStudyListenRank(flag: Boolean) {
        viewModelScope.launch {
            val rankFlow = judgeRankFlow(flag)
            val errorFlow = judgeErrorFlow(flag)
            val date = signDate()
            getStudyListenRanking(date = date, flag = flag).flatMapConcat {
                if (it.data.isEmpty()) {
                    errorFlow.emit(it)
                    rankFlow.emit(FlowResult.Error(Throwable()))
                }
                getStudyRankingByType(flag)
            }.collect {
                rankFlow.emit(FlowResult.Success(it))
            }
        }
    }
    /**
     * 获取(学习||听力)排行
     * */
    private fun getStudyListenRanking(start:String="0", total:String="30", date: String, flag:Boolean=false): Flow<GroupRankResponse> {
        val type="D"
        val sign=with(StringBuilder()){
            append(GlobalMemory.userInfo.uid)
            append(type)
            append(start)
            append(total)
            append(date)
            toString().toMd5()
        }
        val mode=(if (flag) "all" else "listening")
        dataMap.apply {
            clear()
            putUserId("uid")
            put("type",type)
            put("start",start)
            put("total",total)
            put("sign",sign)
            put("mode",mode)
        }
        return Repository.getStudyListenRankingCount(GlobalMemory.studyRankUrl,dataMap)
    }

    private fun getStudyRankingByType(flag:Boolean)=Pager(
        config = PagingConfig(
            pageSize = 30,
            initialLoadSize = 30
        ),
        pagingSourceFactory = { RankStudyPaging(flag) }
    ).flow

}