package com.suzhou.concept.dao

import android.content.Context
import com.google.gson.Gson
import com.suzhou.concept.AppClient
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.LoginResponse
import com.suzhou.concept.bean.SelfResponse
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils

/**
苏州爱语吧科技有限公司
 */
object UserDao {
    private const val firstLogin = "firstLogin"
    private const val loginResponse = "loginResponse"
    private const val languageType = "LanguageType"
    private const val throughWordId="through_word_id"
    private const val sendSms="SendSms"
    private const val splashAd="splashAd"
    private const val speakShow="speakShow"
    private val sharedPreferences = AppClient.context.getSharedPreferences(AppClient.appName, Context.MODE_PRIVATE)
    private val defaultLanguage = Gson().toJson(LanguageType())
    private val defaultYoungSpeakingLanguage = Gson().toJson(LanguageType("新概念英语青少版StarterA",278))
    private val defaultLogin = Gson().toJson(LoginResponse())
    fun getLanguage(): LanguageType {
        var language = sharedPreferences.getString(languageType, defaultLanguage)

        // TODO: 这里根据需要，修改下concept2这个包名的默认数据显示
        if (AppClient.context.packageName.equals(GlobalMemory.package_concept2)){
            language = "{\"bookId\":278,\"language\":\"新概念英语青少版StarterA\"}";
        }

        return Gson().fromJson(language, LanguageType::class.java)
    }

    fun saveLanguage(type: LanguageType) :Boolean{
        val edit = sharedPreferences.edit()
        edit.putString(languageType, Gson().toJson(type))
        return edit.commit()
    }

    fun isFirstLogin(): Boolean {
        return sharedPreferences.getBoolean(firstLogin, true)
    }

    fun saveFirstLogin(isFirstLogin: Boolean) :Boolean{
        sharedPreferences.edit().apply {
            putBoolean(firstLogin, isFirstLogin)
            apply()
        }
        return true
    }

    fun getLoginResponse(): LoginResponse {
        val login = sharedPreferences.getString(loginResponse, defaultLogin)
        return Gson().fromJson(login, LoginResponse::class.java)
    }

    fun saveLoginResponse(login: LoginResponse):Boolean {
        val json = Gson().toJson(login)
        val edit = sharedPreferences.edit()
        edit.putString(loginResponse, json)
        edit.apply()
        return true
    }

    fun modifyHead(url:String):Boolean{
        val l = getLoginResponse()
        l.imgSrc=url
        return saveLoginResponse(l)
    }

    fun modifyName(name:String):Boolean{
        val l = getLoginResponse()
        l.username=name
        return saveLoginResponse(l)
    }

    fun exitLogin() =saveLoginResponse(LoginResponse())

    fun saveSelf(self: SelfResponse):Boolean{
        val result=getLoginResponse()
        result.self=self
        return saveLoginResponse(result)
    }

    fun saveThroughWordId(wordId:Int):Boolean{
        sharedPreferences.edit().apply {
            putInt(throughWordId,wordId)
            apply()
        }
        return true
    }

    /**
     * 单词闯关默认为全四册的第一册
     * */
    fun getThroughWordId()=sharedPreferences.getInt(throughWordId,1)

    fun saveSendSms(){
        sharedPreferences.edit().putBoolean(sendSms,true).apply()
    }

    fun getSendSmsStatus()= sharedPreferences.getBoolean(sendSms,false)

    fun getSplash()=sharedPreferences.getString(splashAd,"${OtherUtils.splashHead}upload/1666082957218.jpg")

    fun saveSplash(url:String){
        sharedPreferences.edit().putString(splashAd,url).apply()
    }

    fun saveSpeakShow(speak:LanguageType){
        sharedPreferences.edit().putString(speakShow,Gson().toJson(speak)).apply()
    }

    fun getSpeakShow(): LanguageType = with(sharedPreferences.getString(speakShow,defaultYoungSpeakingLanguage)){
        Gson().fromJson(this,LanguageType::class.java)
    }

}